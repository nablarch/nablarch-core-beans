package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.core.beans.converter.DateConverter;

public class CopyOptionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void hasNamedConverter() {
        CopyOptions sut = CopyOptions.options()
                .datePatternsByName("foo", Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converterByName("bar", java.util.Date.class,
                        new DateConverter(Collections.singletonList("yyyy.MM.dd")))
                .build();
        assertThat(sut.hasNamedConverter("foo", java.util.Date.class), is(true));
        assertThat(sut.hasNamedConverter("foo", java.sql.Date.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Timestamp.class), is(true));
        assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Object.class), is(false));

        assertThat(sut.hasNamedConverter("bar", java.util.Date.class), is(true));
        assertThat(sut.hasNamedConverter("bar", java.sql.Date.class), is(false));
        assertThat(sut.hasNamedConverter("bar", Timestamp.class), is(false));
        assertThat(sut.hasNamedConverter("bar", String.class), is(false));

        assertThat(sut.hasNamedConverter("baz", java.util.Date.class), is(false));
    }

    @Test
    public void convertByName() {
        CopyOptions sut = CopyOptions.options()
                .datePatternsByName("foo", Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converterByName("bar", java.util.Date.class,
                        new DateConverter(Collections.singletonList("yyyy.MM.dd")))
                .build();
        assertThat((java.util.Date) sut.convertByName("foo", java.util.Date.class, "2018/02/14"),
                is(date("2018-02-14 00:00:00")));
        assertThat((java.util.Date) sut.convertByName("foo", java.util.Date.class, "2018-02-14"),
                is(date("2018-02-14 00:00:00")));
        assertThat((java.sql.Date) sut.convertByName("foo", java.sql.Date.class, "2018/02/14"),
                is(java.sql.Date.valueOf("2018-02-14")));
        assertThat((Timestamp) sut.convertByName("foo", Timestamp.class, "2018/02/14"),
                is(Timestamp.valueOf("2018-02-14 00:00:00")));

        assertThat((java.util.Date) sut.convertByName("bar", java.util.Date.class, "2018.02.14"),
                is(date("2018-02-14 00:00:00")));

        //文字列への変換
        assertThat((String) sut.convertByName("foo", String.class, date("2018-02-14 00:00:00")),
                is("2018/02/14"));
    }

    @Test
    public void convertByName失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatternByName("foo", "yyyyMMdd")
                .converterByName("bar", java.util.Date.class,
                        new DateConverter(Collections.singletonList("yyyyMMdd")))
                .build();
        sut.convertByName("baz", java.util.Date.class, "20180214");
    }

    @Test
    public void hasTypedConverter() {
        Converter<BigDecimal> converter = new Converter<BigDecimal>() {
            @Override
            public BigDecimal convert(Object value) {
                return null;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converter(BigDecimal.class, converter)
                .build();
        assertThat(sut.hasTypedConverter(java.util.Date.class), is(true));
        assertThat(sut.hasTypedConverter(java.sql.Date.class), is(true));
        assertThat(sut.hasTypedConverter(Timestamp.class), is(true));
        assertThat(sut.hasTypedConverter(String.class), is(true));
        assertThat(sut.hasTypedConverter(BigDecimal.class), is(true));
        assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void convertByType() {
        final Object object = new Object();
        Converter<Object> converter = new Converter<Object>() {
            @Override
            public Object convert(Object value) {
                return object;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converter(Object.class, converter)
                .build();
        assertThat((java.util.Date) sut.convertByType(java.util.Date.class, "2018/02/14"),
                is(date("2018-02-14 00:00:00")));
        assertThat((java.util.Date) sut.convertByType(java.util.Date.class, "2018-02-14"),
                is(date("2018-02-14 00:00:00")));
        assertThat((java.sql.Date) sut.convertByType(java.sql.Date.class, "2018/02/14"),
                is(java.sql.Date.valueOf("2018-02-14")));
        assertThat((Timestamp) sut.convertByType(Timestamp.class, "2018/02/14"),
                is(Timestamp.valueOf("2018-02-14 00:00:00")));

        //文字列への変換
        assertThat((String) sut.convertByType(String.class, date("2018-02-14 00:00:00")),
                is("2018/02/14"));

        //登録されたConverterの使用
        assertThat(sut.convertByType(Object.class, "test"), is(sameInstance(object)));
    }

    @Test
    public void convertByType失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        sut.convertByType(java.util.Date.class, "20180214");
    }

    private static java.util.Date date(String sqlTimestampPattern) {
        return new java.util.Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }
}