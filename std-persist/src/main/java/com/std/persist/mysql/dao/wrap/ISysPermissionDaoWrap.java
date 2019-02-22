package com.std.persist.mysql.dao.wrap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

import com.std.persist.model.SysPermission;
import com.std.persist.model.wrap.SysPermissionWrap;
import com.std.persist.mysql.dao.ISysPermissionDao;

@Repository("sysPermissionDaoWrap")
public interface ISysPermissionDaoWrap extends ISysPermissionDao {
	Set<SysPermission> selectByRoles(Map<String, Object> params);
	List<SysPermissionWrap> getSysPermissionByPage(Map<String, Object> params);
}
