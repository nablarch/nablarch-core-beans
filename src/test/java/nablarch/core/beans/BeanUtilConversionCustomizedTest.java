package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class BeanUtilConversionCustomizedTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void デフォルト日付パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("20180214");
        srcBean.setBar("20180215");
        srcBean.setBaz("20180216");
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options().build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
        assertThat(destBean.getBar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destBean.getBaz(), is(Timestamp.valueOf("2018-02-16 00:00:00")));
    }

    @Test
    public void デフォルト日付パターンの変換失敗() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        srcBean.setBar("2018/02/15");
        srcBean.setBaz("2018/02/16");
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options().build();
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
        Dest destBean = new Dest();
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
    public void プロパティ名を指定したカスタムコンバーター() {
        final java.util.Date date = new java.util.Date();
        final java.sql.Date sqlDate = new java.sql.Date(0);
        final Timestamp timestamp = new Timestamp(0);
        Src srcBean = new Src();
        srcBean.setFoo("2018-02-14");
        Dest destBean = new Dest();
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
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options()
                .datePattern("yyyy/MM/dd").build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
        assertThat(destBean.getBar(), is(java.sql.Date.valueOf("2018-02-15")));
        assertThat(destBean.getBaz(), is(Timestamp.valueOf("2018-02-16 00:00:00")));
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
        Dest destBean = new Dest();
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

    public static class Dest {

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
}
