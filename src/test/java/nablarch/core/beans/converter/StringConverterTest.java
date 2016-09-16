package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

import java.math.BigDecimal;

import nablarch.core.beans.ConversionException;

import org.junit.Test;

/**
 * {@link StringConverter}のテスト
 */
public class StringConverterTest {

    private final StringConverter sut = new StringConverter();

    @Test
    public void string() throws Exception {
        assertThat(sut.convert("111"), is("111"));
    }

    @Test
    public void integer() throws Exception {
        assertThat(sut.convert(Integer.valueOf(100)), is("100"));
    }

    @Test
    public void boolean_true() throws Exception {
        assertThat(sut.convert(Boolean.TRUE), is("1"));
    }
    
    @Test
    public void boolean_false() throws Exception {
        assertThat(sut.convert(Boolean.FALSE), is("0"));
    }

    @Test
    public void stringArray_singleValue() throws Exception {
        assertThat(sut.convert(new String[] {"1"}), is("1"));
    }
    
    @Test(expected = ConversionException.class)
    public void stringArray_multipleValue() throws Exception {
        sut.convert(new String[] {"1", "2"});
    }

    @Test
    public void bigDecimal() throws Exception {
        assertThat(sut.convert(new BigDecimal("0.0000000001")), is("0.0000000001"));
    }
}
