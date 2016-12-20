package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link ShortConverter}のテスト。
 */
public class ShortConverterTest {

    private final ShortConverter sut = new ShortConverter();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromShort() throws Exception {
        assertThat(sut.convert(Short.valueOf("1")), is(Short.valueOf("1")));
    }

    @Test
    public void fromTrueBoolean() throws Exception {
        assertThat(sut.convert(true), is(Short.valueOf("1")));
    }

    @Test
    public void fromFalseBoolean() throws Exception {
        assertThat(sut.convert(false), is(Short.valueOf("0")));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("10"), is(Short.valueOf("10")));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"11"}), is(Short.valueOf("11")));
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