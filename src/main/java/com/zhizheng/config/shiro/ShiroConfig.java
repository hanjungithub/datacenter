package com.zhizheng.config.shiro;

import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.authz.ModularRealmAuthorizer;
import org.apache.shiro.codec.Base64;
import org.apache.shiro.mgt.SecurityManager;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.CookieRememberMeManager;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.servlet.Cookie;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.crazycake.shiro.RedisCacheManager;
import org.crazycake.shiro.RedisManager;
import org.crazycake.shiro.RedisSessionDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;

import javax.servlet.Filter;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.springframework.util.StringUtils.isEmpty;

/**
 * Created by DELL on 2017/12/13.
 */
@Configuration
public class ShiroConfig {

    @Value("${spring.redis.host}")
    private String host;

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.password}")
    private String password;

    @Value("${shiro.redisCacheExpire:1800}")
    private int redisCacheExpire;

    @Value("${shiro.filterChainDefinitions}")
    private String filterChainDefinitions;

    @Value("${shiro.sessionValidationInterval:300000}")
    private long sessionValidationInterval;




    /**
     * 安全管理器
     *
     * @return
     */
    @Bean
    public SecurityManager securityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(userRealm());
        securityManager.setSessionManager(sessionManager());
        securityManager.setCacheManager(cacheManager());
        securityManager.setRememberMeManager(rememberMeManager());

   /*     ModularRealmAuthorizer authorizer = new ModularRealmAuthorizer();
        authorizer.setRealms(securityManager.getRealms());
        authorizer.setPermissionResolver(new UserPermissionResolver());
        securityManager.setAuthorizer(authorizer);*/
        return securityManager;
    }

    /**
     * 配置shiro redisManager
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisManager redisManager() {
        RedisManager redisManager = new RedisManager();
        redisManager.setHost(host);
        redisManager.setPort(port);
        if (org.apache.commons.lang3.StringUtils.isNotEmpty(password)) {
            redisManager.setPassword(password);
        }
        redisManager.setExpire(redisCacheExpire);
        return redisManager;
    }

    /**
     * cacheManager 缓存 redis实现
     * 使用的是shiro-redis开源插件
     *
     * @return
     */
    public RedisCacheManager cacheManager() {
        RedisCacheManager redisCacheManager = new RedisCacheManager();
        redisCacheManager.setRedisManager(redisManager());
        redisCacheManager.setExpire(redisCacheExpire);
        return redisCacheManager;
    }

    /**
     * RedisSessionDAO shiro sessionDao层的实现 通过redis
     * 使用的是shiro-redis开源插件
     */
    public RedisSessionDAO redisSessionDAO() {
        RedisSessionDAO redisSessionDAO = new RedisSessionDAO();
        redisSessionDAO.setRedisManager(redisManager());
        return redisSessionDAO;
    }

    /**
     * shiro session的管理
     */
    public DefaultWebSessionManager sessionManager() {
        DefaultWebSessionManager sessionManager = new DefaultWebSessionManager();
        sessionManager.setSessionDAO(redisSessionDAO());
        //不同项目同域名下cookie对sessionId的处理
        Cookie cookie = sessionManager.getSessionIdCookie();
        cookie.setName("sid");
        sessionManager.setSessionIdCookie(cookie);
        sessionManager.setGlobalSessionTimeout(redisCacheExpire*1000);
        sessionManager.setSessionListeners(Arrays.asList(new ShiroSessionListener()));
        sessionManager.setSessionValidationInterval(sessionValidationInterval);
        return sessionManager;
    }

    /**
     * 凭证匹配器
     *
     * @return
     */
    public CredentialsMatcher credentialsMatcher() {
        HashedCredentialsMatcher credentialsMatcher = new HashedCredentialsMatcher();
        credentialsMatcher.setHashAlgorithmName("md5");//加密算法名称
        credentialsMatcher.setHashIterations(2);
        return credentialsMatcher;
    }

    /**
     * Realm实现
     *
     * @return
     */
    @Bean
    public AuthorizingRealm userRealm() {
        UserRealm userRealm = new UserRealm();
        userRealm.setCredentialsMatcher(credentialsMatcher());
        //用户信息不缓存
        userRealm.setAuthenticationCachingEnabled(false);
        userRealm.setAuthorizationCachingEnabled(false);
        return userRealm;
    }

    @Bean
    public ShiroFilterFactoryBean shiroFilter(SecurityManager securityManager) {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(securityManager);
        Map<String, Filter> filters = shiroFilterFactoryBean.getFilters();
        AuthorityFilter authorityFilter = new AuthorityFilter();
        filters.put("auth", authorityFilter);
        shiroFilterFactoryBean.setFilters(filters);
        //拦截器.
        Map<String, String> filterChainDefinitionMap = new LinkedHashMap<String, String>();
        // 配置不会被拦截的链接 顺序判断
        //拦截器url配置格式为：/**=user，等号前面为路径，后面为名称，多个拦截器用','分割，多个配置之间用';'分割
        if (!StringUtils.isEmpty(filterChainDefinitions)) {
            String[] array = StringUtils.delimitedListToStringArray(filterChainDefinitions, ";");
            for (String str : array) {
                if(isEmpty(str)){
                    continue;
                }
                String[] urlArray = str.split("=");

                filterChainDefinitionMap.put(urlArray[0].trim(), urlArray[1].trim());
            }
        }
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        // 如果不设置默认会自动寻找Web工程根目录下的"/login.jsp"页面
        shiroFilterFactoryBean.setLoginUrl("/login");
        // 登录成功后要跳转的链接
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("/401");
        return shiroFilterFactoryBean;
    }


    public CookieRememberMeManager rememberMeManager() {
        CookieRememberMeManager rememberMeManager = new CookieRememberMeManager();
        rememberMeManager.getCookie().setMaxAge(2592000);//有效期30天
        rememberMeManager.setCipherKey(Base64.decode("4AvVhmFLUs0KTA3Kprsdag=="));
        return rememberMeManager;
    }

}
