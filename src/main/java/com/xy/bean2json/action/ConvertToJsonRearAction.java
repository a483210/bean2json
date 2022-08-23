package com.xy.bean2json.action;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiType;
import com.xy.bean2json.base.BaseAction;
import com.xy.bean2json.helper.ClassResolver;
import com.xy.bean2json.utils.JsonUtils;
import com.xy.bean2json.utils.PluginUtils;
import org.apache.commons.lang.StringUtils;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * ConvertToJsonRearAction
 *
 * @author Created by gold on 2020/3/4 16:25
 */
public class ConvertToJsonRearAction extends BaseAction {

    @Override
    protected String actionPerformed(AnActionEvent e, Editor editor, PsiFile psiFile) {
        Project project = editor.getProject();

        PsiType selectedType = PluginUtils.parsePsiFile(project, psiFile);

        Pair<Map<String, Object>, Map<String, Object>> pair = ClassResolver.resolve(project, psiFile, selectedType);

        String json = mergeFiled(pair);

        JsonUtils.copyToClipboard(json);

        return psiFile.getName();
    }

    private String mergeFiled(Pair<Map<String, Object>, Map<String, Object>> pair) {
        Map<String, Object> classes = pair.first;
        Map<String, Object> comments = pair.second;

        String json = JsonUtils.toJson(classes);

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

                Map<String, Object> tempMap;
                if (stacks.isEmpty()) {
                    tempMap = comments;
                } else {
                    Stack stack = stacks.get(endIndex);

                    tempMap = stack.map;

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

                Object comment = tempMap.get(name);
                if (comment == null) {
                    continue;
                }

                if (comment instanceof Map) {
                    //noinspection unchecked
                    Map<String, Object> map = (Map<String, Object>) comment;

                    Object clsComment = map.get(ClassResolver.KEY_COMMENT);
                    if (clsComment != null) {
                        builder.append(" // ")
                                .append(clsComment);
                    }
                } else {
                    String strComment = String.valueOf(comment);
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
                    stack.map = comment instanceof Map ? (Map<String, Object>) comment : null;

                    stacks.add(stack);
                }
            }

            return builder.toString();
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }

    private String parseName(String line) {
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

    private int parseType(String line) {
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

    private boolean isEnd(String line, int type) {
        if (type == TYPE_LIST) {
            return "],".equals(line.trim()) || "]".equals(line.trim());
        } else if (type == TYPE_MAP) {
            return "},".equals(line.trim()) || "}".equals(line.trim());
        }
        return false;
    }

    private static final int TYPE_NON = -1, TYPE_LIST = 0, TYPE_MAP = 1;

    private static class Stack {
        private int type;
        private Map<String, Object> map;
    }
}
