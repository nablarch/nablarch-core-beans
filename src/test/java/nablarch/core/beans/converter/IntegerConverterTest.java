package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link IntegerConverter}のテスト。
 */
public class IntegerConverterTest {

    private final IntegerConverter sut = new IntegerConverter();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    
    @Test
    public void fromInteger() throws Exception {
        assertThat(sut.convert(100), is(100));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("101"), is(101));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"102"}), is(102));
    }

    @Test
    public void fromBooleanTrue() throws Exception {
        assertThat(sut.convert(true), is(1));
    }

    @Test
    public void fromBooleanFalse() throws Exception {
        assertThat(sut.convert(false), is(0));
    }

    @Test
    public void fromNotNumberString() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert("1.1");
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }

    @Test
    public void fromMultipleStringArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"1", "2"});
    }
}