package nablarch.core.beans;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.fail;

/**
 * ネストしたBeanへ値を設定するテスト.
 *
 * @author T.Kawasaki
 */
@SuppressWarnings("NonAsciiCharacters")
public class NestedPropertyForRecordTest {

//    @Test
//    public void test() {
//        NestedBean nestedBean = new NestedBean();
//        BeanUtil.setProperty(nestedBean, "child.grandChild.str", "aaa");
//        Child child = nestedBean.getChild();
//        GrandChild grandChild = child.getGrandChild();
//        assertThat(grandChild.getStr(), is("aaa"));
//    }
//
//
//    @Test
//    public void testMap() {
//        Map<String, String[]> request = new HashMap<>();
//        request.put("child.grandChild.str", new String[]{"aaa"});
//        request.put("child.name", new String[]{"こども"});
//        request.put("array", new String[]{"1", "2", "3"});
//        NestedBean bean = BeanUtil.createAndCopy(NestedBean.class, request);
//
//
//        assertThat(bean.getArray(), is(new String[] {"1", "2", "3"}));
//        Child child = bean.getChild();
//        assertThat(child.getName(), is("こども"));
//        assertThat(child.getGrandChild().getStr(), is("aaa"));
//    }

    public static class Address {
        private String postCode;
        private String addr;

        public Address() {
        }

        public Address(String postCode, String addr) {
            super();
            this.postCode = postCode;
            this.addr = addr;
        }

        public String getPostCode() {
            return postCode;
        }
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
        public String getAddr() {
            return addr;
        }
        public void setAddr(String addr) {
            this.addr = addr;
        }
    }

    public record ChildRecord(Integer id,
                              String name){
    }

    public record TestRecord(Integer sample,
                             Address address,
                             ChildRecord childRecord
    ) {
    }

    @Test
    public void 変換元データを格納するMapのキーに_レコードに存在しないプロパティ名が指定された場合レコードが生成されること() {
        Map<String, Object> request = Map.of(
                "childRecord.invalid", "value"
        );

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, request);
        assertThat(dest.childRecord().id(), is(nullValue()));
        assertThat(dest.childRecord().name(), is(nullValue()));
    }

    @Test
    public void 変換元データを格納するMapのキーに_レコードに存在しないプロパティ名と存在するプロパティ名の両方が指定された場合レコードが生成されること() {
        Map<String, Object> request = Map.of(
                "childRecord.invalid", "value",
                "childRecord.id", "10"
        );

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, request);
        assertThat(dest.childRecord().id(), is(10));
        assertThat(dest.childRecord().name(), is(nullValue()));
    }

    @Test
    public void 変換元データを格納するMapのキーに_null値を含むプロパティのみ指定された場合レコードが生成されること() {
        Map<String, Object> request = new HashMap<>(){{
            put("childRecord.id", "10");
            put("childRecord.name", null);
        }};

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, request);
        assertThat(dest.childRecord().id(), is(10));
        assertThat(dest.childRecord().name(), is(nullValue()));
    }

    @Test
    public void 変換元データを格納するMapのキーに_null値を含むプロパティが指定された場合レコードが生成されること() {
        Map<String, Object> request = new HashMap<>(){{
            put("childRecord.id", null);
            put("childRecord.name", null);
        }};

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, request);
        assertThat(dest.childRecord().id(), is(nullValue()));
        assertThat(dest.childRecord().name(), is(nullValue()));
    }
}
