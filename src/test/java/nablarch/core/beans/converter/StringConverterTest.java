package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

import nablarch.core.beans.ConversionException;

import org.junit.Test;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link StringConverter}のテスト
 */
public class StringConverterTest {

    private final StringConverter sut = new StringConverter();

    @Test
    public void string() {
        assertThat(sut.convert("111"), is("111"));
    }

    @Test
    public void integer() {
        assertThat(sut.convert(100), is("100"));
    }

    @Test
    public void boolean_true() {
        assertThat(sut.convert(Boolean.TRUE), is("1"));
    }

    @Test
    public void boolean_false() {
        assertThat(sut.convert(Boolean.FALSE), is("0"));
    }

    @Test
    public void stringArray_singleValue() {
        assertThat(sut.convert(new String[] { "1" }), is("1"));
    }

    @Test(expected = ConversionException.class)
    public void stringArray_multipleValue() {
        sut.convert(new String[] { "1", "2" });
    }

    @Test
    public void bigDecimal() {
        assertThat(sut.convert(new BigDecimal("0.0000000001")), is("0.0000000001"));
    }

    @Test
    public void dateWithoutPattern() {
        Date value = new Date(Timestamp.valueOf("2018-02-19 00:00:00").getTime());
        assertThat(sut.convert(value), is("Mon Feb 19 00:00:00 JST 2018"));
    }

    @Test
    public void dateWithPattern() {
        Date value = new Date(Timestamp.valueOf("2018-02-19 00:00:00").getTime());
        assertThat(new StringConverter("yyyy/MM/dd", null).convert(value), is("2018/02/19"));
    }

    @Test
    public void longWithoutPattern() {
        Long value = 1234567890L;
        assertThat(sut.convert(value), is("1234567890"));
    }

    @Test
    public void longWithPattern() {
        Long value = 1234567890L;
        assertThat(new StringConverter(null, "#,###").convert(value), is("1,234,567,890"));
    }

    @Test
    public void LocalDateWithoutPattern() {
        assertThat(sut.convert(LocalDate.of(2018, 2, 19)), is("2018-02-19"));
    }

    @Test
    public void LocalDateWithPattern() {
        assertThat(new StringConverter("yyyy/MM/dd", null).convert(LocalDate.of(2018, 2, 19)), is("2018/02/19"));
    }

    @Test
    public void LocalDateTimeWithoutPattern() {
        assertThat(sut.convert(LocalDateTime.of(2018, 2, 19, 15, 10)), is("2018-02-19T15:10"));
    }

    @Test
    public void LocalDateTimeWithPattern() {
        assertThat(new StringConverter("yyyy/MM/dd", null).convert(LocalDateTime.of(2018, 2, 19, 15, 10)), is("2018/02/19"));
    }

    @Test
    public void Merge() {
        StringConverter mergedSut = sut.merge(new StringConverter("yyyy/MM/dd", null));
        assertEquals("2018/02/21", mergedSut.convert(LocalDate.of(2018, 2, 21)));
    }

    @Test
    public void ReceiverPriorityMerge() {
        StringConverter mergedSut = new StringConverter("yyyy/MM/dd", null)
                .merge(new StringConverter("yyyy.MM.dd", null));
        assertEquals("2018/02/21", mergedSut.convert(LocalDate.of(2018, 2, 21)));
    }
}
