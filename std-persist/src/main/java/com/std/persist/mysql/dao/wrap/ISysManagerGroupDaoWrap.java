package com.std.persist.mysql.dao.wrap;

import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.std.persist.model.wrap.SysManagerGroupWrap;
import com.std.persist.mysql.dao.ISysManagerGroupDao;

@Repository("sysManagerGroupDaoWrap")
public interface ISysManagerGroupDaoWrap extends ISysManagerGroupDao {
	List<SysManagerGroupWrap> getSysManagerGroupByPage(Map<String, Object> params);
}
