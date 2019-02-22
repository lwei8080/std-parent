package com.std.manage.integration.shiro.tags;

/**
 * <p>Equivalent to {@link org.apache.shiro.web.tags.HasPermissionTag}</p>
 *
 * @since 0.1
 */
public class HasAnyPermissionTag extends PermissionTag {
	private static final String OR_OPERATOR = " or ";  
	private static final String AND_OPERATOR = " and ";  
	
    protected boolean showTagBody(String p) {
    	boolean isPermission = false;
    	if(p.contains(OR_OPERATOR)) {
    		String[] permissions = p.split(OR_OPERATOR);
    		for(String orPermission : permissions) {
    			if(isPermitted(orPermission)) {
    				isPermission = true;
    				break;
    			}
    		}
    	}else if(p.contains(AND_OPERATOR)) {
    		String[] permissions = p.split(AND_OPERATOR);
    		for(String andPermission : permissions) {
    			if(!isPermitted(andPermission)) {
    				isPermission = false;
    				break;
    			}else {
    				isPermission = true;
    			}
    		}
    	}else {
    		isPermission = isPermitted(p);
    	}
        return isPermission;
    }
}
