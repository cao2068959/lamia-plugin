package org.chy.lamiaplugin.marker;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MarkerMessageGutter extends JPanel {

    private final Project project;

    Map<Integer, AnAction> allButton = new HashMap<>();

    MarkerMessagePanel parentPanel;

    int width = 30;

    public MarkerMessageGutter(MarkerMessagePanel parentPanel, Project project) {
        super(null);
        setPreferredSize(new Dimension(width, getPreferredSize().height));
        this.project = project;
        this.parentPanel = parentPanel;
    }

    public void setHeight(int height) {
        setPreferredSize(new Dimension(width, height));
    }


    public synchronized void clear() {
        // 清除所有的标志
        this.removeAll();
        allButton.clear();
    }

    public synchronized void addButton(int lineNumber, Point point) {
        ErrorTypeConvertButton button = new ErrorTypeConvertButton(parentPanel);
        button.setBound(5, point.y);
        allButton.put(lineNumber, button);
        add(button.actionButton);
    }
}
