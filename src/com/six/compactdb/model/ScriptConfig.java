package com.six.compactdb.model;

public class ScriptConfig {
    private String classPath = null;

    public ScriptConfig(String classPath) {
        this.classPath = classPath;
    }

    public String getClassPath() {
        return this.classPath;
    }
}
