package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.core.beans.ConversionException;

public class ShortConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void 文字列から変換() {
        ShortConverter sut = new ShortConverter();
        assertThat(sut.convert("12345"), is((short) 12345));
    }

    @Test
    public void 文字列から変換失敗() {
        expectedException.expect(ConversionException.class);
        ShortConverter sut = new ShortConverter();
        assertThat(sut.convert("12,345"), is((short) 12345));
    }

    @Test
    public void パターンを指定して文字列から変換() {
        ShortConverter sut = new ShortConverter(Arrays.asList("#,###", "#,####.#"));
        assertThat(sut.convert("12,345"), is((short) 12345));
        assertThat(sut.convert("1,2345.678"), is((short) 12345));
    }

    @Test
    public void パターンを指定して文字列から変換失敗() {
        expectedException.expect(IllegalArgumentException.class);
        ShortConverter sut = new ShortConverter(Arrays.asList("#,###", "#,####.#"));
        sut.convert("Not number");
    }
}
