package org.chy.lamiaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LamiaProjectManagerListener implements ProjectManagerListener {

    private final Project project;

    public LamiaProjectManagerListener(Project project) {
        this.project = project;
    }

    @Override
    public void projectOpened(@NotNull Project project) {
        System.out.println("----> 项目打开");
        ProjectManagerListener.super.projectOpened(project);
    }
}
