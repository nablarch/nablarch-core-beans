package nablarch.core.beans;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

import org.junit.Test;

/**
 * {@link BasicConversionManagerFactory}のテスト。
 * 
 * @author Taichi Uragami
 *
 */
public class BasicConversionManagerTest {

    @Test
    public void 日付_デフォルト() {
        BasicConversionManager sut = new BasicConversionManager();
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(utilDate("2018-02-21 00:00:00"),
                converters.get(java.util.Date.class).convert("20180221"));
        assertEquals(sqlDate("2018-02-21"),
                converters.get(java.sql.Date.class).convert("20180221"));
        assertEquals(timestamp("2018-02-21 00:00:00"),
                converters.get(Timestamp.class).convert("20180221"));

        assertEquals("Wed Feb 21 00:00:00 JST 2018",
                converters.get(String.class).convert(utilDate("2018-02-21 00:00:00")));
        assertEquals("2018-02-21",
                converters.get(String.class).convert(sqlDate("2018-02-21")));
        assertEquals("2018-02-21 00:00:00.0",
                converters.get(String.class).convert(timestamp("2018-02-21 00:00:00")));
    }

    @Test
    public void 日付_パターン指定() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setDatePatterns(Collections.singletonList("yyyy/MM/dd"));
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(utilDate("2018-02-21 00:00:00"),
                converters.get(java.util.Date.class).convert("2018/02/21"));
        assertEquals(sqlDate("2018-02-21"),
                converters.get(java.sql.Date.class).convert("2018/02/21"));
        assertEquals(timestamp("2018-02-21 00:00:00"),
                converters.get(Timestamp.class).convert("2018/02/21"));

        assertEquals("2018/02/21",
                converters.get(String.class).convert(utilDate("2018-02-21 00:00:00")));
        assertEquals("2018/02/21",
                converters.get(String.class).convert(sqlDate("2018-02-21")));
        assertEquals("2018/02/21",
                converters.get(String.class).convert(timestamp("2018-02-21 00:00:00")));
    }

    @Test
    public void 日付_パターン指定を空振り() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setDatePatterns(Collections.<String> emptyList());
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(utilDate("2018-02-21 00:00:00"),
                converters.get(java.util.Date.class).convert("20180221"));

        assertEquals("Wed Feb 21 00:00:00 JST 2018",
                converters.get(String.class).convert(utilDate("2018-02-21 00:00:00")));
    }

    @Test
    public void 数値_デフォルト() {
        BasicConversionManager sut = new BasicConversionManager();
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(Short.valueOf("12345"), converters.get(short.class).convert("12345"));
        assertEquals(Short.valueOf("12345"), converters.get(Short.class).convert("12345"));
        assertEquals(12345, converters.get(int.class).convert("12345"));
        assertEquals(12345, converters.get(Integer.class).convert("12345"));
        assertEquals(12345L, converters.get(long.class).convert("12345"));
        assertEquals(12345L, converters.get(Long.class).convert("12345"));
        assertEquals(new BigDecimal("12345"), converters.get(BigDecimal.class).convert("12345"));

        assertEquals("12345", converters.get(String.class).convert(Short.valueOf("12345")));
        assertEquals("12345", converters.get(String.class).convert(12345));
        assertEquals("12345", converters.get(String.class).convert(12345L));
        assertEquals("12345", converters.get(String.class).convert(new BigDecimal("12345")));
    }

    @Test
    public void 数値_パターン指定() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setNumberPatterns(Collections.singletonList("#,###"));
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(Short.valueOf("12345"), converters.get(short.class).convert("12,345"));
        assertEquals(Short.valueOf("12345"), converters.get(Short.class).convert("12,345"));
        assertEquals(12345, converters.get(int.class).convert("12,345"));
        assertEquals(12345, converters.get(Integer.class).convert("12,345"));
        assertEquals(12345L, converters.get(long.class).convert("12,345"));
        assertEquals(12345L, converters.get(Long.class).convert("12,345"));
        assertEquals(new BigDecimal("12345"), converters.get(BigDecimal.class).convert("12345"));

        assertEquals("12,345", converters.get(String.class).convert(Short.valueOf("12345")));
        assertEquals("12,345", converters.get(String.class).convert(12345));
        assertEquals("12,345", converters.get(String.class).convert(12345L));
        assertEquals("12,345", converters.get(String.class).convert(new BigDecimal("12345")));
    }

    @Test
    public void 数値_パターン指定を空振り() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setNumberPatterns(Collections.<String> emptyList());
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals(12345, converters.get(Integer.class).convert("12345"));

        assertEquals("12345", converters.get(String.class).convert(12345));
    }

    @Test
    public void 日付のパターン指定をしてから数値のパターン指定をする() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setDatePatterns(Collections.singletonList("yyyy/MM/dd"));
        sut.setNumberPatterns(Collections.singletonList("#,###"));
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals("2018/02/21",
                converters.get(String.class).convert(utilDate("2018-02-21 00:00:00")));
        assertEquals("12,345", converters.get(String.class).convert(12345));
    }

    @Test
    public void 数値のパターン指定をしてから日付のパターン指定をする() {
        BasicConversionManager sut = new BasicConversionManager();
        sut.setNumberPatterns(Collections.singletonList("#,###"));
        sut.setDatePatterns(Collections.singletonList("yyyy/MM/dd"));
        Map<Class<?>, Converter<?>> converters = sut.getConverters();

        assertEquals("2018/02/21",
                converters.get(String.class).convert(utilDate("2018-02-21 00:00:00")));
        assertEquals("12,345", converters.get(String.class).convert(12345));
    }

    private static Timestamp timestamp(String s) {
        return Timestamp.valueOf(s);
    }

    private static java.sql.Date sqlDate(String s) {
        return java.sql.Date.valueOf(s);
    }

    private static java.util.Date utilDate(String s) {
        return new java.util.Date(timestamp(s).getTime());
    }
}
