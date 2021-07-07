package com.xy.bean2json.utils;

import com.intellij.codeInsight.generation.GetterSetterPrototypeProvider;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.IconLoader;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;

import javax.swing.*;

/**
 * PluginUtils
 *
 * @author Created by gold on 2020/12/11 10:21
 */
public final class PluginUtils {
    private PluginUtils() {
    }

    public static final Icon ICON_SELECTED = IconLoader.getIcon("/icons/selected1.png");

    /**
     * 解析文件
     *
     * @param editor  编辑器
     * @param psiFile 文件
     * @return class
     */
    public static PsiClass parseForFile(Editor editor, PsiFile psiFile) {
        PsiElement referenceAt = psiFile.findElementAt(editor.getCaretModel().getOffset());
        return (PsiClass) PsiTreeUtil.getContextOfType(referenceAt, new Class[]{PsiClass.class});
    }

    /**
     * 生成get方法
     *
     * @param field 参数
     * @return method
     */
    public static PsiMethod generateGetMethod(PsiField field) {
        PsiMethod[] methods = GetterSetterPrototypeProvider.generateGetterSetters(field, true);
        if (methods == null || methods.length != 1) {
            throw new IllegalArgumentException("unable generate getter method");
        }

        return methods[0];
    }

    /**
     * 生成set方法
     *
     * @param field 参数
     * @return method
     */
    public static PsiMethod generateSetMethod(PsiField field) {
        PsiMethod[] methods = GetterSetterPrototypeProvider.generateGetterSetters(field, false);
        if (methods == null || methods.length != 1) {
            throw new IllegalArgumentException("unable generate setter method");
        }

        return methods[0];
    }
}
