package com.std.manage.integration.shiro;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExcessiveAttemptsException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAuthenticationInfo;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.AllowAllCredentialsMatcher;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.authz.SimpleAuthorizationInfo;
import org.apache.shiro.cache.Cache;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import com.std.biz.ISysService;
import com.std.common.consts.CommonConsts;
import com.std.common.utils.MD5;
import com.std.manage.consts.ApplicationConsts;
import com.std.persist.model.SysManager;
import com.std.persist.model.SysPermission;
import com.std.persist.model.SysRole;

/**
 * 在认证、授权内部实现机制中都有提到，最终处理都将交给Real进行处理
 * @author liuwei3
 *
 */
public class StdShiroRealm extends AuthorizingRealm {
	
    @Autowired
    private ISysService sysService;
    
    private Cache<String,AtomicInteger> passwordRetryCache;

    public StdShiroRealm() {
        super(new AllowAllCredentialsMatcher());
        setAuthenticationTokenClass(UsernamePasswordToken.class);
        //  启用缓存,默认false
    	setCachingEnabled(true);
        //  启用身份验证缓存，即缓存AuthenticationInfo信息，默认false；
    	setAuthenticationCachingEnabled(true);
        //  缓存AuthenticationInfo信息的缓存名称,即配置在ehcache.xml中的cache name
    	setAuthenticationCacheName(ApplicationConsts.authenticationCache);
        //  启用授权缓存，即缓存AuthorizationInfo信息，默认false；
    	setAuthorizationCachingEnabled(true);
        //  缓存AuthorizationInfo信息的缓存名称；
    	setAuthorizationCacheName(ApplicationConsts.authorizationCache);
    }

    /**
     * 权限授权是通过继承AuthorizingRealm抽象类，重载doGetAuthorizationInfo()
     * 当访问到页面的时候，链接配置了相应的权限或者shiro标签才会执行此方法否则不会执行，
     * 所以如果只是简单的身份认证没有权限的控制的话，那么这个方法可以不进行实现，直接返回null即可。
     * 在这个方法中主要是使用类：SimpleAuthorizationInfo进行角色的添加和权限的添加。
     * @author linyuanhuang
     * 16:27 2018/3/29
     * @return org.apache.shiro.authz.AuthorizationInfo
    */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(
            PrincipalCollection principals) {
    	SysManager manager = (SysManager) principals.getPrimaryPrincipal();
        SimpleAuthorizationInfo authorizationInfo = new SimpleAuthorizationInfo();
        Set<String> shiroPermissions = new HashSet<>();
        Set<String> roleSet = new HashSet<String>();
        Set<SysRole> roles = sysService.getRolesByAccount(manager.getAccount());
        if(CollectionUtils.isNotEmpty(roles)) {
        	roleSet.addAll(roles.stream().map(SysRole::getName).collect(Collectors.toList()));
        	Set<SysPermission> permissions = sysService.getValidPermissionsByRoles(roles);
        	if(CollectionUtils.isNotEmpty(permissions))
        		shiroPermissions.addAll(permissions.stream().map(SysPermission::getPermissions).filter(str -> !StringUtils.isEmpty(str)).collect(Collectors.toList()));
        }
        authorizationInfo.setRoles(roleSet);
        authorizationInfo.setStringPermissions(shiroPermissions);
        return authorizationInfo;
    }

    /**
     * Shiro的认证过程最终会交由Realm执行，这时会调用Realm的getAuthenticationInfo(token)方法
     * 该方法主要执行以下操作:
         1、检查提交的进行认证的令牌信息;
         2、根据令牌信息从数据源(通常为数据库)中获取用户信息;
         3、对用户信息进行匹配验证;
         4、验证通过将返回一个封装了用户信息的AuthenticationInfo实例;
         5、验证失败则抛出AuthenticationException异常信息。
     * @author linyuanhuang
     * 16:26 2018/3/29
     * @return org.apache.shiro.authc.AuthenticationInfo  
    */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(
            AuthenticationToken token) throws AuthenticationException {
    	if(null==passwordRetryCache) {
    		passwordRetryCache = getCacheManager().getCache(ApplicationConsts.passwordRetryCacheName);
    	}
    	
        String username = (String) token.getPrincipal();
        
        AtomicInteger retryCount = passwordRetryCache.get(username);
        if(null!=retryCount) {
            if (retryCount.get() > Integer.parseInt(ApplicationConsts.retryCountMax)) {
                throw new ExcessiveAttemptsException(CommonConsts.ReferenceMessage.ACCOUNT_PWD_ERROR_TOO_FREQUENCY.getMessage());
            }
        }

        SysManager manager = sysService.findSysManagerByAccount(username);
        
        // 账号不存在
        if (manager == null) {
            throw new UnknownAccountException(CommonConsts.ReferenceMessage.ACCOUNT_PWD_ERROR.getMessage());
        }
        Object credentials = token.getCredentials();
        if (credentials == null) {
            throw new UnknownAccountException(CommonConsts.ReferenceMessage.ACCOUNT_PWD_ERROR.getMessage());
        }
        String password = new String((char[]) credentials);
        // 密码错误
        if (!MD5.MD5Encode(password).equals(manager.getPwd())) {
            if (null == retryCount) {
                retryCount = new AtomicInteger(1);
            } else {
            	retryCount.incrementAndGet();
            }
            passwordRetryCache.put(username,retryCount);
            throw new IncorrectCredentialsException(CommonConsts.ReferenceMessage.ACCOUNT_PWD_ERROR.getMessage());
        }
        // 账号锁定
        if (manager.getState().intValue() == Integer.parseInt(CommonConsts.ValidState.INVALID.getValue())) {
            throw new LockedAccountException(CommonConsts.ReferenceMessage.ACCOUNT_LOCK.getMessage());
        }

        SimpleAuthenticationInfo info = new SimpleAuthenticationInfo(manager, password, getName());

        return info;
    }

    
}
