package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.core.beans.ConversionException;

public class LongConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void 文字列から変換() {
        LongConverter sut = new LongConverter();
        assertThat(sut.convert("1234567890"), is(1234567890L));
    }

    @Test
    public void 文字列から変換失敗() {
        expectedException.expect(ConversionException.class);
        LongConverter sut = new LongConverter();
        assertThat(sut.convert("1,234,567,890"), is(1234567890L));
    }

    @Test
    public void パターンを指定して文字列から変換() {
        LongConverter sut = new LongConverter(Arrays.asList("#,###", "#,####.#"));
        assertThat(sut.convert("1,234,567,890"), is(1234567890L));
        assertThat(sut.convert("12,3456,7890.123"), is(1234567890L));
    }

    @Test
    public void パターンを指定して文字列から変換失敗() {
        expectedException.expect(IllegalArgumentException.class);
        LongConverter sut = new LongConverter(Arrays.asList("#,###", "#,####.#"));
        sut.convert("Not number");
    }
}
