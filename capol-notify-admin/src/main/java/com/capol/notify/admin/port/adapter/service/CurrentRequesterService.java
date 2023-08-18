package com.capol.notify.admin.port.adapter.service;


import com.capol.notify.admin.port.adapter.enviroment.CurrentRequester;
import com.capol.notify.manage.domain.model.permission.CurrentUserService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * 当前用户上下文
 *
 * @author heyong1
 */
@Component
public class CurrentRequesterService implements CurrentUserService {

    private CurrentRequester getCurrentRequester() {
        if (SecurityContextHolder.getContext().getAuthentication() == null ||
                SecurityContextHolder.getContext().getAuthentication().getPrincipal().toString().equals("anonymousUser")) {
            return null;
        }
        return (CurrentRequester) SecurityContextHolder.getContext().getAuthentication();
    }

    @Override
    public String getCurrentUserId() {
        CurrentRequester requester = getCurrentRequester();
        if (requester == null || StringUtils.isEmpty(requester.getUserDescriptor().getUserId())) {
            return null;
        }
        return requester.getUserDescriptor().getUserId();
    }

    @Override
    public boolean isCurrentUserAuthenticated() {
        CurrentRequester requester = getCurrentRequester();
        return requester != null && requester.isAuthenticated();
    }

    @Override
    public boolean isCurrentUser(String userId) {
        return StringUtils.equals(getCurrentUserId(), userId);
    }

    @Override
    public String getCurrentUserIp() {
        CurrentRequester requester = getCurrentRequester();
        if (requester == null || requester.getIp() == null) {
            return "";
        }
        return requester.getIp();
    }

    @Override
    public String getCurrentUserName() {
        CurrentRequester requester = getCurrentRequester();
        if (requester == null || requester.getName() == null) {
            return "";
        }
        return requester.getName();
    }
}
