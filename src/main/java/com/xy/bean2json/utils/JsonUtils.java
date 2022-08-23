package com.xy.bean2json.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

/**
 * JsonUtils
 *
 * @author Created by gold on 2020/3/4 16:20
 */
public final class JsonUtils {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private JsonUtils() {
    }

    /**
     * toJson
     *
     * @param obj 对象
     * @return json
     */
    public static String toJson(Object obj) {
        return GSON.toJson(obj);
    }

    /**
     * 拷贝到粘贴板
     */
    public static void copyToClipboard(String text) {
        Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection selection = new StringSelection(text);
        clipboard.setContents(selection, selection);
    }

    /**
     * 是否为系统对象
     *
     * @param name 对象
     */
    public static boolean isSystemClass(String name) {
        return name.startsWith("java.") || name.startsWith("javax.");
    }
}
