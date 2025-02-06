package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class BeanUtilForMapSourceCopyTest {

    @Before
    public void setUp() {
        // デフォルト設定で動作させるよう、リポジトリをクリアする。
        SystemRepository.clear();
        OnMemoryLogWriter.clear();
        //BeanDescriptorのキャッシュをクリアしておく
        BeanUtil.clearCache();
    }

    @Test
    public void copy_各プロパティに設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, ?> srcMap = createTestData("");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        assertTestClass1(dstBean);
    }

    @Test
    public void copy_大量データの動作チェック() {
        // 報告ケース
        Map<String, Object> srcMap = new HashMap<>();
        List<Item> list = new ArrayList<>();
        int itemNumber = 1000;
        for(int i = 0; i < itemNumber; i++) {
            String index = String.valueOf(i);
            srcMap.put("items[" + index + "].keyA", "value" + index+ "-A");
            srcMap.put("items[" + index + "].keyB", "value" + index+ "-B");
            srcMap.put("items[" + index + "].keyC", "value" + index+ "-C");
            srcMap.put("items[" + index + "].keyD", "value" + index+ "-D");
            srcMap.put("items[" + index + "].keyE", "value" + index+ "-E");
            srcMap.put("items[" + index + "].keyF", "value" + index+ "-F");
            srcMap.put("items[" + index + "].keyG", "value" + index+ "-G");
            srcMap.put("items[" + index + "].keyH", "value" + index+ "-H");
            srcMap.put("items[" + index + "].keyI", "value" + index+ "-I");
            srcMap.put("items[" + index + "].keyJ", "value" + index+ "-J");
        }

        Form form = new Form();
        form.setItems(list);

        long start = System.currentTimeMillis();
        BeanUtil.copy(form.getClass(), form, srcMap, CopyOptions.empty());
        long end = System.currentTimeMillis();

        for (int i = 0; i < itemNumber; i++) {
            String index = String.valueOf(i);

            assertEquals("value" + index + "-A", form.getItems().get(i).getKeyA());
            assertEquals("value" + index + "-B", form.getItems().get(i).getKeyB());
            assertEquals("value" + index + "-C", form.getItems().get(i).getKeyC());
            assertEquals("value" + index + "-D", form.getItems().get(i).getKeyD());
            assertEquals("value" + index + "-E", form.getItems().get(i).getKeyE());
            assertEquals("value" + index + "-F", form.getItems().get(i).getKeyF());
            assertEquals("value" + index + "-G", form.getItems().get(i).getKeyG());
            assertEquals("value" + index + "-H", form.getItems().get(i).getKeyH());
            assertEquals("value" + index + "-I", form.getItems().get(i).getKeyI());
            assertEquals("value" + index + "-J", form.getItems().get(i).getKeyJ());
        }

        assertTrue((end - start) < 3000);

        // レコード
        RecForm recForm = new RecForm();
        recForm.setItems(new ArrayList<>());
        start = System.currentTimeMillis();
        BeanUtil.copy(recForm.getClass(), recForm, srcMap, CopyOptions.empty());
        end = System.currentTimeMillis();

        for (int i = 0; i < itemNumber; i++) {
            String index = String.valueOf(i);

            assertEquals("value" + index + "-A", recForm.getItems().get(i).keyA());
            assertEquals("value" + index + "-B", recForm.getItems().get(i).keyB());
            assertEquals("value" + index + "-C", recForm.getItems().get(i).keyC());
            assertEquals("value" + index + "-D", recForm.getItems().get(i).keyD());
            assertEquals("value" + index + "-E", recForm.getItems().get(i).keyE());
            assertEquals("value" + index + "-F", recForm.getItems().get(i).keyF());
            assertEquals("value" + index + "-G", recForm.getItems().get(i).keyG());
            assertEquals("value" + index + "-H", recForm.getItems().get(i).keyH());
            assertEquals("value" + index + "-I", recForm.getItems().get(i).keyI());
            assertEquals("value" + index + "-J", recForm.getItems().get(i).keyJ());
        }
        assertTrue((end - start) < 1000);
    }

    @Test
    public void setProperty_プロパティに設定する() {

        TestClass1 dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Flg", true);

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Flg", "true");

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Number", 123);
        assertEquals(123, dstBean.getTest1Number());

        BeanUtil.setProperty(dstBean, "test1Number", "123");
        assertEquals(123, dstBean.getTest1Number());

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1IntNumber", 1234);
        assertEquals(1234, (long)dstBean.getTest1IntNumber());

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1IntNumber", "1234");
        assertEquals(1234, (long)dstBean.getTest1IntNumber());

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Name", "test");
        assertEquals("test", dstBean.getTest1Name());

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Date", "20250204");
        assertEquals(LocalDate.of(2025,2,4), dstBean.getTest1Date());

        dstBean = new TestClass1();
        BeanUtil.setProperty(dstBean, "test1Date", LocalDate.of(2025,2,4));
        assertEquals(LocalDate.of(2025,2,4), dstBean.getTest1Date());
    }

    private Map<String, ?> createTestData(String root) {
        // src設定
        Map<String, String> srcMap = new HashMap<>();
        srcMap.put("test1Flg", "true");
        srcMap.put("test1Number", "123");
        srcMap.put("test1Name", "テスト１名称");
        srcMap.put("test1IntNumber", "12345");
        srcMap.put("test1Date", "20250204");
        // ------
        srcMap.put("test1OneNestClass.oneNumber","5001");
        srcMap.put("test1OneNestClass.oneName","ネストクラス-テスト");
        srcMap.put("test1OneNestClass.oneArray[0]","ネストクラス-配列テスト0");
        srcMap.put("test1OneNestClass.oneArray[1]","ネストクラス-配列テスト1");
        srcMap.put("test1OneNestClass.oneList[0]","ネストクラス-リストテスト0");
        srcMap.put("test1OneNestClass.oneList[1]","ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneNumber","505001");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneName","ネストクラス-ネストクラス-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneArray[0]","ネストクラス-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneArray[1]","ネストクラス-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneList[0]","ネストクラス-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneList[1]","ネストクラス-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneNumber","501001");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneName","ネストクラス-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneArray[0]","ネストクラス-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneArray[1]","ネストクラス-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneList[0]","ネストクラス-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneList[1]","ネストクラス-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneNumber","501101");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneName","ネストクラス-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneArray[0]","ネストクラス-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneArray[1]","ネストクラス-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneList[0]","ネストクラス-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[1].oneList[1]","ネストクラス-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneNumber","502001");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneName","ネストクラス-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneArray[0]","ネストクラス-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneArray[1]","ネストクラス-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneList[0]","ネストクラス-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneList[1]","ネストクラス-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneNumber","502101");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneName","ネストクラス-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneArray[0]","ネストクラス-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneArray[1]","ネストクラス-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneList[0]","ネストクラス-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[1].oneList[1]","ネストクラス-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooNumber","506001");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooName","ネストクラス-ネストレコード-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooArray[0]","ネストクラス-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooArray[1]","ネストクラス-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooList[0]","ネストクラス-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooList[1]","ネストクラス-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooNumber","503001");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooName","ネストクラス-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooArray[0]","ネストクラス-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooArray[1]","ネストクラス-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooList[0]","ネストクラス-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooList[1]","ネストクラス-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooNumber","503101");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooName","ネストクラス-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooArray[0]","ネストクラス-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooArray[1]","ネストクラス-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooList[0]","ネストクラス-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[1].fooList[1]","ネストクラス-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooNumber","504001");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooName","ネストクラス-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooArray[0]","ネストクラス-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooArray[1]","ネストクラス-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooList[0]","ネストクラス-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooList[1]","ネストクラス-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooNumber","504101");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooName","ネストクラス-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooArray[0]","ネストクラス-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooArray[1]","ネストクラス-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooList[0]","ネストクラス-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[1].fooList[1]","ネストクラス-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1Array[0]","配列テスト0");
        srcMap.put("test1Array[1]","配列テスト1");
        srcMap.put("test1OneNestClassArray[0].oneNumber","1001");
        srcMap.put("test1OneNestClassArray[0].oneName","ネストクラス配列0-テスト");
        srcMap.put("test1OneNestClassArray[0].oneArray[0]","ネストクラス配列0-配列テスト0");
        srcMap.put("test1OneNestClassArray[0].oneArray[1]","ネストクラス配列0-配列テスト1");
        srcMap.put("test1OneNestClassArray[0].oneList[0]","ネストクラス配列0-リストテスト0");
        srcMap.put("test1OneNestClassArray[0].oneList[1]","ネストクラス配列0-リストテスト1");
        srcMap.put("test1OneNestClassArray[1].oneNumber","1101");
        srcMap.put("test1OneNestClassArray[1].oneName","ネストクラス配列1-テスト");
        srcMap.put("test1OneNestClassArray[1].oneArray[0]","ネストクラス配列1-配列テスト0");
        srcMap.put("test1OneNestClassArray[1].oneArray[1]","ネストクラス配列1-配列テスト1");
        srcMap.put("test1OneNestClassArray[1].oneList[0]","ネストクラス配列1-リストテスト0");
        srcMap.put("test1OneNestClassArray[1].oneList[1]","ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneNumber","105001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneName","ネストクラス配列0-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneArray[0]","ネストクラス配列0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneArray[1]","ネストクラス配列0-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneList[0]","ネストクラス配列0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneList[1]","ネストクラス配列0-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneNumber","101001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneName","ネストクラス配列0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneArray[0]","ネストクラス配列0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneArray[1]","ネストクラス配列0-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneList[0]","ネストクラス配列0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneList[1]","ネストクラス配列0-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneNumber","101101");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneName","ネストクラス配列0-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneArray[0]","ネストクラス配列0-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneArray[1]","ネストクラス配列0-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneList[0]","ネストクラス配列0-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[1].oneList[1]","ネストクラス配列0-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneNumber","102001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneName","ネストクラス配列0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneArray[0]","ネストクラス配列0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneArray[1]","ネストクラス配列0-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneList[0]","ネストクラス配列0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneList[1]","ネストクラス配列0-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneNumber","102101");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneName","ネストクラス配列0-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneArray[0]","ネストクラス配列0-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneArray[1]","ネストクラス配列0-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneList[0]","ネストクラス配列0-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[1].oneList[1]","ネストクラス配列0-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooNumber","106001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooName","ネストクラス配列0-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooArray[0]","ネストクラス配列0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooArray[1]","ネストクラス配列0-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooList[0]","ネストクラス配列0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooList[1]","ネストクラス配列0-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooNumber","103001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooName","ネストクラス配列0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooArray[0]","ネストクラス配列0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooArray[1]","ネストクラス配列0-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooList[0]","ネストクラス配列0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooList[1]","ネストクラス配列0-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooNumber","103101");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooName","ネストクラス配列0-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooArray[0]","ネストクラス配列0-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooArray[1]","ネストクラス配列0-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooList[0]","ネストクラス配列0-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[1].fooList[1]","ネストクラス配列0-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooNumber","104001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooName","ネストクラス配列0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooArray[0]","ネストクラス配列0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooArray[1]","ネストクラス配列0-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooList[0]","ネストクラス配列0-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooList[1]","ネストクラス配列0-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooNumber","104101");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooName","ネストクラス配列0-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooArray[0]","ネストクラス配列0-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooArray[1]","ネストクラス配列0-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooList[0]","ネストクラス配列0-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[1].fooList[1]","ネストクラス配列0-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneNumber","115001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneName","ネストクラス配列1-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneArray[0]","ネストクラス配列1-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneArray[1]","ネストクラス配列1-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneList[0]","ネストクラス配列1-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClass.oneList[1]","ネストクラス配列1-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneNumber","111001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneName","ネストクラス配列1-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneArray[0]","ネストクラス配列1-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneArray[1]","ネストクラス配列1-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneList[0]","ネストクラス配列1-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[0].oneList[1]","ネストクラス配列1-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneNumber","111101");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneName","ネストクラス配列1-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneArray[0]","ネストクラス配列1-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneArray[1]","ネストクラス配列1-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneList[0]","ネストクラス配列1-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassArray[1].oneList[1]","ネストクラス配列1-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneNumber","112001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneName","ネストクラス配列1-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneArray[0]","ネストクラス配列1-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneArray[1]","ネストクラス配列1-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneList[0]","ネストクラス配列1-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[0].oneList[1]","ネストクラス配列1-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneNumber","112101");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneName","ネストクラス配列1-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneArray[0]","ネストクラス配列1-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneArray[1]","ネストクラス配列1-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneList[0]","ネストクラス配列1-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestClassList[1].oneList[1]","ネストクラス配列1-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooNumber","116001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooName","ネストクラス配列1-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooArray[0]","ネストクラス配列1-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooArray[1]","ネストクラス配列1-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooList[0]","ネストクラス配列1-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecord.fooList[1]","ネストクラス配列1-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooNumber","113001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooName","ネストクラス配列1-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooArray[0]","ネストクラス配列1-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooArray[1]","ネストクラス配列1-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooList[0]","ネストクラス配列1-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[0].fooList[1]","ネストクラス配列1-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooNumber","113101");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooName","ネストクラス配列1-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooArray[0]","ネストクラス配列1-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooArray[1]","ネストクラス配列1-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooList[0]","ネストクラス配列1-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordArray[1].fooList[1]","ネストクラス配列1-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooNumber","114001");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooName","ネストクラス配列1-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooArray[0]","ネストクラス配列1-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooArray[1]","ネストクラス配列1-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooList[0]","ネストクラス配列1-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[0].fooList[1]","ネストクラス配列1-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooNumber","114101");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooName","ネストクラス配列1-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooArray[0]","ネストクラス配列1-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooArray[1]","ネストクラス配列1-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooList[0]","ネストクラス配列1-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassArray[1].oneNoNestRecordList[1].fooList[1]","ネストクラス配列1-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1List[0]","リストテスト0");
        srcMap.put("test1List[1]","リストテスト1");
        srcMap.put("test1OneNestClassList[0].oneNumber","2001");
        srcMap.put("test1OneNestClassList[0].oneName","ネストクラスリスト0-テスト");
        srcMap.put("test1OneNestClassList[0].oneArray[0]","ネストクラスリスト0-配列テスト0");
        srcMap.put("test1OneNestClassList[0].oneArray[1]","ネストクラスリスト0-配列テスト1");
        srcMap.put("test1OneNestClassList[0].oneList[0]","ネストクラスリスト0-リストテスト0");
        srcMap.put("test1OneNestClassList[0].oneList[1]","ネストクラスリスト0-リストテスト1");
        srcMap.put("test1OneNestClassList[1].oneNumber","2101");
        srcMap.put("test1OneNestClassList[1].oneName","ネストクラスリスト1-テスト");
        srcMap.put("test1OneNestClassList[1].oneArray[0]","ネストクラスリスト1-配列テスト0");
        srcMap.put("test1OneNestClassList[1].oneArray[1]","ネストクラスリスト1-配列テスト1");
        srcMap.put("test1OneNestClassList[1].oneList[0]","ネストクラスリスト1-リストテスト0");
        srcMap.put("test1OneNestClassList[1].oneList[1]","ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneNumber","205001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneName","ネストクラスリスト0-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneArray[0]","ネストクラスリスト0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneArray[1]","ネストクラスリスト0-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneList[0]","ネストクラスリスト0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneList[1]","ネストクラスリスト0-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneNumber","201001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneName","ネストクラスリスト0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneArray[0]","ネストクラスリスト0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneArray[1]","ネストクラスリスト0-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneList[0]","ネストクラスリスト0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneList[1]","ネストクラスリスト0-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneNumber","201101");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneName","ネストクラスリスト0-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneArray[0]","ネストクラスリスト0-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneArray[1]","ネストクラスリスト0-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneList[0]","ネストクラスリスト0-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[1].oneList[1]","ネストクラスリスト0-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneNumber","202001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneName","ネストクラスリスト0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneArray[0]","ネストクラスリスト0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneArray[1]","ネストクラスリスト0-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneList[0]","ネストクラスリスト0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneList[1]","ネストクラスリスト0-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneNumber","202101");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneName","ネストクラスリスト0-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneArray[0]","ネストクラスリスト0-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneArray[1]","ネストクラスリスト0-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneList[0]","ネストクラスリスト0-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[1].oneList[1]","ネストクラスリスト0-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooNumber","206001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooName","ネストクラスリスト0-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooArray[0]","ネストクラスリスト0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooArray[1]","ネストクラスリスト0-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooList[0]","ネストクラスリスト0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooList[1]","ネストクラスリスト0-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooNumber","203001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooName","ネストクラスリスト0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooArray[0]","ネストクラスリスト0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooArray[1]","ネストクラスリスト0-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooList[0]","ネストクラスリスト0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooList[1]","ネストクラスリスト0-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooNumber","203101");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooName","ネストクラスリスト0-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooArray[0]","ネストクラスリスト0-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooArray[1]","ネストクラスリスト0-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooList[0]","ネストクラスリスト0-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[1].fooList[1]","ネストクラスリスト0-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooNumber","204001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooName","ネストクラスリスト0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooArray[0]","ネストクラスリスト0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooArray[1]","ネストクラスリスト0-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooList[0]","ネストクラスリスト0-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooList[1]","ネストクラスリスト0-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooNumber","204101");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooName","ネストクラスリスト0-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooArray[0]","ネストクラスリスト0-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooArray[1]","ネストクラスリスト0-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooList[0]","ネストクラスリスト0-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[1].fooList[1]","ネストクラスリスト0-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneNumber","215001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneName","ネストクラスリスト1-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneArray[0]","ネストクラスリスト1-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneArray[1]","ネストクラスリスト1-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneList[0]","ネストクラスリスト1-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClass.oneList[1]","ネストクラスリスト1-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneNumber","211001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneName","ネストクラスリスト1-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneArray[0]","ネストクラスリスト1-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneArray[1]","ネストクラスリスト1-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneList[0]","ネストクラスリスト1-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[0].oneList[1]","ネストクラスリスト1-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneNumber","211101");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneName","ネストクラスリスト1-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneArray[0]","ネストクラスリスト1-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneArray[1]","ネストクラスリスト1-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneList[0]","ネストクラスリスト1-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassArray[1].oneList[1]","ネストクラスリスト1-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneNumber","212001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneName","ネストクラスリスト1-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneArray[0]","ネストクラスリスト1-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneArray[1]","ネストクラスリスト1-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneList[0]","ネストクラスリスト1-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[0].oneList[1]","ネストクラスリスト1-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneNumber","212101");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneName","ネストクラスリスト1-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneArray[0]","ネストクラスリスト1-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneArray[1]","ネストクラスリスト1-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneList[0]","ネストクラスリスト1-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestClassList[1].oneList[1]","ネストクラスリスト1-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooNumber","216001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooName","ネストクラスリスト1-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooArray[0]","ネストクラスリスト1-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooArray[1]","ネストクラスリスト1-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooList[0]","ネストクラスリスト1-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecord.fooList[1]","ネストクラスリスト1-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooNumber","213001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooName","ネストクラスリスト1-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooArray[0]","ネストクラスリスト1-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooArray[1]","ネストクラスリスト1-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooList[0]","ネストクラスリスト1-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[0].fooList[1]","ネストクラスリスト1-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooNumber","213101");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooName","ネストクラスリスト1-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooArray[0]","ネストクラスリスト1-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooArray[1]","ネストクラスリスト1-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooList[0]","ネストクラスリスト1-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordArray[1].fooList[1]","ネストクラスリスト1-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooNumber","214001");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooName","ネストクラスリスト1-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooArray[0]","ネストクラスリスト1-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooArray[1]","ネストクラスリスト1-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooList[0]","ネストクラスリスト1-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[0].fooList[1]","ネストクラスリスト1-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooNumber","214101");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooName","ネストクラスリスト1-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooArray[0]","ネストクラスリスト1-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooArray[1]","ネストクラスリスト1-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooList[0]","ネストクラスリスト1-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestClassList[1].oneNoNestRecordList[1].fooList[1]","ネストクラスリスト1-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1OneNestRecord.fooNumber","6001");
        srcMap.put("test1OneNestRecord.fooName","ネストレコード-テスト");
        srcMap.put("test1OneNestRecord.fooArray[0]","ネストレコード-配列テスト0");
        srcMap.put("test1OneNestRecord.fooArray[1]","ネストレコード-配列テスト1");
        srcMap.put("test1OneNestRecord.fooList[0]","ネストレコード-リストテスト0");
        srcMap.put("test1OneNestRecord.fooList[1]","ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneNumber","605001");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneName","ネストレコード-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneArray[0]","ネストレコード-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneArray[1]","ネストレコード-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneList[0]","ネストレコード-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneList[1]","ネストレコード-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneNumber","601001");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneName","ネストレコード-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneArray[0]","ネストレコード-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneArray[1]","ネストレコード-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneList[0]","ネストレコード-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneList[1]","ネストレコード-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneNumber","601101");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneName","ネストレコード-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneArray[0]","ネストレコード-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneArray[1]","ネストレコード-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneList[0]","ネストレコード-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[1].oneList[1]","ネストレコード-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneNumber","602001");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneName","ネストレコード-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneArray[0]","ネストレコード-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneArray[1]","ネストレコード-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneList[0]","ネストレコード-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneList[1]","ネストレコード-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneNumber","602101");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneName","ネストレコード-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneArray[0]","ネストレコード-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneArray[1]","ネストレコード-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneList[0]","ネストレコード-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[1].oneList[1]","ネストレコード-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooNumber","606001");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooName","ネストレコード-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooArray[0]","ネストレコード-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooArray[1]","ネストレコード-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooList[0]","ネストレコード-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooList[1]","ネストレコード-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooNumber","603001");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooName","ネストレコード-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooArray[0]","ネストレコード-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooArray[1]","ネストレコード-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooList[0]","ネストレコード-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooList[1]","ネストレコード-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooNumber","603101");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooName","ネストレコード-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooArray[0]","ネストレコード-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooArray[1]","ネストレコード-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooList[0]","ネストレコード-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[1].fooList[1]","ネストレコード-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooNumber","604001");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooName","ネストレコード-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooArray[0]","ネストレコード-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooArray[1]","ネストレコード-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooList[0]","ネストレコード-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooList[1]","ネストレコード-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooNumber","604101");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooName","ネストレコード-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooArray[0]","ネストレコード-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooArray[1]","ネストレコード-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooList[0]","ネストレコード-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[1].fooList[1]","ネストレコード-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1OneNestRecordArray[0].fooNumber","3001");
        srcMap.put("test1OneNestRecordArray[0].fooName","ネストレコード配列0-テスト");
        srcMap.put("test1OneNestRecordArray[0].fooArray[0]","ネストレコード配列0-配列テスト0");
        srcMap.put("test1OneNestRecordArray[0].fooArray[1]","ネストレコード配列0-配列テスト1");
        srcMap.put("test1OneNestRecordArray[0].fooList[0]","ネストレコード配列0-リストテスト0");
        srcMap.put("test1OneNestRecordArray[0].fooList[1]","ネストレコード配列0-リストテスト1");
        srcMap.put("test1OneNestRecordArray[1].fooNumber","3101");
        srcMap.put("test1OneNestRecordArray[1].fooName","ネストレコード配列1-テスト");
        srcMap.put("test1OneNestRecordArray[1].fooArray[0]","ネストレコード配列1-配列テスト0");
        srcMap.put("test1OneNestRecordArray[1].fooArray[1]","ネストレコード配列1-配列テスト1");
        srcMap.put("test1OneNestRecordArray[1].fooList[0]","ネストレコード配列1-リストテスト0");
        srcMap.put("test1OneNestRecordArray[1].fooList[1]","ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneNumber","305001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneName","ネストレコード配列0-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneArray[0]","ネストレコード配列0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneArray[1]","ネストレコード配列0-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneList[0]","ネストレコード配列0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneList[1]","ネストレコード配列0-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneNumber","301001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneName","ネストレコード配列0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneArray[0]","ネストレコード配列0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneArray[1]","ネストレコード配列0-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneList[0]","ネストレコード配列0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneList[1]","ネストレコード配列0-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneNumber","301101");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneName","ネストレコード配列0-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneArray[0]","ネストレコード配列0-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneArray[1]","ネストレコード配列0-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneList[0]","ネストレコード配列0-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[1].oneList[1]","ネストレコード配列0-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneNumber","302001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneName","ネストレコード配列0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneArray[0]","ネストレコード配列0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneArray[1]","ネストレコード配列0-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneList[0]","ネストレコード配列0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneList[1]","ネストレコード配列0-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneNumber","302101");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneName","ネストレコード配列0-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneArray[0]","ネストレコード配列0-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneArray[1]","ネストレコード配列0-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneList[0]","ネストレコード配列0-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[1].oneList[1]","ネストレコード配列0-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooNumber","306001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooName","ネストレコード配列0-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooArray[0]","ネストレコード配列0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooArray[1]","ネストレコード配列0-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooList[0]","ネストレコード配列0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooList[1]","ネストレコード配列0-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooNumber","303001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooName","ネストレコード配列0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooArray[0]","ネストレコード配列0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooArray[1]","ネストレコード配列0-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooList[0]","ネストレコード配列0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooList[1]","ネストレコード配列0-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooNumber","303101");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooName","ネストレコード配列0-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooArray[0]","ネストレコード配列0-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooArray[1]","ネストレコード配列0-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooList[0]","ネストレコード配列0-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[1].fooList[1]","ネストレコード配列0-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooNumber","304001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooName","ネストレコード配列0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooArray[0]","ネストレコード配列0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooArray[1]","ネストレコード配列0-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooList[0]","ネストレコード配列0-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooList[1]","ネストレコード配列0-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooNumber","304101");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooName","ネストレコード配列0-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooArray[0]","ネストレコード配列0-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooArray[1]","ネストレコード配列0-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooList[0]","ネストレコード配列0-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[1].fooList[1]","ネストレコード配列0-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneNumber","315001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneName","ネストレコード配列1-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneArray[0]","ネストレコード配列1-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneArray[1]","ネストレコード配列1-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneList[0]","ネストレコード配列1-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClass.oneList[1]","ネストレコード配列1-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneNumber","311001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneName","ネストレコード配列1-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneArray[0]","ネストレコード配列1-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneArray[1]","ネストレコード配列1-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneList[0]","ネストレコード配列1-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[0].oneList[1]","ネストレコード配列1-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneNumber","311101");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneName","ネストレコード配列1-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneArray[0]","ネストレコード配列1-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneArray[1]","ネストレコード配列1-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneList[0]","ネストレコード配列1-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassArray[1].oneList[1]","ネストレコード配列1-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneNumber","312001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneName","ネストレコード配列1-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneArray[0]","ネストレコード配列1-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneArray[1]","ネストレコード配列1-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneList[0]","ネストレコード配列1-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[0].oneList[1]","ネストレコード配列1-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneNumber","312101");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneName","ネストレコード配列1-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneArray[0]","ネストレコード配列1-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneArray[1]","ネストレコード配列1-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneList[0]","ネストレコード配列1-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestClassList[1].oneList[1]","ネストレコード配列1-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooNumber","316001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooName","ネストレコード配列1-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooArray[0]","ネストレコード配列1-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooArray[1]","ネストレコード配列1-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooList[0]","ネストレコード配列1-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecord.fooList[1]","ネストレコード配列1-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooNumber","313001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooName","ネストレコード配列1-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooArray[0]","ネストレコード配列1-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooArray[1]","ネストレコード配列1-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooList[0]","ネストレコード配列1-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[0].fooList[1]","ネストレコード配列1-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooNumber","313101");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooName","ネストレコード配列1-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooArray[0]","ネストレコード配列1-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooArray[1]","ネストレコード配列1-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooList[0]","ネストレコード配列1-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordArray[1].fooList[1]","ネストレコード配列1-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooNumber","314001");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooName","ネストレコード配列1-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooArray[0]","ネストレコード配列1-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooArray[1]","ネストレコード配列1-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooList[0]","ネストレコード配列1-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[0].fooList[1]","ネストレコード配列1-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooNumber","314101");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooName","ネストレコード配列1-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooArray[0]","ネストレコード配列1-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooArray[1]","ネストレコード配列1-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooList[0]","ネストレコード配列1-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[1].barOneNestRecordList[1].fooList[1]","ネストレコード配列1-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1OneNestRecordList[0].fooNumber","4001");
        srcMap.put("test1OneNestRecordList[0].fooName","ネストレコードリスト0-テスト");
        srcMap.put("test1OneNestRecordList[0].fooArray[0]","ネストレコードリスト0-配列テスト0");
        srcMap.put("test1OneNestRecordList[0].fooArray[1]","ネストレコードリスト0-配列テスト1");
        srcMap.put("test1OneNestRecordList[0].fooList[0]","ネストレコードリスト0-リストテスト0");
        srcMap.put("test1OneNestRecordList[0].fooList[1]","ネストレコードリスト0-リストテスト1");
        srcMap.put("test1OneNestRecordList[1].fooNumber","4101");
        srcMap.put("test1OneNestRecordList[1].fooName","ネストレコードリスト1-テスト");
        srcMap.put("test1OneNestRecordList[1].fooArray[0]","ネストレコードリスト1-配列テスト0");
        srcMap.put("test1OneNestRecordList[1].fooArray[1]","ネストレコードリスト1-配列テスト1");
        srcMap.put("test1OneNestRecordList[1].fooList[0]","ネストレコードリスト1-リストテスト0");
        srcMap.put("test1OneNestRecordList[1].fooList[1]","ネストレコードリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneNumber","405001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneName","ネストレコードリスト0-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneArray[0]","ネストレコードリスト0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneArray[1]","ネストレコードリスト0-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneList[0]","ネストレコードリスト0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneList[1]","ネストレコードリスト0-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneNumber","401001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneName","ネストレコードリスト0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneArray[0]","ネストレコードリスト0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneArray[1]","ネストレコードリスト0-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneList[0]","ネストレコードリスト0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneList[1]","ネストレコードリスト0-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneNumber","401101");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneName","ネストレコードリスト0-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneArray[0]","ネストレコードリスト0-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneArray[1]","ネストレコードリスト0-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneList[0]","ネストレコードリスト0-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[1].oneList[1]","ネストレコードリスト0-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneNumber","402001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneName","ネストレコードリスト0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneArray[0]","ネストレコードリスト0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneArray[1]","ネストレコードリスト0-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneList[0]","ネストレコードリスト0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneList[1]","ネストレコードリスト0-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneNumber","402101");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneName","ネストレコードリスト0-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneArray[0]","ネストレコードリスト0-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneArray[1]","ネストレコードリスト0-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneList[0]","ネストレコードリスト0-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[1].oneList[1]","ネストレコードリスト0-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooNumber","406001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooName","ネストレコードリスト0-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooArray[0]","ネストレコードリスト0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooArray[1]","ネストレコードリスト0-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooList[0]","ネストレコードリスト0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooList[1]","ネストレコードリスト0-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooNumber","403001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooName","ネストレコードリスト0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooArray[0]","ネストレコードリスト0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooArray[1]","ネストレコードリスト0-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooList[0]","ネストレコードリスト0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooList[1]","ネストレコードリスト0-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooNumber","403101");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooName","ネストレコードリスト0-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooArray[0]","ネストレコードリスト0-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooArray[1]","ネストレコードリスト0-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooList[0]","ネストレコードリスト0-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[1].fooList[1]","ネストレコードリスト0-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooNumber","404001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooName","ネストレコードリスト0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooArray[0]","ネストレコードリスト0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooArray[1]","ネストレコードリスト0-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooList[0]","ネストレコードリスト0-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooList[1]","ネストレコードリスト0-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooNumber","404101");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooName","ネストレコードリスト0-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooArray[0]","ネストレコードリスト0-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooArray[1]","ネストレコードリスト0-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooList[0]","ネストレコードリスト0-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[1].fooList[1]","ネストレコードリスト0-ネストレコードリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneNumber","415001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneName","ネストレコードリスト1-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneArray[0]","ネストレコードリスト1-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneArray[1]","ネストレコードリスト1-ネストクラス-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneList[0]","ネストレコードリスト1-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClass.oneList[1]","ネストレコードリスト1-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneNumber","411001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneName","ネストレコードリスト1-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneArray[0]","ネストレコードリスト1-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneArray[1]","ネストレコードリスト1-ネストクラス配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneList[0]","ネストレコードリスト1-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[0].oneList[1]","ネストレコードリスト1-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneNumber","411101");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneName","ネストレコードリスト1-ネストクラス配列1-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneArray[0]","ネストレコードリスト1-ネストクラス配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneArray[1]","ネストレコードリスト1-ネストクラス配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneList[0]","ネストレコードリスト1-ネストクラス配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassArray[1].oneList[1]","ネストレコードリスト1-ネストクラス配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneNumber","412001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneName","ネストレコードリスト1-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneArray[0]","ネストレコードリスト1-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneArray[1]","ネストレコードリスト1-ネストクラスリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneList[0]","ネストレコードリスト1-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[0].oneList[1]","ネストレコードリスト1-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneNumber","412101");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneName","ネストレコードリスト1-ネストクラスリスト1-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneArray[0]","ネストレコードリスト1-ネストクラスリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneArray[1]","ネストレコードリスト1-ネストクラスリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneList[0]","ネストレコードリスト1-ネストクラスリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestClassList[1].oneList[1]","ネストレコードリスト1-ネストクラスリスト1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooNumber","416001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooName","ネストレコードリスト1-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooArray[0]","ネストレコードリスト1-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooArray[1]","ネストレコードリスト1-ネストレコード-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooList[0]","ネストレコードリスト1-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecord.fooList[1]","ネストレコードリスト1-ネストレコード-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooNumber","413001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooName","ネストレコードリスト1-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooArray[0]","ネストレコードリスト1-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooArray[1]","ネストレコードリスト1-ネストレコード配列0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooList[0]","ネストレコードリスト1-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[0].fooList[1]","ネストレコードリスト1-ネストレコード配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooNumber","413101");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooName","ネストレコードリスト1-ネストレコード配列1-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooArray[0]","ネストレコードリスト1-ネストレコード配列1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooArray[1]","ネストレコードリスト1-ネストレコード配列1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooList[0]","ネストレコードリスト1-ネストレコード配列1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordArray[1].fooList[1]","ネストレコードリスト1-ネストレコード配列1-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooNumber","414001");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooName","ネストレコードリスト1-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooArray[0]","ネストレコードリスト1-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooArray[1]","ネストレコードリスト1-ネストレコードリスト0-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooList[0]","ネストレコードリスト1-ネストレコードリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[0].fooList[1]","ネストレコードリスト1-ネストレコードリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooNumber","414101");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooName","ネストレコードリスト1-ネストレコードリスト1-テスト");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooArray[0]","ネストレコードリスト1-ネストレコードリスト1-配列テスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooArray[1]","ネストレコードリスト1-ネストレコードリスト1-配列テスト1");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooList[0]","ネストレコードリスト1-ネストレコードリスト1-リストテスト0");
        srcMap.put("test1TwoNestRecordList[1].barOneNestRecordList[1].fooList[1]","ネストレコードリスト1-ネストレコードリスト1-リストテスト1");

        String rootPath = root == null ? "" : root;
        if (rootPath.isEmpty()) {
            return srcMap;
        } else {
            Map<String, String> retMap = new HashMap<>();
            srcMap.forEach((key, value) -> retMap.put(rootPath + "." + key,value));
            return retMap;
        }
    }

    private void assertTestClass1(TestClass1 dstBean) {

        assertTrue(dstBean.isTest1Flg());
        assertEquals(123, dstBean.getTest1Number());
        assertEquals("テスト１名称", dstBean.getTest1Name());
        assertEquals((Integer)12345, dstBean.getTest1IntNumber());
        assertEquals(LocalDate.of(2025, 2, 4), dstBean.getTest1Date());
        OneNestClass result1 = dstBean.getTest1OneNestClass();
        // -----
        assertEquals(5001,dstBean.getTest1OneNestClass().getOneNumber());
        assertEquals("ネストクラス-テスト",dstBean.getTest1OneNestClass().getOneName());
        assertEquals("ネストクラス-配列テスト0",dstBean.getTest1OneNestClass().getOneArray()[0]);
        assertEquals("ネストクラス-配列テスト1",dstBean.getTest1OneNestClass().getOneArray()[1]);
        assertEquals("ネストクラス-リストテスト0",dstBean.getTest1OneNestClass().getOneList().get(0));
        assertEquals("ネストクラス-リストテスト1",dstBean.getTest1OneNestClass().getOneList().get(1));
        assertEquals(505001,dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラス-ネストクラス-テスト",dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneName());
        assertEquals("ネストクラス-ネストクラス-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラス-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneArray()[1]);
        assertEquals("ネストクラス-ネストクラス-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneList().get(0));
        assertEquals("ネストクラス-ネストクラス-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneList().get(1));
        assertEquals(501001,dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス-ネストクラス配列0-テスト",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラス-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラス-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラス-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneList().get(1));
        assertEquals(501101,dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラス-ネストクラス配列1-テスト",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneName());
        assertEquals("ネストクラス-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラス-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラス-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[1].getOneList().get(1));
        assertEquals(502001,dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラス-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラス-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラス-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラス-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneList().get(1));
        assertEquals(502101,dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラス-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneName());
        assertEquals("ネストクラス-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラス-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラス-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(1).getOneList().get(1));
        assertEquals(506001,dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラス-ネストレコード-テスト",dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooName());
        assertEquals("ネストクラス-ネストレコード-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラス-ネストレコード-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooArray()[1]);
        assertEquals("ネストクラス-ネストレコード-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooList().get(0));
        assertEquals("ネストクラス-ネストレコード-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooList().get(1));
        assertEquals(503001,dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラス-ネストレコード配列0-テスト",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラス-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラス-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストクラス-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストクラス-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooList().get(1));
        assertEquals(503101,dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooNumber());
        assertEquals("ネストクラス-ネストレコード配列1-テスト",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooName());
        assertEquals("ネストクラス-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストクラス-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストクラス-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストクラス-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[1].fooList().get(1));
        assertEquals(504001,dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラス-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラス-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラス-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストクラス-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストクラス-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooList().get(1));
        assertEquals(504101,dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooNumber());
        assertEquals("ネストクラス-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooName());
        assertEquals("ネストクラス-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストクラス-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストクラス-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストクラス-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(1).fooList().get(1));
        assertEquals("配列テスト0",dstBean.getTest1Array()[0]);
        assertEquals("配列テスト1",dstBean.getTest1Array()[1]);
        assertEquals(1001,dstBean.getTest1OneNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス配列0-テスト",dstBean.getTest1OneNestClassArray()[0].getOneName());
        assertEquals("ネストクラス配列0-配列テスト0",dstBean.getTest1OneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス配列0-配列テスト1",dstBean.getTest1OneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラス配列0-リストテスト0",dstBean.getTest1OneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラス配列0-リストテスト1",dstBean.getTest1OneNestClassArray()[0].getOneList().get(1));
        assertEquals(1101,dstBean.getTest1OneNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラス配列1-テスト",dstBean.getTest1OneNestClassArray()[1].getOneName());
        assertEquals("ネストクラス配列1-配列テスト0",dstBean.getTest1OneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラス配列1-配列テスト1",dstBean.getTest1OneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラス配列1-リストテスト0",dstBean.getTest1OneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラス配列1-リストテスト1",dstBean.getTest1OneNestClassArray()[1].getOneList().get(1));
        assertEquals(105001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラス-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneName());
        assertEquals("ネストクラス配列0-ネストクラス-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラス-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneArray()[1]);
        assertEquals("ネストクラス配列0-ネストクラス-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneList().get(0));
        assertEquals("ネストクラス配列0-ネストクラス-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneList().get(1));
        assertEquals(101001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラス配列0-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラス配列0-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラス配列0-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラス配列0-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneList().get(1));
        assertEquals(101101,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラス配列1-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneName());
        assertEquals("ネストクラス配列0-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラス配列0-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラス配列0-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[1].getOneList().get(1));
        assertEquals(102001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラス配列0-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラス配列0-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラス配列0-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneList().get(1));
        assertEquals(102101,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneName());
        assertEquals("ネストクラス配列0-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラス配列0-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラス配列0-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(1).getOneList().get(1));
        assertEquals(106001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラス配列0-ネストレコード-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooName());
        assertEquals("ネストクラス配列0-ネストレコード-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコード-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooArray()[1]);
        assertEquals("ネストクラス配列0-ネストレコード-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooList().get(0));
        assertEquals("ネストクラス配列0-ネストレコード-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooList().get(1));
        assertEquals(103001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラス配列0-ネストレコード配列0-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラス配列0-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストクラス配列0-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストクラス配列0-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooList().get(1));
        assertEquals(103101,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooNumber());
        assertEquals("ネストクラス配列0-ネストレコード配列1-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooName());
        assertEquals("ネストクラス配列0-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストクラス配列0-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストクラス配列0-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[1].fooList().get(1));
        assertEquals(104001,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラス配列0-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラス配列0-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストクラス配列0-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストクラス配列0-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooList().get(1));
        assertEquals(104101,dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooNumber());
        assertEquals("ネストクラス配列0-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooName());
        assertEquals("ネストクラス配列0-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストクラス配列0-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストクラス配列0-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(1).fooList().get(1));
        assertEquals(115001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラス配列1-ネストクラス-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneName());
        assertEquals("ネストクラス配列1-ネストクラス-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラス配列1-ネストクラス-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneArray()[1]);
        assertEquals("ネストクラス配列1-ネストクラス-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneList().get(0));
        assertEquals("ネストクラス配列1-ネストクラス-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClass().getOneList().get(1));
        assertEquals(111001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス配列1-ネストクラス配列0-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラス配列1-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス配列1-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラス配列1-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラス配列1-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[0].getOneList().get(1));
        assertEquals(111101,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラス配列1-ネストクラス配列1-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneName());
        assertEquals("ネストクラス配列1-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラス配列1-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラス配列1-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラス配列1-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassArray()[1].getOneList().get(1));
        assertEquals(112001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラス配列1-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラス配列1-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラス配列1-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラス配列1-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラス配列1-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(0).getOneList().get(1));
        assertEquals(112101,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラス配列1-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneName());
        assertEquals("ネストクラス配列1-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラス配列1-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラス配列1-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラス配列1-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestClassList().get(1).getOneList().get(1));
        assertEquals(116001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラス配列1-ネストレコード-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooName());
        assertEquals("ネストクラス配列1-ネストレコード-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラス配列1-ネストレコード-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooArray()[1]);
        assertEquals("ネストクラス配列1-ネストレコード-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooList().get(0));
        assertEquals("ネストクラス配列1-ネストレコード-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecord().fooList().get(1));
        assertEquals(113001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラス配列1-ネストレコード配列0-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラス配列1-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラス配列1-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストクラス配列1-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストクラス配列1-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[0].fooList().get(1));
        assertEquals(113101,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooNumber());
        assertEquals("ネストクラス配列1-ネストレコード配列1-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooName());
        assertEquals("ネストクラス配列1-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストクラス配列1-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストクラス配列1-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストクラス配列1-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordArray()[1].fooList().get(1));
        assertEquals(114001,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラス配列1-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラス配列1-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラス配列1-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストクラス配列1-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストクラス配列1-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(0).fooList().get(1));
        assertEquals(114101,dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooNumber());
        assertEquals("ネストクラス配列1-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooName());
        assertEquals("ネストクラス配列1-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストクラス配列1-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストクラス配列1-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストクラス配列1-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestClassArray()[1].getOneNoNestRecordList().get(1).fooList().get(1));
        assertEquals("リストテスト0",dstBean.getTest1List().get(0));
        assertEquals("リストテスト1",dstBean.getTest1List().get(1));
        assertEquals(2001,dstBean.getTest1OneNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラスリスト0-テスト",dstBean.getTest1OneNestClassList().get(0).getOneName());
        assertEquals("ネストクラスリスト0-配列テスト0",dstBean.getTest1OneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラスリスト0-配列テスト1",dstBean.getTest1OneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラスリスト0-リストテスト0",dstBean.getTest1OneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラスリスト0-リストテスト1",dstBean.getTest1OneNestClassList().get(0).getOneList().get(1));
        assertEquals(2101,dstBean.getTest1OneNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラスリスト1-テスト",dstBean.getTest1OneNestClassList().get(1).getOneName());
        assertEquals("ネストクラスリスト1-配列テスト0",dstBean.getTest1OneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラスリスト1-配列テスト1",dstBean.getTest1OneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラスリスト1-リストテスト0",dstBean.getTest1OneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラスリスト1-リストテスト1",dstBean.getTest1OneNestClassList().get(1).getOneList().get(1));
        assertEquals(205001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラス-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneName());
        assertEquals("ネストクラスリスト0-ネストクラス-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラス-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneArray()[1]);
        assertEquals("ネストクラスリスト0-ネストクラス-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneList().get(0));
        assertEquals("ネストクラスリスト0-ネストクラス-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneList().get(1));
        assertEquals(201001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラス配列0-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラスリスト0-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラスリスト0-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラスリスト0-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneList().get(1));
        assertEquals(201101,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラス配列1-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneName());
        assertEquals("ネストクラスリスト0-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラスリスト0-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラスリスト0-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[1].getOneList().get(1));
        assertEquals(202001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneList().get(1));
        assertEquals(202101,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneName());
        assertEquals("ネストクラスリスト0-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラスリスト0-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラスリスト0-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(1).getOneList().get(1));
        assertEquals(206001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコード-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooName());
        assertEquals("ネストクラスリスト0-ネストレコード-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコード-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooArray()[1]);
        assertEquals("ネストクラスリスト0-ネストレコード-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooList().get(0));
        assertEquals("ネストクラスリスト0-ネストレコード-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooList().get(1));
        assertEquals(203001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコード配列0-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラスリスト0-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストクラスリスト0-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストクラスリスト0-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooList().get(1));
        assertEquals(203101,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコード配列1-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooName());
        assertEquals("ネストクラスリスト0-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストクラスリスト0-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストクラスリスト0-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[1].fooList().get(1));
        assertEquals(204001,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooList().get(1));
        assertEquals(204101,dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooName());
        assertEquals("ネストクラスリスト0-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストクラスリスト0-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストクラスリスト0-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(1).fooList().get(1));
        assertEquals(215001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラスリスト1-ネストクラス-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneName());
        assertEquals("ネストクラスリスト1-ネストクラス-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラスリスト1-ネストクラス-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneArray()[1]);
        assertEquals("ネストクラスリスト1-ネストクラス-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneList().get(0));
        assertEquals("ネストクラスリスト1-ネストクラス-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClass().getOneList().get(1));
        assertEquals(211001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラスリスト1-ネストクラス配列0-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラスリスト1-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラスリスト1-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストクラスリスト1-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストクラスリスト1-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[0].getOneList().get(1));
        assertEquals(211101,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneNumber());
        assertEquals("ネストクラスリスト1-ネストクラス配列1-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneName());
        assertEquals("ネストクラスリスト1-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストクラスリスト1-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストクラスリスト1-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストクラスリスト1-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassArray()[1].getOneList().get(1));
        assertEquals(212001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラスリスト1-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラスリスト1-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラスリスト1-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストクラスリスト1-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストクラスリスト1-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(0).getOneList().get(1));
        assertEquals(212101,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneNumber());
        assertEquals("ネストクラスリスト1-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneName());
        assertEquals("ネストクラスリスト1-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストクラスリスト1-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストクラスリスト1-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストクラスリスト1-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestClassList().get(1).getOneList().get(1));
        assertEquals(216001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラスリスト1-ネストレコード-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooName());
        assertEquals("ネストクラスリスト1-ネストレコード-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラスリスト1-ネストレコード-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooArray()[1]);
        assertEquals("ネストクラスリスト1-ネストレコード-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooList().get(0));
        assertEquals("ネストクラスリスト1-ネストレコード-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecord().fooList().get(1));
        assertEquals(213001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラスリスト1-ネストレコード配列0-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラスリスト1-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラスリスト1-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストクラスリスト1-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストクラスリスト1-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[0].fooList().get(1));
        assertEquals(213101,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooNumber());
        assertEquals("ネストクラスリスト1-ネストレコード配列1-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooName());
        assertEquals("ネストクラスリスト1-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストクラスリスト1-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストクラスリスト1-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストクラスリスト1-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordArray()[1].fooList().get(1));
        assertEquals(214001,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラスリスト1-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラスリスト1-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラスリスト1-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストクラスリスト1-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストクラスリスト1-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(0).fooList().get(1));
        assertEquals(214101,dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooNumber());
        assertEquals("ネストクラスリスト1-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooName());
        assertEquals("ネストクラスリスト1-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストクラスリスト1-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストクラスリスト1-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストクラスリスト1-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestClassList().get(1).getOneNoNestRecordList().get(1).fooList().get(1));
        assertEquals(6001,dstBean.getTest1OneNestRecord().fooNumber());
        assertEquals("ネストレコード-テスト",dstBean.getTest1OneNestRecord().fooName());
        assertEquals("ネストレコード-配列テスト0",dstBean.getTest1OneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード-配列テスト1",dstBean.getTest1OneNestRecord().fooArray()[1]);
        assertEquals("ネストレコード-リストテスト0",dstBean.getTest1OneNestRecord().fooList().get(0));
        assertEquals("ネストレコード-リストテスト1",dstBean.getTest1OneNestRecord().fooList().get(1));
        assertEquals(605001,dstBean.getTest1TwoNestRecord().barOneNestClass().getOneNumber());
        assertEquals("ネストレコード-ネストクラス-テスト",dstBean.getTest1TwoNestRecord().barOneNestClass().getOneName());
        assertEquals("ネストレコード-ネストクラス-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラス-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestClass().getOneArray()[1]);
        assertEquals("ネストレコード-ネストクラス-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコード-ネストクラス-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestClass().getOneList().get(1));
        assertEquals(601001,dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコード-ネストクラス配列0-テスト",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコード-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストレコード-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコード-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(601101,dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneNumber());
        assertEquals("ネストレコード-ネストクラス配列1-テスト",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneName());
        assertEquals("ネストレコード-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストレコード-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストレコード-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestClassArray()[1].getOneList().get(1));
        assertEquals(602001,dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコード-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコード-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストレコード-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコード-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(602101,dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneNumber());
        assertEquals("ネストレコード-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneName());
        assertEquals("ネストレコード-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストレコード-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストレコード-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestClassList().get(1).getOneList().get(1));
        assertEquals(606001,dstBean.getTest1TwoNestRecord().barOneNestRecord().fooNumber());
        assertEquals("ネストレコード-ネストレコード-テスト",dstBean.getTest1TwoNestRecord().barOneNestRecord().fooName());
        assertEquals("ネストレコード-ネストレコード-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード-ネストレコード-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestRecord().fooArray()[1]);
        assertEquals("ネストレコード-ネストレコード-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestRecord().fooList().get(0));
        assertEquals("ネストレコード-ネストレコード-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestRecord().fooList().get(1));
        assertEquals(603001,dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード-ネストレコード配列0-テスト",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコード-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコード-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooList().get(1));
        assertEquals(603101,dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコード-ネストレコード配列1-テスト",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooName());
        assertEquals("ネストレコード-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコード-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコード-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコード-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[1].fooList().get(1));
        assertEquals(604001,dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコード-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコード-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコード-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコード-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコード-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooList().get(1));
        assertEquals(604101,dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコード-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooName());
        assertEquals("ネストレコード-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコード-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコード-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコード-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(1).fooList().get(1));
        assertEquals(3001,dstBean.getTest1OneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード配列0-テスト",dstBean.getTest1OneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード配列0-配列テスト0",dstBean.getTest1OneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード配列0-配列テスト1",dstBean.getTest1OneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコード配列0-リストテスト0",dstBean.getTest1OneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコード配列0-リストテスト1",dstBean.getTest1OneNestRecordArray()[0].fooList().get(1));
        assertEquals(3101,dstBean.getTest1OneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコード配列1-テスト",dstBean.getTest1OneNestRecordArray()[1].fooName());
        assertEquals("ネストレコード配列1-配列テスト0",dstBean.getTest1OneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコード配列1-配列テスト1",dstBean.getTest1OneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコード配列1-リストテスト0",dstBean.getTest1OneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコード配列1-リストテスト1",dstBean.getTest1OneNestRecordArray()[1].fooList().get(1));
        assertEquals(305001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラス-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneName());
        assertEquals("ネストレコード配列0-ネストクラス-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラス-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneArray()[1]);
        assertEquals("ネストレコード配列0-ネストクラス-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコード配列0-ネストクラス-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneList().get(1));
        assertEquals(301001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラス配列0-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコード配列0-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストレコード配列0-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコード配列0-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(301101,dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラス配列1-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneName());
        assertEquals("ネストレコード配列0-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストレコード配列0-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストレコード配列0-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[1].getOneList().get(1));
        assertEquals(302001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコード配列0-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストレコード配列0-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコード配列0-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(302101,dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneName());
        assertEquals("ネストレコード配列0-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストレコード配列0-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストレコード配列0-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(1).getOneList().get(1));
        assertEquals(306001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooNumber());
        assertEquals("ネストレコード配列0-ネストレコード-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooName());
        assertEquals("ネストレコード配列0-ネストレコード-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコード-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooArray()[1]);
        assertEquals("ネストレコード配列0-ネストレコード-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooList().get(0));
        assertEquals("ネストレコード配列0-ネストレコード-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooList().get(1));
        assertEquals(303001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード配列0-ネストレコード配列0-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード配列0-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコード配列0-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコード配列0-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooList().get(1));
        assertEquals(303101,dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコード配列0-ネストレコード配列1-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooName());
        assertEquals("ネストレコード配列0-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコード配列0-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコード配列0-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[1].fooList().get(1));
        assertEquals(304001,dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコード配列0-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコード配列0-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコード配列0-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコード配列0-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooList().get(1));
        assertEquals(304101,dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコード配列0-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooName());
        assertEquals("ネストレコード配列0-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコード配列0-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコード配列0-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(1).fooList().get(1));
        assertEquals(315001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneNumber());
        assertEquals("ネストレコード配列1-ネストクラス-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneName());
        assertEquals("ネストレコード配列1-ネストクラス-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコード配列1-ネストクラス-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneArray()[1]);
        assertEquals("ネストレコード配列1-ネストクラス-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコード配列1-ネストクラス-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClass().getOneList().get(1));
        assertEquals(311001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコード配列1-ネストクラス配列0-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコード配列1-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコード配列1-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストレコード配列1-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコード配列1-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(311101,dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneNumber());
        assertEquals("ネストレコード配列1-ネストクラス配列1-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneName());
        assertEquals("ネストレコード配列1-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストレコード配列1-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストレコード配列1-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストレコード配列1-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassArray()[1].getOneList().get(1));
        assertEquals(312001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコード配列1-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコード配列1-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコード配列1-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストレコード配列1-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコード配列1-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(312101,dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneNumber());
        assertEquals("ネストレコード配列1-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneName());
        assertEquals("ネストレコード配列1-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストレコード配列1-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストレコード配列1-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストレコード配列1-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestClassList().get(1).getOneList().get(1));
        assertEquals(316001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooNumber());
        assertEquals("ネストレコード配列1-ネストレコード-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooName());
        assertEquals("ネストレコード配列1-ネストレコード-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード配列1-ネストレコード-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooArray()[1]);
        assertEquals("ネストレコード配列1-ネストレコード-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooList().get(0));
        assertEquals("ネストレコード配列1-ネストレコード-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecord().fooList().get(1));
        assertEquals(313001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード配列1-ネストレコード配列0-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード配列1-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード配列1-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコード配列1-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコード配列1-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[0].fooList().get(1));
        assertEquals(313101,dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコード配列1-ネストレコード配列1-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooName());
        assertEquals("ネストレコード配列1-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコード配列1-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコード配列1-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコード配列1-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordArray()[1].fooList().get(1));
        assertEquals(314001,dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコード配列1-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコード配列1-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコード配列1-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコード配列1-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコード配列1-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(0).fooList().get(1));
        assertEquals(314101,dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコード配列1-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooName());
        assertEquals("ネストレコード配列1-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコード配列1-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコード配列1-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコード配列1-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestRecordArray()[1].barOneNestRecordList().get(1).fooList().get(1));
        assertEquals(4001,dstBean.getTest1OneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコードリスト0-テスト",dstBean.getTest1OneNestRecordList().get(0).fooName());
        assertEquals("ネストレコードリスト0-配列テスト0",dstBean.getTest1OneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコードリスト0-配列テスト1",dstBean.getTest1OneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコードリスト0-リストテスト0",dstBean.getTest1OneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコードリスト0-リストテスト1",dstBean.getTest1OneNestRecordList().get(0).fooList().get(1));
        assertEquals(4101,dstBean.getTest1OneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコードリスト1-テスト",dstBean.getTest1OneNestRecordList().get(1).fooName());
        assertEquals("ネストレコードリスト1-配列テスト0",dstBean.getTest1OneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコードリスト1-配列テスト1",dstBean.getTest1OneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコードリスト1-リストテスト0",dstBean.getTest1OneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコードリスト1-リストテスト1",dstBean.getTest1OneNestRecordList().get(1).fooList().get(1));
        assertEquals(405001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラス-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneName());
        assertEquals("ネストレコードリスト0-ネストクラス-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラス-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneArray()[1]);
        assertEquals("ネストレコードリスト0-ネストクラス-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラス-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneList().get(1));
        assertEquals(401001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラス配列0-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコードリスト0-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストレコードリスト0-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(401101,dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラス配列1-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneName());
        assertEquals("ネストレコードリスト0-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストレコードリスト0-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[1].getOneList().get(1));
        assertEquals(402001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(402101,dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneName());
        assertEquals("ネストレコードリスト0-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストレコードリスト0-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(1).getOneList().get(1));
        assertEquals(406001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコード-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooName());
        assertEquals("ネストレコードリスト0-ネストレコード-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコード-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooArray()[1]);
        assertEquals("ネストレコードリスト0-ネストレコード-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooList().get(0));
        assertEquals("ネストレコードリスト0-ネストレコード-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooList().get(1));
        assertEquals(403001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコード配列0-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコードリスト0-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコードリスト0-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコードリスト0-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooList().get(1));
        assertEquals(403101,dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコード配列1-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooName());
        assertEquals("ネストレコードリスト0-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコードリスト0-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコードリスト0-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[1].fooList().get(1));
        assertEquals(404001,dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooList().get(1));
        assertEquals(404101,dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooName());
        assertEquals("ネストレコードリスト0-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコードリスト0-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコードリスト0-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(1).fooList().get(1));
        assertEquals(415001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneNumber());
        assertEquals("ネストレコードリスト1-ネストクラス-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneName());
        assertEquals("ネストレコードリスト1-ネストクラス-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコードリスト1-ネストクラス-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneArray()[1]);
        assertEquals("ネストレコードリスト1-ネストクラス-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコードリスト1-ネストクラス-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClass().getOneList().get(1));
        assertEquals(411001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコードリスト1-ネストクラス配列0-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコードリスト1-ネストクラス配列0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコードリスト1-ネストクラス配列0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneArray()[1]);
        assertEquals("ネストレコードリスト1-ネストクラス配列0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコードリスト1-ネストクラス配列0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(411101,dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneNumber());
        assertEquals("ネストレコードリスト1-ネストクラス配列1-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneName());
        assertEquals("ネストレコードリスト1-ネストクラス配列1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneArray()[0]);
        assertEquals("ネストレコードリスト1-ネストクラス配列1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneArray()[1]);
        assertEquals("ネストレコードリスト1-ネストクラス配列1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneList().get(0));
        assertEquals("ネストレコードリスト1-ネストクラス配列1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassArray()[1].getOneList().get(1));
        assertEquals(412001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコードリスト1-ネストクラスリスト0-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコードリスト1-ネストクラスリスト0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコードリスト1-ネストクラスリスト0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneArray()[1]);
        assertEquals("ネストレコードリスト1-ネストクラスリスト0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコードリスト1-ネストクラスリスト0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(412101,dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneNumber());
        assertEquals("ネストレコードリスト1-ネストクラスリスト1-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneName());
        assertEquals("ネストレコードリスト1-ネストクラスリスト1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneArray()[0]);
        assertEquals("ネストレコードリスト1-ネストクラスリスト1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneArray()[1]);
        assertEquals("ネストレコードリスト1-ネストクラスリスト1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneList().get(0));
        assertEquals("ネストレコードリスト1-ネストクラスリスト1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestClassList().get(1).getOneList().get(1));
        assertEquals(416001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooNumber());
        assertEquals("ネストレコードリスト1-ネストレコード-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooName());
        assertEquals("ネストレコードリスト1-ネストレコード-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコードリスト1-ネストレコード-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooArray()[1]);
        assertEquals("ネストレコードリスト1-ネストレコード-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooList().get(0));
        assertEquals("ネストレコードリスト1-ネストレコード-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecord().fooList().get(1));
        assertEquals(413001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコードリスト1-ネストレコード配列0-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコードリスト1-ネストレコード配列0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコードリスト1-ネストレコード配列0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooArray()[1]);
        assertEquals("ネストレコードリスト1-ネストレコード配列0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooList().get(0));
        assertEquals("ネストレコードリスト1-ネストレコード配列0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[0].fooList().get(1));
        assertEquals(413101,dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooNumber());
        assertEquals("ネストレコードリスト1-ネストレコード配列1-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooName());
        assertEquals("ネストレコードリスト1-ネストレコード配列1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooArray()[0]);
        assertEquals("ネストレコードリスト1-ネストレコード配列1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooArray()[1]);
        assertEquals("ネストレコードリスト1-ネストレコード配列1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooList().get(0));
        assertEquals("ネストレコードリスト1-ネストレコード配列1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordArray()[1].fooList().get(1));
        assertEquals(414001,dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコードリスト1-ネストレコードリスト0-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコードリスト1-ネストレコードリスト0-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコードリスト1-ネストレコードリスト0-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooArray()[1]);
        assertEquals("ネストレコードリスト1-ネストレコードリスト0-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooList().get(0));
        assertEquals("ネストレコードリスト1-ネストレコードリスト0-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(0).fooList().get(1));
        assertEquals(414101,dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooNumber());
        assertEquals("ネストレコードリスト1-ネストレコードリスト1-テスト",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooName());
        assertEquals("ネストレコードリスト1-ネストレコードリスト1-配列テスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooArray()[0]);
        assertEquals("ネストレコードリスト1-ネストレコードリスト1-配列テスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooArray()[1]);
        assertEquals("ネストレコードリスト1-ネストレコードリスト1-リストテスト0",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooList().get(0));
        assertEquals("ネストレコードリスト1-ネストレコードリスト1-リストテスト1",dstBean.getTest1TwoNestRecordList().get(1).barOneNestRecordList().get(1).fooList().get(1));

    }

    public static class TestClass1 {
        private boolean test1Flg;
        private int test1Number;
        private String test1Name;
        private Integer test1IntNumber;
        private LocalDate test1Date;

        private OneNestClass test1OneNestClass;
        private TwoNestClass test1TwoNestClass;
        private String[] test1Array;
        private OneNestClass[] test1OneNestClassArray;
        private TwoNestClass[] test1TwoNestClassArray;
        private List<String> test1List;
        private List<OneNestClass> test1OneNestClassList;
        private List<TwoNestClass> test1TwoNestClassList;
        private OneNestRecord test1OneNestRecord;
        private TwoNestRecord test1TwoNestRecord;
        private OneNestRecord[] test1OneNestRecordArray;
        private TwoNestRecord[] test1TwoNestRecordArray;
        private List<OneNestRecord> test1OneNestRecordList;
        private List<TwoNestRecord> test1TwoNestRecordList;

        public boolean isTest1Flg() {
            return test1Flg;
        }

        public void setTest1Flg(boolean test1Flg) {
            this.test1Flg = test1Flg;
        }

        public int getTest1Number() {
            return test1Number;
        }

        public void setTest1Number(int test1Number) {
            this.test1Number = test1Number;
        }

        public String getTest1Name() {
            return test1Name;
        }

        public void setTest1Name(String test1Name) {
            this.test1Name = test1Name;
        }

        public Integer getTest1IntNumber() {
            return test1IntNumber;
        }

        public void setTest1IntNumber(Integer test1IntNumber) {
            this.test1IntNumber = test1IntNumber;
        }

        public LocalDate getTest1Date() {
            return test1Date;
        }

        public void setTest1Date(LocalDate test1Date) {
            this.test1Date = test1Date;
        }

        public OneNestClass getTest1OneNestClass() {
            return test1OneNestClass;
        }

        public void setTest1OneNestClass(OneNestClass test1OneNestClass) {
            this.test1OneNestClass = test1OneNestClass;
        }

        public TwoNestClass getTest1TwoNestClass() {
            return test1TwoNestClass;
        }

        public void setTest1TwoNestClass(TwoNestClass test1TwoNestClass) {
            this.test1TwoNestClass = test1TwoNestClass;
        }

        public String[] getTest1Array() {
            return test1Array;
        }

        public void setTest1Array(String[] test1Array) {
            this.test1Array = test1Array;
        }

        public OneNestClass[] getTest1OneNestClassArray() {
            return test1OneNestClassArray;
        }

        public void setTest1OneNestClassArray(OneNestClass[] test1OneNestClassArray) {
            this.test1OneNestClassArray = test1OneNestClassArray;
        }

        public TwoNestClass[] getTest1TwoNestClassArray() {
            return test1TwoNestClassArray;
        }

        public void setTest1TwoNestClassArray(TwoNestClass[] test1TwoNestClassArray) {
            this.test1TwoNestClassArray = test1TwoNestClassArray;
        }

        public List<String> getTest1List() {
            return test1List;
        }

        public void setTest1List(List<String> test1List) {
            this.test1List = test1List;
        }

        public List<OneNestClass> getTest1OneNestClassList() {
            return test1OneNestClassList;
        }

        public void setTest1OneNestClassList(List<OneNestClass> test1OneNestClassList) {
            this.test1OneNestClassList = test1OneNestClassList;
        }

        public List<TwoNestClass> getTest1TwoNestClassList() {
            return test1TwoNestClassList;
        }

        public void setTest1TwoNestClassList(List<TwoNestClass> test1TwoNestClassList) {
            this.test1TwoNestClassList = test1TwoNestClassList;
        }

        public OneNestRecord getTest1OneNestRecord() {
            return test1OneNestRecord;
        }

        public void setTest1OneNestRecord(OneNestRecord test1OneNestRecord) {
            this.test1OneNestRecord = test1OneNestRecord;
        }

        public TwoNestRecord getTest1TwoNestRecord() {
            return test1TwoNestRecord;
        }

        public void setTest1TwoNestRecord(TwoNestRecord test1TwoNestRecord) {
            this.test1TwoNestRecord = test1TwoNestRecord;
        }

        public OneNestRecord[] getTest1OneNestRecordArray() {
            return test1OneNestRecordArray;
        }

        public void setTest1OneNestRecordArray(OneNestRecord[] test1OneNestRecordArray) {
            this.test1OneNestRecordArray = test1OneNestRecordArray;
        }

        public TwoNestRecord[] getTest1TwoNestRecordArray() {
            return test1TwoNestRecordArray;
        }

        public void setTest1TwoNestRecordArray(TwoNestRecord[] test1TwoNestRecordArray) {
            this.test1TwoNestRecordArray = test1TwoNestRecordArray;
        }

        public List<OneNestRecord> getTest1OneNestRecordList() {
            return test1OneNestRecordList;
        }

        public void setTest1OneNestRecordList(List<OneNestRecord> test1OneNestRecordList) {
            this.test1OneNestRecordList = test1OneNestRecordList;
        }

        public List<TwoNestRecord> getTest1TwoNestRecordList() {
            return test1TwoNestRecordList;
        }

        public void setTest1TwoNestRecordList(List<TwoNestRecord> test1TwoNestRecordList) {
            this.test1TwoNestRecordList = test1TwoNestRecordList;
        }
    }

    public static class OneNestClass {
        private int oneNumber;
        private String oneName;
        private String[] oneArray;
        private List<String> oneList;

        public int getOneNumber() {
            return oneNumber;
        }

        public void setOneNumber(int oneNumber) {
            this.oneNumber = oneNumber;
        }

        public String getOneName() {
            return oneName;
        }

        public void setOneName(String oneName) {
            this.oneName = oneName;
        }

        public String[] getOneArray() {
            return oneArray;
        }

        public void setOneArray(String[] oneArray) {
            this.oneArray = oneArray;
        }

        public List<String> getOneList() {
            return oneList;
        }

        public void setOneList(List<String> oneList) {
            this.oneList = oneList;
        }
    }

    public static class TwoNestClass {
        private OneNestClass oneNoNestClass;
        private OneNestClass[] oneNoNestClassArray;
        private List<OneNestClass> oneNoNestClassList;
        private OneNestRecord oneNoNestRecord;
        private OneNestRecord[] oneNoNestRecordArray;
        private List<OneNestRecord> oneNoNestRecordList;

        public OneNestClass getOneNoNestClass() {
            return oneNoNestClass;
        }

        public void setOneNoNestClass(OneNestClass oneNoNestClass) {
            this.oneNoNestClass = oneNoNestClass;
        }

        public OneNestClass[] getOneNoNestClassArray() {
            return oneNoNestClassArray;
        }

        public void setOneNoNestClassArray(OneNestClass[] oneNoNestClassArray) {
            this.oneNoNestClassArray = oneNoNestClassArray;
        }

        public List<OneNestClass> getOneNoNestClassList() {
            return oneNoNestClassList;
        }

        public void setOneNoNestClassList(List<OneNestClass> oneNoNestClassList) {
            this.oneNoNestClassList = oneNoNestClassList;
        }

        public OneNestRecord getOneNoNestRecord() {
            return oneNoNestRecord;
        }

        public void setOneNoNestRecord(OneNestRecord oneNoNestRecord) {
            this.oneNoNestRecord = oneNoNestRecord;
        }

        public OneNestRecord[] getOneNoNestRecordArray() {
            return oneNoNestRecordArray;
        }

        public void setOneNoNestRecordArray(OneNestRecord[] oneNoNestRecordArray) {
            this.oneNoNestRecordArray = oneNoNestRecordArray;
        }

        public List<OneNestRecord> getOneNoNestRecordList() {
            return oneNoNestRecordList;
        }

        public void setOneNoNestRecordList(List<OneNestRecord> oneNoNestRecordList) {
            this.oneNoNestRecordList = oneNoNestRecordList;
        }
    }

    public record OneNestRecord (
             int fooNumber,
             String fooName,
             String[] fooArray,
             List<String> fooList
    ) {}

    public record TwoNestRecord (
            OneNestClass barOneNestClass,
            OneNestClass[] barOneNestClassArray,
            List<OneNestClass> barOneNestClassList,
            OneNestRecord barOneNestRecord,
            OneNestRecord[] barOneNestRecordArray,
            List<OneNestRecord> barOneNestRecordList
    ) {}

    public static class Item {
        private String keyA;
        private String keyB;
        private String keyC;
        private String keyD;
        private String keyE;
        private String keyF;
        private String keyG;
        private String keyH;
        private String keyI;
        private String keyJ;

        public String getKeyA() {
            return keyA;
        }

        public void setKeyA(String keyA) {
            this.keyA = keyA;
        }

        public String getKeyB() {
            return keyB;
        }

        public void setKeyB(String keyB) {
            this.keyB = keyB;
        }

        public String getKeyC() {
            return keyC;
        }

        public void setKeyC(String keyC) {
            this.keyC = keyC;
        }

        public String getKeyD() {
            return keyD;
        }

        public void setKeyD(String keyD) {
            this.keyD = keyD;
        }

        public String getKeyE() {
            return keyE;
        }

        public void setKeyE(String keyE) {
            this.keyE = keyE;
        }

        public String getKeyF() {
            return keyF;
        }

        public void setKeyF(String keyF) {
            this.keyF = keyF;
        }

        public String getKeyG() {
            return keyG;
        }

        public void setKeyG(String keyG) {
            this.keyG = keyG;
        }

        public String getKeyH() {
            return keyH;
        }

        public void setKeyH(String keyH) {
            this.keyH = keyH;
        }

        public String getKeyI() {
            return keyI;
        }

        public void setKeyI(String keyI) {
            this.keyI = keyI;
        }

        public String getKeyJ() {
            return keyJ;
        }

        public void setKeyJ(String keyJ) {
            this.keyJ = keyJ;
        }

    }

    public record RecItem (
            String keyA,
            String keyB,
            String keyC,
            String keyD,
            String keyE,
            String keyF,
            String keyG,
            String keyH,
            String keyI,
            String keyJ
    ){}

    private static class Form {
        private List<Item> items;

        public List<Item> getItems() {
            return items;
        }

        public void setItems(List<Item> items) {
            this.items = items;
        }
    }

    private static class RecForm {
        private List<RecItem> items;

        public List<RecItem> getItems() {
            return items;
        }

        public void setItems(List<RecItem> items) {
            this.items = items;
        }
    }

}
