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
    public void copy_直下プロパティ_プリミティブ型に設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        // src設定
        Map<String, String> srcMap = new HashMap<>();
        srcMap.put("test1Flg", "true");
        srcMap.put("test1Number", "123");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        assertTrue(dstBean.isTest1Flg());
        assertEquals(123, dstBean.getTest1Number());
    }

    @Test
    public void copy_直下プロパティ_クラスに設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        srcMap.put("test1Name", "テスト１名称");
        srcMap.put("test1IntNumber", "12345");
        srcMap.put("test1Date", "20250204");
        // ネスト1
        srcMap.put("test1OneNestClass.oneNumber", "5001");
        srcMap.put("test1OneNestClass.oneName", "ネストクラス-テスト");
        srcMap.put("test1OneNestClass.oneArray[0]", "ネストクラス-配列テスト0");
        srcMap.put("test1OneNestClass.oneList[0]", "ネストクラス-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneNumber", "505001");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneName", "ネストクラス-ネストクラス-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneArray[0]", "ネストクラス-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClass.oneList[0]", "ネストクラス-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneNumber", "501001");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneName", "ネストクラス-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneArray[0]", "ネストクラス-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassArray[0].oneList[0]", "ネストクラス-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneNumber", "502001");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneName", "ネストクラス-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneArray[0]", "ネストクラス-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestClassList[0].oneList[0]", "ネストクラス-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooNumber", "506001");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooName", "ネストクラス-ネストレコード-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooArray[0]", "ネストクラス-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecord.fooList[0]", "ネストクラス-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooNumber", "503001");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooName", "ネストクラス-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooArray[0]", "ネストクラス-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordArray[0].fooList[0]", "ネストクラス-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooNumber", "504001");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooName", "ネストクラス-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooArray[0]", "ネストクラス-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClass.oneNoNestRecordList[0].fooList[0]", "ネストクラス-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        assertEquals("テスト１名称", dstBean.getTest1Name());
        assertEquals(12345, (long)dstBean.getTest1IntNumber());
        assertEquals(LocalDate.of(2025,2,4), dstBean.getTest1Date());
        // ネスト1
        assertEquals(5001, dstBean.getTest1OneNestClass().getOneNumber());
        assertEquals("ネストクラス-テスト", dstBean.getTest1OneNestClass().getOneName());
        assertEquals("ネストクラス-配列テスト0", dstBean.getTest1OneNestClass().getOneArray()[0]);
        assertEquals("ネストクラス-リストテスト0", dstBean.getTest1OneNestClass().getOneList().get(0));
        // ネスト2
        assertEquals(505001, dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラス-ネストクラス-テスト", dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneName());
        assertEquals("ネストクラス-ネストクラス-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラス-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestClass().getOneList().get(0));
        assertEquals(501001, dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス-ネストクラス配列0-テスト", dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラス-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals(502001, dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラス-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラス-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラス-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals(506001, dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラス-ネストレコード-テスト", dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooName());
        assertEquals("ネストクラス-ネストレコード-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラス-ネストレコード-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecord().fooList().get(0));
        assertEquals(503001, dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラス-ネストレコード配列0-テスト", dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラス-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラス-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals(504001, dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラス-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラス-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラス-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestClass().getOneNoNestRecordList().get(0).fooList().get(0));
    }

    @Test
    public void copy_直下プロパティ_クラスの配列に設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        srcMap.put("test1Array[0]", "配列テスト0");
        // ネスト1
        srcMap.put("test1OneNestClassArray[0].oneNumber", "1001");
        srcMap.put("test1OneNestClassArray[0].oneName", "ネストクラス配列0-テスト");
        srcMap.put("test1OneNestClassArray[0].oneArray[0]", "ネストクラス配列0-配列テスト0");
        srcMap.put("test1OneNestClassArray[0].oneList[0]", "ネストクラス配列0-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneNumber", "105001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneName", "ネストクラス配列0-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneArray[0]", "ネストクラス配列0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClass.oneList[0]", "ネストクラス配列0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneNumber", "101001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneName", "ネストクラス配列0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneArray[0]", "ネストクラス配列0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassArray[0].oneList[0]", "ネストクラス配列0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneNumber", "102001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneName", "ネストクラス配列0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneArray[0]", "ネストクラス配列0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestClassList[0].oneList[0]", "ネストクラス配列0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooNumber", "106001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooName", "ネストクラス配列0-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooArray[0]", "ネストクラス配列0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecord.fooList[0]", "ネストクラス配列0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooNumber", "103001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooName", "ネストクラス配列0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooArray[0]", "ネストクラス配列0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordArray[0].fooList[0]", "ネストクラス配列0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooNumber", "104001");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooName", "ネストクラス配列0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooArray[0]", "ネストクラス配列0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassArray[0].oneNoNestRecordList[0].fooList[0]", "ネストクラス配列0-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        assertEquals("配列テスト0", dstBean.getTest1Array()[0]);
        // ネスト1
        assertEquals(1001, dstBean.getTest1OneNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス配列0-テスト", dstBean.getTest1OneNestClassArray()[0].getOneName());
        assertEquals("ネストクラス配列0-配列テスト0", dstBean.getTest1OneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス配列0-リストテスト0", dstBean.getTest1OneNestClassArray()[0].getOneList().get(0));
        // ネスト2
        assertEquals(105001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラス-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneName());
        assertEquals("ネストクラス配列0-ネストクラス-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラス-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClass().getOneList().get(0));
        assertEquals(101001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラス配列0-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラス配列0-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals(102001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラス配列0-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラス配列0-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラス配列0-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals(106001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラス配列0-ネストレコード-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooName());
        assertEquals("ネストクラス配列0-ネストレコード-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコード-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecord().fooList().get(0));
        assertEquals(103001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラス配列0-ネストレコード配列0-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラス配列0-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals(104001, dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラス配列0-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラス配列0-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラス配列0-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestClassArray()[0].getOneNoNestRecordList().get(0).fooList().get(0));
    }

    @Test
    public void copy_直下プロパティ_クラスのリストに設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        srcMap.put("test1List[0]", "リストテスト0");
        // ネスト1
        srcMap.put("test1OneNestClassList[0].oneNumber", "2001");
        srcMap.put("test1OneNestClassList[0].oneName", "ネストクラスリスト0-テスト");
        srcMap.put("test1OneNestClassList[0].oneArray[0]", "ネストクラスリスト0-配列テスト0");
        srcMap.put("test1OneNestClassList[0].oneList[0]", "ネストクラスリスト0-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneNumber", "205001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneName", "ネストクラスリスト0-ネストクラス-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneArray[0]", "ネストクラスリスト0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClass.oneList[0]", "ネストクラスリスト0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneNumber", "201001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneName", "ネストクラスリスト0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneArray[0]", "ネストクラスリスト0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassArray[0].oneList[0]", "ネストクラスリスト0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneNumber", "202001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneName", "ネストクラスリスト0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneArray[0]", "ネストクラスリスト0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestClassList[0].oneList[0]", "ネストクラスリスト0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooNumber", "206001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooName", "ネストクラスリスト0-ネストレコード-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooArray[0]", "ネストクラスリスト0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecord.fooList[0]", "ネストクラスリスト0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooNumber", "203001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooName", "ネストクラスリスト0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooArray[0]", "ネストクラスリスト0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordArray[0].fooList[0]", "ネストクラスリスト0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooNumber", "204001");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooName", "ネストクラスリスト0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooArray[0]", "ネストクラスリスト0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestClassList[0].oneNoNestRecordList[0].fooList[0]", "ネストクラスリスト0-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        assertEquals("リストテスト0", dstBean.getTest1List().get(0));
        // ネスト1
        assertEquals(2001, dstBean.getTest1OneNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラスリスト0-テスト", dstBean.getTest1OneNestClassList().get(0).getOneName());
        assertEquals("ネストクラスリスト0-配列テスト0", dstBean.getTest1OneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラスリスト0-リストテスト0", dstBean.getTest1OneNestClassList().get(0).getOneList().get(0));
        // ネスト2
        assertEquals(205001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラス-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneName());
        assertEquals("ネストクラスリスト0-ネストクラス-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラス-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClass().getOneList().get(0));
        assertEquals(201001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラス配列0-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneName());
        assertEquals("ネストクラスリスト0-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassArray()[0].getOneList().get(0));
        assertEquals(202001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneNumber());
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneName());
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストクラスリスト0-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestClassList().get(0).getOneList().get(0));
        assertEquals(206001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコード-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooName());
        assertEquals("ネストクラスリスト0-ネストレコード-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコード-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecord().fooList().get(0));
        assertEquals(203001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコード配列0-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooName());
        assertEquals("ネストクラスリスト0-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordArray()[0].fooList().get(0));
        assertEquals(204001, dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooNumber());
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooName());
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストクラスリスト0-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestClassList().get(0).getOneNoNestRecordList().get(0).fooList().get(0));

    }

    @Test
    public void copy_直下プロパティ_レコードの配列に設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        // ネスト1
        srcMap.put("test1OneNestRecordArray[0].fooNumber", "3001");
        srcMap.put("test1OneNestRecordArray[0].fooName", "ネストレコード配列0-テスト");
        srcMap.put("test1OneNestRecordArray[0].fooArray[0]", "ネストレコード配列0-配列テスト0");
        srcMap.put("test1OneNestRecordArray[0].fooList[0]", "ネストレコード配列0-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneNumber", "305001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneName", "ネストレコード配列0-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneArray[0]", "ネストレコード配列0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClass.oneList[0]", "ネストレコード配列0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneNumber", "301001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneName", "ネストレコード配列0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneArray[0]", "ネストレコード配列0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassArray[0].oneList[0]", "ネストレコード配列0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneNumber", "302001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneName", "ネストレコード配列0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneArray[0]", "ネストレコード配列0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestClassList[0].oneList[0]", "ネストレコード配列0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooNumber", "306001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooName", "ネストレコード配列0-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooArray[0]", "ネストレコード配列0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecord.fooList[0]", "ネストレコード配列0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooNumber", "303001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooName", "ネストレコード配列0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooArray[0]", "ネストレコード配列0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordArray[0].fooList[0]", "ネストレコード配列0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooNumber", "304001");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooName", "ネストレコード配列0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooArray[0]", "ネストレコード配列0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordArray[0].barOneNestRecordList[0].fooList[0]", "ネストレコード配列0-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        // ネスト1
        assertEquals(3001, dstBean.getTest1OneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード配列0-テスト", dstBean.getTest1OneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード配列0-配列テスト0", dstBean.getTest1OneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード配列0-リストテスト0", dstBean.getTest1OneNestRecordArray()[0].fooList().get(0));
        // ネスト2
        assertEquals(305001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラス-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneName());
        assertEquals("ネストレコード配列0-ネストクラス-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラス-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClass().getOneList().get(0));
        assertEquals(301001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラス配列0-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコード配列0-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassArray()[0].getOneList().get(0));
        assertEquals(302001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコード配列0-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコード配列0-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコード配列0-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestClassList().get(0).getOneList().get(0));
        assertEquals(306001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooNumber());
        assertEquals("ネストレコード配列0-ネストレコード-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooName());
        assertEquals("ネストレコード配列0-ネストレコード-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコード-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecord().fooList().get(0));
        assertEquals(303001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード配列0-ネストレコード配列0-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード配列0-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordArray()[0].fooList().get(0));
        assertEquals(304001, dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコード配列0-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコード配列0-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコード配列0-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestRecordArray()[0].barOneNestRecordList().get(0).fooList().get(0));

    }

    @Test
    public void copy_直下プロパティ_レコードのリストに設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        // ネスト1
        srcMap.put("test1OneNestRecordList[0].fooNumber", "4001");
        srcMap.put("test1OneNestRecordList[0].fooName", "ネストレコードリスト0-テスト");
        srcMap.put("test1OneNestRecordList[0].fooArray[0]", "ネストレコードリスト0-配列テスト0");
        srcMap.put("test1OneNestRecordList[0].fooList[0]", "ネストレコードリスト0-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneNumber", "405001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneName", "ネストレコードリスト0-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneArray[0]", "ネストレコードリスト0-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneList[0]", "ネストレコードリスト0-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClass.oneList[1]", "ネストレコードリスト0-ネストクラス-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneNumber", "401001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneName", "ネストレコードリスト0-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneArray[0]", "ネストレコードリスト0-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneList[0]", "ネストレコードリスト0-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassArray[0].oneList[1]", "ネストレコードリスト0-ネストクラス配列0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneNumber", "402001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneName", "ネストレコードリスト0-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneArray[0]", "ネストレコードリスト0-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneList[0]", "ネストレコードリスト0-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestClassList[0].oneList[1]", "ネストレコードリスト0-ネストクラスリスト0-リストテスト1");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooNumber", "406001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooName", "ネストレコードリスト0-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooArray[0]", "ネストレコードリスト0-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecord.fooList[0]", "ネストレコードリスト0-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooNumber", "403001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooName", "ネストレコードリスト0-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooArray[0]", "ネストレコードリスト0-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordArray[0].fooList[0]", "ネストレコードリスト0-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooNumber", "404001");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooName", "ネストレコードリスト0-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooArray[0]", "ネストレコードリスト0-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecordList[0].barOneNestRecordList[0].fooList[0]", "ネストレコードリスト0-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());

        // ネスト1
        assertEquals(4001, dstBean.getTest1OneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコードリスト0-テスト", dstBean.getTest1OneNestRecordList().get(0).fooName());
        assertEquals("ネストレコードリスト0-配列テスト0", dstBean.getTest1OneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコードリスト0-リストテスト0", dstBean.getTest1OneNestRecordList().get(0).fooList().get(0));
        // ネスト2
        assertEquals(405001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラス-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneName());
        assertEquals("ネストレコードリスト0-ネストクラス-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラス-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラス-リストテスト1", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClass().getOneList().get(1));
        assertEquals(401001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラス配列0-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコードリスト0-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラス配列0-リストテスト1", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassArray()[0].getOneList().get(1));
        assertEquals(402001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneList().get(0));
        assertEquals("ネストレコードリスト0-ネストクラスリスト0-リストテスト1", dstBean.getTest1TwoNestRecordList().get(0).barOneNestClassList().get(0).getOneList().get(1));
        assertEquals(406001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコード-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooName());
        assertEquals("ネストレコードリスト0-ネストレコード-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコード-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecord().fooList().get(0));
        assertEquals(403001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコード配列0-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコードリスト0-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordArray()[0].fooList().get(0));
        assertEquals(404001, dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコードリスト0-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestRecordList().get(0).barOneNestRecordList().get(0).fooList().get(0));

    }

    @Test
    public void copy_直下プロパティ_レコードに設定する() {

        // dstBean
        TestClass1 dstBean = new TestClass1();

        Map<String, Object> srcMap = new HashMap<>();
        // ネスト1
        srcMap.put("test1OneNestRecord.fooNumber", "6001");
        srcMap.put("test1OneNestRecord.fooName", "ネストレコード-テスト");
        srcMap.put("test1OneNestRecord.fooArray[0]", "ネストレコード-配列テスト0");
        srcMap.put("test1OneNestRecord.fooList[0]", "ネストレコード-リストテスト0");
        // ネスト2
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneNumber", "605001");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneName", "ネストレコード-ネストクラス-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneArray[0]", "ネストレコード-ネストクラス-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClass.oneList[0]", "ネストレコード-ネストクラス-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneNumber", "601001");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneName", "ネストレコード-ネストクラス配列0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneArray[0]", "ネストレコード-ネストクラス配列0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassArray[0].oneList[0]", "ネストレコード-ネストクラス配列0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneNumber", "602001");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneName", "ネストレコード-ネストクラスリスト0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneArray[0]", "ネストレコード-ネストクラスリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestClassList[0].oneList[0]", "ネストレコード-ネストクラスリスト0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooNumber", "606001");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooName", "ネストレコード-ネストレコード-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooArray[0]", "ネストレコード-ネストレコード-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecord.fooList[0]", "ネストレコード-ネストレコード-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooNumber", "603001");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooName", "ネストレコード-ネストレコード配列0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooArray[0]", "ネストレコード-ネストレコード配列0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordArray[0].fooList[0]", "ネストレコード-ネストレコード配列0-リストテスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooNumber", "604001");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooName", "ネストレコード-ネストレコードリスト0-テスト");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooArray[0]", "ネストレコード-ネストレコードリスト0-配列テスト0");
        srcMap.put("test1TwoNestRecord.barOneNestRecordList[0].fooList[0]", "ネストレコード-ネストレコードリスト0-リストテスト0");

        BeanUtil.copy(dstBean.getClass(), dstBean, srcMap, CopyOptions.empty());


        assertEquals(6001, dstBean.getTest1OneNestRecord().fooNumber());
        assertEquals("ネストレコード-テスト", dstBean.getTest1OneNestRecord().fooName());
        assertEquals("ネストレコード-配列テスト0", dstBean.getTest1OneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード-リストテスト0", dstBean.getTest1OneNestRecord().fooList().get(0));
        // ネスト2
        assertEquals(605001, dstBean.getTest1TwoNestRecord().barOneNestClass().getOneNumber());
        assertEquals("ネストレコード-ネストクラス-テスト", dstBean.getTest1TwoNestRecord().barOneNestClass().getOneName());
        assertEquals("ネストレコード-ネストクラス-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestClass().getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラス-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestClass().getOneList().get(0));
        assertEquals(601001, dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneNumber());
        assertEquals("ネストレコード-ネストクラス配列0-テスト", dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneName());
        assertEquals("ネストレコード-ネストクラス配列0-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラス配列0-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestClassArray()[0].getOneList().get(0));
        assertEquals(602001, dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneNumber());
        assertEquals("ネストレコード-ネストクラスリスト0-テスト", dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneName());
        assertEquals("ネストレコード-ネストクラスリスト0-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneArray()[0]);
        assertEquals("ネストレコード-ネストクラスリスト0-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestClassList().get(0).getOneList().get(0));
        assertEquals(606001, dstBean.getTest1TwoNestRecord().barOneNestRecord().fooNumber());
        assertEquals("ネストレコード-ネストレコード-テスト", dstBean.getTest1TwoNestRecord().barOneNestRecord().fooName());
        assertEquals("ネストレコード-ネストレコード-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestRecord().fooArray()[0]);
        assertEquals("ネストレコード-ネストレコード-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestRecord().fooList().get(0));
        assertEquals(603001, dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooNumber());
        assertEquals("ネストレコード-ネストレコード配列0-テスト", dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooName());
        assertEquals("ネストレコード-ネストレコード配列0-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooArray()[0]);
        assertEquals("ネストレコード-ネストレコード配列0-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestRecordArray()[0].fooList().get(0));
        assertEquals(604001, dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooNumber());
        assertEquals("ネストレコード-ネストレコードリスト0-テスト", dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooName());
        assertEquals("ネストレコード-ネストレコードリスト0-配列テスト0", dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooArray()[0]);
        assertEquals("ネストレコード-ネストレコードリスト0-リストテスト0", dstBean.getTest1TwoNestRecord().barOneNestRecordList().get(0).fooList().get(0));

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
