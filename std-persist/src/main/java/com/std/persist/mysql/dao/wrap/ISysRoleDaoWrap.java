package com.std.persist.mysql.dao.wrap;

import java.util.List;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Repository;

import com.std.persist.model.SysRole;
import com.std.persist.mysql.dao.ISysRoleDao;

@Repository("sysRoleDaoWrap")
public interface ISysRoleDaoWrap extends ISysRoleDao {
	Set<SysRole> selectByAccount(Map<String, Object> params);
	List<SysRole> getSysRoleByPage(Map<String, Object> params);
}
