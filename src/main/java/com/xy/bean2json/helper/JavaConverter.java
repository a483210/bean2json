package com.xy.bean2json.helper;

import com.intellij.lang.jvm.JvmModifier;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.PsiClassReferenceType;
import com.intellij.psi.util.InheritanceUtil;
import com.intellij.psi.util.PsiUtil;
import com.xy.bean2json.model.ClassWrapper;
import com.xy.bean2json.model.CommentAttribute;
import com.xy.bean2json.model.FieldAttribute;
import com.xy.bean2json.model.FieldAttribute.FieldType;
import com.xy.bean2json.model.MapTuple;
import com.xy.bean2json.utils.JavaUtils;
import com.xy.bean2json.utils.MockDataUtils;
import com.xy.bean2json.utils.PluginUtils;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.*;
import java.util.stream.Collectors;

/**
 * JavaConverter
 *
 * @author Created by gold on 2020/12/11 17:04
 */
public class JavaConverter {

    protected static ClassWrapper convert(
            @NotNull PsiFile psiFile,
            @NotNull Map<PsiType, ClassWrapper> parsedTypes,
            @NotNull PsiType psiType) {
        return new JavaConverter(psiFile, parsedTypes).convert(psiType);
    }

    private final PsiFile psiFile;
    private final Map<PsiType, ClassWrapper> parsedTypes;

    private final Map<String, FieldAttribute> fields;
    private final Map<String, CommentAttribute> comments;

    private JavaConverter(PsiFile psiFile, Map<PsiType, ClassWrapper> parsedTypes) {
        this.psiFile = psiFile;
        this.parsedTypes = parsedTypes;

        this.fields = new LinkedHashMap<>();
        this.comments = new LinkedHashMap<>();
    }

    private void putField(PsiField field, FieldAttribute value) {
        if (value == null) {
            return;
        }

        fields.put(field.getName(), value);
    }

    private void putComment(PsiField field) {
        putComment(field, PluginUtils.resolveComment(field));
    }

    private void putComment(PsiField field, CommentAttribute comment) {
        comments.put(field.getName(), comment);
    }

    private ClassWrapper convert(PsiType psiType) {
        if (parsedTypes.containsKey(psiType)) {
            return parsedTypes.get(psiType);
        }

        ClassWrapper wrapper = ClassWrapper.create(fields, comments);

        //add to parsedTypes
        parsedTypes.put(psiType, wrapper);

        PsiClass psiClass = PsiUtil.resolveClassInClassTypeOnly(psiType);
        if (psiClass == null) {
            return wrapper;
        }

        //处理泛型
        Map<String, PsiType> table = resolveGenericTable(psiType, psiClass);

        for (PsiField field : psiClass.getAllFields()) {
            //noinspection UnstableApiUsage
            if (field.hasModifier(JvmModifier.STATIC) || field.hasModifier(JvmModifier.TRANSIENT)) {
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
                //primitive type
                handlePsiPrimitiveType(field, ((PsiPrimitiveType) type));
            } else if (type instanceof PsiArrayType) {
                //array type
                handlePsiArrayType(field, ((PsiArrayType) type), table);
            } else if (type instanceof PsiClassReferenceType) {
                //reference Type
                String fieldTypeName = type.getPresentableText();
                if (JavaUtils.isNormalType(fieldTypeName)) {
                    //normal Type
                    handlePsiNormalType(field, fieldTypeName);
                } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_LANG_ITERABLE)) {
                    //iterable type
                    handlePsiIterableType(field, type, table);
                } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_MAP)) {
                    //map type
                    handlePsiMapType(field, type, table);
                } else {
                    convertPsiOther(psiType, field, type, table);
                }
            } else {
                convertPsiOther(psiType, field, type, table);
            }
        }

        return wrapper;
    }

    private Map<String, PsiType> resolveGenericTable(PsiType psiType, PsiClass psiClass) {
        Map<String, PsiType> table = new HashMap<>();

        PsiClassType superPsiClassType = null;
        if (psiType instanceof PsiClassReferenceType) {
            superPsiClassType = (PsiClassReferenceType) psiType;
        }
        PsiClass superPsiClass = psiClass;

        do {
            PsiTypeParameter[] psiTypeParameters = superPsiClass.getTypeParameters();
            if (psiTypeParameters.length > 0 && superPsiClassType != null) {
                PsiType[] psiTypes = superPsiClassType.getParameters();
                for (int i = 0; i < psiTypeParameters.length; i++) {
                    table.put(psiTypeParameters[i].getName(), psiTypes[i]);
                }
            }

            PsiClassType[] superPsiClassTypes = superPsiClass.getSuperTypes();
            if (superPsiClassTypes.length > 0) {
                superPsiClassType = superPsiClassTypes[0];
            } else {
                superPsiClassType = null;
            }
            superPsiClass = superPsiClass.getSuperClass();
        } while (superPsiClass != null && !PluginUtils.isSystemClass(superPsiClass));

        return table;
    }

    private void convertPsiOther(PsiType psiType, PsiField field, PsiType type, Map<String, PsiType> table) {
        PsiClass otherClass = PsiUtil.resolveClassInClassTypeOnly(type);
        if (otherClass != null && otherClass.isEnum()) {
            //enum
            handlePsiEnumType(field, otherClass);
        } else {
            //class type
            if (!table.isEmpty() && psiType instanceof PsiClassReferenceType) {
                PsiType genericType = table.get(((PsiClassReferenceType) type).getName());
                if (genericType != null) {
                    type = genericType;
                }
            }

            handlePsiObjectType(field, type);
        }
    }

    private void handlePsiPrimitiveType(PsiField field, PsiPrimitiveType type) {
        putComment(field, PluginUtils.resolveComment(field));
        putField(field, resolvePsiPrimitiveType(field, type));
    }

    private void handlePsiNormalType(PsiField field, String typeName) {
        putComment(field, PluginUtils.resolveComment(field));
        putField(field, resolvePsiNormalType(field, typeName));
    }

    private void handlePsiArrayType(PsiField field, PsiArrayType type, Map<String, PsiType> table) {
        putComment(field);
        putField(field, resolvePsiArrayType(field, type, table));
    }

    private void handlePsiIterableType(PsiField field, PsiType type, Map<String, PsiType> table) {
        putComment(field);
        putField(field, resolvePsiIterableType(field, type, table));
    }

    private void handlePsiMapType(PsiField field, PsiType type, Map<String, PsiType> table) {
        putComment(field);
        putField(field, resolvePsiMapType(field, type, table));
    }

    private void handlePsiEnumType(PsiField field, PsiClass enumClass) {
        putComment(field);
        putField(field, resolvePsiEnumType(field, enumClass));
    }

    private void handlePsiObjectType(PsiField field, PsiType type) {
        //system class
        if (PluginUtils.isSystemType(type)) {
            putComment(field);
            putField(field, FieldAttribute.create(Object.class, new Object()));
        } else {
            putField(field, handlePsiGeneralClass(field, type));
        }
    }

    private FieldAttribute handlePsiGeneralClass(PsiField field, PsiType type) {
        if (type == null) {
            return FieldAttribute.create(FieldType.OBJECT, Collections.emptyMap());
        }

        ClassWrapper wrapper = convert(psiFile, parsedTypes, type);

        CommentAttribute fieldComment = PluginUtils.resolveComment(field);

        putComment(field, PluginUtils.mergeComment(wrapper.getComments(), fieldComment));

        return FieldAttribute.create(FieldType.OBJECT, wrapper.getFields());
    }

    private FieldAttribute resolvePsiGeneralType(PsiField field, PsiType type, Map<String, PsiType> table) {
        if (type instanceof PsiPrimitiveType) {
            //primitive type
            return resolvePsiPrimitiveType(field, ((PsiPrimitiveType) type));
        } else if (type instanceof PsiArrayType) {
            //array type
            return resolvePsiArrayType(field, ((PsiArrayType) type), table);
        } else if (type instanceof PsiClassReferenceType) {
            //reference Type
            String fieldTypeName = type.getPresentableText();
            if (JavaUtils.isNormalType(fieldTypeName)) {
                //normal Type
                return resolvePsiNormalType(field, fieldTypeName);
            } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_LANG_ITERABLE)) {
                //iterable type
                return resolvePsiIterableType(field, type, table);
            } else if (InheritanceUtil.isInheritor(type, CommonClassNames.JAVA_UTIL_MAP)) {
                //map type
                return resolvePsiMapType(field, type, table);
            } else {
                return resolvePsiOther(field, type);
            }
        } else {
            return resolvePsiOther(field, type);
        }
    }

    private FieldAttribute resolvePsiOther(PsiField field, PsiType type) {
        PsiClass enumClass = PsiUtil.resolveClassInClassTypeOnly(type);
        if (enumClass != null && enumClass.isEnum()) {
            //enum
            return resolvePsiEnumType(field, enumClass);
        }

        //system class
        if (PluginUtils.isSystemType(type)) {
            return FieldAttribute.create(Object.class, new Object());
        }

        //class type
        return handlePsiGeneralClass(field, type);
    }

    private FieldAttribute resolvePsiPrimitiveType(PsiField field, PsiPrimitiveType type) {
        Class<?> javaType = JavaUtils.getPrimitiveType(type);
        Object value = MockDataUtils.getPrimitiveValue(field, type, javaType);

        return FieldAttribute.create(javaType, value);
    }

    private FieldAttribute resolvePsiNormalType(PsiField field, String typeName) {
        Class<?> javaType = JavaUtils.getNormalType(typeName);
        Object value = MockDataUtils.getNormalTypeValue(field, typeName, javaType);

        return FieldAttribute.create(javaType, value);
    }

    private FieldAttribute resolvePsiArrayType(PsiField field, PsiArrayType type, Map<String, PsiType> table) {
        PsiType componentType = type.getComponentType();

        return FieldAttribute.create(FieldType.ARRAY, resolvePsiGeneralType(field, componentType, table));
    }

    private FieldAttribute resolvePsiIterableType(PsiField field, PsiType type, Map<String, PsiType> table) {
        PsiType iterableType = PsiUtil.extractIterableTypeParameter(type, false);
        if (iterableType == null) {
            throw new IllegalArgumentException("cannot parse iterable type");
        }

        if (!table.isEmpty() && iterableType instanceof PsiClassReferenceType) {
            PsiType genericType = table.get(((PsiClassReferenceType) iterableType).getName());
            if (genericType != null) {
                iterableType = genericType;
            }
        }

        return FieldAttribute.create(FieldType.ITERABLE, resolvePsiGeneralType(field, iterableType, table));
    }

    private FieldAttribute resolvePsiMapType(PsiField field, PsiType type, Map<String, PsiType> table) {
        PsiType keyType = PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_MAP, 0, false);
        PsiType valueType = PsiUtil.substituteTypeParameter(type, CommonClassNames.JAVA_UTIL_MAP, 1, false);
        if (keyType == null || valueType == null) {
            throw new NullPointerException("map type null");
        }

        String keyTypeName = keyType.getPresentableText();
        if (!JavaUtils.isNormalType(keyTypeName)) {
            throw new IllegalStateException("map key unsupported types");
        }

        FieldAttribute keyDefParam = resolvePsiNormalType(field, keyTypeName);

        if (!table.isEmpty() && valueType instanceof PsiClassReferenceType) {
            PsiType genericType = table.get(((PsiClassReferenceType) valueType).getName());
            if (genericType != null) {
                valueType = genericType;
            }
        }
        FieldAttribute valueDefParam = resolvePsiGeneralType(field, valueType, table);

        return FieldAttribute.create(FieldType.MAP, MapTuple.create(keyDefParam, valueDefParam));
    }

    private FieldAttribute resolvePsiEnumType(PsiField field, PsiClass enumClass) {
        List<String> list = Arrays.stream(enumClass.getFields())
                .filter(PsiEnumConstant.class::isInstance)
                .map(PsiField::getName)
                .collect(Collectors.toList());

        Type javaType = new ParameterizedType() {
            @Override
            public Type[] getActualTypeArguments() {
                return new Type[]{String.class};
            }

            @Override
            public Type getRawType() {
                return List.class;
            }

            @Override
            public Type getOwnerType() {
                return null;
            }
        };

        return FieldAttribute.create(javaType, list);
    }
}