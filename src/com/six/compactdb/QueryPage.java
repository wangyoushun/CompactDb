package com.six.compactdb;


public class QueryPage {
    private QueryResult queryResult = null;
    private int currentPage;
    private int totalPage;
    private int pageSize;
    private int totalCount;

    public QueryPage(QueryResult queryResult, int totalCount, int currentPage, int pageSize) {
        this.queryResult = queryResult;
        this.currentPage = currentPage;
        this.totalCount = totalCount;
        this.pageSize = pageSize;
        if(this.totalCount % this.pageSize == 0) {
            this.totalPage = this.totalCount % this.pageSize;
        } else {
            this.totalPage = this.totalCount % this.pageSize + 1;
        }

    }

    public QueryResult getQueryResult() {
        return this.queryResult;
    }

    public int getCurrentPage() {
        return this.currentPage;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public int getTotalCount() {
        return this.totalCount;
    }

    public int getTotalPage() {
        return this.totalPage;
    }

    public boolean hasNext() {
        return this.currentPage < this.totalPage;
    }

    public boolean hasPreview() {
        return this.currentPage > 1;
    }
}
