package com.xy.bean2json.utils;

import com.intellij.psi.*;
import com.intellij.psi.util.PsiTypesUtil;
import com.xy.bean2json.manager.FakerManager;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;

/**
 * MockDataUtils
 *
 * @author Created by gold on 2020/3/11 11:30
 */
public final class MockDataUtils {
    private MockDataUtils() {
    }

    private static final String PATTERN = "yyyy-MM-dd HH:mm:ss";

    @Nullable
    public static Object resolveInitValue(Class<?> javaType, PsiExpression initExpression) {
        if (initExpression instanceof PsiLiteralExpression) {
            return ((PsiLiteralExpression) initExpression).getValue();
        } else if (initExpression instanceof PsiArrayInitializerExpression) {
            PsiExpression[] psiExpressions = ((PsiArrayInitializerExpression) initExpression).getInitializers();

            return resolveInitArrayValue(javaType, psiExpressions);
        } else if (initExpression instanceof PsiExpressionList) {
            PsiExpression[] psiExpressions = ((PsiExpressionList) initExpression).getExpressions();

            return resolveInitArrayValue(javaType, psiExpressions);
        } else if (initExpression instanceof PsiNewExpression) {
            PsiNewExpression psiNewExpression = (PsiNewExpression) initExpression;

            PsiArrayInitializerExpression psiArrayInitializerExpression = psiNewExpression.getArrayInitializer();
            if (psiArrayInitializerExpression != null) {
                return resolveInitValue(javaType, psiArrayInitializerExpression);
            }

            PsiExpressionList psiExpressionList = psiNewExpression.getArgumentList();
            if (psiExpressionList != null) {
                return resolveInitArrayValue(javaType, psiExpressionList.getExpressions());
            }

            return null;
        } else if (initExpression instanceof PsiMethodCallExpression) {
            PsiMethodCallExpression psiMethodCallExpression = (PsiMethodCallExpression) initExpression;

            PsiExpressionList psiExpressionList = psiMethodCallExpression.getArgumentList();

            return resolveInitArrayValue(javaType, psiExpressionList.getExpressions());
        }

        return null;
    }

    @Nullable
    private static Object resolveInitArrayValue(Class<?> javaType, PsiExpression[] psiExpressions) {
        int length = psiExpressions.length;

        if (length == 0) {
            return getNormalDefaultValue(javaType);
        } else if (length == 1) {
            return resolveInitValue(javaType, psiExpressions[0]);
        }

        Object array = Array.newInstance(javaType, length);
        for (int i = 0; i < length; i++) {
            try {
                Array.set(array, i, resolveInitValue(javaType, psiExpressions[i]));
            } catch (IllegalArgumentException e) {
                if (i == 1) {
                    return Array.get(array, 0);
                }

                Object newArray = Array.newInstance(javaType, i);

                //noinspection SuspiciousSystemArraycopy
                System.arraycopy(array, 0, newArray, 0, i);

                return newArray;
            }
        }

        return array;
    }

    /**
     * 获取对象类型mock值
     *
     * @param field    字段
     * @param typeName 类型名称
     * @param javaType java类型
     * @return data
     */
    @Nullable
    public static Object getNormalTypeValue(PsiField field, String typeName, Class<?> javaType) {
        if (ParamsManager.get().isDataType(DataType.DEFAULT_VALUE)) {
            PsiExpression initExpression = field.getInitializer();
            if (initExpression != null) {
                return resolveInitValue(javaType, initExpression);
            }

            return getNormalDefaultValue(javaType);
        } else if (ParamsManager.get().isDataType(DataType.WRITE_TYPE)) {
            return typeName;
        }

        return FakerManager.get().mockFaker(field.getName(), javaType);
    }

    private static Object getNormalDefaultValue(Class<?> javaType) {
        if (javaType == Object.class) {
            return new Object();
        } else if (javaType == Boolean.class) {
            return false;
        } else if (javaType == Character.class) {
            return 'a';
        } else if (javaType == Byte.class) {
            return (byte) 0;
        } else if (javaType == Short.class) {
            return (short) 0;
        } else if (javaType == Integer.class) {
            return 0;
        } else if (javaType == Long.class) {
            return 0L;
        } else if (javaType == Float.class) {
            return 0F;
        } else if (javaType == Double.class) {
            return 0D;
        } else if (javaType == String.class) {
            return "";
        } else if (javaType == BigDecimal.class) {
            return 0D;
        } else if (javaType == BigInteger.class) {
            return 0L;
        } else if (javaType == Date.class) {
            return new SimpleDateFormat(PATTERN).format(new Date());
        } else if (javaType == Timestamp.class) {
            return System.currentTimeMillis();
        } else if (javaType == LocalDate.class) {
            return LocalDate.now().toString();
        } else if (javaType == LocalTime.class) {
            return LocalTime.now().toString();
        } else if (javaType == LocalDateTime.class) {
            return LocalDateTime.now().toString();
        }

        return null;
    }

    /**
     * 获取基本类型mock值
     *
     * @param field    字段
     * @param psiType  psi类型
     * @param javaType java类型
     * @return data
     */
    @Nullable
    public static Object getPrimitiveValue(PsiField field, PsiType psiType, Class<?> javaType) {
        if (ParamsManager.get().isDataType(DataType.DEFAULT_VALUE)) {
            PsiExpression initExpression = field.getInitializer();
            if (initExpression != null) {
                return resolveInitValue(javaType, initExpression);
            }

            return PsiTypesUtil.getDefaultValue(psiType);
        } else if (ParamsManager.get().isDataType(DataType.WRITE_TYPE)) {
            return psiType.getCanonicalText();
        }

        if (javaType == void.class) {
            return PsiTypesUtil.getDefaultValue(psiType);
        }

        return FakerManager.get().mockFaker(field.getName(), javaType);
    }
}
