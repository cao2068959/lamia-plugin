package org.chy.lamiaplugin.components.executor;

public abstract class Event {

    String group;
    long startTime;

    public Event(String group) {
        this.group = group;
        this.startTime = System.currentTimeMillis();
    }

}
