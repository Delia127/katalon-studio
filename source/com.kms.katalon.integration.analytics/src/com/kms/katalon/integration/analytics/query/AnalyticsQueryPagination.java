package com.kms.katalon.integration.analytics.query;

public class AnalyticsQueryPagination {
	private int page;
    private int size;
    private String[] sorts;
    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = page;
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size;
    }
    public String[] getSorts() {
        return sorts;
    }
    public void setSorts(String[] sorts) {
        this.sorts = sorts;
    }
    public AnalyticsQueryPagination(int page, int size, String[] sorts) {
        super();
        this.page = page;
        this.size = size;
        this.sorts = sorts;
    }
}
