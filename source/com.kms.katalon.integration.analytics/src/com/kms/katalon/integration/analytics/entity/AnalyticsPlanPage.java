package com.kms.katalon.integration.analytics.entity;

public class AnalyticsPlanPage {
    private AnalyticsPlan[] content;

    private AnalyticsPageable pageable;

    private int totalElements;

    private boolean last;

    private int totalPage;

    private boolean first;

    private AnalyticsSort sort;

    private int numberOfElements;

    private int size;

    private int number;

    private boolean empty;

    public AnalyticsPlan[] getContent() {
        return content;
    }

    public void setContent(AnalyticsPlan[] content) {
        this.content = content;
    }

    public AnalyticsPageable getPageable() {
        return pageable;
    }

    public void setPageable(AnalyticsPageable pageable) {
        this.pageable = pageable;
    }

    public int getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(int totalElements) {
        this.totalElements = totalElements;
    }

    public boolean isLast() {
        return last;
    }

    public void setLast(boolean last) {
        this.last = last;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(int totalPage) {
        this.totalPage = totalPage;
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

}
