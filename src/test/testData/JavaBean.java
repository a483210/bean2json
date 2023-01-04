import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;

/**
 * java对象
 *
 * @author Created by gold on 2022/8/25 14:26
 * @since 1.0.0
 */
public class JavaBean {

    /**
     * 静态参数
     */
    public static String staticParam;
    /**
     * 非持久化参数
     */
    public transient String transientParam;

    //normalStringPrivate
    private String privateString = "pri";
    //normalStringByNotGetPrivate
    private String privateStringByNotGet;

    /**
     * primitiveBoolean
     */
    public boolean priBoolean;
    /**
     * primitiveByte
     */
    public byte priByte;
    /**
     * primitiveShort
     */
    public short priShort;
    /**
     * primitiveInt
     */
    public int priInt;
    /**
     * primitiveLong
     */
    public long priLong;
    /**
     * primitiveFloat
     */
    public float priFloat;
    /**
     * primitiveDouble
     */
    public double priDouble;

    /**
     * normalBoolean
     */
    public Object objObject;
    /**
     * normalBoolean
     */
    public Boolean objBoolean;
    /**
     * normalByte
     */
    public Byte objByte;
    /**
     * normalShort
     */
    public Short objShort;
    /**
     * normalInt
     */
    public Integer objInt;
    /**
     * normalLong
     */
    public Long objLong;
    /**
     * normalFloat
     */
    public Float objFloat;
    /**
     * normalDouble
     */
    public Double objDouble;
    /**
     * normalString
     */
    public String objString;
    /**
     * normalBigDecimal
     */
    public BigDecimal objBigDecimal;
    /**
     * normalBigInteger
     */
    public BigInteger objBigInteger;
    /**
     * normalDate
     */
    public Date objDate;
    /**
     * normalTimestamp
     */
    public Timestamp objTimestamp;
    /**
     * normalLocalDate
     */
    public LocalDate objLocalDate;
    /**
     * normalLocalTime
     */
    public LocalTime objLocalTime;
    /**
     * normalLocalDateTime
     */
    public LocalDateTime objLocalDateTime;

    /**
     * arrayBoolean
     */
    public boolean[] priArrayBoolean = {true};
    /**
     * arrayNormalBoolean
     */
    public Boolean[] objArrayBoolean = {true, false};
    /**
     * arrayNormalString
     */
    public String[] objArrayString;
    /**
     * arrayNormalStringDef
     */
    public String[] objArrayStringDef = new String[]{"str"};

    /**
     * iterableNormal
     */
    public Iterable<String> iterable = List.of("str1", "str2");
    /**
     * collectionNormal
     */
    public Collection<String> collection = new ArrayList<>();
    /**
     * listNormal
     */
    public List<String> list;
    /**
     * setNormal
     */
    public Set<String> set;
    /**
     * queueNormal
     */
    public Queue<String> queue;
    /**
     * mapNormal
     */
    public Map<String, String> map;

    /**
     * arrayListNormal
     */
    public ArrayList<String> arrayList;
    /**
     * hashSetNormal
     */
    public HashSet<String> hashSet;
    /**
     * linkedListNormal
     */
    public LinkedList<String> linkedQueue;
    /**
     * hashMapNormal
     */
    public HashMap<String, String> hashMap;

    /**
     * arrayArrayNormal
     */
    public String[][] arrayArray;
    /**
     * arrayArrayNormalDef
     */
    public String[][] arrayArrayDef = new String[][]{{"str"}, {"str1", "str2"}};
    /**
     * listArrayNormal
     */
    public Iterable<String[]> listArray;
    /**
     * mapArrayNormal
     */
    public Map<String, String[]> mapArray;

    /**
     * beanNormal
     */
    public NormalBean normalBean;
    /**
     * arrayBeanNormal
     */
    public NormalBean[] arrayBean = {new NormalBean("content")};
    /**
     * iterableBeanNormal
     */
    public Iterable<NormalBean> iterableBean;
    /**
     * mapBeanNormal
     */
    public Map<String, NormalBean> mapBean;
    /**
     * mapBeanArrayBeanNormal
     */
    public Map<String, NormalBean[]> mapArrayBean;

    /**
     * beanComplex
     */
    public ComplexBean complexBean;
    /**
     * arrayBeanComplex
     */
    public ComplexBean[] arrayComplexBean = {new ComplexBean()};
    /**
     * iterableBeanComplex
     */
    public Iterable<ComplexBean> iterableComplexBean;
    /**
     * mapBeanComplex
     */
    public Map<String, ComplexBean> mapComplexBean;
    /**
     * mapBeanArrayBeanComplex
     */
    public Map<String, ComplexBean[]> mapArrayComplexBean;

    /**
     * beanGeneric
     */
    public GenericBean<ComplexBean> genericBean;
    /**
     * arrayBeanGeneric
     */
    public GenericBean<ComplexBean>[] arrayGenericBean;
    /**
     * iterableBeanGeneric
     */
    public Iterable<GenericBean<ComplexBean>> iterableGenericBean;
    /**
     * mapBeanGeneric
     */
    public Map<String, GenericBean<ComplexBean>> mapGenericBean;
    /**
     * mapBeanArrayBeanGeneric
     */
    public Map<String, GenericBean<ComplexBean>[]> mapArrayGenericBean;
    /**
     * mapArrayBeanArrayBeanGenericArray
     */
    public Map<String, GenericBean<ComplexBean>[]>[] mapArrayGenericBeanArray;

    /**
     * beanChildGeneric
     */
    public ChildGenericBean childGenericBean;
    /**
     * arrayBeanChildGeneric
     */
    public ChildGenericBean[] arrayChildGenericBean;
    /**
     * iterableBeanChildGeneric
     */
    public Iterable<ChildGenericBean> iterableChildGenericBean;
    /**
     * mapBeanChildGeneric
     */
    public Map<String, ChildGenericBean> mapChildGenericBean;
    /**
     * mapBeanArrayBeanChildGeneric
     */
    public Map<String, ChildGenericBean[]> mapArrayChildGenericBean;

    public String getPrivateString() {
        return privateString;
    }

    public void setPrivateString(String privateString) {
        this.privateString = privateString;
    }

    /**
     * 普通对象
     */
    public static class NormalBean {
        public NormalBean() {
        }

        public NormalBean(String content) {
            this.content = content;
        }

        /**
         * content
         */
        public String content;
    }

    /**
     * 复杂对象
     */
    public static class ComplexBean {
        public ComplexBean() {
        }

        public ComplexBean(String text, NormalBean bean, Iterable<NormalBean> iterable, Map<String, NormalBean> map) {
            this.text = text;
            this.bean = bean;
            this.iterable = iterable;
            this.map = map;
        }

        /**
         * text
         */
        public String text;
        /**
         * bean
         */
        public NormalBean bean;
        /**
         * iterable
         */
        public Iterable<NormalBean> iterable;
        /**
         * map
         */
        public Map<String, NormalBean> map;
    }

    /**
     * 泛型对象
     */
    public static class GenericBean<T> {
        public GenericBean() {
        }

        public GenericBean(String text, T bean, Iterable<T> iterable, Map<String, T> map) {
            this.text = text;
            this.bean = bean;
            this.iterable = iterable;
            this.map = map;
        }

        /**
         * text
         */
        public String text;
        /**
         * bean
         */
        public T bean;
        /**
         * iterable
         */
        public Iterable<T> iterable;
        /**
         * map
         */
        public Map<String, T> map;
    }

    /**
     * 子泛型对象
     */
    public static class ChildGenericBean extends GenericBean<NormalBean> {
        public ChildGenericBean() {
        }

        public ChildGenericBean(String text, NormalBean bean, Iterable<NormalBean> iterable, Map<String, NormalBean> map) {
            super(text, bean, iterable, map);
        }
    }
}