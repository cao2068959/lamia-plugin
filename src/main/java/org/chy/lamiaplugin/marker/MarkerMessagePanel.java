package org.chy.lamiaplugin.marker;

import com.intellij.ide.highlighter.JavaFileType;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.Project;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.components.JBScrollPane;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;


import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;


public class MarkerMessagePanel extends JPanel {

    private final EditorTextField editorTextField;
    private final JPanel toolBar;

    private final JBScrollPane scrollPane;
    private final MarkerStatusButton markerStatusButton;


    public MarkerMessagePanel(Project project) {
        super(new BorderLayout());

        this.editorTextField = new EditorTextField(null, project, JavaFileType.INSTANCE, true);
        editorTextField.setOneLineMode(false);

        // 创建一个滚动面板并设置视口视图
        this.scrollPane = new JBScrollPane(editorTextField);
        // 创建一个工具栏
        this.toolBar = new JPanel(new FlowLayout(FlowLayout.LEFT));

        this.markerStatusButton = new MarkerStatusButton("conversion success", project);
        // 将按钮添加到工具栏
        toolBar.add(markerStatusButton.actionButton);

        // 将工具栏添加到面板的北部（顶部）
        this.add(toolBar, BorderLayout.NORTH);
        toolBar.setBorder(null);
        // 将滚动面板添加到面板的中心
        this.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setBorder(null);
    }


    public void fail(String msg, String data) {
        Document document = EditorFactory.getInstance().createDocument(data);
        editorTextField.setDocument(document);
        markerStatusButton.error(msg);
    }


    public void success(Document document) {
        editorTextField.setDocument(document);
        markerStatusButton.success();
    }

    public void unassociated(LamiaExpression lamiaExpression) {
        markerStatusButton.unassociated(lamiaExpression);
    }

}
