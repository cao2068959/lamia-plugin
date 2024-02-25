package org.chy.lamiaplugin.utlis;

public class Wrapper<T> {
    T data;

    public Wrapper(T data) {
        this.data = data;
    }

    public Wrapper() {
    }

    public static <T> Wrapper<T> of(T data) {
        return new Wrapper<>(data);
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
