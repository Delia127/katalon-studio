package com.kms.katalon.integration.analytics.entity;

import java.util.List;

public class AnalyticsTeamPage {

    private List<AnalyticsTeam> teams;
    
    private Long totalElements;
    
    private Long totalPages;
    
    private Boolean last;
    
    private Boolean first;
    
    private Long size;
    
    private Long number;
    
    private Long numberOfElements;

    public List<AnalyticsTeam> getTeams() {
        return teams;
    }

    public void setTeams(List<AnalyticsTeam> teams) {
        this.teams = teams;
    }

    public Long getTotalElements() {
        return totalElements;
    }

    public void setTotalElements(Long totalElements) {
        this.totalElements = totalElements;
    }

    public Long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(Long totalPages) {
        this.totalPages = totalPages;
    }

    public Boolean getLast() {
        return last;
    }

    public void setLast(Boolean last) {
        this.last = last;
    }

    public Boolean getFirst() {
        return first;
    }

    public void setFirst(Boolean first) {
        this.first = first;
    }

    public Long getSize() {
        return size;
    }

    public void setSize(Long size) {
        this.size = size;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    public Long getNumberOfElements() {
        return numberOfElements;
    }

    public void setNumberOfElements(Long numberOfElements) {
        this.numberOfElements = numberOfElements;
    }
    
}
