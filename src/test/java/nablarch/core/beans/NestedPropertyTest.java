package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.text.IsEmptyString.isEmptyString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

/**
 * ネストしたBeanへ値を設定するテスト.
 *
 * @author T.Kawasaki
 */
public class NestedPropertyTest {

    @Test
    public void test() {
        NestedBean nestedBean = new NestedBean();
        BeanUtil.setProperty(nestedBean, "child.grandChild.str", "aaa");
        Child child = nestedBean.getChild();
        GrandChild grandChild = child.getGrandChild();
        assertThat(grandChild.getStr(), is("aaa"));
    }


    @Test
    public void testMap() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        request.put("child.grandChild.str", new String[]{"aaa"});
        request.put("child.name", new String[]{"こども"});
        request.put("array", new String[]{"1", "2", "3"});
        NestedBean bean = BeanUtil.createAndCopy(NestedBean.class, request);


        assertThat(bean.getArray(), is(new String[] {"1", "2", "3"}));
        Child child = bean.getChild();
        assertThat(child.getName(), is("こども"));
        assertThat(child.getGrandChild().getStr(), is("aaa"));
    }

    public static class NestedBean {
        private Child child;

        private String[] array;

        public Child getChild() {
            return child;
        }

        public void setChild(Child child) {
            this.child = child;
        }

        public String[] getArray() {
            return array;
        }

        public void setArray(String[] array) {
            this.array = array;
        }
    }

    public static class Child {
        GrandChild grandChild;

        private String name;

        private String address;

        public GrandChild getGrandChild() {
            return grandChild;
        }

        public void setGrandChild(GrandChild grandChild) {
            this.grandChild = grandChild;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(String address) {
            this.address = address;
        }
    }

    public static class GrandChild {
        String str;

        public String getStr() {
            return str;
        }

        public void setStr(String str) {
            this.str = str;
        }
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在しないプロパティ名称が指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティが単一のBeanのケース)<br/>
     * (無効な項目名のみ受け取るケース)<br/>
     */
    @Test
    public void testCreateAndCopyInvalidPropertyNameToBean() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        request.put("child.invalid", new String[]{"value"});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (BeansException e) {
            fail();
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
    }

    /**
     * 変換元データを格納するMapのキーの一部に、Beanに存在しない名称のプロパティが指定された場合
     * メソッド内部で例外が発生するが無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。<br/>
     *
     * (受け側のBeanのプロパティが単一のBeanのケース)<br/>
     * (無効な項目名、有効な項目名の両方を受け取るケース)<br/>
     */
    @Test
    public void testCreateAndCopyInvalidAndValidPropertyNameToBean() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        request.put("child.invalid", new String[]{"value"});
        request.put("child.name", new String[]{"john"});
        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (BeansException e) {
            fail();
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
        assertThat(bean.getChild().getName(), is("john"));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueToBean() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        NestedBean bean;

        request.put("child.name", null);
        bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
        assertThat(bean.getChild().getName(), is(nullValue()));

        request.put("child.name", new String[]{null});
        bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
        assertThat(bean.getChild().getName(), is(nullValue()));
    }

    /**
     * 変換元データを格納するMapのキーに、Beanに存在する名称のプロパティが指定されてはいるが、
     * 一部の値がnullの場合に、メソッド内部で例外が発生するが、無視されることを確認する.<br/>
     *
     * createAndCopy()の内部では例外が発生するが、外部に再スローはされない。
     */
    @Test
    public void testCreateAndCopyNullValueAndNotNullValueToBean() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        request.put("child.name", new String[]{null});
        request.put("child.address", new String[]{"tokyo"});

        NestedBean bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
        assertThat(bean.getChild().getName(), is(nullValue()));
        assertThat(bean.getChild().getAddress(), is("tokyo"));
    }

    /**
     * 変換元データを格納するMapの値が空文字の場合に、例外が発生しないことを確認する.
     */
    @Test
    public void testCreateAndCopyEmptyValueToBean() {
        Map<String, String[]> request = new HashMap<String, String[]>();
        NestedBean bean;

        request.put("child.name", new String[]{""});
        bean = null;
        try {
            bean = BeanUtil.createAndCopy(NestedBean.class, request);
        } catch (Exception e) {
            fail();
        }
        assertThat(bean.getChild(), is(not(nullValue())));
        assertThat(bean.getChild().getName(), is(isEmptyString()));
    }
}
