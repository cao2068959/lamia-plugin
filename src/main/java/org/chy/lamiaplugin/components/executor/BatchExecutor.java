package org.chy.lamiaplugin.components.executor;

import java.util.List;

public interface BatchExecutor<T extends Event> {

    /**
     * 任务的组 id
     */
    public String group();

    /**
     * 这个任务最少的延迟是多少， 单位 ms, 如果是 -1那么就不延迟
     *
     * @return
     */
    default public int minLatency() {
        return -1;
    }

    /**
     * 任务批量运行的入口
     *
     * @param events 所有的事件
     */
    public void batchRun(List<T> events);


}
