package com.xy.bean2json.helper;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import com.xy.bean2json.utils.JavaUtils;
import com.xy.bean2json.utils.MockDataUtils;
import com.xy.bean2json.utils.PluginUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.stream.Collectors;

import static com.xy.bean2json.utils.MockDataUtils.getNormalTypeValue;
import static com.xy.bean2json.utils.MockDataUtils.getPrimitiveValue;

/**
 * JavaConverter
 *
 * @author Created by gold on 2020/12/11 17:04
 */
public class JavaConverter {

    public static Pair<Map<String, Object>, Map<String, Object>> convert(
            @NotNull Set<PsiClass> parsedTypes, @NotNull PsiClass psiClass) {
        return new JavaConverter(parsedTypes).convert(psiClass);
    }

    private final Set<PsiClass> parsedTypes;
    private final Map<String, Object> classes;
    private final Map<String, Object> comments;

    private JavaConverter(Set<PsiClass> parsedTypes) {
        this.parsedTypes = parsedTypes;

        this.classes = new LinkedHashMap<>();
        this.comments = new LinkedHashMap<>();
    }

    private void putClass(PsiField field, Object value) {
        if (value == null) {
            return;
        }

        classes.put(field.getName(), value);
    }

    private void putComment(PsiField field) {
        putComment(field, getComment(field));
    }

    private void putComment(PsiField field, Object comment) {
        comments.put(field.getName(), comment);
    }

    private Pair<Map<String, Object>, Map<String, Object>> convert(PsiClass psiClass) {
        //add to parsedTypes
        parsedTypes.add(psiClass);

        for (PsiField field : psiClass.getAllFields()) {
            if (field.hasModifier(JvmModifier.STATIC)) {
                continue;
            }
            if (!field.hasModifier(JvmModifier.PUBLIC)) {
                PsiMethod getMethod = PluginUtils.generateGetMethod(field);

                if (psiClass.findMethodBySignature(getMethod, true) == null) {
                    continue;
                }
            }

            PsiType type = field.getType();

            if (type instanceof PsiPrimitiveType) {
                handlePsiPrimitiveType(field, ((PsiPrimitiveType) type));
            } else {
                //reference Type
                String fieldTypeName = type.getPresentableText();
                if (MockDataUtils.isNormalType(fieldTypeName)) {
                    //normal Type
                    handlePsiNormalType(field, fieldTypeName);
                } else if (type instanceof PsiArrayType) {
                    //array type
                    handlePsiArrayType(field, ((PsiArrayType) type));
                } else if (fieldTypeName.startsWith("List")) {
                    //list type
                    handlePsiListType(field, type);
                } else if (fieldTypeName.startsWith("Map")) {
                    //map type
                    handlePsiMapType(field, type);
                } else {
                    PsiClass enumClass = PsiUtil.resolveClassInClassTypeOnly(type);
                    if (enumClass != null && enumClass.isEnum()) {
                        //enum
                        handlePsiEnumClass(field, enumClass);
                    } else {
                        //class type
                        PsiClass classInClass = PsiUtil.resolveClassInType(type);

                        handlePsiObjectClass(field, classInClass);
                    }
                }
            }
        }

        return Pair.create(classes, comments);
    }

    private void handlePsiPrimitiveType(PsiField field, PsiPrimitiveType type) {
        putClass(field, getPrimitiveValue(type));
        putComment(field, getComment(field));
    }

    private void handlePsiNormalType(PsiField field, String fieldTypeName) {
        putClass(field, getNormalTypeValue(fieldTypeName));
        putComment(field, getComment(field));
    }

    private void handlePsiArrayType(PsiField field, PsiArrayType type) {
        PsiType deepType = type.getDeepComponentType();
        List<Object> list = new ArrayList<>();

        String deepTypeName = deepType.getPresentableText();
        if (deepType instanceof PsiPrimitiveType) {
            list.add(MockDataUtils.getPrimitiveValue(deepType));
            putComment(field);
        } else if (MockDataUtils.isNormalType(deepTypeName)) {
            list.add(MockDataUtils.getNormalTypeValue(deepTypeName));
            putComment(field);
        } else {
            PsiClass listClass = PsiUtil.resolveClassInType(deepType);
            list.add(handlePsiGeneralClass(field, listClass));
        }

        putClass(field, list);
    }

    private void handlePsiListType(PsiField field, PsiType type) {
        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
        PsiClass iterableClass = PsiUtil.resolveClassInClassTypeOnly(iterableType);

        if (iterableClass == null) {
            return;
        }

        List<Object> list = new ArrayList<>();

        String classTypeName = iterableClass.getName();
        if (MockDataUtils.isNormalType(classTypeName)) {
            list.add(MockDataUtils.getNormalTypeValue(classTypeName));
            putComment(field);
        } else {
            list.add(handlePsiGeneralClass(field, iterableClass));
        }

        putClass(field, list);
    }

    private void handlePsiMapType(PsiField field, PsiType type) {
        PsiType keyType = PsiUtil.substituteTypeParameter(type, "java.util.Map", 0, false);
        PsiType valueType = PsiUtil.substituteTypeParameter(type, "java.util.Map", 1, false);
        if (keyType == null || valueType == null) {
            throw new NullPointerException("map type null");
        }

        String keyTypeName = keyType.getPresentableText();
        String valueTypeName = valueType.getPresentableText();

        if (!MockDataUtils.isNormalType(keyTypeName)) {
            throw new IllegalStateException("map key unsupported types");
        }

        Object keyDefParam = getNormalTypeValue(keyTypeName);
        Object valueDefParam;

        if (MockDataUtils.isNormalType(valueTypeName)) {
            valueDefParam = getNormalTypeValue(valueTypeName);
            putComment(field);
        } else {
            PsiClass classInType = PsiUtil.resolveClassInType(valueType);
            boolean isSystemCls = isSystemClass(classInType);
            if (isSystemCls) {
                valueDefParam = new Object();
                putComment(field);
            } else {
                valueDefParam = handlePsiGeneralClass(field, classInType);
            }
        }

        putClass(field, Collections.singletonMap(keyDefParam, valueDefParam));
    }

    private void handlePsiEnumClass(PsiField field, PsiClass enumClass) {
        List<String> list = Arrays.stream(enumClass.getFields())
                .filter(f -> f instanceof PsiEnumConstant)
                .map(PsiField::getName)
                .collect(Collectors.toList());

        putClass(field, list);
        putComment(field);
    }

    private void handlePsiObjectClass(PsiField field, PsiClass classInClass) {
        boolean isSystemCls = isSystemClass(classInClass);

        //system class
        if (isSystemCls) {
            putClass(field, new Object());
            putComment(field);
        } else {
            putClass(field, handlePsiGeneralClass(field, classInClass));
        }
    }

    private Map<String, Object> handlePsiGeneralClass(PsiField field, PsiClass generalClass) {
        if (generalClass == null) {
            return null;
        }

        if (parsedTypes.contains(generalClass)) {
            putClass(field, new Object());
            putComment(field);
            return null;
        } else {
            Pair<Map<String, Object>, Map<String, Object>> pair = convert(parsedTypes, generalClass);

            mergeComment(pair.second, getComment(field));
            putComment(field, pair.second);

            return pair.first;
        }
    }

    private static boolean isSystemClass(PsiClass classInType) {
        if (classInType == null) {
            return false;
        }

        PsiFile file = classInType.getContainingFile();
        if (file instanceof PsiJavaFile) {
            String packageName = ((PsiJavaFile) file).getPackageName();
            return JavaUtils.isSystemClass(packageName);
        }

        return false;
    }

    private static void mergeComment(Map<String, Object> commentMap, String comment) {
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        Map<String, Object> newMap = new LinkedHashMap<>(commentMap);

        commentMap.clear();
        commentMap.put(ConvertToJsonHelper.KEY_COMMENT, comment);
        commentMap.putAll(newMap);
    }

    private static String getComment(PsiField field) {
        PsiDocComment comment = field.getDocComment();
        if (comment == null) {
            return "";
        }

        String text = comment.getText();
        if (StringUtils.isEmpty(text)) {
            return "";
        }

        return formatComment(text);
    }

    private static String formatComment(String comment) {
        String regex = "/\\*+\\s*|\\s*\\*+/|[\r\n]+|\\*+";
        String regexSpace = "\\s+";

        return comment.replaceAll(regex, "")
                .replaceAll(regexSpace, " ")
                .trim();
    }
}