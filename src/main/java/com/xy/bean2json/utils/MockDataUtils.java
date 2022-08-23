package com.xy.bean2json.utils;

import com.github.jsonzou.jmockdata.JMockData;
import com.intellij.psi.PsiExpression;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiType;
import com.intellij.psi.util.PsiTypesUtil;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import org.jetbrains.annotations.NonNls;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * MockDataUtils
 *
 * @author Created by gold on 2020/3/11 11:30
 */
public final class MockDataUtils {

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";
    @NonNls
    private static final Map<String, Object> NORMAL_TYPES = new HashMap<>();

    static {
        NORMAL_TYPES.put("Character", 'a');
        NORMAL_TYPES.put("Boolean", false);
        NORMAL_TYPES.put("Byte", 0);
        NORMAL_TYPES.put("Short", (short) 0);
        NORMAL_TYPES.put("Integer", 0);
        NORMAL_TYPES.put("Long", 0L);
        NORMAL_TYPES.put("Float", 0.0F);
        NORMAL_TYPES.put("Double", 0.0D);
        NORMAL_TYPES.put("String", "");
        NORMAL_TYPES.put("BigDecimal", 0.0);
        NORMAL_TYPES.put("BigInteger", 0.0);
        NORMAL_TYPES.put("Date", new SimpleDateFormat(PATTERN).format(new Date()));
        NORMAL_TYPES.put("Timestamp", System.currentTimeMillis());
        NORMAL_TYPES.put("LocalDate", LocalDate.now().toString());
        NORMAL_TYPES.put("LocalTime", LocalTime.now().toString());
        NORMAL_TYPES.put("LocalDateTime", LocalDateTime.now().toString());
    }

    private MockDataUtils() {
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

    public static Object getNormalTypeValue(PsiField field, String fieldTypeName) {
        if (ParamsManager.get().isDataType(DataType.DEFAULT_VALUE)) {
            PsiExpression initExpression = field.getInitializer();
            if (initExpression instanceof PsiLiteralExpression) {
                return ((PsiLiteralExpression) initExpression).getValue();
            }

            return NORMAL_TYPES.get(fieldTypeName);
        } else if (ParamsManager.get().isDataType(DataType.WRITE_TYPE)) {
            return fieldTypeName;
        }

        switch (fieldTypeName) {
            case "Boolean":
                return JMockData.mock(Boolean.class);
            case "Character":
                return JMockData.mock(Character.class);
            case "Byte":
                return JMockData.mock(Byte.class);
            case "Short":
                return JMockData.mock(Short.class);
            case "Integer":
                return JMockData.mock(Integer.class);
            case "Long":
                return JMockData.mock(Long.class);
            case "Float":
                return JMockData.mock(Float.class);
            case "Double":
                return JMockData.mock(Double.class);
            case "String":
                return JMockData.mock(String.class);
            case "BigDecimal":
                return JMockData.mock(BigDecimal.class);
            case "BigInteger":
                return JMockData.mock(BigInteger.class);
            default:
                return NORMAL_TYPES.get(fieldTypeName);
        }
    }

    /**
     * 获取基本类型mock值
     *
     * @param type 类型
     * @return data
     */
    public static Object getPrimitiveValue(PsiField field, PsiType type) {
        if (ParamsManager.get().isDataType(DataType.DEFAULT_VALUE)) {
            PsiExpression initExpression = field.getInitializer();
            if (initExpression instanceof PsiLiteralExpression) {
                return ((PsiLiteralExpression) initExpression).getValue();
            }

            return PsiTypesUtil.getDefaultValue(type);
        } else if (ParamsManager.get().isDataType(DataType.WRITE_TYPE)) {
            return type.getCanonicalText();
        }

        switch (type.getCanonicalText()) {
            case "boolean":
                return JMockData.mock(boolean.class);
            case "char":
                return JMockData.mock(char.class);
            case "byte":
                return JMockData.mock(byte.class);
            case "short":
                return JMockData.mock(short.class);
            case "int":
                return JMockData.mock(int.class);
            case "long":
                return JMockData.mock(long.class);
            case "float":
                return JMockData.mock(float.class);
            case "double":
                return JMockData.mock(double.class);
            default:
                return PsiTypesUtil.getDefaultValue(type);
        }
    }
}
