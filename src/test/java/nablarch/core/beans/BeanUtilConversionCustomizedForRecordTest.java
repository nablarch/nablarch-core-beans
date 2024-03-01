package nablarch.core.beans;

import org.junit.Test;

import java.sql.Timestamp;

import static nablarch.core.beans.BeanUtilConversionCustomizedTest.date;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * レコード型に対するコンバータのテスト。
 * <p>
 * カスタムコンバータそのもののテストは{@link BeanUtilConversionCustomizedTest}にて行っているため、
 * ここではレコードに対してカスタムコンバータを適用することができることを確認する。
 */
@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilConversionCustomizedForRecordTest {

    public record DateDestRecord(java.util.Date foo,
                                 java.sql.Date bar,
                                 Timestamp baz){}


    @Test
    public void デフォルトコンバータを使用して値を変換できること() {
        BeanUtilConversionCustomizedTest.Src srcBean = new BeanUtilConversionCustomizedTest.Src();
        srcBean.setFoo("20180214");
        srcBean.setBar("20180215");
        srcBean.setBaz("20180216");
        CopyOptions copyOptions = CopyOptions.empty();
        DateDestRecord destRecord = BeanUtil.createAndCopy(DateDestRecord.class, srcBean, copyOptions);

        assertThat(destRecord.foo(), is(date("2018-02-14 00:00:00")));
        assertThat(destRecord.bar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destRecord.baz(), is(Timestamp.valueOf("2018-02-16 00:00:00")));
    }

    @Test
    public void デフォルトコンバータを使用して値の変換に失敗すること() {
        BeanUtilConversionCustomizedTest.Src srcBean = new BeanUtilConversionCustomizedTest.Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        CopyOptions copyOptions = CopyOptions.empty();
        DateDestRecord destRecord = BeanUtil.createAndCopy(DateDestRecord.class, srcBean, copyOptions);

        assertThat(destRecord.foo(), is(nullValue()));
        assertThat(destRecord.bar(), is(nullValue()));
        assertThat(destRecord.baz(), is(nullValue()));
    }

    @Test
    public void プロパティ名を指定したカスタムコンバーターを使用して値を変換できること() {
        BeanUtilConversionCustomizedTest.Src srcBean = new BeanUtilConversionCustomizedTest.Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        final java.util.Date date = new java.util.Date();
        final java.sql.Date sqlDate = new java.sql.Date(0);
        final Timestamp timestamp = new Timestamp(0);
        CopyOptions copyOptions = CopyOptions.options()
                .converterByName("foo", java.util.Date.class, value -> date)
                .converterByName("bar", java.sql.Date.class, value -> sqlDate)
                .converterByName("baz", Timestamp.class, value -> timestamp)
                .build();
        DateDestRecord destRecord = BeanUtil.createAndCopy(DateDestRecord.class, srcBean, copyOptions);

        assertThat(destRecord.foo(), is(sameInstance(date)));
        assertThat(destRecord.bar(), is(sameInstance(sqlDate)));
        assertThat(destRecord.baz(), is(sameInstance(timestamp)));
    }

    @Test
    public void クラスを指定したカスタムコンバーターを使用して値を変換できること() {
        BeanUtilConversionCustomizedTest.Src srcBean = new BeanUtilConversionCustomizedTest.Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        final java.util.Date date = new java.util.Date();
        final java.sql.Date sqlDate = new java.sql.Date(0);
        final Timestamp timestamp = new Timestamp(0);
        CopyOptions copyOptions = CopyOptions.options()
                .converter(java.util.Date.class, value -> date)
                .converterByName("bar", java.sql.Date.class, value -> sqlDate)
                .converterByName("baz", Timestamp.class, value -> timestamp)
                .build();
        DateDestRecord destRecord = BeanUtil.createAndCopy(DateDestRecord.class, srcBean, copyOptions);

        assertThat(destRecord.foo(), is(sameInstance(date)));
        assertThat(destRecord.bar(), is(sameInstance(sqlDate)));
        assertThat(destRecord.baz(), is(sameInstance(timestamp)));
    }


}
