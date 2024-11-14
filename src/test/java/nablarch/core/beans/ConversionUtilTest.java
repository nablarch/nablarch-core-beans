package nablarch.core.beans;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import nablarch.core.repository.SystemRepository;
import nablarch.core.repository.di.DiContainer;
import nablarch.core.repository.di.config.xml.XmlComponentDefinitionLoader;

import org.junit.Test;

/**
 * @author kawasima
 * @author tajima
 */
public class ConversionUtilTest {
    @Test
    public void testConvertBigDecimal() {
        // convert from Numbers
        assertEquals(
            BigDecimal.valueOf(777)
          , ConversionUtil.convert(BigDecimal.class, BigDecimal.valueOf(777))
        );
        assertEquals(
            BigDecimal.valueOf(1234e-24f)
          , ConversionUtil.convert(BigDecimal.class, 1234e-24f)
        );

        assertEquals(
            BigDecimal.valueOf(1234e-52d)
          , ConversionUtil.convert(BigDecimal.class, 1234e-52d)
        );
        assertEquals(
            new BigDecimal("0.54321")
          , ConversionUtil.convert(BigDecimal.class, 0.54321)
        );
        assertEquals(
            new BigDecimal("-0.1")
          , ConversionUtil.convert(BigDecimal.class, -0.1)
        );
        assertEquals(
            BigDecimal.valueOf(777)
          , ConversionUtil.convert(BigDecimal.class, BigInteger.valueOf(777))
        );
        assertEquals(
                BigDecimal.valueOf(8)
              , ConversionUtil.convert(BigDecimal.class, (byte) 8)
            );

        // convert from String
        assertEquals(
            BigDecimal.valueOf(777)
          , ConversionUtil.convert(BigDecimal.class, "777")
        );
        // convert from String[]
        assertEquals(
            BigDecimal.valueOf(777)
          , ConversionUtil.convert(BigDecimal.class, new String[] {"777"})
        );
        
        // convert from String[] {null}
        assertNull(ConversionUtil.convert(BigDecimal.class, new String[] {null}));
        
        assertEquals(
            new BigDecimal("0.54321")
          , ConversionUtil.convert(BigDecimal.class, "0.54321")
        );
        assertEquals(
            new BigDecimal("-0.1")
          , ConversionUtil.convert(BigDecimal.class, "-0.1")
        );
        
        try {
            ConversionUtil.convert(BigDecimal.class, "hogehoge");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        try {
            ConversionUtil.convert(BigDecimal.class, new String[] {"hogehoge", "fugafuga"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // convert from boolean
        assertEquals(
            BigDecimal.valueOf(1)
          , ConversionUtil.convert(BigDecimal.class, true)
        );
        assertEquals(
            BigDecimal.valueOf(0)
          , ConversionUtil.convert(BigDecimal.class, false)
        );

        // not supported
        try {
            ConversionUtil.convert(BigDecimal.class, new Date());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // null
        assertNull(ConversionUtil.convert(BigDecimal.class, null));
    }

    @Test
    public void testConvertBoolean() {
        // primitive
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, true));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, false));

        // primitive wrapper
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, Boolean.TRUE));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, Boolean.FALSE));

        // Number
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, 1));
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, -1));
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, BigDecimal.valueOf(100.01)));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, -0.1));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, BigDecimal.ZERO));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, 0L));

        //String
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, "True"));
        assertEquals(Boolean.TRUE, ConversionUtil.convert(Boolean.class, "oN"));
        assertEquals(Boolean.TRUE, ConversionUtil.convert(boolean.class, "1"));

        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, "false"));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, "off"));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, "0"));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, ""));

        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, "one"));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, "true "));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(boolean.class, "1.0"));

        // null
        assertNull(ConversionUtil.convert(Boolean.class, null));

        //String[]
        assertEquals(Boolean.TRUE, ConversionUtil.convert(boolean.class, new String[] {"1"}));
        assertEquals(Boolean.FALSE, ConversionUtil.convert(Boolean.class, new String[] {"0"}));
        assertNull(ConversionUtil.convert(Boolean.class, new String[] {null}));

        // unsupported types
        try {
            ConversionUtil.convert(Boolean.class, new Date());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        try {
            ConversionUtil.convert(Boolean.class, new String[] {"0", "1"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

    }

    @Test
    public void testConvertInteger() {

        // Number
        assertEquals(1, (int)ConversionUtil.convert(Integer.class, BigDecimal.ONE));
        assertEquals(1, (int)ConversionUtil.convert(int.class, 1L));

        // String
        assertEquals(1, (int)ConversionUtil.convert(Integer.class, "1"));

        // String[]
        assertEquals(1, (int)ConversionUtil.convert(Integer.class, new String[] {"1"}));
        
        // String[] {null}
        assertNull(ConversionUtil.convert(Integer.class, new String[] {null}));
        
        try {
            ConversionUtil.convert(Integer.class, "hogehogehoge");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // Boolean
        assertEquals(1, (int)ConversionUtil.convert(Integer.class, true));
        assertEquals(0, (int)ConversionUtil.convert(Integer.class, Boolean.FALSE));

        // unsupported types
        try {
            ConversionUtil.convert(Integer.class, new Date());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        try {
            ConversionUtil.convert(Integer.class, new String[] {"10", "11"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

    }

    @Test
    public void testConvertDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 13, 0, 0, 0);
        cal.clear(Calendar.MILLISECOND);

        // String
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, "20240213"));

        // String[]
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, new String[] {"20240213"}));

        assertNull(ConversionUtil.convert(Date.class, new String[] {null}));

        // Date
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, cal.getTime()));


        // Calendar
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, cal));

        // null
        assertNull(ConversionUtil.convert(Date.class, null));

        // LocalDate
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, LocalDate.of(2024, 2, 13)));

        // LocalDateTime
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, LocalDateTime.of(2024, 2, 13, 0, 0, 0)));

        // OffsetDateTime
        assertEquals(cal.getTime(), ConversionUtil.convert(Date.class, OffsetDateTime.of(2024, 2, 13, 0, 0, 0, 0, ZoneOffset.ofHours(9))));

        try {
            ConversionUtil.convert(Date.class, new String[] {"20240213", "20240212"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported type
        try {
            ConversionUtil.convert(Date.class, System.currentTimeMillis());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertSqlDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 13, 0, 0, 0);
        cal.clear(Calendar.MILLISECOND);

        // String
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, "20240213"));

        // String[]
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, new String[]{"20240213"}));
        
        // String[] {null}
        assertNull(ConversionUtil.convert(java.sql.Date.class, new String[] {null}));

        // Date
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, cal.getTime()));


        // Calendar
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, cal));

        // null
        assertNull(ConversionUtil.convert(java.sql.Date.class, null));

        // LocalDate
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, LocalDate.of(2024, 2, 13)));

        // LocalDateTime
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, LocalDateTime.of(2024, 2, 13, 0, 0, 0)));

        // OffsetDateTime
        assertEquals(new java.sql.Date(cal.getTimeInMillis()), ConversionUtil.convert(java.sql.Date.class, OffsetDateTime.of(2024, 2, 13, 0, 0, 0, 0, ZoneOffset.UTC)));

        try {
            ConversionUtil.convert(java.sql.Date.class, new String[] {"20240213", "20240212"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported type
        try {
            ConversionUtil.convert(java.sql.Date.class, System.currentTimeMillis());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertTimestamp() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 13, 0, 0, 0);
        cal.clear(Calendar.MILLISECOND);

        // String
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, "20240213"));

        // String[]
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, new String[]{"20240213"}));
        
        // String[] {null}
        assertNull(ConversionUtil.convert(Timestamp.class, new String[] {null}));
        
        // Date
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, cal.getTime()));


        // Calendar
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, cal));

        // null
        assertNull(ConversionUtil.convert(Timestamp.class, null));

        // LocalDate
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, LocalDate.of(2024, 2, 13)));

        // LocalDateTime
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, LocalDateTime.of(2024, 2, 13, 0, 0, 0)));

        // OffsetDateTime
        assertEquals(new Timestamp(cal.getTimeInMillis()), ConversionUtil.convert(Timestamp.class, OffsetDateTime.of(2024, 2, 13, 0, 0, 0, 0, ZoneOffset.ofHours(9))));

        try {
            ConversionUtil.convert(Timestamp.class, new String[] {"20240213", "20240212"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported type
        try {
            ConversionUtil.convert(Timestamp.class, System.currentTimeMillis());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertLocalDate() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 13, 0, 0, 0);
        cal.clear(Calendar.MILLISECOND);
        LocalDate localDate = LocalDate.of(2024, 2, 13);

        // String
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, "20240213"));

        // String[]
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, new String[]{"20240213"}));

        // String[] {null}
        assertNull(ConversionUtil.convert(LocalDate.class, new String[] {null}));

        // Date
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, cal.getTime()));


        // Calendar
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, cal));

        // null
        assertNull(ConversionUtil.convert(LocalDate.class, null));

        // LocalDate
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, LocalDate.of(2024, 2, 13)));

        // LocalDateTime
        assertEquals(localDate, ConversionUtil.convert(LocalDate.class, LocalDateTime.of(2024, 2, 13, 0, 0, 0)));

        try {
            ConversionUtil.convert(LocalDate.class, new String[] {"20240213", "20240212"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported type
        try {
            ConversionUtil.convert(LocalDate.class, System.currentTimeMillis());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertLocalDateTime() {
        Calendar cal = Calendar.getInstance();
        cal.set(2024, Calendar.FEBRUARY, 13, 10, 22, 30);
        cal.clear(Calendar.MILLISECOND);
        LocalDateTime localDateTime =  LocalDateTime.of(2024, 2, 13, 10, 22, 30);

        // String
        assertEquals(localDateTime, ConversionUtil.convert(LocalDateTime.class, "2024-02-13T10:22:30.000Z"));

        // String[]
        assertEquals(localDateTime, ConversionUtil.convert(LocalDateTime.class, new String[]{"2024-02-13T10:22:30.000Z"}));

        // String[] {null}
        assertNull(ConversionUtil.convert(LocalDateTime.class, new String[] {null}));

        // Date
        assertEquals(localDateTime, ConversionUtil.convert(LocalDateTime.class, cal.getTime()));


        // Calendar
        assertEquals(localDateTime, ConversionUtil.convert(LocalDateTime.class, cal));

        // null
        assertNull(ConversionUtil.convert(LocalDateTime.class, null));

        // LocalDate
        assertEquals(LocalDateTime.of(2024, 2, 13, 0, 0), ConversionUtil.convert(LocalDateTime.class, LocalDate.of(2024, 2, 13)));

        // LocalDateTime
        assertEquals(localDateTime, ConversionUtil.convert(LocalDateTime.class, localDateTime));

        try {
            ConversionUtil.convert(LocalDateTime.class, new String[] {"20240213", "20240212"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported type
        try {
            ConversionUtil.convert(LocalDateTime.class, System.currentTimeMillis());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertLong() {

        // Number
        assertEquals(1L, (long)ConversionUtil.convert(Long.class, BigDecimal.ONE));
        assertEquals(1L, (long)ConversionUtil.convert(Long.class, 1L));

        // String
        assertEquals(1L, (long)ConversionUtil.convert(Long.class, "1"));

        // String[]
        assertEquals(1L, (long)ConversionUtil.convert(Long.class, new String[] {"1"}));
        
        // String[] {null}
        assertNull(ConversionUtil.convert(Long.class, new String[] {null}));
        
        try {
            ConversionUtil.convert(Long.class, "hogehogehoge");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // Boolean
        assertEquals(1L, (long)ConversionUtil.convert(Long.class, true));
        assertEquals(0L, (long)ConversionUtil.convert(long.class, Boolean.FALSE));

        try {
            ConversionUtil.convert(Long.class, new String[] {"1", "2"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        try {
            ConversionUtil.convert(long.class, new String[] {"1", "2"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported types
        try {
            ConversionUtil.convert(Long.class, new Date());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertShort() {

        // Number
        assertEquals((short)1, (short)ConversionUtil.convert(Short.class, BigDecimal.ONE));
        assertEquals((short)1, (short)ConversionUtil.convert(short.class, 1L));

        // String
        assertEquals((short)1, (short)ConversionUtil.convert(Short.class, "1"));

        // String[]
        assertEquals((short)1, (short)ConversionUtil.convert(Short.class, new String[] {"1"}));

        // String[] {null}
        assertNull(ConversionUtil.convert(Short.class, new String[] {null}));

        try {
            ConversionUtil.convert(Short.class, "hogehogehoge");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // Boolean
        assertEquals((short)1, (short)ConversionUtil.convert(Short.class, true));
        assertEquals((short)0, (short)ConversionUtil.convert(short.class, Boolean.FALSE));

        try {
            ConversionUtil.convert(Short.class, new String[] {"1", "2"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        try {
            ConversionUtil.convert(short.class, new String[] {"1", "2"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }

        // unsupported types
        try {
            ConversionUtil.convert(Short.class, new Date());
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConverterString() {
        // null
        assertNull(ConversionUtil.convert(String.class, null));

        // String
        assertEquals("HOGE", ConversionUtil.convert(String.class, "HOGE"));

        // String[]
        assertEquals("HOGE", ConversionUtil.convert(String.class, new String[] {"HOGE"}));
        
        // String[] {null}
        assertNull(ConversionUtil.convert(String.class, new String[]{null}));

        // Number
        assertEquals("12.34", ConversionUtil.convert(String.class, 12.34));

        // Boolean
        assertEquals("1", ConversionUtil.convert(String.class, true));
        assertEquals("0", ConversionUtil.convert(String.class, Boolean.FALSE));

        // LocalDate
        assertEquals("2024-05-01", ConversionUtil.convert(String.class, LocalDate.of(2024, 5, 1)));

        // LocalDateTime
        assertEquals("2024-05-01T15:10", ConversionUtil.convert(String.class, LocalDateTime.of(2024, 5, 1, 15, 10)));

        try {
            ConversionUtil.convert(String.class, new String[] {"1", "2"});
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertToStringArray() {
        // null
        assertNull(ConversionUtil.convert(String[].class, null));

        // empty
        assertArrayEquals(new String[0], ConversionUtil.convert(String[].class, new int[0]));
        assertArrayEquals(new String[0], ConversionUtil.convert(String[].class, new BigDecimal[0]));

        // array of String
        assertArrayEquals(
            new String[]{"hoge", "fuga", null, "piyo"}
          , ConversionUtil.convert(String[].class, new String[]{"hoge", "fuga", null, "piyo"})
        );

        // array of Objects
        assertArrayEquals(
            new String[]{"1", "2", "3"}
          , ConversionUtil.convert(String[].class, new Object[]{"1", 2, BigDecimal.valueOf(3)})
        );

        // array of primitives
        assertArrayEquals(
            new String[]{"1", "2", "3"}
          , ConversionUtil.convert(String[].class, new int[]{1, 2, 3})
        );

        // list
        assertArrayEquals(
            new String[]{"1", "2", "3"}
          , ConversionUtil.convert(
                String[].class
              , new ArrayList<>() {{
                            add("1");
                            add(2);
                            add(BigDecimal.valueOf(3));
                        }}
            )
        );

        // unsupported type
        try {
            ConversionUtil.convert(String[].class, "string");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    @Test
    public void testConvertToObjectArray() {
        // null
        assertNull(ConversionUtil.convert(Object[].class, null));

        // empty
        assertArrayEquals(new Object[0], ConversionUtil.convert(Object[].class, new int[0]));
        assertArrayEquals(new Object[0], ConversionUtil.convert(Object[].class, new BigDecimal[0]));

        // array of Object
        assertArrayEquals(
            new Object[]{"1", 2, BigDecimal.valueOf(3), null}
          , ConversionUtil.convert(Object[].class, new Object[]{"1", 2, BigDecimal.valueOf(3), null})
        );

        // array of primitives
        assertArrayEquals(
            new Object[]{1, 2, 3}
          , ConversionUtil.convert(Object[].class, new int[]{1, 2, 3})
        );

        // list
        assertArrayEquals(
            new Object[]{"1", 2, BigDecimal.valueOf(3), null}
          , ConversionUtil.convert(
                Object[].class
              , new ArrayList<>() {{
                            add("1");
                            add(2);
                            add(BigDecimal.valueOf(3));
                            add(null);
                        }}
            )
        );

        // unsupported type
        try {
            ConversionUtil.convert(Object[].class, "string");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof ConversionException);
        }
    }

    /**
     * カスタムコンバータを設定で追加した場合にも正しく動作すること。
     */
    @Test
    public void testCustomConverter() {

        XmlComponentDefinitionLoader loader = new XmlComponentDefinitionLoader("nablarch/core/beans/sample/converter-test.xml");

        DiContainer container = new DiContainer(loader);
        SystemRepository.load(container);

        // フレームワーク提供のコンバータ。正しく変換されること。
        assertEquals(
                BigDecimal.valueOf(777),
                ConversionUtil.convert(BigDecimal.class, "777"));

        // 追加したコンバータ。設定をロード後なので、変換された値が返ること。
        assertEquals(
                BigInteger.valueOf(333),
                ConversionUtil.convert(BigInteger.class, "333"));
    }
}
