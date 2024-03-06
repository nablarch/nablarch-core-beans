package nablarch.core.beans;

import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class NestedListPropertyForRecordTest {


    @Before
    public void setUp() {
        OnMemoryLogWriter.clear();
    }

    @SuppressWarnings("unused")
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

    public record InnerRecord(Integer id,
                              String name){
    }

    public record TestRecord(List<String> strList,
                             List<Address> addressList,
                             List<InnerRecord> innerRecordList,
                             String[] strArray,
                             Address[] addressArray,
                             InnerRecord[] innerRecordArray
    ) {
    }

    public static class SelfNestedBean {
        String name;
        SelfNestedBean bean;
        SelfNestedRecord rec;
        List<SelfNestedBean> beanList;
        List<SelfNestedRecord> recList;
        SelfNestedBean[] beanArray;
        SelfNestedRecord[] recArray;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @SuppressWarnings("unused")
        public SelfNestedBean getBean() {
            return bean;
        }

        @SuppressWarnings("unused")
        public void setBean(SelfNestedBean bean) {
            this.bean = bean;
        }

        @SuppressWarnings("unused")
        public SelfNestedRecord getRec() {
            return rec;
        }

        @SuppressWarnings("unused")
        public void setRec(SelfNestedRecord rec) {
            this.rec = rec;
        }

        @SuppressWarnings("unused")
        public List<SelfNestedBean> getBeanList() {
            return beanList;
        }

        @SuppressWarnings("unused")
        public void setBeanList(List<SelfNestedBean> beanList) {
            this.beanList = beanList;
        }

        @SuppressWarnings("unused")
        public List<SelfNestedRecord> getRecList() {
            return recList;
        }

        @SuppressWarnings("unused")
        public void setRecList(List<SelfNestedRecord> recList) {
            this.recList = recList;
        }

        @SuppressWarnings("unused")
        public SelfNestedBean[] getBeanArray() {
            return beanArray;
        }

        @SuppressWarnings("unused")
        public void setBeanArray(SelfNestedBean[] beanArray) {
            this.beanArray = beanArray;
        }

        @SuppressWarnings("unused")
        public SelfNestedRecord[] getRecArray() {
            return recArray;
        }

        @SuppressWarnings("unused")
        public void setRecArray(SelfNestedRecord[] recArray) {
            this.recArray = recArray;
        }
    }

    public record SelfNestedRecord(
            String name,
            SelfNestedBean bean,
            SelfNestedRecord rec,
            List<SelfNestedBean> beanList,
            List<SelfNestedRecord> recList,
            SelfNestedBean[] beanArray,
            SelfNestedRecord[] recArray
    ) {}



    public record InvalidNestedRecord(Set<NestedListPropertyTest.Child> children) {
    }

    @Test
    public void レコードに設定するデータを持つMapのキーが階層構造を持つ場合に_値のコピー先のプロパティの型がListまたは配列ではない場合_実行時例外が送出されること() {
        BeanUtil.createAndCopy(InvalidNestedRecord.class, Map.of("children[0].name", new String[]{"aaa"}));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("property type must be List or Array."),
                containsString("nablarch.core.beans.BeansException"))));
    }


    @Test
    public void 変換元データを格納するMapのキーに_レコードに存在しないプロパティ名が指定された場合レコードが生成されること() {
        Map<String, Object> request = Map.of("invalid[0].name", "a0-1");
        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(bean.strList(), is(nullValue()));
        assertThat(bean.addressList(), is(nullValue()));
        assertThat(bean.innerRecordList(), is(nullValue()));
        assertThat(bean.strArray(), is(nullValue()));
        assertThat(bean.addressArray(), is(nullValue()));
        assertThat(bean.innerRecordArray(), is(nullValue()));
    }

    @Test
    public void 変換元データを格納するMapのキーに_子プロパティ名として存在しない名前のみ指定した場合_レコード内のBeanプロパティは生成されず_レコードプロパティは生成されること () {
        Map<String, Object> request = Map.of(
                "addressList[0].invalid", "test1",
                "innerRecordList[0].invalid", "test3",
                "addressArray[0].invalid", "test5",
                "innerRecordArray[0].invalid", "test7"
        );
        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(bean.addressList(), is(nullValue()));
        assertThat(bean.innerRecordList().get(0), is(not(nullValue())));
        assertThat(bean.addressArray(), is(nullValue()));
        assertThat(bean.innerRecordArray()[0], is(not(nullValue())));
    }

    @Test
    public void 変換元データを格納するMapのキーに_子プロパティにnullのみ指定した場合_レコード内の各プロパティは生成されること() {
        Map<String, Object> request = new HashMap<>(){{
            put("addressList[0].postCode", null);
            put("addressList[0].addr", new String[]{null});
            put("innerRecordList[0].id", null);
            put("innerRecordList[0].name", new String[]{null});
            put("addressArray[0].postCode", new String[]{null});
            put("addressArray[0].addr", null);
            put("innerRecordArray[0].id", new String[]{null});
            put("innerRecordArray[0].name", null);
        }};


        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(bean.addressList().get(0).postCode, is(nullValue()));
        assertThat(bean.addressList().get(0).addr, is(nullValue()));
        assertThat(bean.innerRecordList().get(0).id, is(nullValue()));
        assertThat(bean.innerRecordList().get(0).name, is(nullValue()));
        assertThat(bean.addressArray()[0].postCode, is(nullValue()));
        assertThat(bean.addressArray()[0].addr, is(nullValue()));
        assertThat(bean.innerRecordArray()[0].id, is(nullValue()));
        assertThat(bean.innerRecordArray()[0].name, is(nullValue()));
    }

    @Test
    public void 変換元データを格納するMapのキーに_子プロパティの一部にnullを指定した場合_レコード内の各プロパティは生成されること() {
        Map<String, Object> request = new HashMap<>(){{
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", new String[]{null});
            put("innerRecordList[0].id", 10);
            put("innerRecordList[0].name", new String[]{null});
            put("addressArray[0].postCode", new String[]{null});
            put("addressArray[0].addr", "東京都江東区");
            put("innerRecordArray[0].id", new String[]{null});
            put("innerRecordArray[0].name", "test");
        }};


        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(bean.addressList().get(0).postCode, is("111-2222"));
        assertThat(bean.addressList().get(0).addr, is(nullValue()));
        assertThat(bean.innerRecordList().get(0).id, is(10));
        assertThat(bean.innerRecordList().get(0).name, is(nullValue()));
        assertThat(bean.addressArray()[0].postCode, is(nullValue()));
        assertThat(bean.addressArray()[0].addr, is("東京都江東区"));
        assertThat(bean.innerRecordArray()[0].id, is(nullValue()));
        assertThat(bean.innerRecordArray()[0].name, is("test"));
    }

    @Test
    public void 変換元データを格納するMapのキーに_子プロパティ名として存在する名前と存在しない名前の両方を指定した場合_レコード内の各プロパティが生成されること() {
        Map<String, Object> request = Map.of(
                "addressList[0].invalid", "test1",
                "addressList[0].addr", "test2",
                "innerRecordList[0].invalid", "test3",
                "innerRecordList[0].name", "test4",
                "addressArray[0].invalid", "test5",
                "addressArray[0].addr", "test6",
                "innerRecordArray[0].invalid", "test7",
                "innerRecordArray[0].name", "test8"
        );
        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(bean.addressList().get(0).getAddr(), is("test2"));
        assertThat(bean.addressList().get(0).getPostCode(), is(nullValue()));
        assertThat(bean.innerRecordList().get(0).id(), is(nullValue()));
        assertThat(bean.innerRecordList().get(0).name(), is("test4"));
        assertThat(bean.addressArray()[0].getAddr(), is("test6"));
        assertThat(bean.addressArray()[0].getPostCode(), is(nullValue()));
        assertThat(bean.innerRecordArray()[0].id(), is(nullValue()));
        assertThat(bean.innerRecordArray()[0].name(), is("test8"));
    }

    @Test
    public void 変換元データを格納するMapのキーに_レコード内のプロパティが持っていないプロパティ名と持っているプロパティの両方を指定した場合_レコードの各プロパティが生成されること() {
        Map<String, Object> request = Map.of(
                "addressList[0].invalid", "test1",
                "addressList[0].addr", "test2",
                "innerRecordList[0].invalid", "test3",
                "innerRecordList[0].name", "test4",
                "addressArray[0].invalid", "test5",
                "addressArray[0].addr", "test6",
                "innerRecordArray[0].invalid", "test7",
                "innerRecordArray[0].name", "test8"
        );
        TestRecord bean = BeanUtil.createAndCopy(TestRecord.class, request);

        assertThat(((Address)((List<?>)(bean.addressList())).get(0)).getAddr(), is("test2"));
        assertThat(((Address)((List<?>)(bean.addressList())).get(0)).getPostCode(), is(nullValue()));
        assertThat(((InnerRecord)((List<?>)(bean.innerRecordList())).get(0)).id(), is(nullValue()));
        assertThat(((InnerRecord)((List<?>)(bean.innerRecordList())).get(0)).name(), is("test4"));
        assertThat(((Address[])(bean.addressArray()))[0].getAddr(), is("test6"));
        assertThat(((Address[])(bean.addressArray()))[0].getPostCode(), is(nullValue()));
        assertThat(((InnerRecord[])(bean.innerRecordArray()))[0].id(), is(nullValue()));
        assertThat(((InnerRecord[])(bean.innerRecordArray()))[0].name(), is("test8"));
    }


    @Test
    public void 複数階層ネストしているMapをレコードにコピーできること() {
        Map<String, Object> request = Map.ofEntries(
                Map.entry("name", "name"),
                Map.entry("bean.name", "beanName" ),
                Map.entry("bean.rec.name", "beanRecordName" ),
                Map.entry("rec.name", "recordName" ),
                Map.entry("rec.bean.name", "recordBeanName" ),
                Map.entry("rec.rec.name", "recordRecordName" ),

                Map.entry("beanList[0].name", "beanL0name" ),
                Map.entry("beanList[0].bean.name", "beanL0beanName" ),
                Map.entry("beanList[0].rec.name", "beanL0recordName" ),
                Map.entry("beanList[1].name", "beanL1name" ),
                Map.entry("beanList[1].bean.name", "beanL1beanName" ),
                Map.entry("beanList[3].name", "beanL3name" ),
                Map.entry("beanList[3].rec.name", "beanL3recordName" ),

                Map.entry("recList[0].name", "recordL0name" ),
                Map.entry("recList[0].bean.name", "recordL0beanName" ),
                Map.entry("recList[0].rec.name", "recordL0recordName" ),
                Map.entry("recList[1].name", "recordL1name" ),
                Map.entry("recList[1].bean.name", "recordL1beanName" ),
                Map.entry("recList[3].name", "recordL3name" ),
                Map.entry("recList[3].rec.name", "recordL3recordName" ),

                Map.entry("beanArray[0].name", "beanA0name" ),
                Map.entry("beanArray[0].bean.name", "beanA0beanName" ),
                Map.entry("beanArray[0].rec.name", "beanA0recordName" ),
                Map.entry("beanArray[1].name", "beanA1name" ),
                Map.entry("beanArray[1].bean.name", "beanA1beanName" ),
                Map.entry("beanArray[3].name", "beanA3name" ),
                Map.entry("beanArray[3].rec.name", "beanA3recordName" ),

                Map.entry("recArray[0].name", "recordA0name" ),
                Map.entry("recArray[0].bean.name", "recordA0beanName" ),
                Map.entry("recArray[0].rec.name", "recordA0recordName" ),
                Map.entry("recArray[1].name", "recordA1name" ),
                Map.entry("recArray[1].bean.name", "recordA1beanName" ),
                Map.entry("recArray[3].name", "recordA3name" ),
                Map.entry("recArray[3].rec.name", "recordA3recordName" )
        );

        SelfNestedRecord dest = BeanUtil.createAndCopy(SelfNestedRecord.class, request);

        assertThat(dest.name, is("name"));
        assertThat(dest.bean.name, is("beanName"));
        assertThat(dest.bean.rec.name, is("beanRecordName"));
        assertThat(dest.rec.name, is("recordName"));
        assertThat(dest.rec.bean.name, is("recordBeanName"));
        assertThat(dest.rec.rec.name, is("recordRecordName"));

        assertThat(dest.beanList.get(0).name, is("beanL0name"));
        assertThat(dest.beanList.get(0).bean.name, is("beanL0beanName"));
        assertThat(dest.beanList.get(0).rec.name, is("beanL0recordName"));
        assertThat(dest.beanList.get(1).name, is("beanL1name"));
        assertThat(dest.beanList.get(1).bean.name, is("beanL1beanName"));
        assertThat(dest.beanList.get(2), is(nullValue()));
        assertThat(dest.beanList.get(3).name, is("beanL3name"));
        assertThat(dest.beanList.get(3).rec.name, is("beanL3recordName"));

        assertThat(dest.recList.get(0).name, is("recordL0name"));
        assertThat(dest.recList.get(0).bean.name, is("recordL0beanName"));
        assertThat(dest.recList.get(0).rec.name, is("recordL0recordName"));
        assertThat(dest.recList.get(1).name, is("recordL1name"));
        assertThat(dest.recList.get(1).bean.name, is("recordL1beanName"));
        assertThat(dest.recList.get(2), is(nullValue()));
        assertThat(dest.recList.get(3).name, is("recordL3name"));
        assertThat(dest.recList.get(3).rec.name, is("recordL3recordName"));

        assertThat(dest.beanArray[0].name, is("beanA0name"));
        assertThat(dest.beanArray[0].bean.name, is("beanA0beanName"));
        assertThat(dest.beanArray[0].rec.name, is("beanA0recordName"));
        assertThat(dest.beanArray[1].name, is("beanA1name"));
        assertThat(dest.beanArray[1].bean.name, is("beanA1beanName"));
        assertThat(dest.beanArray[2], is(nullValue()));
        assertThat(dest.beanArray[3].name, is("beanA3name"));
        assertThat(dest.beanArray[3].rec.name, is("beanA3recordName"));

        assertThat(dest.recArray[0].name, is("recordA0name"));
        assertThat(dest.recArray[0].bean.name, is("recordA0beanName"));
        assertThat(dest.recArray[0].rec.name, is("recordA0recordName"));
        assertThat(dest.recArray[1].name, is("recordA1name"));
        assertThat(dest.recArray[1].bean.name, is("recordA1beanName"));
        assertThat(dest.recArray[2], is(nullValue()));
        assertThat(dest.recArray[3].name, is("recordA3name"));
        assertThat(dest.recArray[3].rec.name, is("recordA3recordName"));
    }

}
