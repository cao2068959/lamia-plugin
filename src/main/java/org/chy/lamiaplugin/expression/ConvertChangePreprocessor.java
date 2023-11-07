package org.chy.lamiaplugin.expression;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.PsiTreeChangeEventImpl;
import com.intellij.psi.impl.PsiTreeChangePreprocessor;
import com.intellij.psi.search.FilenameIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ConvertChangePreprocessor implements PsiTreeChangePreprocessor {

    Project project;

    public ConvertChangePreprocessor(Project project) {
        this.project = project;

        Module module = ModuleManager.getInstance(project).getModules()[0];



        PsiClass aClass = JavaPsiFacade.getInstance(project)
                .findClass("com.chy.lamia.expose.Lamia", GlobalSearchScope.allScope(project));

        Collection<PsiReference> all = ReferencesSearch.search(aClass).findAll();

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
