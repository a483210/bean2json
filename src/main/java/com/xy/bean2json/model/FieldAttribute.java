package com.xy.bean2json.model;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * FieldAttribute
 *
 * @author Created by gold on 2022/8/25 09:55
 * @since 1.0.0
 */
public class FieldAttribute {

    public static FieldAttribute create(Type fieldType, Object value) {
        return new FieldAttribute(FieldType.CLASS, fieldType, value);
    }

    public static FieldAttribute create(FieldType type, Object value) {
        return new FieldAttribute(type, null, value);
    }

    /**
     * 字段种类
     */
    private final FieldType type;
    /**
     * 字段类型
     */
    private final Type javaType;
    /**
     * 值
     */
    private final Object value;

    private FieldAttribute(FieldType type, Type javaType, Object value) {
        this.type = type;
        this.javaType = javaType;
        this.value = value;
    }

    public FieldType getType() {
        return type;
    }

    public Type getJavaType() {
        return javaType;
    }

    public Object getValue() {
        return value;
    }

    public enum FieldType {

        /**
         * 基本类型
         */
        CLASS,

        /**
         * 嵌套对象
         * <p>
         * {@link Map}
         */
        OBJECT,

        /**
         * 数组
         * <p>
         * {@link FieldAttribute}
         */
        ARRAY,

        /**
         * 列表
         * <p>
         * {@link FieldAttribute}
         */
        ITERABLE,

        /**
         * 映射
         * <p>
         * {@link MapTuple}
         */
        MAP

    }
}
