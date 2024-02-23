package org.chy.lamiaplugin.components.executor;

import com.intellij.openapi.application.ApplicationManager;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;

import org.chy.lamiaplugin.expression.LamiaExpressionManager;
import org.chy.lamiaplugin.expression.entity.LamiaExpression;
import org.chy.lamiaplugin.expression.entity.RelationClassWrapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.FileTime;
import java.time.Instant;
import java.util.*;

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
            Project project = file.getProject();
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
            setLastModifiedTime(file);
        }

        for (LamiaExpression lamiaExpression : needRefreshLamiaExpression) {
            Project project = lamiaExpression.getBelongPsiFile().getProject();
            ScheduledBatchExecutor.instance.deliverEvent(new LamiaExpressionChangeEvent(lamiaExpression.getExpression(), ChangeType.update, project));
        }
    }


    public void setLastModifiedTime(PsiFile file) {
        VirtualFile virtualFile = file.getContainingFile().getVirtualFile();
        if (virtualFile != null) {
            Path path = Path.of(virtualFile.getPath());
            try {
                Files.setLastModifiedTime(path, FileTime.from(Instant.now()));
            } catch (IOException e) {
                LOG.error("update time error", e);
            }
        }
    }
}
