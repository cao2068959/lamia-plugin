package org.chy.lamiaplugin;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.util.Function;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class ListenerTest implements ModuleListener {

    private final Project project;

    public ListenerTest(Project project) {
        this.project = project;
    }

    @Override
    public void moduleAdded(@NotNull Project project, @NotNull Module module) {
        ModuleListener.super.moduleAdded(project, module);
    }

    @Override
    public void beforeModuleRemoved(@NotNull Project project, @NotNull Module module) {
        ModuleListener.super.beforeModuleRemoved(project, module);
    }

    @Override
    public void moduleRemoved(@NotNull Project project, @NotNull Module module) {
        ModuleListener.super.moduleRemoved(project, module);
    }

    @Override
    public void modulesRenamed(@NotNull Project project, @NotNull List<? extends Module> modules, @NotNull Function<? super Module, String> oldNameProvider) {
        ModuleListener.super.modulesRenamed(project, modules, oldNameProvider);
    }
}
