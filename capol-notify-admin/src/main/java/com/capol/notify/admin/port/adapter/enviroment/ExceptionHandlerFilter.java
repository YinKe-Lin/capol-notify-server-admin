package com.capol.notify.admin.port.adapter.enviroment;

import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 统一处理过滤器链的异常(响应格式上统一)
 *
 * @author heyong
 * @date 2023-04-17
 */
@Slf4j
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final ObjectMapper objectMapper;

    public ExceptionHandlerFilter(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws IOException {

        EnumExceptionCode exceptionCode;
        String message;
        try {
            filterChain.doFilter(request, response);
            return;
        } catch (ApplicationException exception) {
            exceptionCode = exception.getExceptionCode();
            message = exception.getMessage();
        } catch (RuntimeException exception) {
            exceptionCode = EnumExceptionCode.BadRequest;
            message = exception.getMessage();
            log.error("-->未处理的运行时异常:{}", exception);
        } catch (Exception e) {
            exceptionCode = EnumExceptionCode.InternalServerError;
            message = e.getMessage();
            log.error("-->未处理的运行时异常:{}", e);
        }
        response.setStatus(exceptionCode.httpStatus().value());
        response.setContentType("application/json; charset=utf-8");
        response.getWriter().write(objectMapper.writeValueAsString(GlobalHandler.ResponseBase.of(
                exceptionCode.getCode(),
                exceptionCode.name(),
                message
        )));
    }
}