package com.xy.bean2json.utils;

import com.intellij.codeInsight.generation.GetterSetterPrototypeProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.KotlinLanguage;

/**
 * PluginUtils
 *
 * @author Created by gold on 2020/12/11 10:21
 */
public final class PluginUtils {
    private PluginUtils() {
    }

    /**
     * 解析文件
     *
     * @param project 项目
     * @param psiFile 文件
     * @return class
     */
    public static PsiType parsePsiFile(Project project, PsiFile psiFile) {
        String className = psiFile.getName();
        String shortClassName = className.substring(0, className.lastIndexOf("."));

        PsiClass[] psiClasses = PsiShortNamesCache.getInstance(project)
                .getClassesByName(shortClassName, GlobalSearchScope.allScope(project));
        if (psiClasses.length == 0) {
            throw new IllegalArgumentException("not found class");
        }

        return JavaPsiFacade.getElementFactory(project).createType(psiClasses[0]);
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

    /**
     * 是kotlin类
     *
     * @param psiFile psi文件
     * @return bool
     */
    public static boolean isKotlin(PsiFile psiFile) {
        return psiFile.getFileType() instanceof KotlinFileType;
    }

    /**
     * 是kotlin类
     *
     * @param psiField psi参数
     * @return bool
     */
    public static boolean isKotlin(PsiField psiField) {
        return psiField.getLanguage() instanceof KotlinLanguage;
    }
}
