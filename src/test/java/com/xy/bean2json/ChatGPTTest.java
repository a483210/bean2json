package com.xy.bean2json;

import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.xy.bean2json.chatgpt.JsonChatGPT;
import com.xy.bean2json.http.ProxyAddress;
import org.junit.Test;

/**
 * ChatGPTTest
 *
 * @author Created by gold on 2023/4/7 09:57
 * @since 1.0.0
 */
public class ChatGPTTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Test
    public void test() {
        JsonChatGPT gpt = new JsonChatGPT(
                "",
                "gpt-3.5-turbo",
                new ProxyAddress("127.0.0.1", 7890));

        String json = "{" +
                "\"name\": \"asd\", // 名称" +
                "\"age\": 0, // 年龄" +
                "\"sex\": 0, // 性别" +
                "\"birthday\": 10 // 生日时间戳" +
                "}";

        System.out.println(gpt.completion(json, 1f));
    }
}
