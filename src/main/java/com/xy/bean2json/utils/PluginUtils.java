package com.xy.bean2json.utils;

import com.intellij.codeInsight.generation.GetterSetterPrototypeProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.PsiShortNamesCache;
import com.intellij.psi.util.PsiUtil;
import com.xy.bean2json.model.CommentAttribute;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.kotlin.asJava.elements.KtLightFieldForSourceDeclarationSupport;
import org.jetbrains.kotlin.idea.KotlinFileType;
import org.jetbrains.kotlin.idea.KotlinLanguage;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;
import org.jetbrains.kotlin.psi.KtDeclaration;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * PluginUtils
 *
 * @author Created by gold on 2020/12/11 10:21
 */
public final class PluginUtils {
    private PluginUtils() {
    }

    /**
     * 解析文件获取类型
     *
     * @param psiFile 文件
     * @return class
     */
    public static PsiType parsePsiFile(PsiFile psiFile) {
        Project project = psiFile.getProject();

        String className = psiFile.getName();
        String shortClassName = className.substring(0, className.lastIndexOf("."));

        PsiClass[] psiClasses;
        if (psiFile instanceof PsiJavaFile) {
            psiClasses = ((PsiJavaFile) psiFile).getClasses();
        } else {
            psiClasses = PsiShortNamesCache.getInstance(project)
                    .getClassesByName(shortClassName, GlobalSearchScope.allScope(project));
        }

        if (psiClasses.length == 0) {
            throw new IllegalArgumentException("not found class " + shortClassName);
        }

        //todo 不能直接用0，如果命名有重复可能触发bug
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

    /**
     * 是否为系统类型
     *
     * @param classInType 类型
     * @return bool
     */
    public static boolean isSystemType(PsiType classInType) {
        if (classInType == null) {
            return false;
        }

        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(classInType);

        return isSystemClass(psiClass);
    }

    /**
     * 是否为系统类型
     *
     * @param psiClass 类型
     * @return bool
     */
    public static boolean isSystemClass(PsiClass psiClass) {
        if (psiClass == null) {
            return false;
        }

        PsiFile psiFile = psiClass.getContainingFile();
        if (psiFile instanceof PsiJavaFile) {
            String packageName = ((PsiJavaFile) psiFile).getPackageName();
            return JavaUtils.isSystemClass(packageName);
        }

        return false;
    }

    /**
     * 合并注释
     *
     * @param comments 子字段注释
     * @param comment  整个字段的注释
     */
    public static CommentAttribute mergeComment(Map<String, CommentAttribute> comments, CommentAttribute comment) {
        if (comment.isEmpty()) {
            return CommentAttribute.create(comments);
        }

        Map<String, CommentAttribute> newMap = new LinkedHashMap<>(comments.size() + 1);

        newMap.put(CommentAttribute.KEY_COMMENT, comment);
        newMap.putAll(comments);

        return CommentAttribute.create(newMap);
    }

    /**
     * 解析注释
     *
     * @param field 字段
     * @return 注释
     */
    public static CommentAttribute resolveComment(PsiField field) {
        String text = null;
        if (isKotlin(field)) {
            PsiElement psiElement = field.getOriginalElement();
            if (psiElement instanceof KtLightFieldForSourceDeclarationSupport) {
                KtDeclaration ktDeclaration = ((KtLightFieldForSourceDeclarationSupport) field.getOriginalElement()).getKotlinOrigin();
                if (ktDeclaration != null) {
                    KDoc doc = ktDeclaration.getDocComment();
                    if (doc != null) {
                        text = doc.getText();
                    }
                }
            }
        } else {
            PsiDocComment comment = field.getDocComment();
            if (comment != null) {
                text = comment.getText();
            }
        }

        if (StringUtils.isEmpty(text)) {
            return CommentAttribute.empty();
        }

        return CommentAttribute.create(formatComment(text));
    }

    private static String formatComment(String comment) {
        String regex = "/\\*+\\s*|\\s*\\*+/|[\r\n]+|\\*+";
        String regexSpace = "\\s+";

        return comment.replaceAll(regex, "")
                .replaceAll(regexSpace, " ")
                .trim();
    }
}
