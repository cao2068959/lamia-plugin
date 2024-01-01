package org.chy.lamiaplugin.marker;

import com.chy.lamia.utils.Lists;
import com.intellij.codeInsight.actions.ReformatCodeProcessor;
import com.intellij.codeInsight.hint.HintManager;
import com.intellij.compiler.server.BuildManager;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.Balloon;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.codeStyle.CodeStyleManager;
import com.intellij.psi.formatter.FormatterUtil;
import com.intellij.psi.formatter.java.JavaFormatterUtil;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.javadoc.JavadocManager;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.EditorTextField;
import com.intellij.ui.LightweightHint;
import com.intellij.ui.awt.RelativePoint;
import com.intellij.ui.components.JBScrollPane;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;

import java.awt.*;
import java.awt.event.MouseEvent;
import java.io.File;
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

    public void click(MouseEvent event, PsiElement psiElement, PsiElement lamiaMethod) {
        showTip("出现----->", event, lamiaMethod);
    }


    private void showTip(String msg, MouseEvent event, PsiElement psiElement) {
        LamiaLineMarkerHandler.psiElement = psiElement;

        PsiElement parentMethod = getSpiCodeBlock(psiElement);
        Project project = psiElement.getProject();
        PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);

        // 用点击的表达式生成对应的转换语句
        LamiaExpressionManager lamiaExpressionManager = LamiaExpressionManager.getInstance(project);
        String lamiaCode = lamiaExpressionManager.convert((PsiMethodCallExpression) psiElement);
        PsiFile psiFile;
        PsiClass psiClass = JavaFileManager.getInstance(project).findClass("com.chy.User", GlobalSearchScope.allScope(project));
        VirtualFile virtualFile = psiClass.getContainingFile().getVirtualFile();


        // 告诉编译器哪一些类发生了变动需要重新编译
       //BuildManager.getInstance().notifyFilesChanged(Lists.of(new File(canonicalPath)));

        //CompilerManager.getInstance(project).compile();

        // 把这个转换语句放入代码块中用于显示
        JavaCodeFragmentFactory fragmentFactory = JavaCodeFragmentFactory.getInstance(project);
        JavaCodeFragment code = fragmentFactory.createCodeBlockCodeFragment(lamiaCode, parentMethod, true);
        //code.addImportsFromString();

        // 对这个代码进行格式化
        //CodeStyleManager codeStyleManager = CodeStyleManager.getInstance(project);
        //codeStyleManager.reformat(code);


        Document document = documentManager.getDocument(code);

        editorTextField.setDocument(document);


        ApplicationManager.getApplication().invokeLater(() -> {
            JBPopupFactory.getInstance().createBalloonBuilder(jbScrollPane)
                    .setAnimationCycle(10)
                    .setBorderColor(Color.GRAY)
                    .setFillColor(Color.GRAY)
                    .createBalloon().show(new RelativePoint(event), Balloon.Position.below);
        });
        //.show(new RelativePoint(event), Balloon.Position.below));
    }


    private PsiElement getSpiCodeBlock(PsiElement psiElement) {
        PsiElement data = psiElement.getParent();
        while (true) {
            if (data instanceof PsiCodeBlock method) {
                return method;
            }
            if (data == null || data instanceof PsiClass || data instanceof PsiFile) {
                return null;
            }
            data = data.getParent();
        }
    }

}
