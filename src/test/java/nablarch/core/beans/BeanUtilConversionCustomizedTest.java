package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link BeanUtil#copy(Object, Object, CopyOptions)}で変換をカスタマイズする場合のテスト。
 * 
 * @author Taichi Uragami
 *
 */
public class BeanUtilConversionCustomizedTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void デフォルト日付パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("20180214");
        srcBean.setBar("20180215");
        srcBean.setBaz("20180216");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
        assertThat(destBean.getBar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destBean.getBaz(), is(Timestamp.valueOf("2018-02-16 00:00:00")));
    }

    @Test
    public void 文字列への変換_デフォルト日付パターン() {
        DateDest destBean = new DateDest();
        destBean.setFoo(date("2018-02-14 00:00:00"));
        destBean.setBar(java.sql.Date.valueOf("2018-02-15"));
        destBean.setBaz(Timestamp.valueOf("2018-02-16 00:00:00"));

        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("Wed Feb 14 00:00:00 JST 2018"));
        assertThat(srcBean.getBar(), is("2018-02-15"));
        assertThat(srcBean.getBaz(), is("2018-02-16 00:00:00.0"));
    }

    @Test
    public void デフォルト日付パターンの変換失敗() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(nullValue()));
        assertThat(destBean.getBar(), is(nullValue()));
        assertThat(destBean.getBaz(), is(nullValue()));
    }

    @Test
    public void プロパティ名を指定したカスタム日付パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018-02-15");
        srcBean.setBaz("2018.02.16 12:34:56");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("foo", "yyyy/MM/dd")
                .datePatternByName("bar", "yyyy-MM-dd")
                .datePatternByName("baz", "yyyy.MM.dd HH:mm:ss")
                .build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
        assertThat(destBean.getBar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destBean.getBaz(), is(Timestamp.valueOf("2018-02-16 12:34:56")));
    }

    @Test
    public void 文字列への変換_プロパティ名を指定したカスタム日付パターン() {
        DateDest destBean = new DateDest();
        destBean.setFoo(date("2018-02-14 00:00:00"));
        destBean.setBar(java.sql.Date.valueOf("2018-02-15"));
        destBean.setBaz(Timestamp.valueOf("2018-02-16 12:34:56"));
        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("foo", "yyyy/MM/dd")
                .datePatternByName("bar", "yyyy-MM-dd")
                .datePatternByName("baz", "yyyy.MM.dd HH:mm:ss")
                .build();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("2018/02/14"));
        assertThat(srcBean.getBar(), is("2018-02-15"));
        assertThat(srcBean.getBaz(), is("2018.02.16 12:34:56"));
    }

    @Test
    public void プロパティ名を指定したカスタムコンバーター() {
        final java.util.Date date = new java.util.Date();
        final java.sql.Date sqlDate = new java.sql.Date(0);
        final Timestamp timestamp = new Timestamp(0);
        Src srcBean = new Src();
        srcBean.setFoo("2018-02-14");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.options()
                .converterByName("foo", java.util.Date.class, new Converter<java.util.Date>() {
                    @Override
                    public java.util.Date convert(Object value) {
                        return date;
                    }
                })
                .converterByName("bar", java.sql.Date.class, new Converter<java.sql.Date>() {
                    @Override
                    public java.sql.Date convert(Object value) {
                        return sqlDate;
                    }
                })
                .converterByName("baz", Timestamp.class, new Converter<Timestamp>() {
                    @Override
                    public Timestamp convert(Object value) {
                        return timestamp;
                    }
                })
                .build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(sameInstance(date)));
        assertThat(destBean.getBar(), is(sameInstance(sqlDate)));
        assertThat(destBean.getBaz(), is(sameInstance(timestamp)));
    }

    @Test
    public void グローバルなカスタム日付パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.options()
                .datePattern("yyyy/MM/dd").build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
        assertThat(destBean.getBar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destBean.getBaz(), is(Timestamp.valueOf("2018-02-16 00:00:00")));
    }

    @Test
    public void 文字列への変換_グローバルなカスタム日付パターン() {
        DateDest destBean = new DateDest();
        destBean.setFoo(date("2018-02-14 00:00:00"));
        destBean.setBar(java.sql.Date.valueOf("2018-02-15"));
        destBean.setBaz(Timestamp.valueOf("2018-02-16 00:00:00"));
        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.options()
                .datePattern("yyyy/MM/dd").build();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("2018/02/14"));
        assertThat(srcBean.getBar(), is("2018/02/15"));
        assertThat(srcBean.getBaz(), is("2018/02/16"));
    }

    @Test
    public void クラスを指定したカスタムコンバーター() {
        final java.util.Date date = new java.util.Date();
        final java.sql.Date sqlDate = new java.sql.Date(0);
        final Timestamp timestamp = new Timestamp(0);
        Src srcBean = new Src();
        srcBean.setFoo("2018-02-14");
        srcBean.setBar("2018-02-15");
        srcBean.setBaz("2018-02-16");
        DateDest destBean = new DateDest();
        CopyOptions copyOptions = CopyOptions.options()
                .converter(java.util.Date.class, new Converter<java.util.Date>() {
                    @Override
                    public java.util.Date convert(Object value) {
                        return date;
                    }
                })
                .converterByName("bar", java.sql.Date.class, new Converter<java.sql.Date>() {
                    @Override
                    public java.sql.Date convert(Object value) {
                        return sqlDate;
                    }
                })
                .converterByName("baz", Timestamp.class, new Converter<Timestamp>() {
                    @Override
                    public Timestamp convert(Object value) {
                        return timestamp;
                    }
                })
                .build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(sameInstance(date)));
        assertThat(destBean.getBar(), is(sameInstance(sqlDate)));
        assertThat(destBean.getBaz(), is(sameInstance(timestamp)));
    }

    @Test
    public void デフォルト数字パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("1234567890");
        srcBean.setBar("1234567890");
        srcBean.setBaz("1234567890");
        NumberDest destBean = new NumberDest();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(1234567890));
        assertThat(destBean.getBar(), is(1234567890L));
        assertThat(destBean.getBaz(), is(new BigDecimal("1234567890")));
    }

    @Test
    public void 文字列への変換_デフォルト数字パターン() {
        NumberDest destBean = new NumberDest();
        destBean.setFoo(1234567890);
        destBean.setBar(1234567890L);
        destBean.setBaz(new BigDecimal("1234567890"));

        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("1234567890"));
        assertThat(srcBean.getBar(), is("1234567890"));
        assertThat(srcBean.getBaz(), is("1234567890"));
    }

    @Test
    public void デフォルト数字パターンの変換失敗() {
        Src srcBean = new Src();
        srcBean.setFoo("1,234,567,890");
        srcBean.setBar("1,234,567,890");
        srcBean.setBaz("1,234,567,890");
        NumberDest destBean = new NumberDest();
        CopyOptions copyOptions = CopyOptions.empty();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(nullValue()));
        assertThat(destBean.getBar(), is(nullValue()));
        assertThat(destBean.getBaz(), is(nullValue()));
    }

    @Test
    public void プロパティ名を指定したカスタム数字パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("1,234,567,890");
        srcBean.setBar("1,234,567,890");
        srcBean.setBaz("1,234,567,890");
        NumberDest destBean = new NumberDest();
        CopyOptions copyOptions = CopyOptions.options()
                .numberPatternByName("foo", "#,###")
                .build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(1234567890));
        assertThat(destBean.getBar(), is(nullValue()));
        assertThat(destBean.getBaz(), is(nullValue()));
    }

    @Test
    public void 文字列への変換_プロパティ名を指定したカスタム数字パターン() {
        NumberDest destBean = new NumberDest();
        destBean.setFoo(1234567890);
        destBean.setBar(1234567890L);
        destBean.setBaz(new BigDecimal("1234567890"));
        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.options()
                .numberPatternByName("foo", "#,###")
                .numberPatternByName("bar", "#,####")
                .numberPatternByName("baz", "#,#####")
                .build();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("1,234,567,890"));
        assertThat(srcBean.getBar(), is("12,3456,7890"));
        assertThat(srcBean.getBaz(), is("12345,67890"));
    }

    @Test
    public void グローバルなカスタム数字パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("1,234,567,890");
        srcBean.setBar("1,234,567,890");
        srcBean.setBaz("1,234,567,890");
        NumberDest destBean = new NumberDest();
        CopyOptions copyOptions = CopyOptions.options()
                .numberPattern("#,###").build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(1234567890));
        assertThat(destBean.getBar(), is(1234567890L));
        assertThat(destBean.getBaz(), is(new BigDecimal("1234567890")));
    }

    @Test
    public void 文字列への変換_グローバルなカスタム数字パターン() {
        NumberDest destBean = new NumberDest();
        destBean.setFoo(1234567890);
        destBean.setBar(1234567890L);
        destBean.setBaz(new BigDecimal("1234567890"));
        Src srcBean = new Src();
        CopyOptions copyOptions = CopyOptions.options()
                .numberPattern("#,###").build();
        BeanUtil.copy(destBean, srcBean, copyOptions);

        assertThat(srcBean.getFoo(), is("1,234,567,890"));
        assertThat(srcBean.getBar(), is("1,234,567,890"));
        assertThat(srcBean.getBaz(), is("1,234,567,890"));
    }

    private static Date date(String sqlTimestampPattern) {
        return new Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }

    public static class Src {

        private String foo;
        private String bar;
        private String baz;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBaz() {
            return baz;
        }

        public void setBaz(String baz) {
            this.baz = baz;
        }
    }

    public static class DateDest {

        private java.util.Date foo;
        private java.sql.Date bar;
        private Timestamp baz;

        public java.util.Date getFoo() {
            return foo;
        }

        public void setFoo(java.util.Date foo) {
            this.foo = foo;
        }

        public java.sql.Date getBar() {
            return bar;
        }

        public void setBar(java.sql.Date bar) {
            this.bar = bar;
        }

        public Timestamp getBaz() {
            return baz;
        }

        public void setBaz(Timestamp baz) {
            this.baz = baz;
        }
    }

    public static class NumberDest {

        private Integer foo;
        private Long bar;
        private BigDecimal baz;

        public Integer getFoo() {
            return foo;
        }

        public void setFoo(Integer foo) {
            this.foo = foo;
        }

        public Long getBar() {
            return bar;
        }

        public void setBar(Long bar) {
            this.bar = bar;
        }

        public BigDecimal getBaz() {
            return baz;
        }

        public void setBaz(BigDecimal baz) {
            this.baz = baz;
        }
    }
}
