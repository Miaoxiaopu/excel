package com.fileinfo.shiro;

import com.fileinfo.shiro.cache.CustomCacheManager;
import com.fileinfo.shiro.session.CustomSessionDAO;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.mgt.SessionsSecurityManager;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.session.mgt.SessionManager;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.apache.shiro.web.session.mgt.DefaultWebSessionManager;
import org.springframework.aop.framework.autoproxy.DefaultAdvisorAutoProxyCreator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;

@Configuration
public class ShiroConfig {

    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean() {
        ShiroFilterFactoryBean shiroFilterFactoryBean = new ShiroFilterFactoryBean();
        shiroFilterFactoryBean.setSecurityManager(getSecurityManager());

        HashMap<String, String> filterChainDefinitionMap = new HashMap();
        filterChainDefinitionMap.put("/logout", "anon");
        filterChainDefinitionMap.put("/js/**", "anon");
        filterChainDefinitionMap.put("/css/**", "anon");
        filterChainDefinitionMap.put("/login.html", "anon");
        filterChainDefinitionMap.put("/login", "anon");
        filterChainDefinitionMap.put("/**", "authc");

        shiroFilterFactoryBean.setLoginUrl("/login.html");
        shiroFilterFactoryBean.setSuccessUrl("/");
        shiroFilterFactoryBean.setUnauthorizedUrl("login.html");
        shiroFilterFactoryBean.setFilterChainDefinitionMap(filterChainDefinitionMap);
        return shiroFilterFactoryBean;
    }

    @Bean("securityManager")
    public SessionsSecurityManager getSecurityManager() {
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getRealm());
        securityManager.setSessionManager(getDefaultWebSessionManager());
        SecurityUtils.setSecurityManager(securityManager);
        return securityManager;
    }

    @Bean("sessionManager")
    public SessionManager getDefaultWebSessionManager() {
        DefaultWebSessionManager defaultSessionManager = new DefaultWebSessionManager();
        // 设置session管理
        defaultSessionManager.setGlobalSessionTimeout(30 * 60 * 1000L);
        defaultSessionManager.setDeleteInvalidSessions(true);
        // 定时删除失效session
        defaultSessionManager.setSessionValidationSchedulerEnabled(true);
        defaultSessionManager.setSessionDAO(new CustomSessionDAO());
        return defaultSessionManager;
    }

    @Bean
    public Realm getRealm() {
        CustomRealm customRealm = new CustomRealm();
        customRealm.setCredentialsMatcher(getHashedCredentialsMatcher());
        // 设置认证信息、权限信息缓存
        customRealm.setCacheManager(new CustomCacheManager());
        customRealm.setAuthenticationCacheName("authentication");
        customRealm.setAuthorizationCacheName("authorization");
        customRealm.setAuthenticationCachingEnabled(true);
        customRealm.setAuthorizationCachingEnabled(true);
        return customRealm;
    }

    @Bean
    public HashedCredentialsMatcher getHashedCredentialsMatcher() {
        HashedCredentialsMatcher matcher = new HashedCredentialsMatcher();
        matcher.setHashAlgorithmName("MD5");
        matcher.setHashIterations(1024);
        return matcher;
    }

    @Bean
    public static DefaultAdvisorAutoProxyCreator getDefaultAdvisorAutoProxyCreator() {
        DefaultAdvisorAutoProxyCreator defaultAdvisorAutoProxyCreator = new DefaultAdvisorAutoProxyCreator();
        /**
         * 还有未认证会默认跳转到login.jsp
         * setUsePrefix(false)用于解决一个奇怪的bug。在引入spring aop的情况下。
         * 在@Controller注解的类的方法中加入@RequiresRole等shiro注解，会导致该方法无法映射请求，导致返回404。 加入这项配置能解决这个bug
         */
        defaultAdvisorAutoProxyCreator.setUsePrefix(true);
        return defaultAdvisorAutoProxyCreator;
    }
}
