package com.std.persist.mysql.plugins;

public class PageInfo {
	private int pageNo = 1;//页码，默认是第一页  
	private int pageSize = 10;//每页显示的记录数，默认是10
	private int totalRecord;//总记录数
	private int totalPage;//总页数 
	public int getPageNo() {
		return pageNo;
	}
	public void setPageNo(int pageNo) {
		this.pageNo = pageNo;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getTotalRecord() {
		return totalRecord;
	}
	public void setTotalRecord(int totalRecord) {
		this.totalRecord = totalRecord;
		// 在设置总记录数的时候计算出对应的总页数。
		int totalPage = totalRecord % pageSize == 0 ? totalRecord / pageSize : totalRecord / pageSize + 1;
		this.setTotalPage(totalPage);
	}
	public int getTotalPage() {
		return totalPage;
	}
	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}
	@Override
	public String toString() {
		return "Page [pageNo=" + pageNo + ", pageSize=" + pageSize
				+ ", totalRecord=" + totalRecord + ", totalPage=" + totalPage
				+ "]";
	}
}
