package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.*;

/**
 * {@link BeanUtil}の、レコード型に対するテストクラス。
 *
 * @author Takayuki Uchida
 */
@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilForRecordTest {

    @SuppressWarnings("deprecation")
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

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

    @Test
    public void Mapからレコードに値を設定できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

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
    public void Null値のプロパティを持つMapからレコードを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", null);
            put("address.postCode", null);
            put("address.addr", null);
            put("innerRecord.id", null);
            put("innerRecord.name", null);
            put("strList[0]", null);
            put("strList[1]", null);
            put("addressList[0].postCode", null);
            put("addressList[0].addr", null);
            put("addressList[1].postCode", null);
            put("addressList[1].addr", null);
            put("innerRecordList[0].id", null);
            put("innerRecordList[0].name", null);
            put("innerRecordList[1].id", null);
            put("innerRecordList[1].name", null);
            put("strArray[0]", null);
            put("strArray[1]", null);
            put("addressArray[0].postCode", null);
            put("addressArray[0].addr", null);
            put("addressArray[1].postCode", null);
            put("addressArray[1].addr", null);
            put("innerRecordArray[0].id", null);
            put("innerRecordArray[0].name", null);
            put("innerRecordArray[1].id", null);
            put("innerRecordArray[1].name", null);
        }};

        TestRecord dest = BeanUtil.createAndCopy(TestRecord.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address.postCode, is(nullValue()));
        assertThat(dest.address.addr, is(nullValue()));
        assertThat(dest.innerRecord.id, is(nullValue()));
        assertThat(dest.innerRecord.name, is(nullValue()));
        assertThat(dest.strList.get(0), is(nullValue()));
        assertThat(dest.strList.get(1), is(nullValue()));
        assertThat(dest.addressList.get(0).postCode, is(nullValue()));
        assertThat(dest.addressList.get(0).addr, is(nullValue()));
        assertThat(dest.addressList.get(1).postCode, is(nullValue()));
        assertThat(dest.addressList.get(1).addr, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).name, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).name, is(nullValue()));
        assertThat(dest.strArray[0], is(nullValue()));
        assertThat(dest.strArray[1], is(nullValue()));
        assertThat(dest.addressArray[0].postCode, is(nullValue()));
        assertThat(dest.addressArray[0].addr, is(nullValue()));
        assertThat(dest.addressArray[1].postCode, is(nullValue()));
        assertThat(dest.addressArray[1].addr, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(nullValue()));
        assertThat(dest.innerRecordArray[0].name, is(nullValue()));
        assertThat(dest.innerRecordArray[1].id, is(nullValue()));
        assertThat(dest.innerRecordArray[1].name, is(nullValue()));



    }

    @Test
    public void 指定したパラメータのみ使用してレコードを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

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



        TestRecord dest = BeanUtil.createAndCopyIncludes(TestRecord.class, srcMap, includeParamList);

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

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            String tmp = dest.strArray[1];
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Address tmp = dest.addressArray[1];
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            InnerRecord tmp = dest.innerRecordArray[1];
        });
    }

    @Test
    public void 指定したトップレベルパラメータのみ使用してレコードを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address", new Address("111-2222", "東京都江東区") );
            put("innerRecord", new InnerRecord(10001, "中田昇"));
            put("strList", List.of("1", "2"));
            put("addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")));
            put("innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")));
            put("strArray", new String[]{"3", "4"});
            put("addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")});
            put("innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")});
        }};

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
    public void 指定したパラメータを除外してレコードを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", 10);
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

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

        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, srcMap, includeParamList);

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

        assertThrows(IndexOutOfBoundsException.class, () -> {
            String tmp = dest.strList.get(1);
        });

    }

    @Test
    public void 指定したトップレベルパラメータを除外してレコードを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10");
            put("address", new Address("111-2222", "東京都江東区") );
            put("innerRecord", new InnerRecord(10001, "中田昇"));
            put("strList", List.of("1", "2"));
            put("addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")));
            put("innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")));
            put("strArray", new String[]{"3", "4"});
            put("addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")});
            put("innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")});
        }};

        String[] includeParamList = new String[] {
                "sample",
                "address",
                "addressList",
                "strArray",
                "innerRecordArray"
        };

        TestRecord dest = BeanUtil.createAndCopyExcludes(TestRecord.class, srcMap, includeParamList);

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

        Map<String, Object> srcMap = new HashMap<>();

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
    public void プリミティブ型のコンポーネントに対応するパラメタが存在しない場合_デフォルト値で置換されること() {
        TestPrimRecord dest = BeanUtil.createAndCopy(TestPrimRecord.class, new HashMap<>(), CopyOptions.empty());
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
    public void MapからBeanに値を設定できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

        TestBean dest = BeanUtil.createAndCopy(TestBean.class, srcMap, CopyOptions.empty());

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
    public void Null値のプロパティを持つMapからBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", null);
            put("address.postCode", null);
            put("address.addr", null);
            put("innerRecord.id", null);
            put("innerRecord.name", null);
            put("strList[0]", null);
            put("strList[1]", null);
            put("addressList[0].postCode", null);
            put("addressList[0].addr", null);
            put("addressList[1].postCode", null);
            put("addressList[1].addr", null);
            put("innerRecordList[0].id", null);
            put("innerRecordList[0].name", null);
            put("innerRecordList[1].id", null);
            put("innerRecordList[1].name", null);
            put("strArray[0]", null);
            put("strArray[1]", null);
            put("addressArray[0].postCode", null);
            put("addressArray[0].addr", null);
            put("addressArray[1].postCode", null);
            put("addressArray[1].addr", null);
            put("innerRecordArray[0].id", null);
            put("innerRecordArray[0].name", null);
            put("innerRecordArray[1].id", null);
            put("innerRecordArray[1].name", null);
        }};

        TestBean dest = BeanUtil.createAndCopy(TestBean.class, srcMap, CopyOptions.empty());

        assertThat(dest.sample, is(nullValue()));
        assertThat(dest.address.postCode, is(nullValue()));
        assertThat(dest.address.addr, is(nullValue()));
        assertThat(dest.innerRecord.id, is(nullValue()));
        assertThat(dest.innerRecord.name, is(nullValue()));
        assertThat(dest.strList.get(0), is(nullValue()));
        assertThat(dest.strList.get(1), is(nullValue()));
        assertThat(dest.addressList.get(0).postCode, is(nullValue()));
        assertThat(dest.addressList.get(0).addr, is(nullValue()));
        assertThat(dest.addressList.get(1).postCode, is(nullValue()));
        assertThat(dest.addressList.get(1).addr, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(0).name, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).id, is(nullValue()));
        assertThat(dest.innerRecordList.get(1).name, is(nullValue()));
        assertThat(dest.strArray[0], is(nullValue()));
        assertThat(dest.strArray[1], is(nullValue()));
        assertThat(dest.addressArray[0].postCode, is(nullValue()));
        assertThat(dest.addressArray[0].addr, is(nullValue()));
        assertThat(dest.addressArray[1].postCode, is(nullValue()));
        assertThat(dest.addressArray[1].addr, is(nullValue()));
        assertThat(dest.innerRecordArray[0].id, is(nullValue()));
        assertThat(dest.innerRecordArray[0].name, is(nullValue()));
        assertThat(dest.innerRecordArray[1].id, is(nullValue()));
        assertThat(dest.innerRecordArray[1].name, is(nullValue()));



    }

    @Test
    public void 指定したパラメータのみ使用してBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

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

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            String tmp = dest.strArray[1];
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            Address tmp = dest.addressArray[1];
        });

        assertThrows(ArrayIndexOutOfBoundsException.class, () -> {
            InnerRecord tmp = dest.innerRecordArray[1];
        });
    }

    @Test
    public void 指定したトップレベルパラメータのみ使用してBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10"); // StringからIntegerへ変換できることも合わせて確認する
            put("address", new Address("111-2222", "東京都江東区") );
            put("innerRecord", new InnerRecord(10001, "中田昇"));
            put("strList", List.of("1", "2"));
            put("addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")));
            put("innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")));
            put("strArray", new String[]{"3", "4"});
            put("addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")});
            put("innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")});
        }};

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
    public void 指定したパラメータを除外してBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", 10);
            put("address.postCode", "111-2222");
            put("address.addr", "東京都江東区");
            put("innerRecord.id", 10001);
            put("innerRecord.name", "中田昇");
            put("strList[0]", "1");
            put("strList[1]", "2");
            put("addressList[0].postCode", "111-2222");
            put("addressList[0].addr", "東京都新宿区");
            put("addressList[1].postCode", "333-4444");
            put("addressList[1].addr", "兵庫県神戸市");
            put("innerRecordList[0].id", 10002);
            put("innerRecordList[0].name", "武藤菊夜");
            put("innerRecordList[1].id", 10003);
            put("innerRecordList[1].name", "猪野麻天");
            put("strArray[0]", "3");
            put("strArray[1]", "4");
            put("addressArray[0].postCode", "555-6666");
            put("addressArray[0].addr", "大阪府大阪市");
            put("addressArray[1].postCode", "777-8888");
            put("addressArray[1].addr", "福岡県福岡市");
            put("innerRecordArray[0].id", 10004);
            put("innerRecordArray[0].name", "神田幹太");
            put("innerRecordArray[1].id", 10005);
            put("innerRecordArray[1].name", "森川瑛太");
        }};

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

        assertThrows(IndexOutOfBoundsException.class, () -> {
            String tmp = dest.strList.get(1);
        });

    }

    @Test
    public void 指定したトップレベルパラメータを除外してBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>() {{
            put("sample", "10");
            put("address", new Address("111-2222", "東京都江東区") );
            put("innerRecord", new InnerRecord(10001, "中田昇"));
            put("strList", List.of("1", "2"));
            put("addressList", List.of(new Address("111-2222", "東京都新宿区"), new Address("333-4444", "兵庫県神戸市")));
            put("innerRecordList", List.of(new InnerRecord(10002, "武藤菊夜"), new InnerRecord(10003, "猪野麻天")));
            put("strArray", new String[]{"3", "4"});
            put("addressArray", new Address[]{new Address("555-6666", "大阪府大阪市"), new Address("777-8888", "福岡県福岡市")});
            put("innerRecordArray", new InnerRecord[]{new InnerRecord(10004, "神田幹太"), new InnerRecord(10005, "森川瑛太")});
        }};

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
    public void 空のMapからBeanを生成できること() {

        Map<String, Object> srcMap = new HashMap<>();

        TestBean dest = BeanUtil.createAndCopy(TestBean.class, srcMap, CopyOptions.empty());

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
    public void NullのMapからBeanを生成できること() {

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

}
