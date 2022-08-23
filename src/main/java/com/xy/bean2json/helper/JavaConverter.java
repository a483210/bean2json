package com.xy.bean2json.helper;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.javadoc.PsiDocComment;
import com.intellij.psi.util.PsiUtil;
import com.xy.bean2json.utils.JsonUtils;
import com.xy.bean2json.utils.MockDataUtils;
import com.xy.bean2json.utils.PluginUtils;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.kotlin.asJava.elements.KtLightFieldForSourceDeclarationSupport;
import org.jetbrains.kotlin.kdoc.psi.api.KDoc;
import org.jetbrains.kotlin.psi.KtDeclaration;

import java.util.*;
import java.util.stream.Collectors;

import static com.xy.bean2json.utils.MockDataUtils.getNormalTypeValue;

/**
 * JavaConverter
 *
 * @author Created by gold on 2020/12/11 17:04
 */
public class JavaConverter {

    protected static Pair<Map<String, Object>, Map<String, Object>> convert(
            @NotNull Project project,
            @NotNull PsiFile psiFile,
            @NotNull Map<PsiType, PsiTypeCache> parsedTypes,
            @NotNull PsiType psiType) {
        return new JavaConverter(project, psiFile, parsedTypes).convert(psiType);
    }

    private final Project project;
    private final PsiFile psiFile;
    private final Map<PsiType, PsiTypeCache> parsedTypes;

    private final Map<String, Object> classes;
    private final Map<String, Object> comments;

    private JavaConverter(Project project, PsiFile psiFile, Map<PsiType, PsiTypeCache> parsedTypes) {
        this.project = project;
        this.psiFile = psiFile;
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

    private Pair<Map<String, Object>, Map<String, Object>> convert(PsiType psiType) {
        if (parsedTypes.containsKey(psiType)) {
            return parsedTypes.get(psiType).resolve();
        }

        //add to parsedTypes
        parsedTypes.put(psiType, new PsiTypeCache(classes, comments));

        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (psiClass == null) {
            return Pair.create(classes, comments);
        }

        for (PsiField field : psiClass.getAllFields()) {
            //noinspection UnstableApiUsage
            if (field.hasModifier(JvmModifier.STATIC)) {
                continue;
            }
            //noinspection UnstableApiUsage
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

                        if (enumClass instanceof PsiTypeParameter
                                && psiType instanceof PsiClassReferenceType) {
                            int genericIndex = ((PsiTypeParameter) enumClass).getIndex();

                            PsiType[] parameters = ((PsiClassReferenceType) psiType).getParameters();
                            if (genericIndex < parameters.length) {
                                type = parameters[genericIndex];
                            }
                        }

                        handlePsiObjectClass(field, type);
                    }
                }
            }
        }

        return Pair.create(classes, comments);
    }

    private void handlePsiPrimitiveType(PsiField field, PsiPrimitiveType type) {
        putClass(field, MockDataUtils.getPrimitiveValue(field, type));
        putComment(field, getComment(field));
    }

    private void handlePsiNormalType(PsiField field, String fieldTypeName) {
        putClass(field, MockDataUtils.getNormalTypeValue(field, fieldTypeName));
        putComment(field, getComment(field));
    }

    private void handlePsiArrayType(PsiField field, PsiArrayType type) {
        PsiType deepType = type.getDeepComponentType();
        List<Object> list = new ArrayList<>();

        String deepTypeName = deepType.getPresentableText();
        if (deepType instanceof PsiPrimitiveType) {
            list.add(MockDataUtils.getPrimitiveValue(field, deepType));
            putComment(field);
        } else if (MockDataUtils.isNormalType(deepTypeName)) {
            list.add(MockDataUtils.getNormalTypeValue(field, deepTypeName));
            putComment(field);
        } else {
            list.add(handlePsiGeneralClass(field, deepType));
        }

        putClass(field, list);
    }

    private void handlePsiListType(PsiField field, PsiType type) {
        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
        if (iterableType == null) {
            return;
        }

        List<Object> list = new ArrayList<>();

        String classTypeName = iterableType.getCanonicalText();
        if (MockDataUtils.isNormalType(classTypeName)) {
            list.add(MockDataUtils.getNormalTypeValue(field, classTypeName));
            putComment(field);
        } else {
            list.add(handlePsiGeneralClass(field, iterableType));
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

        Object keyDefParam = getNormalTypeValue(field, keyTypeName);
        Object valueDefParam;

        if (MockDataUtils.isNormalType(valueTypeName)) {
            valueDefParam = getNormalTypeValue(field, valueTypeName);
            putComment(field);
        } else {
            boolean isSystemCls = isSystemClass(valueType);
            if (isSystemCls) {
                valueDefParam = new Object();
                putComment(field);
            } else {
                valueDefParam = handlePsiGeneralClass(field, valueType);
            }
        }

        putClass(field, Collections.singletonMap(keyDefParam, valueDefParam));
    }

    private void handlePsiEnumClass(PsiField field, PsiClass enumClass) {
        List<String> list = Arrays.stream(enumClass.getFields())
                .filter(PsiEnumConstant.class::isInstance)
                .map(PsiField::getName)
                .collect(Collectors.toList());

        putClass(field, list);
        putComment(field);
    }

    private void handlePsiObjectClass(PsiField field, PsiType classInType) {
        boolean isSystemCls = isSystemClass(classInType);

        //system class
        if (isSystemCls) {
            putClass(field, new Object());
            putComment(field);
        } else {
            putClass(field, handlePsiGeneralClass(field, classInType));
        }
    }

    private Map<String, Object> handlePsiGeneralClass(PsiField field, PsiType generalType) {
        if (generalType == null) {
            return Collections.emptyMap();
        }

        Pair<Map<String, Object>, Map<String, Object>> pair = convert(project, psiFile, parsedTypes, generalType);

        mergeComment(pair.second, getComment(field));
        putComment(field, pair.second);

        return pair.first;
    }

    private static boolean isSystemClass(PsiType classInType) {
        if (classInType == null) {
            return false;
        }

        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(classInType);
        if (psiClass == null) {
            return false;
        }

        PsiFile file = psiClass.getContainingFile();
        if (file instanceof PsiJavaFile) {
            String packageName = ((PsiJavaFile) file).getPackageName();
            return JsonUtils.isSystemClass(packageName);
        }

        return false;
    }

    private static void mergeComment(Map<String, Object> commentMap, String comment) {
        if (StringUtils.isEmpty(comment)) {
            return;
        }

        Map<String, Object> newMap = new LinkedHashMap<>(commentMap);

        commentMap.clear();
        commentMap.put(ClassResolver.KEY_COMMENT, comment);
        commentMap.putAll(newMap);
    }

    private static String getComment(PsiField field) {
        String text = null;
        if (PluginUtils.isKotlin(field)) {
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

    private static class PsiTypeCache {

        final Map<String, Object> classes;
        final Map<String, Object> comments;

        private PsiTypeCache(Map<String, Object> classes, Map<String, Object> comments) {
            this.classes = classes;
            this.comments = comments;
        }

        private Pair<Map<String, Object>, Map<String, Object>> resolve() {
            return Pair.create(classes, comments);
        }
    }
}