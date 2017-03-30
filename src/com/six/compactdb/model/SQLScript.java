package com.six.compactdb.model;

import java.util.List;

public class SQLScript {
    private String sql = null;
    private List<SQLParameter> parameters = null;
    private boolean query = true;

    public SQLScript(String sql, List<SQLParameter> parameters, String isQuery) {
        this.sql = sql;
        this.parameters = parameters;
        if(isQuery == null) {
            this.query = true;
        } else {
            this.query = Boolean.valueOf(isQuery).booleanValue();
        }

    }

    public String getSql() {
        return this.sql;
    }

    public List<SQLParameter> getParameters() {
        return this.parameters;
    }

    public boolean isQuery() {
        return this.query;
    }
}
