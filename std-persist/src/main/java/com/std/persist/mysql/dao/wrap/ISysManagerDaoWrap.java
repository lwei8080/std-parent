package com.std.persist.mysql.dao.wrap;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Repository;

import com.std.persist.model.SysManager;
import com.std.persist.mysql.dao.ISysManagerDao;

@Repository("sysManagerDaoWrap")
public interface ISysManagerDaoWrap extends ISysManagerDao {
	List<SysManager> getSysManagerByPage(Map<String, Object> params);
	Date getSysDate();
}
