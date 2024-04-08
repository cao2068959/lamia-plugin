package org.chy.lamiaplugin.marker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.task.UpdateExpRelationTask;
import org.chy.lamiaplugin.utlis.IconConstant;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class UpdateExpRelationButton extends AnAction {

    private final Project project;
    private Presentation presentation;

    ActionButton actionButton;


    public UpdateExpRelationButton(Project project) {
        this.presentation = new Presentation();
        presentation.setIcon(IconConstant.REFRESH_ICON);
        presentation.setText("Click to reassociate all lamia expressions");
        this.actionButton = new ActionButton(this, presentation
                , "MarkerMessagePanel", new Dimension(20, 20));
        this.project = project;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        ProgressManager.getInstance().run(new UpdateExpRelationTask(project, true));
    }

}
