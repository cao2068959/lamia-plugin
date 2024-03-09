package org.chy.lamiaplugin.marker;

import com.intellij.openapi.project.Project;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

public class MarkerMessageGutter extends JPanel {

    private final Project project;

    Map<Integer, JLabel> lineNumbers = new HashMap<>();

    int width = 30;

    public MarkerMessageGutter(Project project) {
        super(null);
        setPreferredSize(new Dimension(width, getPreferredSize().height));
        setBackground(Color.RED);
        this.project = project;
    }

    public void setHeight(int height) {
        setPreferredSize(new Dimension(width, height));
    }

    public synchronized void clearLineNumber() {
        lineNumbers = new HashMap<>();
    }

    public synchronized void setLineNumber(int lineNumber, Point point) {
        JLabel label = new JLabel(lineNumber + "");
        label.setBounds(5, point.y + 5, 20, 10);
        lineNumbers.put(lineNumber, label);
        add(label);
    }
}
