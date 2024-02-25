package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiJavaFile;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;
import org.chy.lamiaplugin.task.UpdateExpRelationTask;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;

public class UpdateExpRelationExecutor implements BatchExecutor<UpdateExpRelationEvent> {

    private static final Logger LOG = Logger.getInstance(UpdateExpRelationExecutor.class);

    @Override
    public String group() {
        return "UpdateExpRelationExecutor";
    }

    @Override
    public void batchRun(List<UpdateExpRelationEvent> events) {
        buildRefresh(events);
    }


    public void buildRefresh(List<UpdateExpRelationEvent> events) {
        Set<Project> projects = new HashSet<>();
        events.forEach(event -> projects.add(event.project));
        projects.forEach(project -> ProgressManager.getInstance().run(new UpdateExpRelationTask(project, false)));
    }


}
