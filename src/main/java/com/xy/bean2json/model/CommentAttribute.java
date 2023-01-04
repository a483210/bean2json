package com.xy.bean2json.model;

import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * CommentAttribute
 *
 * @author Created by gold on 2022/8/25 09:57
 * @since 1.0.0
 */
public class CommentAttribute {

    public static CommentAttribute empty() {
        return create("");
    }

    public static CommentAttribute create(String value) {
        return new CommentAttribute(CommentType.TEXT, value);
    }

    public static CommentAttribute create(Map<String, CommentAttribute> comments) {
        return new CommentAttribute(CommentType.OBJECT, comments);
    }

    public static final String KEY_COMMENT = "@comment";

    /**
     * 类型
     */
    private final CommentType type;
    /**
     * 值
     */
    private final Object value;

    private CommentAttribute(CommentType type, Object value) {
        this.type = type;
        this.value = value;
    }

    public CommentType getType() {
        return type;
    }

    public Object getValue() {
        return value;
    }

    public boolean isEmpty() {
        return type == CommentType.TEXT && StringUtils.isEmpty(value.toString());
    }

    public enum CommentType {

        /**
         * 注释
         */
        TEXT,

        /**
         * 嵌套对象
         */
        OBJECT

    }
}
