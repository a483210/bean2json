package com.xy.bean2json;

import com.intellij.psi.PsiType;
import com.intellij.testFramework.LightProjectDescriptor;
import com.intellij.testFramework.fixtures.LightJavaCodeInsightFixtureTestCase;
import com.xy.bean2json.helper.ClassResolver;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.type.DataType;
import com.xy.bean2json.utils.PluginUtils;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * ClassResolverTest
 *
 * @author Created by gold on 2023/1/4 15:04
 * @since 1.0.0
 */
public class ClassResolverTest extends LightJavaCodeInsightFixtureTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    protected @NotNull LightProjectDescriptor getProjectDescriptor() {
        return JAVA_11;
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ParamsManager.get().setDataType(DataType.DEFAULT_VALUE);

        myFixture.configureByFile("JavaBean.java");
    }

    @Test
    public void testJsonField() {
        PsiType selectedType = PluginUtils.parsePsiFile(getFile());

        String json = ClassResolver.toJsonField(getFile(), selectedType);

        assertThat(json)
                .isNotNull();

        System.out.println(json);
    }

    @Test
    public void testJsonComment() {
        PsiType selectedType = PluginUtils.parsePsiFile(getFile());

        String json = ClassResolver.toJsonComment(getFile(), selectedType);

        assertThat(json)
                .isNotNull();

        System.out.println(json);
    }

    @Test
    public void testJsonReadable() {
        PsiType selectedType = PluginUtils.parsePsiFile(getFile());

        String json = ClassResolver.toJsonReadable(getFile(), selectedType);

        assertThat(json)
                .isNotNull();

        System.out.println(json);
    }
}