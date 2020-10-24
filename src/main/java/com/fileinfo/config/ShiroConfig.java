package com.fileinfo.config;

import com.fileinfo.shiro.MD5Realm;
import com.fileinfo.shiro.ShiroAccessFilter;
import org.apache.shiro.authc.credential.HashedCredentialsMatcher;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.spring.LifecycleBeanPostProcessor;
import org.apache.shiro.spring.web.ShiroFilterFactoryBean;
import org.apache.shiro.web.mgt.DefaultWebSecurityManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.servlet.Filter;
import java.util.HashMap;
import java.util.Map;

@Configuration
public class ShiroConfig {
    @Bean
    public ShiroFilterFactoryBean getShiroFilterFactoryBean(){
        ShiroFilterFactoryBean shiroBean = new ShiroFilterFactoryBean();
        // shiro过滤请求路径
        Map<String, String> filterChainMap = new HashMap<>();
        filterChainMap.put("/login","anon");
        filterChainMap.put("/**","anon");
        shiroBean.setFilterChainDefinitionMap(filterChainMap);
        shiroBean.setLoginUrl("/login");
        shiroBean.setSuccessUrl("/index");
        shiroBean.setUnauthorizedUrl("/login");

        // shiro过滤器（基于serverlet的Filter）
        Map<String, Filter> filters = new HashMap<>();
        filters.put("accessFilter",new ShiroAccessFilter());
        shiroBean.setFilters(filters);
        shiroBean.setSecurityManager(getDefaultWebSecurityManager());
        return shiroBean;
    }

    @Bean
    public DefaultWebSecurityManager getDefaultWebSecurityManager(){
        DefaultWebSecurityManager securityManager = new DefaultWebSecurityManager();
        securityManager.setRealm(getRealm());
        return securityManager;
    }
    @Bean
    public Realm getRealm(){
        MD5Realm md5Realm = new MD5Realm();
        md5Realm.setCredentialsMatcher(getHashedCredentialsMatcher());
        return new MD5Realm();
    }

    @Bean
    public HashedCredentialsMatcher getHashedCredentialsMatcher(){
        HashedCredentialsMatcher hashedCredentialsMatcher = new HashedCredentialsMatcher();
        hashedCredentialsMatcher.setHashAlgorithmName("MD5");
        hashedCredentialsMatcher.setHashIterations(1024);
        return hashedCredentialsMatcher;
    }

    @Bean("lifecycleBeanPostProcessor")
    public LifecycleBeanPostProcessor getLifecycleBeanPostProcessor(){
        return new LifecycleBeanPostProcessor();
    }
}
