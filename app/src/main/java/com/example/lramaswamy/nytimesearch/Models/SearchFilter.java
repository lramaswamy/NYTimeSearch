package com.example.lramaswamy.nytimesearch.Models;

/**
 * Created by lramaswamy on 10/18/16.
 */

public class SearchFilter {

    String beginDate;
    String sortOrder;
    boolean isArts;
    boolean isFasStyle;
    boolean isSports;

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(String sortOrder) {
        this.sortOrder = sortOrder;
    }

    public boolean isArts() {
        return isArts;
    }

    public void setArts(boolean arts) {
        isArts = arts;
    }

    public boolean isFasStyle() {
        return isFasStyle;
    }

    public void setFasStyle(boolean fasStyle) {
        isFasStyle = fasStyle;
    }

    public boolean isSports() {
        return isSports;
    }

    public void setSports(boolean sports) {
        isSports = sports;
    }

}
