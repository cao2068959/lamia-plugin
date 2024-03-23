package org.chy.lamiaplugin.marker;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class ErrorTypeConvertButton extends AnAction {

    private final MarkerMessagePanel parentPanel;
    private Presentation presentation;

    ActionButton actionButton;

    int width = 20;
    int height = 20;

    public ErrorTypeConvertButton(MarkerMessagePanel parentPanel) {
        this.presentation = Presentation.newTemplatePresentation();
        presentation.setIcon(AllIcons.Actions.AddList);
        presentation.setText("The converted field types do not match. Click to generate the corresponding conversion statement");
        this.actionButton = new ActionButton(this, presentation
                , "MarkerMessageGutter", new Dimension(width, height));
        this.parentPanel = parentPanel;
    }

    public void setBound(int x, int y) {
        actionButton.setBounds(x, y, width, height);
    }


    @Override
    public void actionPerformed(@NotNull AnActionEvent anActionEvent) {
        parentPanel.closeBalloon();
    }

}
