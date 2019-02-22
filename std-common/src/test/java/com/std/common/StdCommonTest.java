package com.std.common;

import static org.junit.Assert.*;

import java.util.Random;

import org.apache.commons.lang3.RandomUtils;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.std.common.utils.StdCommonRedis;

public class StdCommonTest {
	private final static Logger logger = LoggerFactory.getLogger(StdCommonTest.class);
	
	@Test
	public void test() {
		StdCommonRedis stdCommonRedis = StdCommonRedis.getSingletonInstance();
		String s =stdCommonRedis.get("test");
		System.out.println(s);
	}

	public static void main(String[] args) {
//		StdCommonRedis stdCommonRedis = StdCommonRedis.getSingletonInstance();
//		long t = stdCommonRedis.ttl("a6267f5972e4440a8a2dc6f3680dfe1d");
//		//String s =stdCommonRedis.get("00f4717756eb41988e54861805a82994");
//		logger.info("{}###{}",t);
//		//stdCommonRedis.del("89586699ee7d404d9b2877ff106ee4b6");
//		//logger.info("{}",stdCommonRedis.keys("*"));
	}
}
