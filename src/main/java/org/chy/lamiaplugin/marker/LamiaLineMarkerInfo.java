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

    static Icon LAMIA_ICON = IconLoader.getIcon("/images/img_2.png", LamiaLineMarkerInfo.class);


    public LamiaLineMarkerInfo(@NotNull T element) {
        super(element, element.getTextRange(), LAMIA_ICON, (data) -> "Lamia转换语句", LamiaLineMarkerInfo::click,
                GutterIconRenderer.Alignment.CENTER, () -> "LamiaMarkerInfo");
    }

    private static void click(MouseEvent event, PsiElement psiElement) {
        event.getComponent();
        showTip("出现----->", event, psiElement);
        System.out.println("----> 点击");
    }

    private static void showTip(String msg, MouseEvent event, PsiElement psiElement) {
        PsiFile containingFile = psiElement.getContainingFile();
        Project project = psiElement.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        JavaCodeFragmentFactory fragmentFactory = JavaCodeFragmentFactory.getInstance(project);

        String a = "    @LamiaMapping\n" +
                "    private WorkspaceClassVO toWorkspaceVO2(WorkspaceBO workspaceBO) {\n" +
                "        return (WorkspaceClassVO) Lamia.mapping(workspaceBO);\n" +
                "    }";
        JavaCodeFragment code = fragmentFactory.createCodeBlockCodeFragment(a, containingFile, true);


        Document document = documentManager.getDocument(containingFile);
        EditorTextField editorTextField = new EditorTextField(document, project, JavaFileType.INSTANCE, true);
        editorTextField.setOneLineMode(false);

        //editorTextField.setMaximumSize(new Dimension(0, 300));
        //editorTextField.setPreferredSize(new Dimension(1200, 1300));

        JBScrollPane jbScrollPane = new JBScrollPane(editorTextField);
        //jbScrollPane.setPreferredSize(new Dimension(400, 300));
        //editorTextField.setToolTipText("fefdf");
        //editorTextField.setText("12312312312\n 12312312312\n 地方额黑胡椒大石街道手机打·1\n");


        JBPopupFactory instance = JBPopupFactory.getInstance();
        LightweightHint lightweightHint = new LightweightHint(editorTextField);
        HintManager instance1 = HintManager.getInstance();


        ApplicationManager.getApplication().invokeLater(() -> {
            instance1.showHint(jbScrollPane, new RelativePoint(event), 0, 0);

        });
        //.show(new RelativePoint(event), Balloon.Position.below));
    }


}
