package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import org.junit.Test;

/**
 * {@link BooleanConverter.Primitive}のテスト。
 */
public class BooleanPrimitiveConverter {

    private final BooleanConverter.Primitive sut = new BooleanConverter.Primitive();

    @Test
    public void fromBoolean() throws Exception {
        assertThat(sut.convert(true), is(true));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("True"), is(true));
        assertThat(sut.convert("Frue"), is(false));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(false));
    }
}
