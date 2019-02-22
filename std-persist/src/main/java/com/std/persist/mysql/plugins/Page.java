package com.std.persist.mysql.plugins;

import java.util.List;

/**
 * 对分页的基本数据进行一个简单的封装 
 * @author liuwei3
 * @param <T>
 */
public class Page<T> {
	private PageInfo pageInfo;
	private List<T> results;//对应的当前页记录
	public PageInfo getPageInfo() {
		return pageInfo;
	}
	public void setPageInfo(PageInfo pageInfo) {
		this.pageInfo = pageInfo;
	}
	public List<T> getResults() {
		return results;
	}
	public void setResults(List<T> results) {
		this.results = results;
	}
	
}
