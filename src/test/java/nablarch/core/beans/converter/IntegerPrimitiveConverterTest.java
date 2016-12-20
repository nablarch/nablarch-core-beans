package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * {@link IntegerConverter.Primitive}のテスト。
 */
public class IntegerPrimitiveConverterTest {

    private final IntegerConverter.Primitive sut = new IntegerConverter.Primitive();

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
        assertThat(sut.convert(""), is(0));
    }
}
