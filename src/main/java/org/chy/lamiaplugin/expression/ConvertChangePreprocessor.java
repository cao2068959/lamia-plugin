package org.chy.lamiaplugin.expression;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiTreeChangeListener;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    Project project;

    public ConvertChangePreprocessor(Project project) {
        this.project = project;

        Module module = ModuleManager.getInstance(project).getModules()[0];
        Collection<VirtualFile> java = FilenameIndex.getAllFilesByExt(project, "java", GlobalSearchScope.moduleScope(module));
        VirtualFile virtualFile = java.stream().findFirst().get();
        PsiFile file = PsiManager.getInstance(project).findFile(virtualFile);
        System.out.println(file);

    }

    @Override
    public void treeChanged(@NotNull PsiTreeChangeEventImpl event) {
        System.out.println(event);
    }
}
