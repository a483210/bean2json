package com.xy.bean2json.helper;

import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.xy.bean2json.error.ConvertException;
import com.xy.bean2json.utils.JavaUtils;

import java.util.*;

/**
 * ConvertToJsonHelper
 *
 * @author Created by gold on 2020/12/10 11:02
 */
public class ConvertToJsonHelper {

    public static final String KEY_COMMENT = "@comment";

    /**
     * 解析为json
     *
     * @param psiClass class
     * @return json
     */
    public String toJson(PsiClass psiClass) {
        Map<String, Object> map = convertClass(psiClass);

        return JavaUtils.toJson(map);
    }

    /**
     * 解析类
     *
     * @param psiClass class
     * @return map
     */
    public Map<String, Object> convertClass(PsiClass psiClass) {
        Pair<Map<String, Object>, Map<String, Object>> pair = convert(psiClass);
        if (pair == null) {
            return null;
        }

        return pair.first;
    }

    /**
     * 解析注释
     *
     * @param psiClass class
     * @return map
     */
    public Map<String, Object> convertComment(PsiClass psiClass) {
        Pair<Map<String, Object>, Map<String, Object>> pair = convert(psiClass);
        if (pair == null) {
            return null;
        }

        return pair.second;
    }

    /**
     * 解析类
     *
     * @param psiClass class
     * @return params
     */
    public Pair<Map<String, Object>, Map<String, Object>> convert(PsiClass psiClass) {
        if (psiClass == null) {
            throw new ConvertException();
        }

        return JavaConverter.convert(new HashSet<>(), psiClass);
    }
}