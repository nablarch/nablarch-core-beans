package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.collection.IsArrayContainingInOrder.arrayContaining;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Test;

/**
 * ネストしたBeanへ値を設定するテスト.（Beanがリストまたは配列として保持されるケース）
 *
 * @author sumida
 */
public class NestedListPropertyTest {

    /**
     * ネストしたBeanのプロパティへ値を設定する.(ネストが1階層のケース)
     */
    @Test
    public void testCopyToChildsProperty() {
        NestedBean nestedBean = new NestedBean();
        BeanUtil.setProperty(nestedBean, "children[0].name", "aaa");
        List<Child> children = nestedBean.getChildren();
        assertThat(children.get(0).getName(), is("aaa"));
    }

    /**
     * ネストしたBeanのプロパティへ値を設定する.(ネストが2階層のケース)
     */
    @Test
    public void testCopyToGrandChildsProperty() {
        NestedBean nestedBean = new NestedBean();
        BeanUtil.setProperty(nestedBean, "children[0].grandChild.str", "bbb");
        List<Child> children = nestedBean.getChildren();
        GrandChild grandChild = children.get(0).getGrandChild();
        assertThat(grandChild.getStr(), is("bbb"));
    }

    /**
     * Generic型が未指定の場合、例外がスローされること。
     */
    @Test
    public void testNoGenericType() {
        NoGenericTypeBean bean = new NoGenericTypeBean();
        try {
            BeanUtil.setProperty(bean, "children[0].name", "aaa");
            fail("BeansExceptionがスローされるはず");
        } catch (BeansException e) {
            assertThat(e.getMessage(), is(
                            "must set generics type for property. "
                            + "class: class nablarch.core.beans.NestedListPropertyTest$NoGenericTypeBean "
                            + "property: children"));
        }
    }

    @SuppressWarnings("rawtypes")
    public static class NoGenericTypeBean {
        private List children;
        @SuppressWarnings("unused")
        public List getChildren() {
            return children;
        }
        @SuppressWarnings("unused")
        public void setChildren(List children) {
            this.children = children;
        }
    }

    /**
     * {@link Map}のキーが階層構造を持つ場合に、ネストしたBeanに値をコピーできることを確認する.
     */
    @Test
    public void testCreateAndCopy() {
        Map<String, String[]> request = Map.ofEntries(
                Map.entry("children[0].name", new String[]{"a0-1"}),
                Map.entry("children[0].grandChild.str", new String[]{"a0-2"}),
                Map.entry("children[1].name", new String[]{"a1-1"}),
                Map.entry("children[1].grandChild.str", new String[]{"a1-2"}),
                Map.entry("children[3].name", new String[]{"a3-1"}),
                Map.entry("children[3].grandChild.str", new String[]{"a3-2"}),
                Map.entry("stringListProp[0]", new String[]{"s0"}),
                Map.entry("stringListProp[1]", new String[]{"s1"}),
                Map.entry("stringListProp[3]", new String[]{"s3"}),
                Map.entry("array[0].name", new String[]{"b0-1"}),
                Map.entry("array[0].grandChild.str", new String[]{"b0-2"}),
                Map.entry("array[1].name", new String[]{"b1-1"}),
                Map.entry("array[1].grandChild.str", new String[]{"b1-2"}),
                Map.entry("array[3].name", new String[]{"b3-1"}),
                Map.entry("array[3].grandChild.str", new String[]{"b3-2"}),
                Map.entry("stringArray[0]", new String[]{"sa0"}),
                Map.entry("stringArray[1]", new String[]{"sa1"}),
                Map.entry("stringArray[3]", new String[]{"sa3"})
        );

        NestedBean bean = BeanUtil.createAndCopy(NestedBean.class, request);

        // beanのリスト
        assertThat(bean.getChildren().size(), is(4));

        Child child0 = bean.getChildren().get(0);
        assertThat(child0.getName(), is("a0-1"));
        assertThat(child0.getGrandChild().getStr(), is("a0-2"));

        Child child1 = bean.getChildren().get(1);
        assertThat(child1.getName(), is("a1-1"));
        assertThat(child1.getGrandChild().getStr(), is("a1-2"));

        assertThat(bean.getChildren().get(2), is(nullValue()));

        Child child3 = bean.getChildren().get(3);
        assertThat(child3.getName(), is("a3-1"));
        assertThat(child3.getGrandChild().getStr(), is("a3-2"));

        // Stringのリスト
        assertThat(bean.getStringListProp().size(), is(4));
        assertThat(bean.getStringListProp().get(0), is("s0"));
        assertThat(bean.getStringListProp().get(1), is("s1"));
        assertThat(bean.getStringListProp().get(2), is(nullValue()));
        assertThat(bean.getStringListProp().get(3), is("s3"));

        // beanの配列
        assertThat(bean.array.length, is(4));
        assertThat(bean.array[0].getName(), is("b0-1"));
        assertThat(bean.array[0].getGrandChild().getStr(), is("b0-2"));

        assertThat(bean.array[1].getName(), is("b1-1"));
        assertThat(bean.array[1].getGrandChild().getStr(), is("b1-2"));

        assertThat(bean.array[2], is(nullValue()));

        assertThat(bean.array[3].getName(), is("b3-1"));
        assertThat(bean.array[3].getGrandChild().getStr(), is("b3-2"));

        // Stringの配列
        assertThat(bean.stringArray.length, is(4));
        assertThat(bean.stringArray[0], is("sa0"));
        assertThat(bean.stringArray[1], is("sa1"));
        assertThat(bean.stringArray[2], is(nullValue()));
        assertThat(bean.stringArray[3], is("sa3"));
    }

    /** 親Bean */
    public static class NestedBean {

        private List<Child> children;

        private List<String> stringListProp;

        private Child[] array;

        private String[] stringArray = new String[] {"a"};

        public List<Child> getChildren() {
            return children;
        }

        @SuppressWarnings("unused")
        public void setChildren(List<Child> children) {
            this.children = children;
        }

        public List<String> getStringListProp() {
            return stringListProp;
        }

        @SuppressWarnings("unused")
        public void setStringListProp(List<String> stringListProp) {
            this.stringListProp = stringListProp;
        }

        public Child[] getArray() {
            return array;
        }

        @SuppressWarnings("unused")
        public void setArray(Child[] array) {
            this.array = array;
        }

        @SuppressWarnings("unused")
        public String[] getStringArray() {
            return stringArray;
        }

        @SuppressWarnings("unused")
        public void setStringArray(String[] stringArray) {
            this.stringArray = stringArray;
        }
    }

    /** 子Bean */
    public static class Child {

        private String name;

        private String address;

        private GrandChild grandChild;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        @SuppressWarnings("unused")
        public void setAddress(String address) {
            this.address = address;
        }

        public GrandChild getGrandChild() {
            return grandChild;
        }

        @SuppressWarnings("unused")
        public void setGrandChild(GrandChild grandChild) {
            this.grandChild = grandChild;
        }

    }

    /** 孫Bean */
    public static class GrandChild {

        private String str;

        public String getStr() {
            return str;
        }

        @SuppressWarnings("unused")
        public void setStr(String str) {
            this.str = str;
        }
    }

    /**
     * Beanに設定するデータを持つ、Mapのキーが階層構造を持つ場合、
     * 値のコピー先のプロパティの型が{@link List}または配列ではない場合、
     * 例外が発生することを確認する.
     */
    @Test
    public void testCreateAndCopyNotListAndArray() {
        InvalidNestedBean nestedBean = new InvalidNestedBean();
        try {
            BeanUtil.setProperty(nestedBean, "children[0].name", new String[]{"aaa"});
            fail();
        } catch (BeansException e) {
            assertThat(e.getMessage(), is("property type must be List or Array."));
        }
    }

    /** コピー先Bean */
    public static class InvalidNestedBean {
        private Set<Child> children; // List、配列でない
        @SuppressWarnings("unused")
        public Set<Child> getChildren() {
            return children;
        }
        @SuppressWarnings("unused")
        public void setChildren(Set<Child> children) {
            this.children = children;
        }
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在しないプロパティ名称が指定された場合
     * メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyInvalidListPropertyNameToListOrArray() {
        Map<String, String[]> request = Map.of("invalid[0].name", new String[]{"a0-1"});
        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren(), is(nullValue()));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在しないプロパティ名称が指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティがListのケース)<br/>
     * (無効な項目名のみ受け取るケース)<br/>
     */
    @Test
    public void testCreateAndCopyInvalidPropertyNameToList() {
        Map<String, String[]> request = Map.of("children[0].invalid", new String[]{"value"});
        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren(), is(nullValue()));
    }

    /**
     * 変換元データを格納するMapのキーの一部に、Beanに存在しない名称のプロパティが指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティがリストのケース)<br/>
     * (無効な項目名、有効な項目名の両方を受け取るケース)<br/>
     */
    @Test
    public void testCreateAndCopyInvalidAndValidPropertyNameToList() {
        Map<String, String[]> request = Map.of(
                "children[0].invalid", new String[]{"value"},
                "children[0].name", new String[]{"john"}
        );

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren().get(0).getName(), is("john"));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueToList() {
        Map<String, String[]> request = new HashMap<>();
        NestedBean bean;

        // パラメータの値がnullだけの場合
        request.put("children[0].name", null);
        bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren(), is(not(nullValue())));
        assertThat(bean.getChildren().get(0).getName(), is(nullValue()));

        // パラメータの値がnull(配列要素)だけの場合
        request.put("children[0].name", new String[]{null});
        bean = BeanUtil.createAndCopy(NestedBean.class, request);
        assertThat(bean.getChildren(), contains(
                hasProperty("name", is(nullValue()))
        ));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 一部の値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueAndNotNullValueToList() {
        Map<String, String[]> request = Map.of(
                "children[0].name", new String[]{null},
                "children[0].address", new String[]{"tokyo"}
        );

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren().get(0).getName(), is(nullValue()));
        assertThat(bean.getChildren().get(0).getAddress(), is("tokyo"));
    }

    /**
     * 変換元データを格納するMapの値が空文字の場合に、例外が発生しないことを確認する.
     */
    @Test
    public void testCreateAndCopyEmptyValueToList() {
        Map<String, String[]> request = Map.of("children[0].name", new String[]{""});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChildren().get(0).getName(), is(""));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在しないプロパティ名称が指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティが配列のケース)
     * (無効な項目名のみ受け取るケース)
     */
    @Test
    public void testCreateAndCopyInvalidPropertyNameToArray() {
        Map<String, String[]> request = Map.of("array[0].invalid", new String[]{"value"});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getArray(), is(nullValue()));
    }

    /**
     * 変換元データを格納するMapのキーの一部に、Beanに存在しない名称のプロパティが指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティが配列のケース)
     * (無効な項目名、有効な項目名の両方を受け取るケース)
     */
    @Test
    public void testCreateAndCopyInvalidAndValidPropertyNameToArray() {
        Map<String, String[]> request = Map.of(
                "array[0].invalid", new String[]{"value"},
                "array[0].name", new String[]{"john"});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getArray()[0].getName(), is("john"));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueToArray() {
        Map<String, String[]> request = new HashMap<>();
        NestedBean bean;

        // パラメータの値がnullだけの場合
        request.put("array[0].name", null);
        bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getArray(), is(not(nullValue())));
        assertThat(bean.getArray()[0].getName(), is(nullValue()));

        // パラメータの値がnull(配列要素)だけの場合
        request.put("array[0].name", new String[]{null});
        bean = BeanUtil.createAndCopy(NestedBean.class, request);
        //noinspection unchecked
        assertThat(bean.getArray(), arrayContaining(
                hasProperty("name", is(nullValue()))
        ));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 一部の値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueAndNotNullValueToArray() {
        Map<String, String[]> request = Map.of(
                "array[0].name", new String[]{null},
                "array[0].address", new String[]{"tokyo"});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getArray()[0].getName(), is(nullValue()));
        assertThat(bean.getArray()[0].getAddress(), is("tokyo"));
    }

    /**
     * 変換元データを格納するMapの値が空文字の場合に、例外が発生しないことを確認する.
     */
    @Test
    public void testCreateAndCopyEmptyValueToArray() {
        Map<String, String[]> request = Map.of("array[0].name", new String[]{""});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getArray()[0].getName(), is(""));
    }

}
