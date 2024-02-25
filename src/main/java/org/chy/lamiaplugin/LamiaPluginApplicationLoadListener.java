package org.chy.lamiaplugin;

import com.chy.lamia.convert.core.components.ComponentFactory;
import com.chy.lamia.convert.core.components.NameHandler;
import com.chy.lamia.convert.core.components.TreeFactory;
import com.chy.lamia.convert.core.components.TypeResolverFactory;
import com.chy.lamia.convert.core.components.entity.Expression;
import com.chy.lamia.convert.core.components.entity.Statement;
import com.chy.lamia.expose.Lamia;
import com.intellij.ide.ApplicationLoadListener;
import com.intellij.openapi.application.Application;
import com.intellij.psi.PsiManager;
import org.chy.lamiaplugin.components.executor.BuildRefreshExecutor;
import org.chy.lamiaplugin.components.executor.ScheduledBatchExecutor;
import org.chy.lamiaplugin.components.executor.UpdateExpRelationExecutor;
import org.chy.lamiaplugin.expression.components.SimpleNameHandler;
import org.chy.lamiaplugin.expression.components.StringExpression;
import org.chy.lamiaplugin.expression.components.StringTreeFactory;
import org.chy.lamiaplugin.expression.components.statement.StringStatement;
import org.chy.lamiaplugin.expression.components.type_resolver.IdeaJavaTypeResolverFactory;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

public class LamiaPluginApplicationLoadListener implements ApplicationLoadListener {

    @Override
    public void beforeApplicationLoaded(@NotNull Application application, @NotNull Path configPath) {
        ScheduledBatchExecutor.instance = new ScheduledBatchExecutor(6000);
        ScheduledBatchExecutor.instance.registerBatchExecutor(new BuildRefreshExecutor());
        ScheduledBatchExecutor.instance.registerBatchExecutor(new UpdateExpRelationExecutor());
        registerLamiaComponents();
    }


    private void registerLamiaComponents() {
        ComponentFactory.registerComponents(TreeFactory.class, new StringTreeFactory());
        ComponentFactory.registerEntityStructure(Expression.class, StringExpression::new);
        ComponentFactory.registerEntityStructure(Statement.class, StringStatement::new);
        ComponentFactory.registerComponents(NameHandler.class, new SimpleNameHandler());
    }

}
