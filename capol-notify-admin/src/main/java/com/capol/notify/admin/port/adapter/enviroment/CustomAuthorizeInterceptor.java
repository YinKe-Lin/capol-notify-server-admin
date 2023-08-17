package com.capol.notify.admin.port.adapter.enviroment;


import com.capol.notify.admin.port.adapter.restapi.AllowAnonymous;
import com.capol.notify.admin.port.adapter.restapi.AuthorizedOperation;
import com.capol.notify.manage.application.ApplicationException;
import com.capol.notify.manage.domain.EnumExceptionCode;
import com.capol.notify.manage.domain.model.permission.AuthorizationService;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import com.capol.notify.manage.domain.model.user.UserId;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Slf4j
@Component
public class CustomAuthorizeInterceptor implements HandlerInterceptor {

    private final CurrentUserService currentUserService;
    private final AuthorizationService authorizationService;

    public CustomAuthorizeInterceptor(CurrentUserService currentUserService,
                                      AuthorizationService authorizationService) {
        this.currentUserService = currentUserService;
        this.authorizationService = authorizationService;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
            return true;
        }
        if (!(handler instanceof HandlerMethod)) {
            return true;
        }
        HandlerMethod method = (HandlerMethod) handler;
        AllowAnonymous allowAnonymous = method.getMethodAnnotation(AllowAnonymous.class);
        if (allowAnonymous != null && allowAnonymous.value()) {
            return true;
        }
        if (!currentUserService.isCurrentUserAuthenticated()) {
            throw new ApplicationException(String.format("当前账户未鉴权,不允许访问!"), EnumExceptionCode.Unauthorized);
        }
        if (!authorize(method)) {
            throw new ApplicationException(String.format("当前账户权限不合法,不允许访问!"), EnumExceptionCode.Forbidden);
        }
        return true;
    }

    /**
     * 检查权限
     */
    private boolean authorize(HandlerMethod method) {
        AuthorizedOperation authorizedOperation = method.getMethodAnnotation(AuthorizedOperation.class);
        if (authorizedOperation == null) {
            return true;
        }
        log.info("-->【检查权限】当前操作名称:{} KEY:{},{}", authorizedOperation.name(), authorizedOperation.key(), authorizedOperation.description());
        return authorizationService.authorizedOperation(new UserId(currentUserService.getCurrentUserId()));
    }
}
