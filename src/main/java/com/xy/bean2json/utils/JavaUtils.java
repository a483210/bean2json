package com.xy.bean2json.utils;

import com.intellij.psi.PsiType;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JavaUtils
 *
 * @author Created by gold on 2022/8/25 11:02
 * @since 1.0.0
 */
public final class JavaUtils {
    private JavaUtils() {
    }

    @NonNls
    private static final Map<String, Class<?>> NORMAL_TYPES = new HashMap<>();

    static {
        NORMAL_TYPES.put("Object", Object.class);
        NORMAL_TYPES.put("Boolean", Boolean.class);
        NORMAL_TYPES.put("Character", Character.class);
        NORMAL_TYPES.put("Byte", Byte.class);
        NORMAL_TYPES.put("Short", Short.class);
        NORMAL_TYPES.put("Integer", Integer.class);
        NORMAL_TYPES.put("Long", Long.class);
        NORMAL_TYPES.put("Float", Float.class);
        NORMAL_TYPES.put("Double", Double.class);
        NORMAL_TYPES.put("String", String.class);
        NORMAL_TYPES.put("BigDecimal", BigDecimal.class);
        NORMAL_TYPES.put("BigInteger", BigInteger.class);
        NORMAL_TYPES.put("Date", Date.class);
        NORMAL_TYPES.put("Timestamp", Timestamp.class);
        NORMAL_TYPES.put("LocalDate", LocalDate.class);
        NORMAL_TYPES.put("LocalTime", LocalTime.class);
        NORMAL_TYPES.put("LocalDateTime", LocalDateTime.class);
    }

    /**
     * 是否为系统对象
     *
     * @param name 对象
     */
    public static boolean isSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.");
    }

    /**
     * 判断是否为基本对象
     *
     * @param typeName 类型名称
     * @return bool
     */
    public static boolean isNormalType(String typeName) {
        return NORMAL_TYPES.containsKey(typeName);
    }

    /**
     * 获取对象类型mock值
     *
     * @param typeName 类型名称
     * @return data
     */
    @Nullable
    public static Class<?> getNormalType(String typeName) {
        return NORMAL_TYPES.get(typeName);
    }

    /**
     * 获取基本类型mock值
     *
     * @param psiType 类型
     * @return data
     */
    @NotNull
    public static Class<?> getPrimitiveType(PsiType psiType) {
        switch (psiType.getCanonicalText()) {
            case "boolean":
                return boolean.class;
            case "char":
                return char.class;
            case "byte":
                return byte.class;
            case "short":
                return short.class;
            case "int":
                return int.class;
            case "long":
                return long.class;
            case "float":
                return float.class;
            case "double":
                return double.class;
            default:
                return void.class;
        }
    }
}
