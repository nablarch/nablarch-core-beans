package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;

public class BeanUtilSetPropertyTest {

    @Before
    public void setUp() {
        // デフォルト設定で動作させるよう、リポジトリをクリアする。
        SystemRepository.clear();
        OnMemoryLogWriter.clear();
        //BeanDescriptorのキャッシュをクリアしておく
        BeanUtil.clearCache();
    }

    @Test
    public void setProperty_ネストした階層のあるプロパティに設定する() {

        TestDest1 dstBean = new TestDest1();

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneNumber", "123");
        assertEquals(123, dstBean.getKaiso().getTestClass1().getOneNestClass().getOneNumber());

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneName", "name");
        assertEquals("name", dstBean.getKaiso().getTestClass1().getOneNestClass().getOneName());

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneArray[0]", "array0");
        assertEquals("array0", dstBean.getKaiso().getTestClass1().getOneNestClass().getOneArray()[0]);

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneArray[1]", "array1");
        assertEquals("array1", dstBean.getKaiso().getTestClass1().getOneNestClass().getOneArray()[1]);

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneList[0]", "list0");
        assertEquals("list0", dstBean.getKaiso().getTestClass1().getOneNestClass().getOneList().get(0));

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestClass.oneList[1]", "list1");
        assertEquals("list1", dstBean.getKaiso().getTestClass1().getOneNestClass().getOneList().get(1));

        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooNumber", "123");
        assertEquals(123, dstBean.getKaiso().getTestClass1().getOneNestRecord().fooNumber());

        dstBean = new TestDest1();
        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooName", "name");
        assertEquals("name", dstBean.getKaiso().getTestClass1().getOneNestRecord().fooName());

        dstBean = new TestDest1();
        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooArray[0]", "array0");
        assertEquals("array0", dstBean.getKaiso().getTestClass1().getOneNestRecord().fooArray()[0]);

        dstBean = new TestDest1();
        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooArray[1]", "array1");
        assertEquals("array1", dstBean.getKaiso().getTestClass1().getOneNestRecord().fooArray()[1]);

        dstBean = new TestDest1();
        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooList[0]", "list0");
        assertEquals("list0", dstBean.getKaiso().getTestClass1().getOneNestRecord().fooList().get(0));

        dstBean = new TestDest1();
        BeanUtil.setProperty(dstBean, "kaiso.testClass1.oneNestRecord.fooList[1]", "list1");
        assertEquals("list1", dstBean.getKaiso().getTestClass1().getOneNestRecord().fooList().get(1));
    }

    public static class Kaiso {
        private TestClass1 testClass1;

        public TestClass1 getTestClass1() {
            return testClass1;
        }
        public void setTestClass1(TestClass1 testClass1) {
            this.testClass1 = testClass1;
        }
    }

    public static class TestDest1 {
        private Kaiso kaiso;

        public Kaiso getKaiso() {
            return kaiso;
        }

        public void setKaiso(Kaiso kaiso) {
            this.kaiso = kaiso;
        }
    }

    public static class TestClass1 {
        private OneNestClass oneNestClass;
        private OneNestRecord oneNestRecord;

        public OneNestClass getOneNestClass() {
            return oneNestClass;
        }

        public void setOneNestClass(OneNestClass oneNestClass) {
            this.oneNestClass = oneNestClass;
        }

        public OneNestRecord getOneNestRecord() {
            return oneNestRecord;
        }

        public void setOneNestRecord(OneNestRecord oneNestRecord) {
            this.oneNestRecord = oneNestRecord;
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

    public record OneNestRecord (
            int fooNumber,
            String fooName,
            String[] fooArray,
            List<String> fooList
    ) {}
}
