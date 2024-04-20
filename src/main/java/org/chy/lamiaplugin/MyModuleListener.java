package org.chy.lamiaplugin;


import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;

import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.components.executor.UpdateExpRelationEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MyModuleListener implements ModuleListener {


    @Override
    public void modulesAdded(@NotNull Project project, @NotNull List<? extends Module> modules) {

        if (hasJavaModule(modules)) {
            DumbService.getInstance(project).smartInvokeLater(() -> {
                ScheduledBatchExecutor.instance.deliverEvent(new UpdateExpRelationEvent(project));
            });
        }
    }

    private boolean hasJavaModule(List<? extends Module> modules) {
        for (Module module : modules) {
            if (ModuleType.get(module).equals(ModuleTypeManager.getInstance().findByID("JAVA_MODULE"))) {
                return true;
            }
        }
        return false;
    }
}