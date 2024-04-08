package org.chy.lamiaplugin.marker.gutter;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.actionSystem.impl.ActionButton;
import org.chy.lamiaplugin.marker.MarkerMessagePanel;

import javax.swing.*;
import java.awt.*;

public abstract class GutterButton extends AnAction {

    protected MarkerMessagePanel parentPanel;
    private Presentation presentation;

    ActionButton actionButton;

    int width = 20;
    int height = 20;

    public GutterButton(String text, Icon icon) {
        presentation = new Presentation();
        presentation.setIcon(icon);
        presentation.setText(text);
        actionButton = new ActionButton(this, presentation, "MarkerMessageGutter", new Dimension(width, height));
    }

    public void setParentPanel(MarkerMessagePanel parentPanel) {
        this.parentPanel = parentPanel;
    }

    public void setBound(int x, int y) {
        actionButton.setBounds(x, y, width, height);
    }

    public void closeBalloon() {
        parentPanel.closeBalloon();
    }

}
