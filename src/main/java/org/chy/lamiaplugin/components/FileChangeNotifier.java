package org.chy.lamiaplugin.components;

import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import groovy.util.logging.Slf4j;

import javax.annotation.PostConstruct;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Slf4j
public class FileChangeNotifier implements Runnable {

    public static FileChangeNotifier instance = new FileChangeNotifier();

    private static final Logger LOG = Logger.getInstance(FileChangeNotifier.class);

    private final ArrayBlockingQueue<PsiFile> queue = new ArrayBlockingQueue<>(512);

    private Thread thread;

    public FileChangeNotifier() {
        init();
    }

    public void init() {
        this.thread = new Thread(this);
        this.thread.start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                doRun();
            } catch (Exception e) {
                LOG.error("FileChangeNotifier 通知执行失败", e);
            }
        }
    }


    private void doRun() throws InterruptedException {
        Map<String, File> handlerData = new HashMap<>();
        readData(5000, psiFile -> {
            VirtualFile virtualFile = psiFile.getVirtualFile();
            if (virtualFile == null) {
                return;
            }
            String path = virtualFile.getCanonicalPath();
            if (path == null || handlerData.containsKey(path)) {
                return;
            }
            handlerData.put(path, new File(path));
        });

        List<File> files = handlerData.values().stream().toList();
        BuildManager.getInstance().notifyFilesChanged(files);
    }


    /**
     * 当管道里开始有数据的时候，延迟指定时间，获取出一批数据
     *
     * @param waitTime 当开始有数据的时候，延迟等待的时间
     * @return
     */
    private void readData(long waitTime, Consumer<PsiFile> dataHandler) throws InterruptedException {
        // 从队列中获取数据, 获取不到就等待
        PsiFile data = queue.take();
        // 获取到了第一条，那么可能后面会连续来很多，先等一段时间
        Thread.sleep(waitTime);
        dataHandler.accept(data);
        while (true) {
            PsiFile item = queue.poll();
            if (item == null) {
                return;
            }
            dataHandler.accept(item);
        }

    }

}