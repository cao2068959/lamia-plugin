package org.chy.lamiaplugin.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.ide.IdeTooltip;
import com.intellij.ide.IdeTooltipManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.ui.panel.ComponentPanelBuilder;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.openapi.util.IconLoader;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;

import com.intellij.testFramework.MapDataContext;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBPanel;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTextArea;
import com.intellij.util.IconUtil;
import com.intellij.xdebugger.impl.ui.TextViewer;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;

public class LamiaLineMarkerInfo<T extends PsiElement> extends LineMarkerInfo<T> {


    static Icon LAMIA_ICON = IconLoader.getIcon("/images/img_1.png", LamiaLineMarkerInfo.class);


    public LamiaLineMarkerInfo(@NotNull T element, T lamiaMethod) {
        super(element, element.getTextRange(), LAMIA_ICON, (data) -> "Lamia转换语句",
                (event,psiElement)-> {
                    LamiaLineMarkerHandler handler = LamiaLineMarkerHandler.of(element.getProject());
                    handler.click(event, psiElement, lamiaMethod);
                },
                GutterIconRenderer.Alignment.CENTER, () -> "LamiaMarkerInfo");
    }


}
