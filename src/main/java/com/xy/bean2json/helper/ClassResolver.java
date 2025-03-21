package com.xy.bean2json.helper;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.xy.bean2json.error.ConvertException;
import com.xy.bean2json.model.ClassWrapper;
import com.xy.bean2json.model.CommentAttribute;
import com.xy.bean2json.model.FieldAttribute;
import com.xy.bean2json.model.MapTuple;
import com.xy.bean2json.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.*;

/**
 * ClassResolver
 *
 * @author Created by gold on 2022/8/18 18:17
 * @since 1.0.0
 */
public final class ClassResolver {
    private ClassResolver() {
    }

    /**
     * 解析为json
     *
     * @param psiFile 文件
     * @param psiType type
     * @return json
     */
    public static String toJsonField(PsiFile psiFile, PsiType psiType) {
        return toJsonField(resolveClass(psiFile, psiType));
    }

    private static String toJsonField(Map<String, FieldAttribute> fields) {
        Map<String, Object> map = toMap(fields);

        return JsonUtils.toJsonPretty(map);
    }

    private static Map<String, Object> toMap(Map<String, FieldAttribute> fields) {
        Map<String, Object> map = new LinkedHashMap<>(fields.size());

        fields.forEach((key, attribute) -> map.put(key, toObject(attribute)));

        return map;
    }

    @SuppressWarnings("unchecked")
    private static Object toObject(FieldAttribute attribute) {
        switch (attribute.getType()) {
            case CLASS:
                return attribute.getValue();
            case OBJECT:
                return toMap((Map<String, FieldAttribute>) attribute.getValue());
            case ARRAY:
            case ITERABLE:
                FieldAttribute arrayAttribute = (FieldAttribute) attribute.getValue();
                Object object = arrayAttribute.getValue();
                if (object.getClass().isArray()) {
                    return toObject(arrayAttribute);
                } else {
                    return List.of(toObject(arrayAttribute));
                }
            case MAP:
                MapTuple mapTuple = (MapTuple) attribute.getValue();
                FieldAttribute keyAttribute = mapTuple.getKey();

                //key只支持基本类型
                if (keyAttribute.getType() != FieldAttribute.FieldType.CLASS) {
                    throw new IllegalStateException("map key unsupported types");
                }
                return Map.of(keyAttribute.getValue(), toObject(mapTuple.getValue()));
            default:
                throw new IllegalStateException("unsupported types");
        }
    }

    /**
     * 解析为json
     *
     * @param psiFile 文件
     * @param psiType type
     * @return json
     */
    public static String toJsonComment(PsiFile psiFile, PsiType psiType) {
        return toJsonComment(resolveComment(psiFile, psiType));
    }

    private static String toJsonComment(Map<String, CommentAttribute> comments) {
        Map<String, Object> map = toCommentMap(comments);

        return JsonUtils.toJsonPretty(map);
    }

    private static Map<String, Object> toCommentMap(Map<String, CommentAttribute> comments) {
        Map<String, Object> map = new LinkedHashMap<>(comments.size());

        comments.forEach((key, attribute) -> {
            Object value = toCommentObject(attribute);
            if (value != null) {
                map.put(key, toCommentObject(attribute));
            }
        });
        return map;
    }

    @SuppressWarnings("unchecked")
    private static Object toCommentObject(CommentAttribute attribute) {
        switch (attribute.getType()) {
            case TEXT:
                String value = (String) attribute.getValue();
                if (StringUtils.isEmpty(value)) {
                    return "";
                }

                return value;
            case OBJECT:
                return toCommentMap((Map<String, CommentAttribute>) attribute.getValue());
            default:
                throw new IllegalStateException("unsupported types");
        }
    }

    /**
     * 解析为json并且合并注释
     *
     * @param psiFile 文件
     * @param psiType type
     * @return json
     */
    public static String toJsonReadable(PsiFile psiFile, PsiType psiType) {
        ClassWrapper wrapper = resolve(psiFile, psiType);
        if (wrapper == null) {
            return "{}";
        }

        Map<String, FieldAttribute> fields = wrapper.getFields();
        Map<String, CommentAttribute> comments = wrapper.getComments();

        String json = toJsonField(fields);

        try (ByteArrayInputStream arrayIs = new ByteArrayInputStream(json.getBytes());
             InputStreamReader inputSr = new InputStreamReader(arrayIs);
             BufferedReader br = new BufferedReader(inputSr)) {

            StringBuilder builder = new StringBuilder();

            List<Stack> stacks = new ArrayList<>();

            String line;
            while ((line = br.readLine()) != null) {
                builder.append("\r\n")
                        .append(line);

                int endIndex = stacks.size() - 1;

                Map<String, CommentAttribute> tempMap;
                if (stacks.isEmpty()) {
                    tempMap = comments;
                } else {
                    Stack stack = stacks.get(endIndex);

                    tempMap = stack.comments;

                    if (isEnd(line, stack.type)) {
                        stacks.remove(endIndex);
                        continue;
                    }
                }

                if (tempMap == null) {
                    continue;
                }

                String name = parseName(line);
                if (StringUtils.isEmpty(name)) {
                    continue;
                }

                CommentAttribute comment = tempMap.get(name);
                if (comment == null) {
                    continue;
                }

                if (comment.getType() == CommentAttribute.CommentType.OBJECT) {
                    //noinspection unchecked
                    Map<String, CommentAttribute> map = (Map<String, CommentAttribute>) comment.getValue();

                    CommentAttribute clsComment = map.get(CommentAttribute.KEY_COMMENT);
                    if (clsComment != null) {
                        builder.append(" // ")
                                .append(clsComment.getValue());
                    }
                } else {
                    String strComment = (String) comment.getValue();
                    if (StringUtils.isEmpty(strComment)) {
                        continue;
                    }
                    builder.append(" // ")
                            .append(strComment);
                }

                int type = parseType(line);
                if (type != -1) {
                    Stack stack = new Stack();
                    stack.type = type;
                    //noinspection unchecked
                    stack.comments = comment.getType() == CommentAttribute.CommentType.OBJECT ? (Map<String, CommentAttribute>) comment.getValue() : null;

                    stacks.add(stack);
                }
            }

            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private static String parseName(String line) {
        int index = -1;
        for (int i = 0, length = line.length(); i < length; i++) {
            char c = line.charAt(i);
            if (c == '"') {
                if (index != -1) {
                    return line.substring(index + 1, i);
                } else {
                    index = i;
                }
            }
        }

        return null;
    }

    private static final int TYPE_NON = -1, TYPE_LIST = 0, TYPE_MAP = 1;

    private static int parseType(String line) {
        int type = TYPE_NON;
        for (int i = line.length() - 1; i >= 0; i--) {
            char c = line.charAt(i);
            if (c == '[') {
                type = TYPE_LIST;
            } else if (c == '{') {
                type = TYPE_MAP;
            } else if (c == ':' && type != TYPE_NON) {
                return type;
            } else if (c != ' ') {
                return TYPE_NON;
            }
        }

        if ("{".equals(line.trim())) {
            return TYPE_MAP;
        } else if ("[".equals(line.trim())) {
            return TYPE_LIST;
        }

        return TYPE_NON;
    }

    private static boolean isEnd(String line, int type) {
        String finalLine = line.trim();
        if (type == TYPE_LIST) {
            return "],".equals(finalLine) || "]".equals(finalLine);
        } else if (type == TYPE_MAP) {
            return "},".equals(finalLine) || "}".equals(finalLine);
        }
        return false;
    }

    private static class Stack {
        private int type;
        private Map<String, CommentAttribute> comments;
    }

    /**
     * 解析类
     *
     * @param psiFile 文件
     * @param psiType type
     * @return map
     */
    public static Map<String, FieldAttribute> resolveClass(PsiFile psiFile, PsiType psiType) {
        ClassWrapper wrapper = resolve(psiFile, psiType);
        if (wrapper == null) {
            return Collections.emptyMap();
        }

        return wrapper.getFields();
    }

    /**
     * 解析注释
     *
     * @param psiFile 文件
     * @param psiType type
     * @return map
     */
    public static Map<String, CommentAttribute> resolveComment(PsiFile psiFile, PsiType psiType) {
        ClassWrapper wrapper = resolve(psiFile, psiType);
        if (wrapper == null) {
            return Collections.emptyMap();
        }

        return wrapper.getComments();
    }

    /**
     * 解析类
     *
     * @param psiFile 文件
     * @param psiType type
     * @return params
     */
    public static ClassWrapper resolve(PsiFile psiFile, PsiType psiType) {
        if (psiType == null) {
            throw new ConvertException();
        }

        return JavaConverter.convert(psiFile, new LinkedHashMap<>(), psiType);
    }
}