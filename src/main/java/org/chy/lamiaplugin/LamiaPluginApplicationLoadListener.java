package org.chy.lamiaplugin;

import com.chy.lamia.expose.Lamia;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiManager;
import org.chy.lamiaplugin.components.executor.BuildRefreshExecutor;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class LamiaPluginApplicationLoadListener implements ApplicationLoadListener {

    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull Path configPath) {

        ScheduledBatchExecutor.instance = new ScheduledBatchExecutor(6000);
        ScheduledBatchExecutor.instance.registerBatchExecutor(new BuildRefreshExecutor());
        System.out.println("------> 初始化");
    }
}
