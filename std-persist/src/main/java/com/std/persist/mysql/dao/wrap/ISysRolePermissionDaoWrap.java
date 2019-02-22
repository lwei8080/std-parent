package com.std.persist.mysql.dao.wrap;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.std.persist.model.SysRolePermission;
import com.std.persist.model.wrap.SysRolePermissionWrap;
import com.std.persist.mysql.dao.ISysRolePermissionDao;

@Repository("sysRolePermissionDaoWrap")
public interface ISysRolePermissionDaoWrap extends ISysRolePermissionDao {
	List<SysRolePermissionWrap> getSysRolePermissionByPage(Map<String, Object> params);
	int insertBatch(@Param("list") List<SysRolePermission> list);
}
