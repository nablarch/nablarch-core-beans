package nablarch.core.beans.converter;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.DateUtil;
import static org.hamcrest.MatcherAssert.assertThat;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;

/**
 * {@link LocalDateConverter}のテスト
 */
@RunWith(Enclosed.class)
public class LocalDateConverterTest {
    static java.sql.Date newSqlDate(String date, String pattern) {
        return new java.sql.Date(DateUtil.getParsedDate(date, pattern).getTime());
    }

    @RunWith(Theories.class)
    public static class LocalDateConvertSuccessTest {
        @DataPoints
        public static Object[][] params = {
                {"20170601", LocalDate.of(2017, 6, 1)},
                {LocalDate.of(2017, 6, 12), LocalDate.of(2017, 6, 12)},
                {LocalDateTime.of(2017, 6, 13, 12, 30, 15), LocalDate.of(2017, 6, 13)},
                // デフォルトタイムゾーンのオフセットに読み替えられる（デフォルトはAsia/Tokyo）
                {OffsetDateTime.of(2017, 6, 13, 20, 30, 15, 0, ZoneOffset.ofHours(9)), LocalDate.of(2017, 6, 13)},
                {OffsetDateTime.of(2017, 6, 13, 20, 30, 15, 0, ZoneOffset.UTC), LocalDate.of(2017, 6, 14)},
                {OffsetDateTime.of(2017, 6, 13, 20, 30, 15, 0, ZoneOffset.ofHours(3)), LocalDate.of(2017, 6, 14)},
                {DateUtil.getParsedDate("20170621000000000", "yyyyMMddHHmmssSSS"), LocalDate.of(2017, 6, 21)},
                {DateUtil.getParsedDate("20170622235011300", "yyyyMMddHHmmssSSS"), LocalDate.of(2017, 6, 22)},
                {newSqlDate("19490402000000", "yyyyMMddHHmmss"), LocalDate.of(1949, 4, 2)}
        };

        @Theory
        public void test(Object[] testParams) {
            Object value = testParams[0];
            LocalDate expected = (LocalDate) testParams[1];

            Converter converter = new LocalDateConverter();
            assertThat(converter.convert(value), is(expected));
        }
    }

    @RunWith(Theories.class)
    public static class DateConvertParseFailTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @DataPoints
        public static Object[] params = {"abc", "201706251045123"};

        @Theory
        public void test(Object value) {
            expectedException.expect(DateTimeParseException.class);
            Converter converter = new LocalDateConverter();
            converter.convert(value);
        }
    }

    @RunWith(Theories.class)
    public static class DateConvertUnsupportedTypeTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @DataPoints
        public static Object[] params = {10};

        @Theory
        public void test(Object value) {
            expectedException.expect(ConversionException.class);
            Converter converter = new LocalDateConverter();
            converter.convert(value);
        }
    }


    /**
     * 日付パターンのテスト。
     *
     */
    public static class PatternTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void デフォルト() {
            final LocalDateConverter sut = new LocalDateConverter();
            assertEquals(LocalDate.of(2018, 2, 21), sut.convert("20180221"));
        }

        @Test
        public void パターン指定() {
            final LocalDateConverter sut = new LocalDateConverter(
                    Arrays.asList("yyyy/MM/dd", "yyyy.MM.dd"));
            assertEquals(LocalDate.of(2018, 2, 21), sut.convert("2018/02/21"));
            assertEquals(LocalDate.of(2018, 2, 21), sut.convert("2018.02.21"));
        }

        @Test
        public void 変換失敗() {
            expectedException.expect(IllegalArgumentException.class);
            final LocalDateConverter sut = new LocalDateConverter(
                    Arrays.asList("yyyy/MM/dd", "yyyy.MM.dd"));
            sut.convert("20180221");
        }
    }
}
