package com.six.compactdb;


import java.util.HashMap;
import java.util.Map;

import com.six.compactdb.exception.CompactdbException;
import com.six.compactdb.model.SQLScript;

public class SQLScriptMapping {
    private Map<String, SQLScript> scripts = new HashMap<String, SQLScript>();

    public SQLScriptMapping() {
    }

    public SQLScript getSQLScript(String id) {
        if(this.scripts.containsKey(id)) {
            return (SQLScript)this.scripts.get(id);
        } else {
            throw new CompactdbException("未定义的脚本ID\"" + id + "\"");
        }
    }

    public void addSQLScript(String id, SQLScript script) {
        this.scripts.put(id, script);
    }
}
