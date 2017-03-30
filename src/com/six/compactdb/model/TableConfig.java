package com.six.compactdb.model;

public class TableConfig {
    private String id = null;
    private String name = null;
    private String keyGenerate = null;
    private String sequence = null;

    public TableConfig(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public TableConfig(String id, String name, String keyGenerate, String sequence) {
        this.id = id;
        this.name = name;
        this.keyGenerate = keyGenerate;
        this.sequence = sequence;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyGenerate() {
        return this.keyGenerate;
    }

    public void setKeyGenerate(String keyGenerate) {
        this.keyGenerate = keyGenerate;
    }

    public String getSequence() {
        return this.sequence;
    }
}
