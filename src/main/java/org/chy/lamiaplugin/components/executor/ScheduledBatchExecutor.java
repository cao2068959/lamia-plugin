package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.diagnostic.Logger;
import groovy.util.logging.Slf4j;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

@Slf4j
public class ScheduledBatchExecutor implements Runnable {

    public static ScheduledBatchExecutor instance;

    private static final Logger LOG = Logger.getInstance(ScheduledBatchExecutor.class);

    private final ArrayBlockingQueue<Event> queue = new ArrayBlockingQueue<>(1024);

    private final Map<String, BatchExecutor<?>> executorMap = new HashMap<>();

    long waitTime;

    private Thread thread;

    public ScheduledBatchExecutor(long waitTime) {
        this.waitTime = waitTime;
        init();
    }

    public void init() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    public void registerBatchExecutor(BatchExecutor<?> batchExecutor) {
        executorMap.put(batchExecutor.group(), batchExecutor);
    }

    /**
     * 投递一个事件
     *
     * @param event
     */
    public void deliverEvent(Event event) {
        queue.offer(event);
    }

    @Override
    public void run() {
        while (true) {
            try {
                doRun();
            } catch (Exception e) {
                LOG.error("ScheduledBatchExecutor 通知执行失败", e);
            }
        }
    }


    private void doRun() throws InterruptedException {
        Map<BatchExecutor, List<Event>> batchExecutorListMap = readData();
        batchExecutorListMap.forEach(BatchExecutor::batchRun);
    }


    /**
     * 当管道里开始有数据的时候，延迟指定时间，获取出一批数据
     *
     * @return
     */
    private Map<BatchExecutor, List<Event>> readData() throws InterruptedException {
        // 从队列中获取数据, 获取不到就等待
        Event data = queue.take();
        // 获取到了第一条，那么可能后面会连续来很多，先等一段时间
        Thread.sleep(waitTime);
        Map<BatchExecutor, List<Event>> result = new HashMap<>();
        List<Event> reEvent = new ArrayList<>();
        while (true) {
            BatchExecutor<?> batchExecutor = executorMap.get(data.group);
            if (batchExecutor == null) {
                throw new RuntimeException("无效的event:[" + data.group + "] 无法找到 BatchExecutor");
            }

            int minLatency = batchExecutor.minLatency();
            // 这个执行器要执行的任务 有一个最低间隔时间, 如果没到这个时间间隔，这个事件就不会触发，会等到一次 执行的时候触发
            if (minLatency > 0 && minLatency >= System.currentTimeMillis() - data.startTime) {
                reEvent.add(data);
            } else {
                result.computeIfAbsent(batchExecutor, __ -> new ArrayList<>()).add(data);
            }

            // 继续管道中读取数据
            data = queue.poll();
            if (data == null) {
                break;
            }
        }
        // 把还没执行时机的任务给投进去
        reEvent.forEach(this::deliverEvent);
        return result;
    }

}