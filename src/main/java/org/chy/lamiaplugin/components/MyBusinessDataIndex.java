package org.chy.lamiaplugin.components;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.*;
import com.intellij.util.io.EnumeratorStringDescriptor;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;

public class MyBusinessDataIndex extends ScalarIndexExtension<String> {
    public static final ID<String, Void> NAME = ID.create("MyBusinessDataIndex");

    private final EnumeratorStringDescriptor myKeyDescriptor = new EnumeratorStringDescriptor();

    @NotNull
    @Override
    public ID<String, Void> getName() {
        return NAME;
    }

    @NotNull
    @Override
    public DataIndexer<String, Void, FileContent> getIndexer() {
        return inputData -> {
            // 这里是将文件内容转换为索引的代码，具体实现根据你的需求来定
            return Collections.emptyMap();
        };
    }

    @NotNull
    @Override
    public KeyDescriptor<String> getKeyDescriptor() {
        return myKeyDescriptor;
    }

    @NotNull
    @Override
    public FileBasedIndex.InputFilter getInputFilter() {
        return file -> true;
    }

    @Override
    public boolean dependsOnFileContent() {
        return true;
    }

    @Override
    public int getVersion() {
        return 0;
    }

    public static void queryBusinessData(Project project, String key) {
        FileBasedIndex.getInstance().getFilesWithKey(NAME, Collections.singleton(key), virtualFile -> {
            // 这里是处理找到的文件的代码，具体实现根据你的需求来定
            return true;
        }, GlobalSearchScope.allScope(project));
    }
}