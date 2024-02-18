package org.chy.lamiaplugin.marker;

import java.util.Set;

public class LamiaCode {

    boolean success;
    String data;
    Set<String> importClassPath;

    public LamiaCode(String data, boolean success) {
        this.data = data;
        this.success = success;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public Set<String> getImportClassPath() {
        return importClassPath;
    }

    public void setImportClassPath(Set<String> importClassPath) {
        this.importClassPath = importClassPath;
    }
}
