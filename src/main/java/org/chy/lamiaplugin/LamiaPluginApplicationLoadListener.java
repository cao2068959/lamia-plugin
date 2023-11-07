package org.chy.lamiaplugin;

import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class LamiaPluginApplicationLoadListener implements ApplicationLoadListener {

    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull Path configPath) {

        System.out.println("------> 初始化");
    }
}
