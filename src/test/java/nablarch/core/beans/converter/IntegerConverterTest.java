package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Arrays;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.core.beans.ConversionException;

public class IntegerConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void 文字列から変換() {
        IntegerConverter sut = new IntegerConverter();
        assertThat(sut.convert("1234567890"), is(1234567890));
    }

    @Test
    public void 文字列から変換失敗() {
        expectedException.expect(ConversionException.class);
        IntegerConverter sut = new IntegerConverter();
        assertThat(sut.convert("1,234,567,890"), is(1234567890));
    }

    @Test
    public void パターンを指定して文字列から変換() {
        IntegerConverter sut = new IntegerConverter(Arrays.asList("#,###", "#,####.#"));
        assertThat(sut.convert("1,234,567,890"), is(1234567890));
        assertThat(sut.convert("12,3456,7890.123"), is(1234567890));
    }

    @Test
    public void パターンを指定して文字列から変換失敗() {
        expectedException.expect(IllegalArgumentException.class);
        IntegerConverter sut = new IntegerConverter(Arrays.asList("#,###", "#,####.#"));
        sut.convert("Not number");
    }
}
