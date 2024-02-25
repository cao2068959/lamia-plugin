package org.chy.lamiaplugin;

import com.chy.lamia.expose.Lamia;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.ModuleListener;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.components.executor.UpdateExpRelationEvent;

public class MyModuleListener implements ModuleListener {

    @Override
    public void moduleAdded(Project project, Module module) {
        // 如果是 java 模块打开，那么就触发更新
        if (ModuleType.get(module).equals(ModuleTypeManager.getInstance().findByID("JAVA_MODULE"))) {
            DumbService.getInstance(project).smartInvokeLater(() -> {
                ScheduledBatchExecutor.instance.deliverEvent(new UpdateExpRelationEvent(project));
            });
        }
    }
}