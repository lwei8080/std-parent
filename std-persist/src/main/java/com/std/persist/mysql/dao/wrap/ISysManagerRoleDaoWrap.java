package com.std.persist.mysql.dao.wrap;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.std.persist.model.SysManagerRole;
import com.std.persist.model.wrap.SysManagerRoleWrap;
import com.std.persist.mysql.dao.ISysManagerRoleDao;

@Repository("sysManagerRoleDaoWrap")
public interface ISysManagerRoleDaoWrap extends ISysManagerRoleDao {
	List<SysManagerRoleWrap> getSysManagerRoleByPage(Map<String, Object> params);
	int insertBatch(@Param("list") List<SysManagerRole> list);
}
