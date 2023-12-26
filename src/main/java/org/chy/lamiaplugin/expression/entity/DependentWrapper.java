package org.chy.lamiaplugin.expression.entity;

import com.intellij.psi.PsiFile;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author bignosecat
 */
public class DependentWrapper {

    PsiFile psiFile;
    AtomicInteger dependentCount = new AtomicInteger(0);


    public DependentWrapper(PsiFile psiFile) {
        this.psiFile = psiFile;
    }

    public void  increment(){
        dependentCount.incrementAndGet();
    }

}
