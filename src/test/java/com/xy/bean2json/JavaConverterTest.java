package com.xy.bean2json;

import com.intellij.psi.PsiType;
import com.intellij.testFramework.fixtures.BasePlatformTestCase;
import com.xy.bean2json.helper.ClassResolver;
import com.xy.bean2json.manager.ParamsManager;
import com.xy.bean2json.model.ClassWrapper;
import com.xy.bean2json.model.CommentAttribute;
import com.xy.bean2json.model.CommentAttribute.CommentType;
import com.xy.bean2json.model.FieldAttribute;
import com.xy.bean2json.model.FieldAttribute.FieldType;
import com.xy.bean2json.model.MapTuple;
import com.xy.bean2json.type.DataType;
import com.xy.bean2json.utils.PluginUtils;
import org.junit.Test;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Date;
import java.util.Map;
import java.util.function.Consumer;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;

/**
 * JavaConverterTest
 *
 * @author Created by gold on 2022/8/25 13:26
 * @since 1.0.0
 */
public class JavaConverterTest extends BasePlatformTestCase {

    @Override
    protected String getTestDataPath() {
        return "src/test/testData";
    }

    @Override
    public void setUp() throws Exception {
        super.setUp();

        ParamsManager.get().setDataType(DataType.DEFAULT_VALUE);

        myFixture.configureByFile("JavaBean.java");
    }

    @Test
    public void testParsingTestData() {
        PsiType selectedType = PluginUtils.parsePsiFile(myFixture.getFile());

        ClassWrapper wrapper = ClassResolver.resolve(myFixture.getFile(), selectedType);

        Map<String, FieldAttribute> fields = wrapper.getFields();

        assertThat(fields)
                .doesNotContainKeys("staticParam", "transientParam", "privateStringByNotGet");

        assertFieldForClass(fields.get("privateString"), String.class, "pri");

        assertFieldForClass(fields.get("priBoolean"), boolean.class, false);
        assertFieldForClass(fields.get("priByte"), byte.class, (byte) 0);
        assertFieldForClass(fields.get("priShort"), short.class, (short) 0);
        assertFieldForClass(fields.get("priInt"), int.class, 0);
        assertFieldForClass(fields.get("priLong"), long.class, 0L);
        assertFieldForClass(fields.get("priFloat"), float.class, 0F);
        assertFieldForClass(fields.get("priDouble"), double.class, 0D);

        assertFieldForClass(fields.get("objBoolean"), Boolean.class, false);
        assertFieldForClass(fields.get("objByte"), Byte.class, (byte) 0);
        assertFieldForClass(fields.get("objShort"), Short.class, (short) 0);
        assertFieldForClass(fields.get("objInt"), Integer.class, 0);
        assertFieldForClass(fields.get("objLong"), Long.class, 0L);
        assertFieldForClass(fields.get("objFloat"), Float.class, 0F);
        assertFieldForClass(fields.get("objDouble"), Double.class, 0D);
        assertFieldForClass(fields.get("objString"), String.class, "");
        assertFieldForClass(fields.get("objBigDecimal"), BigDecimal.class, 0D);
        assertFieldForClass(fields.get("objBigInteger"), BigInteger.class, 0L);

        assertFieldForClass(fields.get("objDate"), Date.class, null);
        assertFieldForClass(fields.get("objTimestamp"), Timestamp.class, null);
        assertFieldForClass(fields.get("objLocalDate"), LocalDate.class, null);
        assertFieldForClass(fields.get("objLocalTime"), LocalTime.class, null);
        assertFieldForClass(fields.get("objLocalDateTime"), LocalDateTime.class, null);

        assertFieldForArray(fields.get("priArrayBoolean"),
                it -> assertFieldForClass(it, boolean.class, true));
        assertFieldForArray(fields.get("objArrayBoolean"),
                it -> assertFieldForClass(it, Boolean.class, new Boolean[]{true, false}));
        assertFieldForArray(fields.get("objArrayString"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForArray(fields.get("objArrayStringDef"),
                it -> assertFieldForClass(it, String.class, "str"));

        assertFieldForIterable(fields.get("iterable"),
                it -> assertFieldForClass(it, String.class, new String[]{"str1", "str2"}));
        assertFieldForIterable(fields.get("collection"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForIterable(fields.get("set"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForIterable(fields.get("queue"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForIterable(fields.get("list"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldMap(fields.get("map"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertFieldForClass(it, String.class, ""));

        assertFieldForIterable(fields.get("arrayList"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForIterable(fields.get("hashSet"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldForIterable(fields.get("linkedQueue"),
                it -> assertFieldForClass(it, String.class, ""));
        assertFieldMap(fields.get("hashMap"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertFieldForClass(it, String.class, ""));

        assertFieldForArray(fields.get("arrayArray"),
                it -> assertFieldForArray(it, attribute -> assertFieldForClass(attribute, String.class, "")));
        assertFieldForArray(fields.get("arrayArrayDef"),
                it -> assertFieldForArray(it, attribute -> assertFieldForClass(attribute, String.class, "str")));

        assertFieldForIterable(fields.get("listArray"),
                it -> assertFieldForArray(it, attribute -> assertFieldForClass(attribute, String.class, "")));

        assertFieldMap(fields.get("mapArray"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertFieldForArray(it, attribute -> assertFieldForClass(attribute, String.class, "")));

        assertFieldForNormalBean(fields.get("normalBean"));
        //暂不支持复杂类型初始化参数
        assertFieldForArray(fields.get("arrayBean"), this::assertFieldForNormalBean);
        assertFieldForIterable(fields.get("iterableBean"), this::assertFieldForNormalBean);
        assertFieldMap(fields.get("mapBean"),
                it -> assertFieldForClass(it, String.class, ""),
                this::assertFieldForNormalBean);
        assertFieldMap(fields.get("mapArrayBean"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertField(it, FieldType.ARRAY, this::assertFieldForNormalBean));

        assertFieldForComplexBean(fields.get("complexBean"));
        assertFieldForArray(fields.get("arrayComplexBean"), this::assertFieldForComplexBean);
        assertFieldForIterable(fields.get("iterableComplexBean"), this::assertFieldForComplexBean);
        assertFieldMap(fields.get("mapComplexBean"),
                it -> assertFieldForClass(it, String.class, ""),
                this::assertFieldForComplexBean);
        assertFieldMap(fields.get("mapArrayComplexBean"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertField(it, FieldType.ARRAY, this::assertFieldForComplexBean));

        assertFieldForGenericBean(fields.get("genericBean"));
        assertFieldForArray(fields.get("arrayGenericBean"), this::assertFieldForGenericBean);
        assertFieldForIterable(fields.get("iterableGenericBean"), this::assertFieldForGenericBean);
        assertFieldMap(fields.get("mapGenericBean"),
                it -> assertFieldForClass(it, String.class, ""),
                this::assertFieldForGenericBean);
        assertFieldMap(fields.get("mapArrayGenericBean"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertField(it, FieldType.ARRAY, this::assertFieldForGenericBean));
        assertFieldForArray(fields.get("mapArrayGenericBeanArray"),
                it -> assertFieldMap(it,
                        c -> assertFieldForClass(c, String.class, ""),
                        c -> assertField(c, FieldType.ARRAY, this::assertFieldForGenericBean)));

        assertFieldForChildGenericBean(fields.get("childGenericBean"));
        assertFieldForArray(fields.get("arrayChildGenericBean"), this::assertFieldForChildGenericBean);
        assertFieldForIterable(fields.get("iterableChildGenericBean"), this::assertFieldForChildGenericBean);
        assertFieldMap(fields.get("mapChildGenericBean"),
                it -> assertFieldForClass(it, String.class, ""),
                this::assertFieldForChildGenericBean);
        assertFieldMap(fields.get("mapArrayChildGenericBean"),
                it -> assertFieldForClass(it, String.class, ""),
                it -> assertField(it, FieldType.ARRAY, this::assertFieldForChildGenericBean));
    }

    @Test
    public void testParsingTestDataByComment() {
        PsiType selectedType = PluginUtils.parsePsiFile(myFixture.getFile());

        ClassWrapper wrapper = ClassResolver.resolve(myFixture.getFile(), selectedType);

        Map<String, CommentAttribute> comments = wrapper.getComments();

        assertThat(comments)
                .doesNotContainKeys("staticParam", "transientParam", "privateStringByNotGet");

        assertCommentForText(comments.get("privateString"), "");

        assertCommentForText(comments.get("priBoolean"), "primitiveBoolean");
        assertCommentForText(comments.get("priByte"), "primitiveByte");
        assertCommentForText(comments.get("priShort"), "primitiveShort");
        assertCommentForText(comments.get("priInt"), "primitiveInt");
        assertCommentForText(comments.get("priLong"), "primitiveLong");
        assertCommentForText(comments.get("priFloat"), "primitiveFloat");
        assertCommentForText(comments.get("priDouble"), "primitiveDouble");

        assertCommentForText(comments.get("objBoolean"), "normalBoolean");
        assertCommentForText(comments.get("objByte"), "normalByte");
        assertCommentForText(comments.get("objShort"), "normalShort");
        assertCommentForText(comments.get("objInt"), "normalInt");
        assertCommentForText(comments.get("objLong"), "normalLong");
        assertCommentForText(comments.get("objFloat"), "normalFloat");
        assertCommentForText(comments.get("objDouble"), "normalDouble");
        assertCommentForText(comments.get("objString"), "normalString");
        assertCommentForText(comments.get("objBigDecimal"), "normalBigDecimal");
        assertCommentForText(comments.get("objBigInteger"), "normalBigInteger");

        assertCommentForText(comments.get("objDate"), "normalDate");
        assertCommentForText(comments.get("objTimestamp"), "normalTimestamp");
        assertCommentForText(comments.get("objLocalDate"), "normalLocalDate");
        assertCommentForText(comments.get("objLocalTime"), "normalLocalTime");
        assertCommentForText(comments.get("objLocalDateTime"), "normalLocalDateTime");

        assertCommentForText(comments.get("priArrayBoolean"), "arrayBoolean");
        assertCommentForText(comments.get("objArrayBoolean"), "arrayNormalBoolean");
        assertCommentForText(comments.get("objArrayString"), "arrayNormalString");
        assertCommentForText(comments.get("objArrayStringDef"), "arrayNormalStringDef");

        assertCommentForText(comments.get("iterable"), "iterableNormal");
        assertCommentForText(comments.get("collection"), "collectionNormal");
        assertCommentForText(comments.get("set"), "setNormal");
        assertCommentForText(comments.get("queue"), "queueNormal");
        assertCommentForText(comments.get("list"), "listNormal");
        assertCommentForText(comments.get("map"), "mapNormal");

        assertCommentForText(comments.get("arrayList"), "arrayListNormal");
        assertCommentForText(comments.get("hashSet"), "hashSetNormal");
        assertCommentForText(comments.get("linkedQueue"), "linkedListNormal");
        assertCommentForText(comments.get("hashMap"), "hashMapNormal");

        assertCommentForText(comments.get("arrayArray"), "arrayArrayNormal");
        assertCommentForText(comments.get("arrayArrayDef"), "arrayArrayNormalDef");
        assertCommentForText(comments.get("listArray"), "listArrayNormal");
        assertCommentForText(comments.get("mapArray"), "mapArrayNormal");

        assertCommentForObject(comments.get("normalBean"), "beanNormal",
                this::assertCommentForNormalBean);
        assertCommentForObject(comments.get("arrayBean"), "arrayBeanNormal",
                this::assertCommentForNormalBean);
        assertCommentForObject(comments.get("iterableBean"), "iterableBeanNormal",
                this::assertCommentForNormalBean);
        assertCommentForObject(comments.get("mapArrayBean"), "mapBeanArrayBeanNormal",
                this::assertCommentForNormalBean);

        assertCommentForObject(comments.get("complexBean"), "beanComplex",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("arrayComplexBean"), "arrayBeanComplex",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("iterableComplexBean"), "iterableBeanComplex",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("mapComplexBean"), "mapBeanComplex",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("mapArrayComplexBean"), "mapBeanArrayBeanComplex",
                this::assertCommentForComplexBean);

        assertCommentForObject(comments.get("genericBean"), "beanGeneric",
                this::assertCommentForGenericBean);
        assertCommentForObject(comments.get("arrayGenericBean"), "arrayBeanGeneric",
                this::assertCommentForGenericBean);
        assertCommentForObject(comments.get("iterableGenericBean"), "iterableBeanGeneric",
                this::assertCommentForGenericBean);
        assertCommentForObject(comments.get("mapGenericBean"), "mapBeanGeneric",
                this::assertCommentForGenericBean);
        assertCommentForObject(comments.get("mapArrayGenericBean"), "mapBeanArrayBeanGeneric",
                this::assertCommentForGenericBean);
        assertCommentForObject(comments.get("mapArrayGenericBeanArray"), "mapArrayBeanArrayBeanGenericArray",
                this::assertCommentForGenericBean);

        assertCommentForObject(comments.get("childGenericBean"), "beanChildGeneric",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("arrayChildGenericBean"), "arrayBeanChildGeneric",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("iterableChildGenericBean"), "iterableBeanChildGeneric",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("mapChildGenericBean"), "mapBeanChildGeneric",
                this::assertCommentForComplexBean);
        assertCommentForObject(comments.get("mapArrayChildGenericBean"), "mapBeanArrayBeanChildGeneric",
                this::assertCommentForComplexBean);
    }

    private void assertCommentForGenericBean(Map<String, CommentAttribute> map) {
        assertCommentForText(map.get("text"), "text");
        assertCommentForObject(map.get("bean"), "bean",
                this::assertCommentForComplexBean);
        assertCommentForObject(map.get("iterable"), "iterable",
                this::assertCommentForComplexBean);
        assertCommentForObject(map.get("map"), "map",
                this::assertCommentForComplexBean);
    }

    private void assertCommentForComplexBean(Map<String, CommentAttribute> map) {
        assertCommentForText(map.get("text"), "text");
        assertCommentForObject(map.get("bean"), "bean",
                this::assertCommentForNormalBean);
        assertCommentForObject(map.get("iterable"), "iterable",
                this::assertCommentForNormalBean);
        assertCommentForObject(map.get("map"), "map",
                this::assertCommentForNormalBean);
    }

    private void assertCommentForNormalBean(Map<String, CommentAttribute> map) {
        assertCommentForText(map.get("content"), "content");
    }

    private void assertFieldForNormalBean(FieldAttribute attribute) {
        assertFieldForObject(attribute,
                it -> assertFieldForClass(it.get("content"), String.class, ""));
    }

    private void assertFieldForComplexBean(FieldAttribute attribute) {
        assertFieldForObject(attribute,
                it -> {
                    assertFieldForClass(it.get("text"), String.class, "");
                    assertFieldForNormalBean(it.get("bean"));
                    assertFieldForIterable(it.get("iterable"), this::assertFieldForNormalBean);
                    assertFieldMap(it.get("map"),
                            c -> assertFieldForClass(c, String.class, ""),
                            this::assertFieldForNormalBean);
                });
    }

    private void assertFieldForGenericBean(FieldAttribute attribute) {
        assertFieldForObject(attribute,
                it -> {
                    assertFieldForClass(it.get("text"), String.class, "");
                    assertFieldForComplexBean(it.get("bean"));
                    assertFieldForIterable(it.get("iterable"), this::assertFieldForComplexBean);
                    assertFieldMap(it.get("map"),
                            c -> assertFieldForClass(c, String.class, ""),
                            this::assertFieldForComplexBean);
                });
    }

    private void assertFieldForChildGenericBean(FieldAttribute attribute) {
        assertFieldForObject(attribute,
                it -> {
                    assertFieldForClass(it.get("text"), String.class, "");
                    assertFieldForNormalBean(it.get("bean"));
                    assertFieldForIterable(it.get("iterable"), this::assertFieldForNormalBean);
                    assertFieldMap(it.get("map"),
                            c -> assertFieldForClass(c, String.class, ""),
                            this::assertFieldForNormalBean);
                });
    }

    private void assertFieldForClass(FieldAttribute attribute, Type javaType, Object value) {
        assertField(attribute, FieldType.CLASS, javaType, value);
    }

    private void assertFieldForArray(FieldAttribute attribute, Consumer<FieldAttribute> consumer) {
        assertField(attribute, FieldType.ARRAY, consumer);
    }

    private void assertFieldForIterable(FieldAttribute attribute, Consumer<FieldAttribute> consumer) {
        assertField(attribute, FieldType.ITERABLE, consumer);
    }

    private void assertField(FieldAttribute attribute, FieldType fieldType, Type javaType, Object value) {
        assertThat(attribute)
                .isNotNull();

        assertThat(attribute.getType())
                .isEqualTo(fieldType);
        assertThat(attribute.getJavaType())
                .isEqualTo(javaType);

        if (value != null) {
            assertThat(attribute.getValue())
                    .isEqualTo(value);
        } else {
            assertThat(attribute.getValue())
                    .isNotNull();
        }
    }

    private void assertField(FieldAttribute attribute, FieldType fieldType, Consumer<FieldAttribute> consumer) {
        assertThat(attribute)
                .isNotNull();

        assertThat(attribute.getType())
                .isEqualTo(fieldType);
        assertThat(attribute.getJavaType())
                .isNull();

        Object value = attribute.getValue();
        assertThat(value)
                .isNotNull()
                .isInstanceOf(FieldAttribute.class);

        consumer.accept(((FieldAttribute) value));
    }

    private void assertFieldMap(FieldAttribute attribute,
                                Consumer<FieldAttribute> keyConsumer,
                                Consumer<FieldAttribute> valueConsumer) {
        assertThat(attribute)
                .isNotNull();

        assertThat(attribute.getType())
                .isEqualTo(FieldType.MAP);
        assertThat(attribute.getJavaType())
                .isNull();

        Object value = attribute.getValue();
        assertThat(value)
                .isNotNull()
                .isInstanceOf(MapTuple.class);

        MapTuple mapTuple = ((MapTuple) value);

        keyConsumer.accept(mapTuple.getKey());
        valueConsumer.accept(mapTuple.getValue());
    }

    private void assertFieldForObject(FieldAttribute attribute, Consumer<Map<String, FieldAttribute>> consumer) {
        assertThat(attribute)
                .isNotNull();

        assertThat(attribute.getType())
                .isEqualTo(FieldType.OBJECT);
        assertThat(attribute.getJavaType())
                .isNull();

        Object value = attribute.getValue();
        assertThat(value)
                .isNotNull()
                .isInstanceOf(Map.class);

        //noinspection unchecked
        Map<String, FieldAttribute> childrenAttributes = ((Map<String, FieldAttribute>) value);

        assertThat(childrenAttributes)
                .isNotEmpty();

        consumer.accept(childrenAttributes);
    }

    private void assertCommentForText(CommentAttribute attribute, String value) {
        assertComment(attribute, CommentType.TEXT, value);
    }

    private void assertComment(CommentAttribute attribute, CommentType commentType, Object value) {
        assertThat(attribute)
                .isNotNull();

        assertThat(attribute.getType())
                .isEqualTo(commentType);
        assertThat(attribute.getValue())
                .isEqualTo(value);
    }

    private void assertCommentForObject(CommentAttribute commentAttribute, String atComment,
                                        Consumer<Map<String, CommentAttribute>> consumer) {
        assertThat(commentAttribute)
                .isNotNull();

        assertThat(commentAttribute.getType())
                .isEqualTo(CommentType.OBJECT);

        Object commentValue = commentAttribute.getValue();
        assertThat(commentValue)
                .isNotNull()
                .isInstanceOf(Map.class);

        //noinspection unchecked
        Map<String, CommentAttribute> childrenCommentAttributes = ((Map<String, CommentAttribute>) commentValue);

        assertThat(childrenCommentAttributes)
                .isNotEmpty();

        assertCommentForText(childrenCommentAttributes.get("@comment"), atComment);

        consumer.accept(childrenCommentAttributes);
    }
}