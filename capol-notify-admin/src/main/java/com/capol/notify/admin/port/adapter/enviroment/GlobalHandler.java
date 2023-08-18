package com.capol.notify.admin.port.adapter.enviroment;


import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.CatchableDomainException;
import com.capol.notify.manage.domain.DomainException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.context.MessageSource;
import org.springframework.core.MethodParameter;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.PathMatcher;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseBodyAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.ConstraintViolationException;
import javax.validation.ValidationException;
import java.lang.reflect.Executable;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static com.capol.notify.manage.domain.EnumExceptionCode.BadRequest;
import static com.capol.notify.manage.domain.EnumExceptionCode.InternalServerError;

/**
 * 响应数据全局处理
 *
 * @author heyong
 * @date 2023/04/17
 */
@Slf4j
@ControllerAdvice
public class GlobalHandler implements ResponseBodyAdvice<Object> {

    private static final Object NULL_OBJECT = new Object();

    private final List<String> responseNoWrapPaths = Arrays.asList("/actuator/**");
    private final PathMatcher pathMatcher = new AntPathMatcher();

    private final ObjectMapper objectMapper;
    private final MessageSource messageSource;

    public GlobalHandler(ObjectMapper objectMapper, MessageSource messageSource) {
        this.objectMapper = objectMapper;
        this.messageSource = messageSource;
    }

    /**
     * 实体叁数校验失败异常捕获
     *
     * @param ex
     * @param request
     * @return
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseBody
    public ErrorMessage methodArgumentNotValidException(MethodArgumentNotValidException ex, HttpServletRequest request) {
        String requestURI = request.getRequestURI();
        List<ObjectError> allErrors = ex.getBindingResult().getAllErrors();
        String errorStr = "";
        if (CollectionUtils.isNotEmpty(allErrors)) {
            errorStr = ex.getBindingResult().getAllErrors().stream().map(ObjectError::getDefaultMessage).collect(Collectors.joining(","));
        }
        ex.getParameter().getParameter();
        Executable executable = ex.getParameter().getParameter().getDeclaringExecutable();
        if (Objects.nonNull(executable)) {
            String clazz = executable.getDeclaringClass().getName();
            String method = executable.getName();
            log.error("路由：{}, 类：{}#{} 参数校验失败： [{}]", requestURI, clazz, method, errorStr);
        }
        return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", "参数校验失败：" + errorStr));
    }

    /**
     * 默认全局异常捕获
     *
     * @param request
     * @param response
     * @param e
     * @return
     */
    @ExceptionHandler(value = Exception.class)
    @ResponseBody
    public ErrorMessage defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception e) {
        if (e instanceof ApplicationException) {
            // 平台异常
            ApplicationException ae = (ApplicationException) e;
            return new ErrorMessage(ae.getExceptionCode(),
                    new CodeAndMessage(ae.getCode(), ae.getMessage()), ae.getArgs());
        } else if (e instanceof DomainException) {
            DomainException de = (DomainException) e;
            return new ErrorMessage(de.getExceptionCode(),
                    new CodeAndMessage(de.getExceptionCode().getCode(), de.getMessage()), de.getArgs());
        } else if (e instanceof CatchableDomainException) {
            CatchableDomainException cde = (CatchableDomainException) e;
            return new ErrorMessage(cde.getExceptionCode(),
                    new CodeAndMessage(cde.getExceptionCode().getCode(), cde.getMessage()), cde.getArgs());
        } else if (e instanceof BindException) {
            // 方法参数校验失败
            BindException ex = (BindException) e;
            if (ex.getBindingResult().hasErrors()) {
                Optional<ObjectError> firstError = ex.getBindingResult().getAllErrors().stream().findFirst();
                if (firstError.isPresent()) {
                    return new ErrorMessage(BadRequest,
                            new CodeAndMessage("IllegalArgument", firstError.get().getDefaultMessage()));
                }
            }
        } else if (e instanceof IllegalArgumentException) {
            // 参数错误
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof IllegalStateException) {
            // 不适当的状态
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalState", e.getMessage()));
        } else if (e instanceof ConstraintViolationException) {
            // 违反约束条件
            StringBuilder builder = new StringBuilder("[");
            ((ConstraintViolationException) e).getConstraintViolations().forEach(violation -> {
                builder.append(violation.getMessage()).append(",");
            });
            String message = builder.substring(0, builder.length() - 1) + "]";
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", message));
        } else if (e instanceof ValidationException) {
            // 参数校验异常
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof HttpMessageNotReadableException) {
            // 参数校验异常（枚举字段）
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        } else if (e instanceof MethodArgumentTypeMismatchException) {
            return new ErrorMessage(BadRequest, new CodeAndMessage("IllegalArgument", e.getMessage()));
        }

        log.error("GlobalHandler-未捕获的异常:{}", e);
        return new ErrorMessage(InternalServerError, new CodeAndMessage("InternalServerError", "系统内部异常,请联系管理员!"));
    }

    /**
     * 是否需要支持ResponseBody重写
     *
     * @param methodParameter
     * @param aClass
     * @return
     */
    @Override
    public boolean supports(MethodParameter methodParameter, Class<? extends HttpMessageConverter<?>> aClass) {
        // 不拦截的路径
        RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
        if (requestAttributes instanceof ServletRequestAttributes) {
            HttpServletRequest request = ((ServletRequestAttributes) requestAttributes).getRequest();
            if (responseNoWrapPaths.stream().anyMatch(pattern -> pathMatcher.match(pattern, request.getRequestURI()))) {
                return false;
            }
        }

        //获取当前处理请求的controller的方法
        Method method = methodParameter.getMethod();
        if (method == null) {
            return false;
        }

        // 不拦截不需要处理返回值的方法
        String name = method.getName();
        return !name.equals("uiConfiguration") && !name.equals("swaggerResources") && !name.equals("getDocumentation");
    }

    /**
     * 实现统一的接口返回类型
     *
     * @param o
     * @param methodParameter
     * @param mediaType
     * @param aClass
     * @param request
     * @param response
     * @return
     */
    @Override
    public Object beforeBodyWrite(Object o, MethodParameter methodParameter, MediaType mediaType,
                                  Class<? extends HttpMessageConverter<?>> aClass,
                                  ServerHttpRequest request, ServerHttpResponse response) {
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        if (o == null) {
            return ResponseBase.ok(NULL_OBJECT);
        }
        if (o instanceof ErrorMessage) {
            ErrorMessage errorMessage = (ErrorMessage) o;
            response.setStatusCode(errorMessage.getExceptionCode().httpStatus());
            String msg = errorMessage.getDefaultMsg() != null ? errorMessage.getDefaultMsg()
                    : errorMessage.getExceptionCode().getCode();
            return ResponseBase.of(String.valueOf(errorMessage.getExceptionCode().httpStatus().value()), msg, errorMessage.getContent());
        } else if (o instanceof JsonNode) {
            return ResponseBase.ok(o.toString());
        } else if (o instanceof String) {
            try {
                return objectMapper.writeValueAsString(ResponseBase.ok(o));
            } catch (JsonProcessingException e) {
                return ResponseBase.of(InternalServerError.getCode(), InternalServerError.getCode(), null);
            }
        }
        return ResponseBase.ok(o);
    }

    /**
     * 初始化绑定器Binder实现日期类型转换
     *
     * @param binder
     */
    @InitBinder
    public void initBinder(final WebDataBinder binder) {
        binder.registerCustomEditor(LocalDateTime.class, new CustomDateEditor(objectMapper.getDateFormat(), true));
        binder.registerCustomEditor(Date.class, new CustomDateEditor(objectMapper.getDateFormat(), true));
    }

    /**
     * 返回响应码及消息
     */
    @Getter
    static class CodeAndMessage {
        private final String code;
        private final String msg;

        public CodeAndMessage(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }
    }

    /**
     * 异常类型结构
     */
    @Getter
    static class ErrorMessage {

        private final EnumExceptionCode exceptionCode;
        private final Object content;

        private Object[] args;
        private String defaultMsg;

        public ErrorMessage(EnumExceptionCode exceptionCode, Object[] args) {
            this.exceptionCode = exceptionCode;
            this.content = NULL_OBJECT;
            this.args = args;
        }

        public ErrorMessage(EnumExceptionCode exceptionCode, Object content, Object[] args) {
            this.exceptionCode = exceptionCode;
            this.content = content;
            this.args = args;
        }

        public ErrorMessage(EnumExceptionCode exceptionCode, Object content) {
            this.exceptionCode = exceptionCode;
            this.content = content;
        }
    }

    /**
     * 统一返回数据结构
     *
     * @param <T>
     */
    @Data
    static class ResponseBase<T> {

        private String code;
        private String message;
        private T data;

        public ResponseBase(String code, String message, T data) {
            this.code = code;
            this.message = message;
            this.data = data;
        }

        public ResponseBase() {
        }

        public static <T> ResponseBase<T> of(String code, String message, T data) {
            return new ResponseBase<T>(code, message, data);
        }

        public static <T> ResponseBase<T> ok(T data) {
            return new ResponseBase<T>(EnumExceptionCode.OK.getCode(), EnumExceptionCode.OK.name(), data);
        }
    }
}