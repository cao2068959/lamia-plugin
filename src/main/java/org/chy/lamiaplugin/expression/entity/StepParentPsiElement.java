package org.chy.lamiaplugin.expression.entity;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.NlsSafe;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.util.IncorrectOperationException;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class StepParentPsiElement implements PsiElement {

    private PsiElement stepParent;
    private PsiElement parent;

    public StepParentPsiElement(PsiElement stepParent, PsiElement parent) {
        this.stepParent = stepParent;
        this.parent = parent;
    }

    @Override
    public @NotNull Project getProject() throws PsiInvalidElementAccessException {
        return parent.getProject();
    }

    @Override
    public @NotNull Language getLanguage() {
        return parent.getLanguage();
    }

    @Override
    public PsiManager getManager() {
        return parent.getManager();
    }

    @Override
    public PsiElement @NotNull [] getChildren() {
        return parent.getChildren();
    }

    @Override
    public PsiElement getParent() {
        return parent.getParent();
    }

    @Override
    public PsiElement getFirstChild() {
        return parent.getFirstChild();
    }

    @Override
    public PsiElement getLastChild() {
        return parent.getLastChild();
    }

    @Override
    public PsiElement getNextSibling() {
        return parent.getNextSibling();
    }

    @Override
    public PsiElement getPrevSibling() {
        return parent.getPrevSibling();
    }

    @Override
    public PsiFile getContainingFile() throws PsiInvalidElementAccessException {
        return stepParent.getContainingFile();
    }

    @Override
    public TextRange getTextRange() {
        return stepParent.getTextRange();
    }

    @Override
    public int getStartOffsetInParent() {
        return parent.getStartOffsetInParent();
    }

    @Override
    public int getTextLength() {
        return stepParent.getTextLength();
    }

    @Override
    public @Nullable PsiElement findElementAt(int offset) {
        return parent.findElementAt(offset);
    }

    @Override
    public @Nullable PsiReference findReferenceAt(int offset) {
        return stepParent.findReferenceAt(offset);
    }

    @Override
    public int getTextOffset() {
        return stepParent.getTextOffset();
    }

    @Override
    public @NlsSafe String getText() {
        return stepParent.getText();
    }

    @Override
    public char @NotNull [] textToCharArray() {
        return parent.textToCharArray();
    }

    @Override
    public PsiElement getNavigationElement() {
        return parent.getNavigationElement();
    }

    @Override
    public PsiElement getOriginalElement() {
        return parent.getOriginalElement();
    }

    @Override
    public boolean textMatches(@NotNull @NonNls CharSequence text) {
        return parent.textMatches(text);
    }

    @Override
    public boolean textMatches(@NotNull PsiElement element) {
        return parent.textMatches(element);
    }

    @Override
    public boolean textContains(char c) {
        return stepParent.textContains(c);
    }

    @Override
    public void accept(@NotNull PsiElementVisitor visitor) {
        parent.accept(visitor);
    }

    @Override
    public void acceptChildren(@NotNull PsiElementVisitor visitor) {
        parent.acceptChildren(visitor);
    }

    @Override
    public PsiElement copy() {
        return parent.copy();
    }

    @Override
    public PsiElement add(@NotNull PsiElement element) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addBefore(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addAfter(@NotNull PsiElement element, @Nullable PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void checkAdd(@NotNull PsiElement element) throws IncorrectOperationException {

    }

    @Override
    public PsiElement addRange(PsiElement first, PsiElement last) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeBefore(@NotNull PsiElement first, @NotNull PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public PsiElement addRangeAfter(PsiElement first, PsiElement last, PsiElement anchor) throws IncorrectOperationException {
        return null;
    }

    @Override
    public void delete() throws IncorrectOperationException {

    }

    @Override
    public void checkDelete() throws IncorrectOperationException {

    }

    @Override
    public void deleteChildRange(PsiElement first, PsiElement last) throws IncorrectOperationException {

    }

    @Override
    public PsiElement replace(@NotNull PsiElement newElement) throws IncorrectOperationException {
        return parent.replace(newElement);
    }

    @Override
    public boolean isValid() {
        return parent.isValid();
    }

    @Override
    public boolean isWritable() {
        return parent.isWritable();
    }

    @Override
    public @Nullable PsiReference getReference() {
        return parent.getReference();
    }

    @Override
    public PsiReference @NotNull [] getReferences() {
        return new PsiReference[0];
    }

    @Override
    public <T> @Nullable T getCopyableUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putCopyableUserData(@NotNull Key<T> key, @Nullable T value) {

    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, @Nullable PsiElement lastParent, @NotNull PsiElement place) {
        return false;
    }

    @Override
    public @Nullable PsiElement getContext() {
        return parent.getContext();
    }

    @Override
    public boolean isPhysical() {
        return false;
    }

    @Override
    public @NotNull GlobalSearchScope getResolveScope() {
        return null;
    }

    @Override
    public @NotNull SearchScope getUseScope() {
        return null;
    }

    @Override
    public ASTNode getNode() {
        return null;
    }

    @Override
    public boolean isEquivalentTo(PsiElement another) {
        return false;
    }

    @Override
    public Icon getIcon(int flags) {
        return null;
    }

    @Override
    public <T> @Nullable T getUserData(@NotNull Key<T> key) {
        return null;
    }

    @Override
    public <T> void putUserData(@NotNull Key<T> key, @Nullable T value) {

    }
}
