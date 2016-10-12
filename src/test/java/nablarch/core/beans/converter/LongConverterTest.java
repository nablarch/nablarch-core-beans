package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link LongConverter}のテスト。
 */
public class LongConverterTest {

    private final LongConverter sut = new LongConverter();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromLong() throws Exception {
        assertThat(sut.convert(100L), is(100L));
    }

    @Test
    public void fromTrue() throws Exception {
        assertThat(sut.convert(true), is(1L));
    }

    @Test
    public void fromFalse() throws Exception {
        assertThat(sut.convert(false), is(0L));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("12345"), is(12345L));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"54321"}), is(54321L));
    }

    @Test
    public void fromNotNumberString() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert("abc");
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