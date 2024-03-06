package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.lang.reflect.Method;
import java.lang.reflect.RecordComponent;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThrows;

/**
 * {@link BeanUtil}の、レコード型に対するテストクラス。
 *
 * @author Takayuki Uchida
 */
public class BeanUtilForRecordTest {

    @SuppressWarnings("RedundantThrows")
    @Before
    public void setUp() throws Exception {
        // デフォルト設定で動作させるよう、リポジトリをクリアする。
        SystemRepository.clear();
        OnMemoryLogWriter.clear();
        //BeanDescriptorのキャッシュをクリアしておく
        BeanUtil.clearCache();
    }

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

    public record TestRecord(Integer sample,
                             String onlyInTestRecord,
                             Address address,
                             InnerRecord innerRecord,
                             List<String> strList,
                             List<Address> addressList,
                             List<InnerRecord> innerRecordList,
                             String[] strArray,
                             Address[] addressArray,
                             InnerRecord[] innerRecordArray
                             ) {
    }

    public record SourceRecord(String sample,
                               String onlyInSourceRecord,
                               Address address,
                               InnerRecord innerRecord,
                               List<String> strList,
                               List<Address> addressList,
                               List<InnerRecord> innerRecordList,
                               String[] strArray,
                               Address[] addressArray,
                               InnerRecord[] innerRecordArray
    ) {
    }

    public static class TestBean {
        private Integer sample;
        private String onlyInTestBean;
        private Address address;
        private InnerRecord innerRecord;
        private List<String> strList;
        private List<Address> addressList;
        private List<InnerRecord> innerRecordList;
        private String[] strArray;
        private Address[] addressArray;
        private InnerRecord[] innerRecordArray;

        public Integer getSample() {
            return sample;
        }

        public void setSample(Integer sample) {
            this.sample = sample;
        }

        public String getOnlyInTestBean() {
            return onlyInTestBean;
        }

        public void setOnlyInTestBean(String onlyInTestBean) {
            this.onlyInTestBean = onlyInTestBean;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public InnerRecord getInnerRecord() {
            return innerRecord;
        }

        public void setInnerRecord(InnerRecord innerRecord) {
            this.innerRecord = innerRecord;
        }

        public List<String> getStrList() {
            return strList;
        }

        public void setStrList(List<String> strList) {
            this.strList = strList;
        }

        public List<Address> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<Address> addressList) {
            this.addressList = addressList;
        }

        public List<InnerRecord> getInnerRecordList() {
            return innerRecordList;
        }

        public void setInnerRecordList(List<InnerRecord> innerRecordList) {
            this.innerRecordList = innerRecordList;
        }

        public String[] getStrArray() {
            return strArray;
        }

        public void setStrArray(String[] strArray) {
            this.strArray = strArray;
        }

        public Address[] getAddressArray() {
            return addressArray;
        }

        public void setAddressArray(Address[] addressArray) {
            this.addressArray = addressArray;
        }

        public InnerRecord[] getInnerRecordArray() {
            return innerRecordArray;
        }

        public void setInnerRecordArray(InnerRecord[] innerRecordArray) {
            this.innerRecordArray = innerRecordArray;
        }
    }

    public static class SourceBean {
        private String sample;
        private String onlyInSourceBean;
        private Address address;
        private InnerRecord innerRecord;
        private List<String> strList;
        private List<Address> addressList;
        private List<InnerRecord> innerRecordList;
        private String[] strArray;
        private Address[] addressArray;
        private InnerRecord[] innerRecordArray;

        public String getSample() {
            return sample;
        }

        public void setSample(String sample) {
            this.sample = sample;
        }

        public String getOnlyInSourceBean() {
            return onlyInSourceBean;
        }

        public void setOnlyInSourceBean(String onlyInSourceBean) {
            this.onlyInSourceBean = onlyInSourceBean;
        }

        public Address getAddress() {
            return address;
        }

        public void setAddress(Address address) {
            this.address = address;
        }

        public InnerRecord getInnerRecord() {
            return innerRecord;
        }

        public void setInnerRecord(InnerRecord innerRecord) {
            this.innerRecord = innerRecord;
        }

        public List<String> getStrList() {
            return strList;
        }

        public void setStrList(List<String> strList) {
            this.strList = strList;
        }

        public List<Address> getAddressList() {
            return addressList;
        }

        public void setAddressList(List<Address> addressList) {
            this.addressList = addressList;
        }

        public List<InnerRecord> getInnerRecordList() {
            return innerRecordList;
        }

        public void setInnerRecordList(List<InnerRecord> innerRecordList) {
            this.innerRecordList = innerRecordList;
        }

        public String[] getStrArray() {
            return strArray;
        }

        public void setStrArray(String[] strArray) {
            this.strArray = strArray;
        }

        public Address[] getAddressArray() {
            return addressArray;
        }

        public void setAddressArray(Address[] addressArray) {
            this.addressArray = addressArray;
        }

        public InnerRecord[] getInnerRecordArray() {
            return innerRecordArray;
        }

        public void setInnerRecordArray(InnerRecord[] innerRecordArray) {
            this.innerRecordArray = innerRecordArray;
        }
    }

    public record TestPrimRecord(int vint,
                                 long vlong,
                                 float vfloat,
                                 double vdouble,
                                 boolean vboolean,
                                 char vchar,
                                 byte vbyte,
                                 short vshort) {
    }

    public static class SourcePrimRecord {


        public void setVboolean(boolean vboolean) {
            // nop
            // getterメソッドを持たないプロパティとして実装
        }

        public void setVchar(char vchar) {
            // nop
            // getterメソッドを持たないプロパティとして実装
        }

        public void setVbyte(byte vbyte) {
            // nop
            // getterメソッドを持たないプロパティとして実装
        }
    }

    @Test
    public void Mapからレコードに値を設定できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", "10"),
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇😁"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇😁"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

    }

    @Test
    public void Mapからレコードに値を設定できること_Mapエントリの値がBeanやレコードである場合() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10),
                Map.entry("address", new Address("111-2222", "東京都江東区")),
                Map.entry("innerRecord", new InnerRecord(10001, "中田昇")),
                Map.entry("strList", List.of("1", "2")),
                Map.entry("addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")) ),
                Map.entry("innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天"))),
                Map.entry("strArray", new String[]{"3", "4"}),
                Map.entry("addressArray", new Address[]{new Address( "555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")}),
                Map.entry("innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")})
        );

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

    }




    @Test
    public void 指定したネストパラメータおよびリストパラメータを使用してレコードを生成できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10),
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        String[] includeParamList = new String[] {
                "address.postCode",
                "innerRecord.name",
                "strList[1]",
                "addressList[1].addr",
                "innerRecordList[1].id",
                "strArray[0]",
                "addressArray[0].postCode",
                "innerRecordArray[0].name"
        };



        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, srcMap, includeParamList);

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is(nullValue()));
        assertThat(dest.innerRecord.id, is(nullValue()));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is(nullValue()));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0), is(nullValue()));
        assertThat(dest.addressList.get(1).postCode, is(nullValue()));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0), is(nullValue()));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is(nullValue()));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(nullValue()));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.strArray[1]));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.addressArray[1]));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.innerRecordArray[1]));
    }

    @Test
    public void 指定したパラメータのみ使用してレコードを生成できること() {

        Map<String, Object> srcMap = Map.of(
                "sample", 10,
                "address", new Address("111-2222", "東京都江東区"),
                "innerRecord", new InnerRecord(10001, "中田昇"),
                "strList", List.of("1", "2"),
                "addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                "innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                "strArray", new String[]{"3", "4"},
                "addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                "innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        String[] includeParamList = new String[] {
                "sample",
                "address",
                "addressList",
                "strArray",
                "innerRecordArray"
        };

        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, srcMap, includeParamList);

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord, is(nullValue()));
        assertThat(dest.strList, is(nullValue()));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList, is(nullValue()));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void 指定したネストパラメータおよびリストパラメータを除外してレコードを生成できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10),
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );
        String[] excludeParamList = new String[] {
                "address.postCode",
                "innerRecord.name",
                "strList[1]",
                "addressList[1].addr",
                "innerRecordList[1].id",
                "strArray[0]",
                "addressArray[0].postCode",
                "innerRecordArray[0].name"
        };

        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, srcMap, excludeParamList);

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is(nullValue()));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is(nullValue()));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is(nullValue()));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is(nullValue()));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is(nullValue()));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

        assertThrows(IndexOutOfBoundsException.class, () -> System.out.println(dest.strList.get(1)));

    }

    @Test
    public void 指定したパラメータを除外してレコードを生成できること() {

        Map<String, Object> srcMap = Map.of(
                "sample", 10,
                "address", new Address("111-2222", "東京都江東区"),
                "innerRecord", new InnerRecord(10001, "中田昇"),
                "strList", List.of("1", "2"),
                "addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                "innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                "strArray", new String[]{"3", "4"},
                "addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                "innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        String[] excludeParamList = new String[] {
                "sample",
                "address",
                "addressList",
                "strArray",
                "innerRecordArray"
        };

        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, srcMap, excludeParamList);

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray, is(nullValue()));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray, is(nullValue()));

    }

    @Test
    public void 空のMapからレコードを生成できること() {

        Map<String, Object> srcMap = Map.of();

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.innerRecord, is(nullValue()));
        assertThat(dest.strList, is(nullValue()));
        assertThat(dest.addressList, is(nullValue()));
        assertThat(dest.innerRecordList, is(nullValue()));
        assertThat(dest.strArray, is(nullValue()));
        assertThat(dest.addressArray, is(nullValue()));
        assertThat(dest.innerRecordArray, is(nullValue()));
    }

    @Test
    public void NullのMapからレコードを生成できること() {

        Map<String, Object> srcMap = null;

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.innerRecord, is(nullValue()));
        assertThat(dest.strList, is(nullValue()));
        assertThat(dest.addressList, is(nullValue()));
        assertThat(dest.innerRecordList, is(nullValue()));
        assertThat(dest.strArray, is(nullValue()));
        assertThat(dest.addressArray, is(nullValue()));
        assertThat(dest.innerRecordArray, is(nullValue()));
    }


    @Test
    public void Beanのプロパティにレコードが含まれる場合でも_MapからBeanに値を設定できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10), // StringからIntegerへ変換できることも合わせて確認する
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇😁"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        TestBean dest = BeanUtil.createAndCopy(TestBean.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇😁"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

    }

    @Test
    public void Beanのプロパティにレコードが含まれる場合でも_指定したネストパラメータ及びリストパラメータを使用してBeanを生成できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10), // StringからIntegerへ変換できることも合わせて確認する
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        String[] includeParamList = new String[] {
                "sample",
                "address.postCode",
                "innerRecord.name",
                "strList[1]",
                "addressList[1].addr",
                "innerRecordList[1].id",
                "strArray[0]",
                "addressArray[0].postCode",
                "innerRecordArray[0].name"
        };



        TestBean dest = BeanUtil.createAndCopyIncludes(TestBean.class, srcMap, includeParamList);

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is(nullValue()));
        assertThat(dest.innerRecord.id, is(nullValue()));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is(nullValue()));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0), is(nullValue()));
        assertThat(dest.addressList.get(1).postCode, is(nullValue()));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0), is(nullValue()));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is(nullValue()));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(nullValue()));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.strArray[1]));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.addressArray[1]));

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> System.out.println(dest.innerRecordArray[1]));
    }

    @Test
    public void Beanのプロパティにレコードが含まれる場合でも_指定したパラメータのみ使用してBeanを生成できること() {

        Map<String, Object> srcMap = Map.of(
                "sample", 10, // StringからIntegerへ変換できることも合わせて確認する
                "address", new Address("111-2222", "東京都江東区"),
                "innerRecord", new InnerRecord(10001, "中田昇"),
                "strList", List.of("1", "2"),
                "addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                "innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                "strArray", new String[]{"3", "4"},
                "addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                "innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        String[] includeParamList = new String[] {
                "sample",
                "address",
                "addressList",
                "strArray",
                "innerRecordArray"
        };

        TestBean dest = BeanUtil.createAndCopyIncludes(TestBean.class, srcMap, includeParamList);

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord, is(nullValue()));
        assertThat(dest.strList, is(nullValue()));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList, is(nullValue()));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void Beanのプロパティにレコードが含まれる場合でも_指定したネストパラメータ及びリストパラメータを除外してBeanを生成できること() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("sample", 10), // StringからIntegerへ変換できることも合わせて確認する
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        String[] includeParamList = new String[] {
                "sample",
                "address.postCode",
                "innerRecord.name",
                "strList[1]",
                "addressList[1].addr",
                "innerRecordList[1].id",
                "strArray[0]",
                "addressArray[0].postCode",
                "innerRecordArray[0].name"
        };

        TestBean dest = BeanUtil.createAndCopyExcludes(TestBean.class, srcMap, includeParamList);

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address.postCode, is(nullValue()));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is(nullValue()));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is(nullValue()));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is(nullValue()));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is(nullValue()));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

        assertThrows(IndexOutOfBoundsException.class, () -> System.out.println(dest.strList.get(1)));

    }

    @Test
    public void Beanのプロパティにレコードが含まれる場合でも_指定したパラメータを除外してBeanを生成できること() {

        Map<String, Object> srcMap = Map.of(
                "sample", 10, // StringからIntegerへ変換できることも合わせて確認する
                "address", new Address("111-2222", "東京都江東区"),
                "innerRecord", new InnerRecord(10001, "中田昇"),
                "strList", List.of("1", "2"),
                "addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                "innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                "strArray", new String[]{"3", "4"},
                "addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                "innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        String[] includeParamList = new String[] {
                "sample",
                "address",
                "addressList",
                "strArray",
                "innerRecordArray"
        };

        TestBean dest = BeanUtil.createAndCopyExcludes(TestBean.class, srcMap, includeParamList);

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray, is(nullValue()));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray, is(nullValue()));

    }

    @Test
    public void レコードからレコードに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
            "10",
            "test",
            new Address("111-2222", "東京都江東区"),
            new InnerRecord(10001, "中田昇"),
            List.of("1", "2"),
            List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
            List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
            new String[]{"3", "4"},
            new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
            new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcRecord, CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.onlyInTestRecord, is(nullValue()));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void 指定したプロパティのみレコードからレコードに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, srcRecord, "sample", "strList", "innerRecordArray", "onlyInSourceRecord");

        assertThat(dest.sample, is(10));
        assertThat(dest.onlyInTestRecord, is(nullValue()));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.innerRecord, is(nullValue()));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList, is(nullValue()));
        assertThat(dest.innerRecordList, is(nullValue()));
        assertThat(dest.strArray, is(nullValue()));
        assertThat(dest.addressArray, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));

        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while copying the property :onlyInSourceRecord"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));
    }

    @Test
    public void 指定したプロパティを除外してレコードからレコードに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, srcRecord, "sample", "strList", "innerRecordArray", "onlyInSourceRecord");

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.onlyInTestRecord, is(nullValue()));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList, is(nullValue()));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray, is(nullValue()));
    }

    @Test
    public void レコードからBeanに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        TestBean dest = BeanUtil.copy(srcRecord, new TestBean(), CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.onlyInTestBean, is(nullValue()));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void 指定したプロパティのみレコードからBeanに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                null,
                null, null, null, null, null, null
        );

        TestBean dest = new TestBean();
        dest.sample = 20;
        dest.onlyInTestBean = "hoge";
        dest.address = new Address("123-4567", "東京都品川区");
        dest.innerRecord = new InnerRecord(10001, "中田昇");

        BeanUtil.copyIncludes(srcRecord, dest, "onlyInSourceRecord", "onlyInTestRecord", "innerRecord");

        assertThat(dest.sample, is(20));
        assertThat(dest.onlyInTestBean, is("hoge"));
        assertThat(dest.address.postCode, is("123-4567"));
        assertThat(dest.address.addr, is("東京都品川区"));
        // CopyOptions.empty().isExcludesNull() == false なので、nullのプロパティもコピーされるはずだが、Nablarch6時点ではではコピーされない。
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
    }

    @Test
    public void 指定したプロパティを除外してレコードからBeanに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                null,
                null, null, null, null, null, null
        );

        TestBean dest = new TestBean();
        dest.sample = 20;
        dest.onlyInTestBean = "hoge";
        dest.address = new Address("123-4567", "東京都品川区");
        dest.innerRecord = new InnerRecord(10001, "中田昇");

        BeanUtil.copyExcludes(srcRecord, dest, "sample", "onlyInSourceRecord", "onlyInTestRecord", "address");

        assertThat(dest.sample, is(20));
        assertThat(dest.onlyInTestBean, is("hoge"));
        assertThat(dest.address.postCode, is("123-4567"));
        assertThat(dest.address.addr, is("東京都品川区"));
        // CopyOptions.empty().isExcludesNull() == false なので、nullのプロパティもコピーされるはずだが、Nablarch6時点ではではコピーされない。
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
    }

    @Test
    public void Nullコンポーネントを除いて_レコードからBeanに値を設定できること() {

        SourceRecord srcRecord = new SourceRecord(null, null, new Address("111-2222", "東京都江東区"), null, null, null, null, null, null, null);

        TestBean testBean = new TestBean();
        testBean.sample = 10;
        testBean.address = new Address("333-4444", "東京都新宿区");
        TestBean dest = BeanUtil.copyExcludesNull(srcRecord, testBean);

        assertThat(dest.sample, is(10));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
    }

    @Test
    public void Beanからレコードに値を設定できること() {

        SourceBean srcBean = new SourceBean();
        srcBean.setSample("10");
        srcBean.setOnlyInSourceBean("test");
        srcBean.setAddress(new Address("111-2222", "東京都江東区"));
        srcBean.setInnerRecord(new InnerRecord(10001, "中田昇"));
        srcBean.setStrList(List.of("1", "2"));
        srcBean.setAddressList(List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")));
        srcBean.setInnerRecordList(List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")));
        srcBean.setStrArray(new String[]{"3", "4"});
        srcBean.setAddressArray(new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")});
        srcBean.setInnerRecordArray(new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")});

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcBean, CopyOptions.empty());

        assertThat(dest.sample, is(10));
        assertThat(dest.onlyInTestRecord, is(nullValue()));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都江東区"));
        assertThat(dest.innerRecord.id, is(10001));
        assertThat(dest.innerRecord.name, is("中田昇"));
        assertThat(dest.strList.get(0), is("1"));
        assertThat(dest.strList.get(1), is("2"));
        assertThat(dest.addressList.get(0).postCode, is("111-2222"));
        assertThat(dest.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(dest.addressList.get(1).postCode, is("333-4444"));
        assertThat(dest.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(dest.innerRecordList.get(0).id, is(10002));
        assertThat(dest.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(dest.innerRecordList.get(1).id, is(10003));
        assertThat(dest.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(dest.strArray[0], is("3"));
        assertThat(dest.strArray[1], is("4"));
        assertThat(dest.addressArray[0].postCode, is("555-6666"));
        assertThat(dest.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(dest.addressArray[1].postCode, is("777-8888"));
        assertThat(dest.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(dest.innerRecordArray[0].id, is(10004));
        assertThat(dest.innerRecordArray[0].name, is("神田幹太"));
        assertThat(dest.innerRecordArray[1].id, is(10005));
        assertThat(dest.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void Map内にプリミティブ型のコンポーネントに対応するパラメタが存在しない場合_デフォルト値で置換されること() {
        TestPrimRecord dest = BeanUtil.createAndCopy(TestPrimRecord.class, Map.of(), CopyOptions.empty());
        assertThat(dest.vint, is(0));
        assertThat(dest.vlong, is(0L));
        assertThat(dest.vfloat, is(0.0f));
        assertThat(dest.vdouble, is(0.0));
        assertThat(dest.vboolean, is(false));
        assertThat(dest.vchar, is('\u0000'));
        assertThat(dest.vbyte, is((byte) 0));
        assertThat(dest.vshort, is((short) 0));
    }

    @Test
    public void Bean内にプリミティブ型のコンポーネントに対応するパラメタもしくはgetterが存在しない場合_デフォルト値で置換されること() {
        TestPrimRecord dest = BeanUtil.createAndCopy(TestPrimRecord.class, new SourcePrimRecord(), CopyOptions.empty());
        assertThat(dest.vint, is(0));
        assertThat(dest.vlong, is(0L));
        assertThat(dest.vfloat, is(0.0f));
        assertThat(dest.vdouble, is(0.0));
        assertThat(dest.vboolean, is(false));
        assertThat(dest.vchar, is('\u0000'));
        assertThat(dest.vbyte, is((byte) 0));
        assertThat(dest.vshort, is((short) 0));
    }

    @Test
    public void レコードからMapを生成できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        Map<String, Object> dest = BeanUtil.createMapAndCopy(srcRecord, CopyOptions.empty());

        assertThat(dest.get("sample"), is("10"));
        assertThat(dest.get("onlyInSourceRecord"), is("test"));
        assertThat(dest.get("address.postCode"), is("111-2222"));
        assertThat(dest.get("address.addr"), is("東京都江東区"));
        assertThat(dest.get("innerRecord.id"), is(10001));
        assertThat(dest.get("innerRecord.name"), is("中田昇"));
        assertThat(((List<?>)dest.get("strList")).get(0), is("1"));
        assertThat(((List<?>)dest.get("strList")).get(1), is("2"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(0))).postCode, is("111-2222"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(0))).addr, is("東京都新宿区"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(1))).postCode, is("333-4444"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(1))).addr, is("兵庫県神戸市"));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(0))).id, is(10002));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(0))).name, is("武藤菊夜"));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(1))).id, is(10003));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(1))).name, is("猪野麻天"));
        assertThat(((String[])dest.get("strArray"))[0], is("3"));
        assertThat(((String[])dest.get("strArray"))[1], is("4"));
        assertThat(((Address[])dest.get("addressArray"))[0].postCode, is("555-6666"));
        assertThat(((Address[])dest.get("addressArray"))[0].addr, is("大阪府大阪市"));
        assertThat(((Address[])dest.get("addressArray"))[1].postCode, is("777-8888"));
        assertThat(((Address[])dest.get("addressArray"))[1].addr, is("福岡県福岡市"));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[0].id, is(10004));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[0].name, is("神田幹太"));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[1].id, is(10005));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[1].name, is("森川瑛太"));

    }

    @Test
    public void 指定したパラメータのみ使用してレコードからMapを生成できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        Map<String, Object> dest = BeanUtil.createMapAndCopyIncludes(srcRecord, "sample", "strList", "innerRecordArray", "onlyInSourceRecord");

        assertThat(dest.get("sample"), is("10"));
        assertThat(dest.get("onlyInSourceRecord"), is("test"));
        assertThat(dest.containsKey("address"), is(false));
        assertThat(dest.containsKey("innerRecord"), is(false));
        assertThat(((List<?>)dest.get("strList")).get(0), is("1"));
        assertThat(((List<?>)dest.get("strList")).get(1), is("2"));
        assertThat(dest.containsKey("addressList"), is(false));
        assertThat(dest.containsKey("innerRecordList"), is(false));
        assertThat(dest.containsKey("strArray"), is(false));
        assertThat(dest.containsKey("addressArray"), is(false));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[0].id, is(10004));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[0].name, is("神田幹太"));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[1].id, is(10005));
        assertThat(((InnerRecord[])dest.get("innerRecordArray"))[1].name, is("森川瑛太"));

    }

    @Test
    public void 指定したパラメータを除外してレコードからMapを生成できること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        Map<String, Object> dest = BeanUtil.createMapAndCopyExcludes(srcRecord, "sample", "strList", "innerRecordArray", "onlyInSourceRecord");

        assertThat(dest.containsKey("sample"), is(false));
        assertThat(dest.containsKey("onlyInSourceRecord"), is(false));
        assertThat(dest.get("address.postCode"), is("111-2222"));
        assertThat(dest.get("address.addr"), is("東京都江東区"));
        assertThat(dest.get("innerRecord.id"), is(10001));
        assertThat(dest.get("innerRecord.name"), is("中田昇"));
        assertThat(dest.containsKey("strList"), is(false));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(0))).postCode, is("111-2222"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(0))).addr, is("東京都新宿区"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(1))).postCode, is("333-4444"));
        assertThat(((Address)(((List<?>)dest.get("addressList")).get(1))).addr, is("兵庫県神戸市"));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(0))).id, is(10002));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(0))).name, is("武藤菊夜"));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(1))).id, is(10003));
        assertThat(((InnerRecord)(((List<?>)dest.get("innerRecordList")).get(1))).name, is("猪野麻天"));
        assertThat(((String[])dest.get("strArray"))[0], is("3"));
        assertThat(((String[])dest.get("strArray"))[1], is("4"));
        assertThat(((Address[])dest.get("addressArray"))[0].postCode, is("555-6666"));
        assertThat(((Address[])dest.get("addressArray"))[0].addr, is("大阪府大阪市"));
        assertThat(((Address[])dest.get("addressArray"))[1].postCode, is("777-8888"));
        assertThat(((Address[])dest.get("addressArray"))[1].addr, is("福岡県福岡市"));
        assertThat(dest.containsKey("innerRecordArray"), is(false));

    }

    @Test
    public void レコード_to_Map_to_レコードの相互変換ができること() {

        SourceRecord srcRecord = new SourceRecord(
                "10",
                "test",
                new Address("111-2222", "東京都江東区"),
                new InnerRecord(10001, "中田昇"),
                List.of("1", "2"),
                List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")),
                List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")),
                new String[]{"3", "4"},
                new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")},
                new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")}
        );

        Map<String, Object> destMap = BeanUtil.createMapAndCopy(srcRecord, CopyOptions.empty());

        assertThat(destMap.get("sample"), is("10"));
        assertThat(destMap.get("onlyInSourceRecord"), is("test"));
        assertThat(destMap.get("address.postCode"), is("111-2222"));
        assertThat(destMap.get("address.addr"), is("東京都江東区"));
        assertThat(destMap.get("innerRecord.id"), is(10001));
        assertThat(destMap.get("innerRecord.name"), is("中田昇"));
        assertThat(((List<?>)destMap.get("strList")).get(0), is("1"));
        assertThat(((List<?>)destMap.get("strList")).get(1), is("2"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(0))).postCode, is("111-2222"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(0))).addr, is("東京都新宿区"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(1))).postCode, is("333-4444"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(1))).addr, is("兵庫県神戸市"));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(0))).id, is(10002));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(0))).name, is("武藤菊夜"));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(1))).id, is(10003));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(1))).name, is("猪野麻天"));
        assertThat(((String[])destMap.get("strArray"))[0], is("3"));
        assertThat(((String[])destMap.get("strArray"))[1], is("4"));
        assertThat(((Address[])destMap.get("addressArray"))[0].postCode, is("555-6666"));
        assertThat(((Address[])destMap.get("addressArray"))[0].addr, is("大阪府大阪市"));
        assertThat(((Address[])destMap.get("addressArray"))[1].postCode, is("777-8888"));
        assertThat(((Address[])destMap.get("addressArray"))[1].addr, is("福岡県福岡市"));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[0].id, is(10004));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[0].name, is("神田幹太"));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[1].id, is(10005));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[1].name, is("森川瑛太"));

        TestRecord destRecord = BeanUtil.createAndCopy(TestRecord.class, destMap, CopyOptions.empty());

        assertThat(destRecord.sample, is(10));
        assertThat(destRecord.address.postCode, is("111-2222"));
        assertThat(destRecord.address.addr, is("東京都江東区"));
        assertThat(destRecord.innerRecord.id, is(10001));
        assertThat(destRecord.innerRecord.name, is("中田昇"));
        assertThat(destRecord.strList.get(0), is("1"));
        assertThat(destRecord.strList.get(1), is("2"));
        assertThat(destRecord.addressList.get(0).postCode, is("111-2222"));
        assertThat(destRecord.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(destRecord.addressList.get(1).postCode, is("333-4444"));
        assertThat(destRecord.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(destRecord.innerRecordList.get(0).id, is(10002));
        assertThat(destRecord.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(destRecord.innerRecordList.get(1).id, is(10003));
        assertThat(destRecord.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(destRecord.strArray[0], is("3"));
        assertThat(destRecord.strArray[1], is("4"));
        assertThat(destRecord.addressArray[0].postCode, is("555-6666"));
        assertThat(destRecord.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(destRecord.addressArray[1].postCode, is("777-8888"));
        assertThat(destRecord.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(destRecord.innerRecordArray[0].id, is(10004));
        assertThat(destRecord.innerRecordArray[0].name, is("神田幹太"));
        assertThat(destRecord.innerRecordArray[1].id, is(10005));
        assertThat(destRecord.innerRecordArray[1].name, is("森川瑛太"));
    }

    @Test
    public void Map_to_レコード_to_Mapの相互変換ができること() {

        Map<String, Object> src = Map.ofEntries(
                Map.entry("sample", 10),
                Map.entry("address.postCode", "111-2222"),
                Map.entry("address.addr", "東京都江東区"),
                Map.entry("innerRecord.id", 10001),
                Map.entry("innerRecord.name", "中田昇"),
                Map.entry("strList[0]", "1"),
                Map.entry("strList[1]", "2"),
                Map.entry("addressList[0].postCode", "111-2222"),
                Map.entry("addressList[0].addr", "東京都新宿区"),
                Map.entry("addressList[1].postCode", "333-4444"),
                Map.entry("addressList[1].addr", "兵庫県神戸市"),
                Map.entry("innerRecordList[0].id", 10002),
                Map.entry("innerRecordList[0].name", "武藤菊夜"),
                Map.entry("innerRecordList[1].id", 10003),
                Map.entry("innerRecordList[1].name", "猪野麻天"),
                Map.entry("strArray[0]", "3"),
                Map.entry("strArray[1]", "4"),
                Map.entry("addressArray[0].postCode", "555-6666"),
                Map.entry("addressArray[0].addr", "大阪府大阪市"),
                Map.entry("addressArray[1].postCode", "777-8888"),
                Map.entry("addressArray[1].addr", "福岡県福岡市"),
                Map.entry("innerRecordArray[0].id", 10004),
                Map.entry("innerRecordArray[0].name", "神田幹太"),
                Map.entry("innerRecordArray[1].id", 10005),
                Map.entry("innerRecordArray[1].name", "森川瑛太")
        );

        TestRecord destRecord = BeanUtil.createAndCopy(TestRecord.class, src, CopyOptions.empty());

        assertThat(destRecord.sample, is(10));
        assertThat(destRecord.address.postCode, is("111-2222"));
        assertThat(destRecord.address.addr, is("東京都江東区"));
        assertThat(destRecord.innerRecord.id, is(10001));
        assertThat(destRecord.innerRecord.name, is("中田昇"));
        assertThat(destRecord.strList.get(0), is("1"));
        assertThat(destRecord.strList.get(1), is("2"));
        assertThat(destRecord.addressList.get(0).postCode, is("111-2222"));
        assertThat(destRecord.addressList.get(0).addr, is("東京都新宿区"));
        assertThat(destRecord.addressList.get(1).postCode, is("333-4444"));
        assertThat(destRecord.addressList.get(1).addr, is("兵庫県神戸市"));
        assertThat(destRecord.innerRecordList.get(0).id, is(10002));
        assertThat(destRecord.innerRecordList.get(0).name, is("武藤菊夜"));
        assertThat(destRecord.innerRecordList.get(1).id, is(10003));
        assertThat(destRecord.innerRecordList.get(1).name, is("猪野麻天"));
        assertThat(destRecord.strArray[0], is("3"));
        assertThat(destRecord.strArray[1], is("4"));
        assertThat(destRecord.addressArray[0].postCode, is("555-6666"));
        assertThat(destRecord.addressArray[0].addr, is("大阪府大阪市"));
        assertThat(destRecord.addressArray[1].postCode, is("777-8888"));
        assertThat(destRecord.addressArray[1].addr, is("福岡県福岡市"));
        assertThat(destRecord.innerRecordArray[0].id, is(10004));
        assertThat(destRecord.innerRecordArray[0].name, is("神田幹太"));
        assertThat(destRecord.innerRecordArray[1].id, is(10005));
        assertThat(destRecord.innerRecordArray[1].name, is("森川瑛太"));


        Map<String, Object> destMap = BeanUtil.createMapAndCopy(destRecord, CopyOptions.empty());

        assertThat(destMap.get("sample"), is(10));
        assertThat(destMap.get("address.postCode"), is("111-2222"));
        assertThat(destMap.get("address.addr"), is("東京都江東区"));
        assertThat(destMap.get("innerRecord.id"), is(10001));
        assertThat(destMap.get("innerRecord.name"), is("中田昇"));
        assertThat(((List<?>)destMap.get("strList")).get(0), is("1"));
        assertThat(((List<?>)destMap.get("strList")).get(1), is("2"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(0))).postCode, is("111-2222"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(0))).addr, is("東京都新宿区"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(1))).postCode, is("333-4444"));
        assertThat(((Address)(((List<?>)destMap.get("addressList")).get(1))).addr, is("兵庫県神戸市"));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(0))).id, is(10002));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(0))).name, is("武藤菊夜"));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(1))).id, is(10003));
        assertThat(((InnerRecord)(((List<?>)destMap.get("innerRecordList")).get(1))).name, is("猪野麻天"));
        assertThat(((String[])destMap.get("strArray"))[0], is("3"));
        assertThat(((String[])destMap.get("strArray"))[1], is("4"));
        assertThat(((Address[])destMap.get("addressArray"))[0].postCode, is("555-6666"));
        assertThat(((Address[])destMap.get("addressArray"))[0].addr, is("大阪府大阪市"));
        assertThat(((Address[])destMap.get("addressArray"))[1].postCode, is("777-8888"));
        assertThat(((Address[])destMap.get("addressArray"))[1].addr, is("福岡県福岡市"));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[0].id, is(10004));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[0].name, is("神田幹太"));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[1].id, is(10005));
        assertThat(((InnerRecord[])destMap.get("innerRecordArray"))[1].name, is("森川瑛太"));
    }


    @Test
    public void 移送元をMapとするcreateAndCopy_値を変換して設定できること(){
        Map<String, Object> src = new HashMap<>();
        src.put("sample", "123");
        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, src);

        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopy_値を変換して設定できること(){
        SourceBean src = new SourceBean();
        src.setSample("123");
        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, src);
        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyIncludes_値を変換して設定できること(){
        Map<String, Object> src = new HashMap<>();
        src.put("sample", "123");
        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, src, "sample");

        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopyIncludes_値を変換して設定できること(){
        SourceBean src = new SourceBean();
        src.setSample("123");
        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, src, "sample");
        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyExcludes_値を変換して設定できること(){
        Map<String, Object> src = new HashMap<>();
        src.put("sample", "123");
        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, src, "");

        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopyExcludes_値を変換して設定できること(){
        SourceBean src = new SourceBean();
        src.setSample("123");
        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, src, "");
        assertThat(dest.sample(), is(123));
    }

    @Test
    public void 移送元をMapとするcreateAndCopy_nullからなるString配列を渡してもレコードが生成できること() {
        Map<String, Object> input = Map.of(
                "sample", new String[] {null}
        );

        TestRecord actual = BeanUtil.createAndCopy(TestRecord.class, input);
        assertThat(actual.sample(), is(nullValue()));
    }


    public record WithTimestampRecord(Timestamp timestamp){}
    @Test
    public void マイクロ秒を持つTimestampのプロパティがコピーできること() {
        BeanUtilTest.WithTimestamp src = new BeanUtilTest.WithTimestamp();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setNanos(100000001);
        src.setTimestamp(timestamp);

        WithTimestampRecord actual = BeanUtil.createAndCopy(WithTimestampRecord.class, src);
        assertThat(actual.timestamp, is(timestamp));
    }

    @Test
    public void CopyOptionsでincludesPropertiesを指定する() {
        SourceBean src = new SourceBean();
        src.setSample("123");
        src.setAddress(new Address("111-2233", "Nablarch市"));
        src.setInnerRecord(new InnerRecord(10, "testname"));

        CopyOptions options = CopyOptions.options().includes("sample", "innerRecord").build();
        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, src, options);

        assertThat(dest.sample(), is(123));
        assertThat(dest.address(), is(nullValue()));
        assertThat(dest.innerRecord().id(), is(10));
        assertThat(dest.innerRecord().name(), is("testname"));
    }

    @Test
    public void CopyOptionsでexcludesPropertiesを指定する() {
        SourceBean src = new SourceBean();
        src.setSample("123");
        src.setAddress(new Address("111-2233", "Nablarch市"));
        src.setInnerRecord(new InnerRecord(10, "testname"));

        CopyOptions options = CopyOptions.options().excludes("sample", "innerRecord").build();
        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, src, options);

        assertThat(dest.sample(), is(nullValue()));
        assertThat(dest.address().addr, is("Nablarch市"));
        assertThat(dest.address().postCode, is("111-2233"));
        assertThat(dest.innerRecord(), is(nullValue()));
    }

    public record SelfNestedRecord(String foo, String bar, SelfNestedRecord rec) {}
    @Test
    public void ネストしたレコードにはexcludesPropertiesは引き継がれない() {
        SelfNestedRecord srcChild = new SelfNestedRecord("3", "4", null);
        SelfNestedRecord srcRoot = new SelfNestedRecord("1","2", srcChild);

        SelfNestedRecord dest = BeanUtil.createAndCopyExcludes(SelfNestedRecord.class, srcRoot, "bar");

        assertThat(dest.foo(), is("1"));
        assertThat(dest.bar(), is(nullValue()));
        assertThat(dest.rec(), is(not(sameInstance(srcChild))));
        assertThat(dest.rec().foo(), is("3"));
        assertThat(dest.rec().bar(), is("4"));
    }

    @Test
    public void ネストしたレコードにはincludesPropertiesは引き継がれない() {
        SelfNestedRecord srcChild = new SelfNestedRecord("3", "4", null);
        SelfNestedRecord srcRoot = new SelfNestedRecord("1","2", srcChild);

        SelfNestedRecord dest = BeanUtil.createAndCopyIncludes(SelfNestedRecord.class, srcRoot, "bar", "rec");

        assertThat(dest.foo(), is(nullValue()));
        assertThat(dest.bar(), is("2"));
        assertThat(dest.rec(), is(not(sameInstance(srcChild))));
        assertThat(dest.rec().foo(), is("3"));
        assertThat(dest.rec().bar(), is("4"));
    }

    @Test
    public void getPropertyメソッドでレコードからプロパティの値を取得できること() {
        SourceRecord src = new SourceRecord("10",null,null,null,null,null,null,null,null,null);
        assertThat(BeanUtil.getProperty(src, "sample"), is("10"));
    }

    @Test
    public void getPropertyメソッドでレコードから値を変換してプロパティの値を取得できること() {
        SourceRecord src = new SourceRecord(null,"25",null,null,null,null,null,null,null,null);
        assertThat(BeanUtil.getProperty(src, "onlyInSourceRecord", Integer.class), is(25));
        assertThat(BeanUtil.getProperty(src, "onlyInSourceRecord", null), is("25"));
    }

    @Test
    public void 移送元をオブジェクトとするcopyメソッドの引数に_destBeanとしてレコードを指定した場合_実行時例外が発生すること() {
        SourceRecord srcRecord = new SourceRecord(null, null, null, null, null, null, null, null, null, null);
        TestRecord destRecord = new TestRecord(null, null, null, null, null, null, null, null, null, null);
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.copy(srcRecord, destRecord, CopyOptions.empty()));

        assertThat(result.getMessage(), is("The destination bean must not be a record."));

    }

    @Test
    public void 移送元をMapとするcopyメソッドの引数に_destBeanとしてレコードを指定した場合_実行時例外が発生すること() {
        Map<String, ?> srcMap = Map.of();
        TestRecord destRecord = new TestRecord(null, null, null, null, null, null, null, null, null, null);
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.copy(TestRecord.class, destRecord, srcMap, CopyOptions.empty()));

        assertThat(result.getMessage(), is("The target bean class must not be a record class."));

    }

    @Test
    public void setPropertyメソッドの引数に_destBeanとしてレコードを指定した場合_実行時例外が発生すること() {
        TestRecord destRecord = new TestRecord(null, null, null, null, null, null, null, null, null, null);
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.setProperty(destRecord, "sample", 10));

        assertThat(result.getMessage(), is("The target bean must not be a record."));

    }

    @Test
    public void getPropertyDescriptorsメソッドの引数にレコードを指定した場合_実行時例外が発生すること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getPropertyDescriptors(TestRecord.class));
        assertThat(result.getMessage(), is("The target bean class must not be a record class."));
    }

    @Test
    public void getPropertyDescriptorメソッドの引数にレコードを指定した場合_実行時例外が発生すること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getPropertyDescriptor(TestRecord.class, "sample"));
        assertThat(result.getMessage(), is("The target bean class must not be a record class."));
    }

    @Test
    public void getRecordComponentsメソッドの引数にBeanを指定した場合_実行時例外が発生すること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getRecordComponents(TestBean.class));
        assertThat(result.getMessage(), is("The target bean class must be a record class."));
    }

    @Test
    public void getRecordComponentメソッドの引数にBeanを指定した場合_実行時例外が発生すること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getRecordComponent(TestBean.class, "sample"));
        assertThat(result.getMessage(), is("The target bean class must be a record class."));
    }

    @Test
    public void getRecordComponentsメソッドからレコードコンポーネントを受け取れること() {
        RecordComponent[] rcs = BeanUtil.getRecordComponents(TestRecord.class);
        assertThat(rcs.length, is(10));
    }

    @Test
    public void getRecordComponentメソッドからレコードコンポーネントを受け取れること() {
        RecordComponent rc = BeanUtil.getRecordComponent(TestRecord.class, "innerRecord");
        assertThat(rc.getName(), is("innerRecord"));
    }

    @Test
    public void getPropertyDescriptorsメソッドにレコードを渡した場合_実行時例外が発生すること () {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getPropertyDescriptors(TestRecord.class));
        assertThat(result.getMessage(), is("The target bean class must not be a record class."));
    }

    @Test
    public void getPropertyDescriptorメソッドにレコードを渡した場合_実行時例外が発生すること () {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getPropertyDescriptor(TestRecord.class, "sample"));
        assertThat(result.getMessage(), is("The target bean class must not be a record class."));
    }

    @Test
    public void getRecordComponentsメソッドにレコードを渡した場合_実行時例外が発生すること () {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getRecordComponents(TestBean.class));
        assertThat(result.getMessage(), is("The target bean class must be a record class."));
    }

    @Test
    public void getRecordComponentメソッドにレコードを渡した場合_実行時例外が発生すること () {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () -> BeanUtil.getRecordComponent(TestBean.class, "sample"));
        assertThat(result.getMessage(), is("The target bean class must be a record class."));
    }

    @Test
    public void getPropertyNamesメソッドからすべてのレコードプロパティ名を受け取れること() {
        Set<String> propertyNames = BeanUtil.getPropertyNames(TestRecord.class);
        assertThat(propertyNames.size(), is(10));
        assertThat(propertyNames, containsInAnyOrder("sample", "onlyInTestRecord", "address", "innerRecord", "strList", "addressList", "innerRecordList", "strArray", "addressArray", "innerRecordArray"));
    }

    @Test
    public void getPropertyTypeメソッドでレコードプロパティの型を受け取れること() {
        Class<?> propertyType = BeanUtil.getPropertyType(TestRecord.class, "innerRecord");
        assertThat(propertyType.getName(), is("nablarch.core.beans.BeanUtilForRecordTest$InnerRecord"));
    }

    @Test
    public void getReadMethodメソッドでレコードプロパティのgetterを受け取れること() {
        Method accessor = BeanUtil.getReadMethod(TestRecord.class, "innerRecord");
        assertThat(accessor.getName(), is("innerRecord"));
    }

    @Test
    public void getPropertyメソッドでレコードからString配列のプロパティの値を取得できること() {
        SourceRecord src = new SourceRecord(null,null,null,null,null,null,null,new String[]{"test1", "test2"},null,null);
        String[] result = (String[])BeanUtil.getProperty(src, "strArray");
        assertThat(result[0], is("test1"));
        assertThat(result[1], is("test2"));
    }

    @Test
    public void getPropertyメソッドでレコードに存在しないプロパティを指定した場合_実行時例外が送出されること() {
        SourceRecord src = new SourceRecord("10",null,null,null,null,null,null,null,null,null);
        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.getProperty(src, "invalid"));
        assertThat(result.getMessage(), is("The property does not exist in the bean or record. property name: invalid"));
    }


    public static class BeanWithNoGetterProperty {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public void setSample(String sample) {
            // nop
        }
    }

    public record RecordForNoGetterProperty(String test, String sample) {}

    @Test
    public void 移送元のgetterがないプロパティはコピーされないこと() {
        BeanWithNoGetterProperty src = new BeanWithNoGetterProperty();
        src.setTest("test");

        RecordForNoGetterProperty dest = BeanUtil.createAndCopy(RecordForNoGetterProperty.class, src);
        assertThat(dest.test, is("test"));
        assertThat(dest.sample, is(nullValue()));

    }


    public static class BeanWithPrivateGetterProperty {
        private String test;
        private String sample;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        private String getSample() {
            return sample;
        }

        public void setSample(String sample) {
            this.sample = sample;
        }
    }


    @Test
    public void 移送元のgetterがprivateの場合はコピーされないこと() {
        BeanWithNoGetterProperty src = new BeanWithNoGetterProperty();
        src.setTest("test");

        RecordForNoGetterProperty dest = BeanUtil.createAndCopy(RecordForNoGetterProperty.class, src);
        assertThat(dest.test, is("test"));
        assertThat(dest.sample, is(nullValue()));
    }


}
