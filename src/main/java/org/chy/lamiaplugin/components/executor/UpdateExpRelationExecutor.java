package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;

import org.chy.lamiaplugin.task.UpdateExpRelationTask;

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
