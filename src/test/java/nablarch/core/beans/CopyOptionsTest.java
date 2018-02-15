package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

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
                .converterByName("bar", new DateConverter(Collections.singletonList("yyyy.MM.dd")))
                .build();
        assertThat(sut.hasNamedConverter("foo"), is(true));
        assertThat(sut.hasNamedConverter("bar"), is(true));
        assertThat(sut.hasNamedConverter("baz"), is(false));
    }

    @Test
    public void convertByName() {
        CopyOptions sut = CopyOptions.options()
                .datePatternsByName("foo", Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converterByName("bar", new DateConverter(Collections.singletonList("yyyy.MM.dd")))
                .build();
        assertThat((Date) sut.convertByName("foo", "2018/02/14"), is(date("2018-02-14 00:00:00")));
        assertThat((Date) sut.convertByName("foo", "2018-02-14"), is(date("2018-02-14 00:00:00")));
        assertThat((Date) sut.convertByName("bar", "2018.02.14"), is(date("2018-02-14 00:00:00")));
    }

    @Test
    public void convertByName失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatternByName("foo", "yyyyMMdd")
                .converterByName("bar", new DateConverter(Collections.singletonList("yyyyMMdd")))
                .build();
        sut.convertByName("baz", "20180214");
    }

    @Test
    public void hasTypedConverter() {
        Converter<Object> converter = new Converter<Object>() {
            @Override
            public Object convert(Object value) {
                return null;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .converter(Object.class, converter)
                .build();
        assertThat(sut.hasTypedConverter(Date.class), is(true));
        assertThat(sut.hasTypedConverter(Object.class), is(true));
        assertThat(sut.hasTypedConverter(String.class), is(false));
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
        assertThat((Date) sut.convertByType(Date.class, "2018/02/14"),
                is(date("2018-02-14 00:00:00")));
        assertThat((Date) sut.convertByType(Date.class, "2018-02-14"),
                is(date("2018-02-14 00:00:00")));
        assertThat(sut.convertByType(Object.class, "test"), is(sameInstance(object)));
    }

    @Test
    public void convertByType失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        sut.convertByType(Date.class, "20180214");
    }

    private static Date date(String sqlTimestampPattern) {
        return new Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }
}