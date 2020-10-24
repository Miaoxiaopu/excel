package com.fileinfo.shiro;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.fileinfo.entity.GovUserEntity;
import com.fileinfo.service.IGovUserService;
import com.fileinfo.utils.UserUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.ByteSource;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Set;

public class MD5Realm extends AuthorizingRealm {
    @Autowired
    private IGovUserService IGovUserService;

    // 授权
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principalCollection) {
        // 用户名
        GovUserEntity primaryPrincipal = (GovUserEntity)principalCollection.getPrimaryPrincipal();
        // 从数据库中获取角色和权限
        SimpleAuthorizationInfo simpleAuthorizationInfo = new SimpleAuthorizationInfo();
        // 设置角色
        Set<String> roles = new HashSet<>();
        roles.add("admin");
        simpleAuthorizationInfo.setRoles(roles);
        // 设置权限
        simpleAuthorizationInfo.addStringPermission("admin:*:02");
        return simpleAuthorizationInfo;
    }

    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken authenticationToken) throws AuthenticationException {
        String username = (String)authenticationToken.getPrincipal();
        GovUserEntity user = UserUtils.getUser(username);
        if(user == null){
            return null;
        }
        SimpleAuthenticationInfo simpleAuthenticationInfo = new SimpleAuthenticationInfo(username,
                user,
                ByteSource.Util.bytes(user.getSalt()),getName());
        return simpleAuthenticationInfo;
    }

}
