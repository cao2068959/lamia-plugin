package org.chy.lamiaplugin.components.executor;

import com.chy.lamia.convert.core.utils.Lists;
import com.intellij.compiler.server.BuildManager;
import com.intellij.openapi.application.ApplicationManager;

import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.compiler.CompilerManager;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.module.ModuleUtilCore;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.persistent.PersistentFS;
import com.intellij.psi.*;

import com.intellij.psi.util.PsiUtil;
import org.chy.lamiaplugin.BuildRefreshHandler;
import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;
import org.chy.lamiaplugin.utlis.PsiMethodUtils;
import org.chy.lamiaplugin.utlis.Wrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class BuildRefreshExecutor implements BatchExecutor<FileChangeEvent> {

    private static final Logger LOG = Logger.getInstance(BuildRefreshExecutor.class);

    @Override
    public String group() {
        return "BuildRefreshExecutor";
    }

    @Override
    public void batchRun(List<FileChangeEvent> events) {
        buildRefresh(events);
    }


    public void buildRefresh(List<FileChangeEvent> events) {
        Map<String, PsiClass> handlerData = new HashMap<>();

        ApplicationManager.getApplication().runReadAction(() -> {
            for (FileChangeEvent event : events) {
                PsiFile psiFile = event.getData();
                if (!(psiFile instanceof PsiJavaFile javaFile)) {
                    return;
                }
                PsiClass[] classes = javaFile.getClasses();
                for (PsiClass aClass : classes) {
                    String qualifiedName = aClass.getQualifiedName();
                    if (qualifiedName != null) {
                        handlerData.put(qualifiedName, aClass);
                    }
                }
            }
        });

        Set<LamiaExpression> needRefreshLamiaExpression = new HashSet<>();
        Set<PsiFile> needRefreshFile = new HashSet<>();
        handlerData.forEach((classPath, file) -> {
            Project project = safeRead(file::getProject);
            LamiaExpressionManager manager = LamiaExpressionManager.getInstance(project);
            // 查找这个class 参与了哪些 lamia表达式
            Set<RelationClassWrapper> relationLamia = manager.getRelationLamia(classPath);
            relationLamia.forEach(relationClassWrapper -> {
                LamiaExpression lamiaExpression = relationClassWrapper.getLamiaExpression();
                PsiFile javaFile = lamiaExpression.getBelongPsiFile();
                // 把这个表达式 所属的文件加入到刷新列表中
                needRefreshFile.add(javaFile);
                // 把这个表达式加入到刷新列表中
                needRefreshLamiaExpression.add(lamiaExpression);
            });
        });

        if (needRefreshFile.isEmpty()) {
            return;
        }

        for (PsiFile file : needRefreshFile) {
            refreshFile(file);
        }

        for (LamiaExpression lamiaExpression : needRefreshLamiaExpression) {
            Project project = lamiaExpression.getBelongPsiFile().getProject();
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaExpression.getExpression(), ChangeType.update, project));
        }
    }

    public <T> T safeRead(Supplier<T> supplier) {
        Wrapper<T> result = new Wrapper<>();
        ApplicationManager.getApplication().runReadAction(() -> {
            T t = supplier.get();
            result.setData(t);
        });
        return result.getData();
    }

    public void refreshFile(PsiFile file) {
        BuildRefreshHandler.getInstance(file.getProject()).refresh(file);
    }
}
