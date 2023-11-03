package org.chy.lamiaplugin;

import com.intellij.compiler.impl.javaCompiler.BackendCompiler;
import com.intellij.compiler.impl.javaCompiler.eclipse.EclipseCompiler;
import com.intellij.compiler.impl.javaCompiler.javac.JavacCompiler;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.CompilerProjectExtension;
import com.intellij.openapi.roots.ModuleExtension;
import com.intellij.openapi.roots.ProjectExtension;
import com.intellij.openapi.util.NlsContexts;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.pointers.VirtualFilePointer;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class CompilerListener extends JavacCompiler {


    public CompilerListener(Project project) {
        super(project);
    }

    @Override
    public @NotNull String getId() {
        return super.getId();
    }

    @Override
    public @NlsContexts.ListItem @NotNull String getPresentableName() {
        return super.getPresentableName();
    }

    @Override
    public @NotNull Configurable createConfigurable() {
        return super.createConfigurable();
    }

    @Override
    public @NotNull Set<FileType> getCompilableFileTypes() {
        System.out.println("---->");
        return super.getCompilableFileTypes();
    }
}
