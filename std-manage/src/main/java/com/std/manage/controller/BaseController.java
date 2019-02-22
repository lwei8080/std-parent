package com.std.manage.controller;

import org.springframework.beans.factory.annotation.Value;

public abstract class BaseController {
	@Value("${academic.manage.page.size.default}")
	private int DEFAULT_PAGE_SIZE;
	private final static int DEFAULT_PAGE_NO = 1;

	public static int getDefaultPageNo() {
		return DEFAULT_PAGE_NO;
	}
	public int getDefaultPageSize() {
		return DEFAULT_PAGE_SIZE;
	}
}
