package org.chy.lamiaplugin.expression;

import com.intellij.openapi.project.Project;

public class LamiaExpressionManager {

    Project project;

    public LamiaExpressionManager(Project project) {
        System.out.println("-------------------> LamiaExpressionManager");
        this.project = project;
    }

}
