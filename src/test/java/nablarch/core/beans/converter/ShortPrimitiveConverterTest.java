package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * {@link ShortConverter.Primitive}のテスト。
 */
public class ShortPrimitiveConverterTest {

    private final ShortConverter.Primitive sut = new ShortConverter.Primitive();

    @Test
    public void fromShort() throws Exception {
        assertThat(sut.convert(Short.valueOf("123")), is((short) 123));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("321"), is((short) 321));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is((short) 0));
    }
}
