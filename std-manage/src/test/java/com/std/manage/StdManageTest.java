package com.std.manage;

import static org.junit.Assert.*;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.shiro.codec.Base64;
import org.junit.Test;

public class StdManageTest {

	@Test
	public void test(){
		System.out.println(Base64.encodeToString("sitie~^08*4$%`16nd(_det++".getBytes()));
		System.out.println(new String(Base64.decode("c2l0aWV+XjA4KjQkJWAxNm5kKF9kZXQrKw==")));
		System.out.println(SystemUtils.getHostName());
		System.out.println(SystemUtils.IS_JAVA_1_8);
		System.out.println(SystemUtils.IS_JAVA_1_6);
		System.out.println(SystemUtils.FILE_ENCODING);
		System.out.println(SystemUtils.IS_OS_WINDOWS);
		System.out.println(SystemUtils.getJavaHome());
		System.out.println(RandomStringUtils.randomAlphanumeric(100));
	}

}
