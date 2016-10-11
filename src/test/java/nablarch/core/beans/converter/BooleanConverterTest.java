package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.*;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link BooleanConverter}のテスト。
 */
public class BooleanConverterTest {

    private final BooleanConverter sut = new BooleanConverter();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromBoolean() throws Exception {
        assertThat(sut.convert(true), is(true));
    }

    @Test
    public void fromNumberEqualZero() throws Exception {
        assertThat(sut.convert(0), is(false));
    }

    @Test
    public void fromNumberNotZero() throws Exception {
        assertThat(sut.convert(Long.MAX_VALUE), is(true));
    }

    @Test
    public void fromTrueString() throws Exception {
        assertThat(sut.convert("true"), is(true));
        assertThat(sut.convert("True"), is(true));
        assertThat(sut.convert("TRUE"), is(true));
        assertThat(sut.convert("1"), is(true));
        assertThat(sut.convert("on"), is(true));
        assertThat(sut.convert("On"), is(true));
        assertThat(sut.convert("ON"), is(true));
    }

    @Test
    public void fromFalseString() throws Exception {
        assertThat(sut.convert("false"), is(false));
        assertThat(sut.convert("off"), is(false));
        assertThat(sut.convert("0"), is(false));
        assertThat(sut.convert("2"), is(false));
        assertThat(sut.convert("a"), is(false));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"true"}), is(true));
        assertThat(sut.convert(new String[] {"on"}), is(true));
        assertThat(sut.convert(new String[] {"false"}), is(false));
        assertThat(sut.convert(new String[] {"0"}), is(false));
    }

    @Test
    public void fromMultipleStringArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"true", "false"});
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }
}