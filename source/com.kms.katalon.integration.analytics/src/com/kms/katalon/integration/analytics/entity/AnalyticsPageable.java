package com.kms.katalon.integration.analytics.entity;

public class AnalyticsPageable {
	private AnalyticsSort sort;
	private int pageNumber;
	private int pageSize;
	private int offset;
	private boolean paged;
	private boolean unpaged;
	public AnalyticsSort getSort() {
		return sort;
	}
	public void setSort(AnalyticsSort sort) {
		this.sort = sort;
	}
	public int getPageNumber() {
		return pageNumber;
	}
	public void setPageNumber(int pageNumber) {
		this.pageNumber = pageNumber;
	}
	public int getPageSize() {
		return pageSize;
	}
	public void setPageSize(int pageSize) {
		this.pageSize = pageSize;
	}
	public int getOffset() {
		return offset;
	}
	public void setOffset(int offset) {
		this.offset = offset;
	}
	public boolean isPaged() {
		return paged;
	}
	public void setPaged(boolean paged) {
		this.paged = paged;
	}
	public boolean isUnpaged() {
		return unpaged;
	}
	public void setUnpaged(boolean unpaged) {
		this.unpaged = unpaged;
	}
	public AnalyticsPageable(AnalyticsSort sort, int pageNumber, int pageSize, int offset,
			boolean paged, boolean unpaged) {
		super();
		this.sort = sort;
		this.pageNumber = pageNumber;
		this.pageSize = pageSize;
		this.offset = offset;
		this.paged = paged;
		this.unpaged = unpaged;
	}
	
}
