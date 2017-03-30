package com.six.compactdb.model;

public class SQLParameter {
    public static final String IN = "IN";
    public static final String OUT = "OUT";
    private String name = null;
    private int dataType = 12;
    private String type = null;

    public SQLParameter(String name, String dataType, String type) {
        this.name = name;
        if(dataType != null) {
            if(dataType.toLowerCase().equals("date")) {
                this.dataType = 91;
            } else if(dataType.toLowerCase().equals("int")) {
                this.dataType = 4;
            } else if(dataType.toLowerCase().equals("long")) {
                this.dataType = -5;
            } else if(dataType.toLowerCase().equals("decimal")) {
                this.dataType = 3;
            } else if(dataType.toLowerCase().equals("binary")) {
                this.dataType = -2;
            } else if(dataType.toLowerCase().equals("cursor")) {
                this.dataType = -10;
            }
        }

        if(type == null) {
            this.type = "IN";
        } else {
            this.type = type.toUpperCase();
        }

    }

    public String getName() {
        return this.name;
    }

    public int getDataType() {
        return this.dataType;
    }

    public String getType() {
        return this.type;
    }
}
