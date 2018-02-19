package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.comparesEqualTo;
import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link BigDecimalConverter}のテスト。
 */
public class BigDecimalConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final BigDecimalConverter sut = new BigDecimalConverter();

    @Test
    public void fromBigDecimal() throws Exception {
        final BigDecimal input = new BigDecimal("100.1234");
        final BigDecimal actual = sut.convert(input);

        assertThat(actual, comparesEqualTo(input));
    }

    @Test
    public void fromBigInteger() throws Exception {
        final BigInteger input = new BigInteger("1234554321");
        final BigDecimal actual = sut.convert(input);

        assertThat(actual, comparesEqualTo(new BigDecimal("1234554321")));
    }

    @Test
    public void fromDouble() throws Exception {
        final BigDecimal actual = sut.convert(1.1D);
        assertThat(actual, closeTo(new BigDecimal("1.1"), new BigDecimal("0.000001")));
    }

    @Test
    public void fromFloat() throws Exception {
        final BigDecimal actual = sut.convert(1.2F);
        assertThat(actual, closeTo(new BigDecimal("1.2"), new BigDecimal("0.00001")));
    }

    @Test
    public void fromInteger() throws Exception {
        final BigDecimal actual = sut.convert(Integer.MAX_VALUE);
        assertThat(actual, comparesEqualTo(new BigDecimal(Integer.MAX_VALUE)));
    }

    @Test
    public void fromLong() throws Exception {
        final BigDecimal actual = sut.convert(Long.MAX_VALUE);
        assertThat(actual, comparesEqualTo(BigDecimal.valueOf(Long.MAX_VALUE)));
    }

    @Test
    public void fromString() throws Exception {
        final BigDecimal actual = sut.convert("100");
        assertThat(actual, comparesEqualTo(new BigDecimal("100")));
    }

    @Test
    public void fromExponentialExpression() throws Exception {
        final BigDecimal actual = sut.convert("1e2");
        assertThat(actual, comparesEqualTo(new BigDecimal("100")));
    }

    @Test
    public void fromBooleanTrue() throws Exception {
        final BigDecimal actual = sut.convert(true);
        assertThat(actual, comparesEqualTo(BigDecimal.ONE));
    }

    @Test
    public void fromBooleanFalse() throws Exception {
        final BigDecimal actual = sut.convert(false);
        assertThat(actual, comparesEqualTo(BigDecimal.ZERO));
    }

    @Test
    public void fromStringArray() throws Exception {
        final BigDecimal actual = sut.convert(new String[] { "123" });
        assertThat(actual, comparesEqualTo(new BigDecimal("123")));
    }

    @Test
    public void fromInvalidString() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert あいうえお to BigDecimal.");
        sut.convert("あいうえお");
    }

    @Test
    public void fromUnsupportedDataType() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert [] to BigDecimal.");
        sut.convert(new ArrayList<String>());
    }

    @Test
    public void fromInvalidScale() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal scale(10000): needs to be between(-9999, 9999)");
        sut.convert("100e-10000");
    }

    @Test
    public void fromInvalidScaleInArray() throws Exception {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage("Illegal scale(-99999): needs to be between(-9999, 9999)");
        sut.convert(new String[] { "100e99999" });
    }

    @Test
    public void パターンを指定して文字列から変換() {
        BigDecimalConverter converter = new BigDecimalConverter(Arrays.asList("#,###", "#,####.#"));
        assertThat(converter.convert("1,234,567"), is(new BigDecimal("1234567")));
        assertThat(converter.convert("1,2345.67"), is(new BigDecimal("12345.67")));
    }

    @Test
    public void パターンを指定して文字列から変換失敗() {
        expectedException.expect(IllegalArgumentException.class);
        BigDecimalConverter sut = new BigDecimalConverter(Arrays.asList("#,###", "#,####.#"));
        sut.convert("Not number");
    }
}