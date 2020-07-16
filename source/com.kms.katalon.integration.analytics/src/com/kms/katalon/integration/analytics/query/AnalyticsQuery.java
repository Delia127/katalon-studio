package com.kms.katalon.integration.analytics.query;

public class AnalyticsQuery {
	private String type;
    private AnalyticsQueryCondition[] conditions;
    private AnalyticsQueryFunction[] functions;
    private AnalyticsQueryPagination pagination;
    private String[] groupBys;
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
    public AnalyticsQueryCondition[] getConditions() {
        return conditions;
    }
    public void setConditions(AnalyticsQueryCondition[] conditions) {
        this.conditions = conditions;
    }
    public AnalyticsQueryFunction[] getFunctions() {
        return functions;
    }
    public void setFunctions(AnalyticsQueryFunction[] functions) {
        this.functions = functions;
    }
    public AnalyticsQueryPagination getPagination() {
        return pagination;
    }
    public void setPagination(AnalyticsQueryPagination pagination) {
        this.pagination = pagination;
    }
    public String[] getGroupBys() {
        return groupBys;
    }
    public void setGroupBys(String[] groupBys) {
        this.groupBys = groupBys;
    }
    public AnalyticsQuery() {
        groupBys = new String[] {};
        conditions = new AnalyticsQueryCondition[] {};
        pagination = new AnalyticsQueryPagination(0, 30, new String[] {});
    }
}
