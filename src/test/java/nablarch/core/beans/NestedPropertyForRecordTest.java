package nablarch.core.beans;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * ネストしたBeanへ値を設定するテスト.
 *
 * @author T.Kawasaki
 */
public class NestedPropertyForRecordTest {

    public static class Address {
        private String postCode;
        private String addr;

        @SuppressWarnings("unused")
        public Address() {
        }

        @SuppressWarnings("unused")
        public Address(String postCode, String addr) {
            super();
            this.postCode = postCode;
            this.addr = addr;
        }

        @SuppressWarnings("unused")
        public String getPostCode() {
            return postCode;
        }
        @SuppressWarnings("unused")
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
        @SuppressWarnings("unused")
        public String getAddr() {
            return addr;
        }
        @SuppressWarnings("unused")
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
