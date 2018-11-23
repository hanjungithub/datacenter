package com.zhizheng.config.shiro;

import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.filter.authz.AuthorizationFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * 自定义shiro过滤器，用来做动态权限验证
 * Created by DELL on 2017/12/16.
 */
public class AuthorityFilter extends AuthorizationFilter {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Override
    protected boolean isAccessAllowed(ServletRequest servletRequest, ServletResponse servletResponse, Object o){
        if (isLoginRequest(servletRequest, servletResponse)) {
            return true;
        } else {
            Subject subject = this.getSubject(servletRequest, servletResponse);
            String requestUri = getPathWithinApplication(servletRequest);
            //判断是否登录或者401
            if (subject.getPrincipal() == null || isUnauthorizedRequest(requestUri)) {
                return false;
            }
            String path = getPathWithinApplication(servletRequest);
            try {
                subject.checkPermission(path);
            } catch (AuthorizationException e) {
                if (logger.isErrorEnabled()) {
                    logger.error(e.getMessage(), e);
                }
                return false;
            }
        }
        return true;
    }


    private boolean isUnauthorizedRequest(String requestUri) {
        if (getUnauthorizedUrl() != null && getUnauthorizedUrl().equals(requestUri)) {
            return true;
        }
        return false;
    }

}
