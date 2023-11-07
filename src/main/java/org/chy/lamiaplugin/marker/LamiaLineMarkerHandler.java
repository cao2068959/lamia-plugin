package org.chy.lamiaplugin.marker;

import com.intellij.codeInsight.hint.HintManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.*;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.intellij.codeInsight.highlighting.HighlightManager.HIDE_BY_ESCAPE;

/**
 * @author bignosecat
 */
public class LamiaLineMarkerHandler {

    private static Map<Project, LamiaLineMarkerHandler> instanceList = new ConcurrentHashMap<>();
    private final EditorTextField editorTextField;
    private final JBScrollPane jbScrollPane;

    public static PsiElement psiElement;

    public static LamiaLineMarkerHandler of(Project project) {
        return instanceList.computeIfAbsent(project, LamiaLineMarkerHandler::new);
    }

    public LamiaLineMarkerHandler(Project project) {
        this.editorTextField = new EditorTextField(null, project, JavaFileType.INSTANCE, true);
        editorTextField.setOneLineMode(false);
        this.jbScrollPane = new JBScrollPane(editorTextField);

    }

    public void click(MouseEvent event, PsiElement psiElement) {
        Project project = psiElement.getProject();
        System.out.println("----> 点击");
        showTip("出现----->", event, psiElement);
    }


    private void showTip(String msg, MouseEvent event, PsiElement psiElement) {
        LamiaLineMarkerHandler.psiElement = psiElement;

        PsiFile containingFile = psiElement.getContainingFile();
        Project project = psiElement.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        JavaCodeFragmentFactory fragmentFactory = JavaCodeFragmentFactory.getInstance(project);

        String a = "    @LamiaMapping\n" +
                "    private WorkspaceClassVO toWorkspaceVO2(WorkspaceBO workspaceBO) {\n" +
                "        return (WorkspaceClassVO) Lamia.mapping(workspaceBO);\n" +
                "    }";
        JavaCodeFragment code = fragmentFactory.createCodeBlockCodeFragment(a, containingFile, true);

        Document document = documentManager.getDocument(code);
        editorTextField.setDocument(document);


        //jbScrollPane.setPreferredSize(new Dimension(400, 300));
        //editorTextField.setToolTipText("fefdf");
        //editorTextField.setText("12312312312\n 12312312312\n 地方额黑胡椒大石街道手机打·1\n");


        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory.getInstance().createBalloonBuilder(jbScrollPane)
                    .setAnimationCycle(10)
                    .setBorderColor(Color.GRAY)
                    .setFillColor(Color.GRAY)
                    .createBalloon().show(new RelativePoint(event), Balloon.Position.below);
        });
        //.show(new RelativePoint(event), Balloon.Position.below));
    }


}
