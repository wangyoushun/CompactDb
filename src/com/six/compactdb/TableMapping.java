package com.six.compactdb;

import java.util.HashMap;
import java.util.Map;

import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.Table;

public class TableMapping {
    private Map<String, Table> tables = new HashMap<String, Table>();

    public TableMapping() {
    }

    public Table getTable(String id) throws CompactdbException {
        if(this.tables.containsKey(id)) {
            return (Table)this.tables.get(id);
        } else {
            throw new CompactdbException("未定义的表ID\"" + id + "\"");
        }
    }

    public void addTable(String id, Table table) {
        this.tables.put(id, table);
    }
}
