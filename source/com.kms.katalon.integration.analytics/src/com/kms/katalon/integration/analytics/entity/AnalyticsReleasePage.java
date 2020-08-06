package com.kms.katalon.integration.analytics.entity;

public class AnalyticsReleasePage {
	private AnalyticsRelease[] content;
	private AnalyticsPageable pageable;
	private int totalPages;
	private boolean last;
	private long totalElement;
	private boolean first;
	private AnalyticsSort sort;
	private int numberOfElements;
	private int size;
	private int number;
	private boolean empty;
	public AnalyticsRelease[] getContent() {
		return content;
	}
	public void setContent(AnalyticsRelease[] content) {
		this.content = content;
	}
	public AnalyticsPageable getPageable() {
		return pageable;
	}
	public void setPageable(AnalyticsPageable pageable) {
		this.pageable = pageable;
	}
	public int getTotalPages() {
		return totalPages;
	}
	public void setTotalPages(int totalPages) {
		this.totalPages = totalPages;
	}
	public boolean isLast() {
		return last;
	}
	public void setLast(boolean last) {
		this.last = last;
	}
	public long getTotalElement() {
		return totalElement;
	}
	public void setTotalElement(long totalElement) {
		this.totalElement = totalElement;
	}
	public boolean isFirst() {
		return first;
	}
	public void setFirst(boolean first) {
		this.first = first;
	}
	public AnalyticsSort getSort() {
		return sort;
	}
	public void setSort(AnalyticsSort sort) {
		this.sort = sort;
	}
	public int getNumberOfElements() {
		return numberOfElements;
	}
	public void setNumberOfElements(int numberOfElements) {
		this.numberOfElements = numberOfElements;
	}
	public int getSize() {
		return size;
	}
	public void setSize(int size) {
		this.size = size;
	}
	public int getNumber() {
		return number;
	}
	public void setNumber(int number) {
		this.number = number;
	}
	public boolean isEmpty() {
		return empty;
	}
	public void setEmpty(boolean empty) {
		this.empty = empty;
	}
	public AnalyticsReleasePage(AnalyticsRelease[] content, AnalyticsPageable pageable, int totalPages,
			boolean last, long totalElement, boolean first, AnalyticsSort sort, int numberOfElements, int size,
			int number, boolean empty) {
		super();
		this.content = content;
		this.pageable = pageable;
		this.totalPages = totalPages;
		this.last = last;
		this.totalElement = totalElement;
		this.first = first;
		this.sort = sort;
		this.numberOfElements = numberOfElements;
		this.size = size;
		this.number = number;
		this.empty = empty;
	}

}
