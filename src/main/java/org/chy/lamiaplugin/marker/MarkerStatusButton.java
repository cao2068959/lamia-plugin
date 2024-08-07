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

public class MarkerStatusButton extends AnAction {


    private final Project project;
    private Presentation presentation;
    String defaultText;

    ActionButton actionButton;

    LamiaExpression unassociatedPsiElement;

    public MarkerStatusButton(String defaultText, Project project) {
        this.defaultText = defaultText;
        this.presentation = new Presentation();
        setSuccessIcons();
        presentation.setText(defaultText);
        this.actionButton = new ActionButton(this, presentation
                , "MarkerMessagePanel", new Dimension(20, 20));
        this.project = project;
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        if (unassociatedPsiElement == null) {
            return;
        }
        LamiaExpressionManager manager = LamiaExpressionManager.getInstance(project);
        manager.updateDependentRelations(unassociatedPsiElement.getExpression());
        success();
    }

    public void error(String msg) {
        init();
        setFailIcons();
        presentation.setText("error：" + msg);
    }

    public void init() {
        unassociatedPsiElement = null;
    }

    private void setSuccessIcons() {
        presentation.setIcon(IconConstant.TICK_ICON);
    }

    private void setFailIcons() {
        presentation.setIcon(IconConstant.X_ICON);
    }

    public void success() {
        init();
        setSuccessIcons();
        presentation.setText(defaultText);
    }


    public void unassociated(LamiaExpression lamiaExpression) {
        init();
        this.unassociatedPsiElement = lamiaExpression;
        presentation.setIcon(IconConstant.WARNING_ICON);
        presentation.setText("The expression is not associated, \n" +
                "which may result in failure to jump and incremental compilation. \n" +
                "\n" +
                "If there is no automatic association for a long time, you can click to associate.");
    }

}
