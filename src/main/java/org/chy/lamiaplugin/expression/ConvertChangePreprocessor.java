package org.chy.lamiaplugin.expression;

import com.chy.lamia.expose.Lamia;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    Project project;

    public ConvertChangePreprocessor(Project project) {
        this.project = project;
        new LamiaExpressionManager(project);

        DumbService.getInstance(project).smartInvokeLater(() -> {
            PsiClass lamiaClass = JavaFileManager.getInstance(project).findClass(Lamia.class.getName(), GlobalSearchScope.allScope(project));
            if (lamiaClass == null) {
                return;
            }
            Collection<PsiReference> all = ReferencesSearch.search(lamiaClass).findAll();
            System.out.println(all);
        });
    }

    private void createdLamiaConvert() {

    }


    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        if (event.getCode() != PsiTreeChangeEventImpl.PsiEventType.CHILDREN_CHANGED){
            return;
        }
        // 发生变更之后执行


        System.out.println(event);
    }
}
