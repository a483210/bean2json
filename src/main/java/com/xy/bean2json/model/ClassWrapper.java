package com.xy.bean2json.model;

import java.util.Map;

/**
 * ClassWrapper
 *
 * @author Created by gold on 2022/8/25 10:03
 * @since 1.0.0
 */
public class ClassWrapper {

    public static ClassWrapper create(Map<String, FieldAttribute> fields, Map<String, CommentAttribute> comments) {
        return new ClassWrapper(fields, comments);
    }

    /**
     * 字段
     */
    private final Map<String, FieldAttribute> fields;
    /**
     * 评论
     */
    private final Map<String, CommentAttribute> comments;

    private ClassWrapper(Map<String, FieldAttribute> fields, Map<String, CommentAttribute> comments) {
        this.fields = fields;
        this.comments = comments;
    }

    public Map<String, FieldAttribute> getFields() {
        return fields;
    }

    public Map<String, CommentAttribute> getComments() {
        return comments;
    }
}
