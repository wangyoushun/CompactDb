package com.six.compactdb;

import java.io.File;

class Resource {
    private String name = null;
    private String classPath = null;
    private File file = null;
    private long lastTime;

    public Resource(String name, String classPath, File file, long lastTime) {
        this.name = name;
        this.classPath = classPath;
        this.file = file;
        this.lastTime = lastTime;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getClassPath() {
        return this.classPath;
    }

    public void setClassPath(String classPath) {
        this.classPath = classPath;
    }

    public File getFile() {
        return this.file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public long getLastTime() {
        return this.lastTime;
    }

    public void setLastTime(long lastTime) {
        this.lastTime = lastTime;
    }

    public void updateTime() {
        this.lastTime = this.file.lastModified();
    }

    public boolean isModified() {
        return this.file.lastModified() != this.lastTime;
    }
}
