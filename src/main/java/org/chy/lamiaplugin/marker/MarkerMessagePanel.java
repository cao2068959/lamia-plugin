package org.chy.lamiaplugin.marker;

import com.intellij.icons.AllIcons;
import com.intellij.ide.highlighter.JavaFileType;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.*;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.HighlighterLayer;
import com.intellij.openapi.editor.markup.HighlighterTargetArea;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.PlainTextFileType;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.util.TextRange;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBLabel;
import com.intellij.ui.components.JBScrollPane;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.jetbrains.annotations.NotNull;


import javax.swing.*;
import javax.swing.text.PlainDocument;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;


public class MarkerMessagePanel extends JPanel {

    private final EditorTextField editorTextField;
    private final JPanel toolBar;
    private final MarkerMessageGutter gutter;

    private final JBScrollPane scrollPane;
    private final MarkerStatusButton markerStatusButton;
    private final Project project;
    private Balloon balloon;


    public MarkerMessagePanel(Project project) {
        super(new BorderLayout());
        this.project = project;

        this.editorTextField = new EditorTextField(null, project, PlainTextFileType.INSTANCE, true);
        editorTextField.setOneLineMode(false);
        editorTextField.setDisposedWith(() -> {
            System.out.println("dispose");
        });
        // 创建一个滚动面板并设置视口视图
        this.scrollPane = new JBScrollPane(editorTextField);
        // 创建一个工具栏
        this.toolBar = new JPanel(new BorderLayout());

        this.markerStatusButton = new MarkerStatusButton("conversion success", project);
        UpdateExpRelationButton updateExpRelationButton = new UpdateExpRelationButton(project);
        // 将按钮添加到工具栏
        toolBar.add(markerStatusButton.actionButton, BorderLayout.WEST);
        toolBar.add(updateExpRelationButton.actionButton, BorderLayout.EAST);

        // 将工具栏添加到面板的北部（顶部）
        this.add(toolBar, BorderLayout.NORTH);
        toolBar.setBorder(null);
        // 将滚动面板添加到面板的中心
        this.add(scrollPane, BorderLayout.CENTER);
        scrollPane.setBorder(null);

        // 创建一个标记消息栏
        gutter = new MarkerMessageGutter(this, project);
        this.add(gutter, BorderLayout.WEST);
    }


    public void fail(String msg, String data) {
        editorTextField.setFileType(PlainTextFileType.INSTANCE);
        editorTextField.setText(data);
        markerStatusButton.error(msg);
    }


    public void success(Document document) {
        editorTextField.setFileType(JavaFileType.INSTANCE);

        editorTextField.setDocument(document);
        EditorEx editor = editorTextField.getEditor(true);
        gutter.clear();
        int lineHeight = editor.getLineHeight();
        gutter.setHeight((document.getLineCount() + 1) * 20);
        for (int i = 0; i < document.getLineCount(); i++) {
            markErrorLine(i, document, editor);
        }
        markerStatusButton.success();
    }

    private void markErrorLine(int lineNumber, Document document, EditorEx editor) {
        if (editor == null) {
            return;
        }
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        VisualPosition visualPosition = editor.offsetToVisualPosition(lineStartOffset);
        Point point = editor.visualPositionToXY(visualPosition);
        gutter.addButton(lineNumber, point);
    }


    public void highlightLine(int lineNumber, EditorEx editor) {
        Document document = editor.getDocument();
        int lineStartOffset = document.getLineStartOffset(lineNumber);
        int lineEndOffset = document.getLineEndOffset(lineNumber);

        // 获取行的文本
        String lineText = document.getText(new TextRange(lineStartOffset, lineEndOffset));

        // 计算文本的开始位置
        int textStartOffset = lineStartOffset + lineText.indexOf(lineText.trim());

        TextAttributes attributes = new TextAttributes();
        attributes.setEffectColor(Color.RED);
        attributes.setEffectType(EffectType.WAVE_UNDERSCORE);

        editor.getMarkupModel().addRangeHighlighter(
                textStartOffset,
                lineEndOffset,
                HighlighterLayer.ADDITIONAL_SYNTAX,
                attributes,
                HighlighterTargetArea.EXACT_RANGE
        );
    }

    public void unassociated(LamiaExpression lamiaExpression) {
        markerStatusButton.unassociated(lamiaExpression);
    }


    public void closeBalloon() {
        if (balloon == null || balloon.isDisposed()) {
            return;
        }
        balloon.hide();
    }

    public Balloon getBalloon() {
        return balloon;
    }

    public void setBalloon(Balloon balloon) {
        this.balloon = balloon;
    }
}
