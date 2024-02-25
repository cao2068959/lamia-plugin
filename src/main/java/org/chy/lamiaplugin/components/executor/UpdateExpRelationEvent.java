package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.project.Project;

public class UpdateExpRelationEvent extends Event {

    Project project;

    public UpdateExpRelationEvent(Project project) {
        super("UpdateExpRelationExecutor");
        this.project = project;
    }
}
