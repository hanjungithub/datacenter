package com.zhizheng.config.shiro;


import org.apache.shiro.session.Session;
import org.apache.shiro.session.SessionListenerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Administrator on 2018/2/5.
 */
public class ShiroSessionListener extends SessionListenerAdapter {

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    //会话过期触发
    @Override
    public void onExpiration(Session session) {
        super.onExpiration(session);
    }

    //退出时触发
    @Override
    public void onStop(Session session) {
        super.onStop(session);
    }
}
