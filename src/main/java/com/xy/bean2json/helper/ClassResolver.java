package com.xy.bean2json.helper;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.xy.bean2json.error.ConvertException;
import com.xy.bean2json.utils.JsonUtils;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ClassResolver
 *
 * @author Created by gold on 2022/8/18 18:17
 * @since 1.0.0
 */
public final class ClassResolver {
    private ClassResolver() {
    }

    public static final String KEY_COMMENT = "@comment";

    /**
     * 解析为json
     *
     * @param project 项目
     * @param psiFile 文件
     * @param psiType type
     * @return json
     */
    public static String toJson(Project project, PsiFile psiFile, PsiType psiType) {
        Map<String, Object> map = resolveClass(project, psiFile, psiType);

        return JsonUtils.toJson(map);
    }

    /**
     * 解析类
     *
     * @param project 项目
     * @param psiFile 文件
     * @param psiType type
     * @return map
     */
    public static Map<String, Object> resolveClass(Project project, PsiFile psiFile, PsiType psiType) {
        Pair<Map<String, Object>, Map<String, Object>> pair = resolve(project, psiFile, psiType);
        if (pair == null) {
            return Collections.emptyMap();
        }

        return pair.first;
    }

    /**
     * 解析注释
     *
     * @param project 项目
     * @param psiFile 文件
     * @param psiType type
     * @return map
     */
    public static Map<String, Object> resolveComment(Project project, PsiFile psiFile, PsiType psiType) {
        Pair<Map<String, Object>, Map<String, Object>> pair = resolve(project, psiFile, psiType);
        if (pair == null) {
            return Collections.emptyMap();
        }

        return pair.second;
    }

    /**
     * 解析类
     *
     * @param project 项目
     * @param psiFile 文件
     * @param psiType type
     * @return params
     */
    public static Pair<Map<String, Object>, Map<String, Object>> resolve(Project project, PsiFile psiFile, PsiType psiType) {
        if (psiType == null) {
            throw new ConvertException();
        }

        return JavaConverter.convert(project, psiFile, new LinkedHashMap<>(), psiType);
    }
}