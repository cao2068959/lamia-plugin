package org.chy.lamiaplugin.marker;

import com.chy.lamia.convert.core.entity.AbnormalVar;

import java.util.Map;
import java.util.Set;

public class LamiaCode {

    boolean success;
    String data;
    Set<String> importClassPath;

    Map<Integer, Set<AbnormalVar>> abnormalData;

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

    public Map<Integer, Set<AbnormalVar>> getAbnormalData() {
        return abnormalData;
    }

    public void setAbnormalData(Map<Integer, Set<AbnormalVar>> abnormalData) {
        this.abnormalData = abnormalData;
    }
}
