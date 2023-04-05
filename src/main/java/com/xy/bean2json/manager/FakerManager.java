package com.xy.bean2json.manager;

import com.github.jsonzou.jmockdata.JMockData;
import com.xy.bean2json.utils.FakerUtils;
import com.xy.bean2json.utils.FakerUtils.ProviderInfo;
import org.ahocorasick.trie.Trie;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;

/**
 * FakerUtils
 *
 * @author Created by gold on 2023/4/4 11:23
 * @since 1.0.0
 */
public final class FakerManager {

    private static volatile FakerManager instance;

    public static FakerManager get() {
        if (instance == null) {
            synchronized (FakerManager.class) {
                if (instance == null) {
                    instance = new FakerManager();
                }
            }
        }
        return instance;
    }

    private final Map<String, ProviderInfo> providerMap;
    private final Trie trie;

    private FakerManager() {
        this.providerMap = FakerUtils.createProviderMap();

        this.trie = Trie.builder()
                .ignoreCase()
                .addKeywords(providerMap.keySet())
                .build();
    }

    /**
     * 生成假数据
     *
     * @param fieldName 字段名
     * @param javaType  字段类型
     * @return 生成的数据
     */
    public Object mockFaker(String fieldName, Class<?> javaType) {
        String matchName = matchWordsWithAC(fieldName);
        if (matchName == null) {
            String[] splits = StringUtils.splitByCharacterTypeCamelCase(fieldName);
            for (String split : splits) {
                matchName = matchWordsWithAC(split);
                if (matchName != null) {
                    break;
                }
            }
        }

        if (matchName != null) {
            ProviderInfo info = providerMap.get(matchName);
            if (info != null) {
                Object res = info.mockData(javaType);
                if (res != null) {
                    return res;
                }
            }
        }

        return JMockData.mock(javaType);
    }

    /**
     * 根据文本匹配关键字
     *
     * @param inputString 文本
     * @return 匹配到的关键字
     */
    public String matchWordsWithAC(String inputString) {
        return FakerUtils.matchWordsWithAC(trie, inputString);
    }
}
