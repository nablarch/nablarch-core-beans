package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;

import org.junit.Test;

import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.BooleanConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.ObjectArrayConverter;
import nablarch.core.beans.converter.ShortConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.beans.converter.StringArrayConverter;
import nablarch.core.beans.converter.StringConverter;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

/**
 * {@link BasicConverterFactory}のテストクラス。
 * 
 * @author Naoki Yamamoto
 */
public class BasicConverterFactoryTest {
    
    /** テスト対象 */
    ConverterFactory sut = new BasicConverterFactory();
    
    @Test
    public void create() {
        
        Map<Class<?>, Converter<?>> map = sut.create();
        
        assertThat("想定しているコンバーターがすべて格納されていること", map.size(), is(15));
        assertThat("booleanのコンバータ－が格納されていること", map.get(boolean.class), is(instanceOf(BooleanConverter.class)));
        assertThat("Booleanのコンバータ－が格納されていること", map.get(Boolean.class), is(instanceOf(BooleanConverter.class)));
        assertThat("intのコンバータ－が格納されていること", map.get(int.class), is(instanceOf(IntegerConverter.class)));
        assertThat("Integerのコンバータ－が格納されていること", map.get(Integer.class), is(instanceOf(IntegerConverter.class)));
        assertThat("shortのコンバータ－が格納されていること", map.get(short.class), is(instanceOf(ShortConverter.class)));
        assertThat("Shortのコンバータ－が格納されていること", map.get(Short.class), is(instanceOf(ShortConverter.class)));
        assertThat("longのコンバータ－が格納されていること", map.get(long.class), is(instanceOf(LongConverter.class)));
        assertThat("Longのコンバータ－が格納されていること", map.get(Long.class), is(instanceOf(LongConverter.class)));
        assertThat("BigDecimalのコンバータ－が格納されていること", map.get(BigDecimal.class), is(instanceOf(BigDecimalConverter.class)));
        assertThat("Stringのコンバータ－が格納されていること", map.get(String.class), is(instanceOf(StringConverter.class)));
        assertThat("String[]のコンバータ－が格納されていること", map.get(String[].class), is(instanceOf(StringArrayConverter.class)));
        assertThat("Object[]のコンバータ－が格納されていること", map.get(Object[].class), is(instanceOf(ObjectArrayConverter.class)));
        assertThat("Dateのコンバータ－が格納されていること", map.get(Date.class), is(instanceOf(DateConverter.class)));
        assertThat("java.sql.Dateのコンバータ－が格納されていること", map.get(java.sql.Date.class), is(instanceOf(SqlDateConverter.class)));
        assertThat("Timestampのコンバータ－が格納されていること", map.get(Timestamp.class), is(instanceOf(SqlTimestampConverter.class)));
        
    }
}
