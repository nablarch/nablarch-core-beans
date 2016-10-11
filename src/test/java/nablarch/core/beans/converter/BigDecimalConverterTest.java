package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.hamcrest.CoreMatchers;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link BigDecimalConverter}のテスト。
 */
public class BigDecimalConverterTest {

    private final BigDecimalConverter sut = new BigDecimalConverter();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromBigDecimal() throws Exception {
        final BigDecimal input = new BigDecimal("1.1");
        assertThat(sut.convert(input), is(input));
    }

    @Test
    public void fromInteger() throws Exception {
        assertThat(sut.convert(1), is(BigDecimal.ONE));
    }

    @Test
    public void fromLong() throws Exception {
        assertThat(sut.convert(Long.MAX_VALUE), is(BigDecimal.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void fromBigInteger() throws Exception {
        assertThat(sut.convert(BigInteger.valueOf(Long.MAX_VALUE)), is(BigDecimal.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void fromDouble() throws Exception {
        assertThat(sut.convert(Double.valueOf("1.1")), is(new BigDecimal("1.1")));
    }

    @Test
    public void fromShort() throws Exception {
        assertThat(sut.convert(Float.valueOf("1.0")), is(new BigDecimal("1.0")));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("100.100"), is(new BigDecimal("100.100")));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"1.1"}), is(new BigDecimal("1.1")));
    }

    @Test
    public void fromBooleanTrue() throws Exception {
        assertThat(sut.convert(true), is(BigDecimal.ONE));
    }

    @Test
    public void fromBooleanFalse() throws Exception {
        assertThat(sut.convert(false), is(BigDecimal.ZERO));
    }

    @Test
    public void fromNotNumberString() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert("a");
    }

    @Test
    public void fromMultipleStringArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"1", "2"});
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }
}