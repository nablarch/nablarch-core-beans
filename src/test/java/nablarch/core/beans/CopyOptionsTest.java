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
import nablarch.core.beans.converter.StringConverter;

public class CopyOptionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void datePatternsByName() {
        CopyOptions sut = CopyOptions.options()
                .datePatternsByName("foo", Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        assertThat(sut.hasNamedConverter("foo", java.util.Date.class), is(true));
        assertThat(sut.hasNamedConverter("foo", java.sql.Date.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Timestamp.class), is(true));
        assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Object.class), is(false));

        assertThat(sut.hasNamedConverter("bar", java.util.Date.class), is(false));
        assertThat(sut.hasNamedConverter("baz", java.util.Date.class), is(false));
    }

    @Test
    public void numberPatternsByName() {
        CopyOptions sut = CopyOptions.options()
                .numberPatternsByName("foo", Arrays.asList("#,###", "#,####"))
                .build();
        assertThat(sut.hasNamedConverter("foo", Integer.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Long.class), is(true));
        assertThat(sut.hasNamedConverter("foo", BigDecimal.class), is(true));
        assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        assertThat(sut.hasNamedConverter("foo", Object.class), is(false));

        assertThat(sut.hasNamedConverter("bar", Integer.class), is(false));
        assertThat(sut.hasNamedConverter("baz", Integer.class), is(false));
    }

    @Test
    public void converterByName() {
        CopyOptions sut = CopyOptions.options()
                .converterByName("bar", String.class, new StringConverter())
                .build();
        assertThat(sut.hasNamedConverter("foo", String.class), is(false));
        assertThat(sut.hasNamedConverter("bar", String.class), is(true));
        assertThat(sut.hasNamedConverter("bar", Object.class), is(false));
        assertThat(sut.hasNamedConverter("baz", String.class), is(false));
    }

    @Test
    public void convertByName() {
        final Object mockValue = new Object();
        Converter<Object> mockConverter = new Converter<Object>() {
            @Override
            public Object convert(Object value) {
                return mockValue;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .converterByName("foo", Object.class, mockConverter)
                .build();
        assertThat(sut.convertByName("foo", Object.class, "Source value"),
                is(sameInstance(mockValue)));
    }

    @Test
    public void convertByNameToStringWithDatePattern() {
        CopyOptions sut = CopyOptions.options()
                .datePatternByName("foo", "yyyy/MM/dd")
                .build();
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
    public void datePatterns() {
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        assertThat(sut.hasTypedConverter(java.util.Date.class), is(true));
        assertThat(sut.hasTypedConverter(java.sql.Date.class), is(true));
        assertThat(sut.hasTypedConverter(Timestamp.class), is(true));
        assertThat(sut.hasTypedConverter(String.class), is(true));
        assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void numberPatterns() {
        CopyOptions sut = CopyOptions.options()
                .numberPatterns(Arrays.asList("#,###", "#,###,###"))
                .build();
        assertThat(sut.hasTypedConverter(Integer.class), is(true));
        assertThat(sut.hasTypedConverter(Long.class), is(true));
        assertThat(sut.hasTypedConverter(BigDecimal.class), is(true));
        assertThat(sut.hasTypedConverter(String.class), is(true));
        assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void converter() {
        Converter<BigDecimal> converter = new Converter<BigDecimal>() {
            @Override
            public BigDecimal convert(Object value) {
                return null;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .converter(BigDecimal.class, converter)
                .build();
        assertThat(sut.hasTypedConverter(BigDecimal.class), is(true));
        assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void convertByType() {
        final Object mockValue = new Object();
        Converter<Object> mockConverter = new Converter<Object>() {
            @Override
            public Object convert(Object value) {
                return mockValue;
            }
        };
        CopyOptions sut = CopyOptions.options()
                .converter(Object.class, mockConverter)
                .build();
        assertThat(sut.convertByType(Object.class, "test"), is(sameInstance(mockValue)));
    }

    @Test
    public void convertByTypeToStringWithDatePattern() {
        CopyOptions sut = CopyOptions.options()
                .datePattern("yyyy/MM/dd")
                .build();
        assertThat((String) sut.convertByType(String.class, date("2018-02-14 00:00:00")),
                is("2018/02/14"));
    }

    @Test
    public void convertByTypeToStringWithNumberPattern() {
        CopyOptions sut = CopyOptions.options()
                .numberPattern("#,###")
                .build();
        assertThat((String) sut.convertByType(String.class, 1234567890), is("1,234,567,890"));
    }

    @Test
    public void convertByType失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        sut.convertByType(java.util.Date.class, "20180214");
    }

    @Test
    public void datePatternAndNumberPattern() throws Exception {
        CopyOptions sut = CopyOptions.options()
                .datePattern("yyyy/MM/dd").numberPattern("#,###")
                .datePatternByName("foo", "yyyy.MM.dd").numberPatternByName("foo", "#,####")
                .build();

        assertThat(sut.hasTypedConverter(String.class), is(true));
        assertThat((String) sut.convertByType(String.class, date("2018-02-19 00:00:00")),
                is("2018/02/19"));
        assertThat((String) sut.convertByType(String.class, 1234567890), is("1,234,567,890"));

        assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        assertThat((String) sut.convertByName("foo", String.class, date("2018-02-19 00:00:00")),
                is("2018.02.19"));
        assertThat((String) sut.convertByName("foo", String.class, 1234567890),
                is("12,3456,7890"));
    }

    private static java.util.Date date(String sqlTimestampPattern) {
        return new java.util.Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }
}