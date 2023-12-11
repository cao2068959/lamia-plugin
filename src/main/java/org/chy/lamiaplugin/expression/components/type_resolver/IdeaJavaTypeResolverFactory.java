package org.chy.lamiaplugin.expression.components.type_resolver;

import com.chy.lamia.convert.core.components.TypeResolver;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.entity.TypeDefinition;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiClass;
import com.intellij.psi.impl.file.impl.JavaFileManager;
import com.intellij.psi.search.GlobalSearchScope;
import org.chy.lamiaplugin.exception.LamiaException;

public class IdeaJavaTypeResolverFactory implements TypeResolverFactory {


    private final Project project;

    public IdeaJavaTypeResolverFactory(Project project) {
        this.project = project;
    }

    @Override
    public TypeResolver getTypeResolver(TypeDefinition targetType) {
        PsiClass psiClass = JavaFileManager.getInstance(project)
                .findClass(targetType.getClassPath(), GlobalSearchScope.allScope(project));
        if (psiClass == null) {
            throw new LamiaException("Unable to find class [" + targetType.getClassPath() + "] to pars");
        }
        return new IdeaJavaTypeResolver(psiClass, targetType);
    }
}
