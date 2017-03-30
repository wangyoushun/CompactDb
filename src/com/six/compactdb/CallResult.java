package com.six.compactdb;

public class CallResult {
    private QueryResult resultList = null;
    private DataValue outputs = null;

    public CallResult(QueryResult resultList, DataValue outputs) {
        this.resultList = resultList;
        this.outputs = outputs;
    }

    public QueryResult getResultList() {
        return this.resultList;
    }

    public DataValue getOutputs() {
        return this.outputs;
    }
}
