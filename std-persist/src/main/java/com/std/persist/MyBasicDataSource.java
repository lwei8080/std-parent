package com.std.persist;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import com.alibaba.druid.pool.DruidDataSource;
import com.std.common.utils.EncryptAES;

public class MyBasicDataSource extends DruidDataSource {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public MyBasicDataSource(){
		super();
		InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("jdbc.properties");
		Properties prop = new Properties();
		try
		{
			prop.load(in);
			in.close();
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String driverClassName = prop.getProperty("jdbc.driverClassName");
		String url = prop.getProperty("jdbc.url");
		String username = prop.getProperty("jdbc.username");
		String password = prop.getProperty("jdbc.password");
		username = EncryptAES.decrypt(username);
		password = EncryptAES.decrypt(password);
		 
		setDriverClassName(driverClassName);
		setUrl(url);
		setUsername(username);
		setPassword(password);
	}
}
