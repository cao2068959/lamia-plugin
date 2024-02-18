package org.chy.lamiaplugin.expression;

import java.util.Set;

public class ConvertResult {

    String data;

    Set<String> importClassPath;

    boolean success;
    String msg;

    public static ConvertResult success(String data) {
        ConvertResult result = new ConvertResult();
        result.setData(data);
        result.setSuccess(true);
        return result;
    }

    public static ConvertResult fail(String failMsg) {
        ConvertResult result = new ConvertResult();
        result.setSuccess(false);
        result.setMsg(failMsg);
        return result;
    }


    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Set<String> getImportClassPath() {
        return importClassPath;
    }

    public void setImportClassPath(Set<String> importClassPath) {
        this.importClassPath = importClassPath;
    }
}
