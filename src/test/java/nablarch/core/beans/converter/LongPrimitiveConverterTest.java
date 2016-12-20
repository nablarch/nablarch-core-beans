package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * {@link LongConverter.Primitive}のテスト。
 */
public class LongPrimitiveConverterTest {

    private final LongConverter.Primitive sut = new LongConverter.Primitive();

    @Test
    public void fromLong() throws Exception {
        assertThat(sut.convert(100L), is(100L));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("101"), is(101L));
    }


    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(0L));
    }
}
