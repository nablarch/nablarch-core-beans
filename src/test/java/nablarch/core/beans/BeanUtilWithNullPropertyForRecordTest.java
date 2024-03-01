package nablarch.core.beans;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilWithNullPropertyForRecordTest {

    @Test
    public void 移送元をMapとするcreateAndCopy_keyがnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put(null, 10);}};

        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                BeanUtil.createAndCopy(BeanUtilForRecordTest.TestRecord.class, srcMap, CopyOptions.empty()));
        assertThat(result.getMessage(), is("expression is null or blank."));
    }

    @Test
    public void 移送元をMapとするcreateAndCopy_valueがnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("sample", null);}};
        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopy(BeanUtilForRecordTest.TestRecord.class, srcMap, CopyOptions.empty());
        assertThat(dest.sample(), is(nullValue()));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopy_移送元の各プロパティがnullの場合は成功すること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopy(BeanUtilForRecordTest.TestRecord.class, src, CopyOptions.empty());
        assertThat(dest.sample(), is(nullValue()));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopy_移送元の一部のプロパティがnullの場合は成功すること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();
        src.setSample("10");

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopy(BeanUtilForRecordTest.TestRecord.class, src, CopyOptions.empty());
        assertThat(dest.sample(), is(10));
    }

    @Test
    public void 移送元をBeanとするcreateAndCopy_コピー元beanがnullの場合は成功すること() {
        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopy(BeanUtilForRecordTest.TestRecord.class, (BeanUtilForRecordTest.SourceBean) null, CopyOptions.empty());
        assertThat(dest.sample(), is(nullValue()));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyIncludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("sample", 10);}};

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyIncludes(BeanUtilForRecordTest.TestRecord.class, srcMap, (String[]) null));

    }

    @Test
    public void 移送元をMapとするcreateAndCopyIncludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("sample", 10);}};

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopyIncludes(BeanUtilForRecordTest.TestRecord.class, srcMap, (String) null);
        assertThat(dest.sample(), is(nullValue()));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyExcludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("sample", 10);}};

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyExcludes(BeanUtilForRecordTest.TestRecord.class, srcMap, (String[]) null));

    }

    @Test
    public void 移送元をMapとするcreateAndCopyExcludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("sample", 10);}};

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopyExcludes(BeanUtilForRecordTest.TestRecord.class, srcMap, (String) null);
        assertThat(dest.sample(), is(10));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyIncludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();
        src.setSample("10");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyIncludes(BeanUtilForRecordTest.TestRecord.class, src, (String[]) null));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyIncludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();
        src.setSample("10");

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopyIncludes(BeanUtilForRecordTest.TestRecord.class, src, (String) null);
        assertThat(dest.sample(), is(nullValue()));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyExcludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();
        src.setSample("10");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyExcludes(BeanUtilForRecordTest.TestRecord.class, src, (String[]) null));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyExcludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        BeanUtilForRecordTest.SourceBean src = new BeanUtilForRecordTest.SourceBean();
        src.setSample("10");

        BeanUtilForRecordTest.TestRecord dest = BeanUtil.createAndCopyExcludes(BeanUtilForRecordTest.TestRecord.class, src, (String) null);
        assertThat(dest.sample(), is(10));

    }

}
