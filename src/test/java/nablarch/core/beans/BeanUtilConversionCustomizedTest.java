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
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options().build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
    }

    @Test
    public void デフォルト日付パターンの変換失敗() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options().build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(nullValue()));
    }

    @Test
    public void カスタム日付パターン() {
        Src srcBean = new Src();
        srcBean.setFoo("2018/02/14");
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options()
                .datePattern("foo", "yyyy/MM/dd").build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(date("2018-02-14 00:00:00")));
    }

    @Test
    public void カスタムコンバーター() {
        final java.util.Date date = new java.util.Date();
        Src srcBean = new Src();
        srcBean.setFoo("2018-02-14");
        Dest destBean = new Dest();
        CopyOptions copyOptions = CopyOptions.options()
                .converter("foo", new Converter<java.util.Date>() {
                    @Override
                    public java.util.Date convert(Object value) {
                        return date;
                    }
                }).build();
        BeanUtil.copy(srcBean, destBean, copyOptions);

        assertThat(destBean.getFoo(), is(sameInstance(date)));
    }

    private static Date date(String sqlTimestampPattern) {
        return new Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }

    public static class Src {

        private String foo;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }
    }

    public static class Dest {

        private java.util.Date foo;

        public java.util.Date getFoo() {
            return foo;
        }

        public void setFoo(java.util.Date foo) {
            this.foo = foo;
        }
    }
}
