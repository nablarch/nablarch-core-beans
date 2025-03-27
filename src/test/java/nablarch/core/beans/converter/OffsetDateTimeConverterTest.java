package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.DateUtil;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.experimental.theories.DataPoints;
import org.junit.experimental.theories.Theories;
import org.junit.experimental.theories.Theory;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

/**
 * {@link OffsetDateTime}のテスト
 */
@RunWith(Enclosed.class)
public class OffsetDateTimeConverterTest {
    static java.sql.Date newSqlDate(String date, String pattern) {
        return new java.sql.Date(DateUtil.getParsedDate(date, pattern).getTime());
    }

    static Calendar newCalendar(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        return calendar;
    }

    @RunWith(Theories.class)
    public static class OffsetDateTimeConvertSuccessTest {
        @DataPoints
        public static Object[][] params = {
                {"2017-06-01T10:22:30.100Z", OffsetDateTime.of(2017, 6, 1, 10, 22, 30, 100000000, ZoneOffset.UTC)},
                {new String[]{"2017-06-01T10:22:30.100Z"}, OffsetDateTime.of(2017, 6, 1, 10, 22, 30, 100000000, ZoneOffset.UTC)},
                {LocalDate.of(2017, 6, 12), OffsetDateTime.of(2017, 6, 12, 0, 0, 0, 0, ZoneOffset.ofHours(9))},
                {LocalDateTime.of(2017, 6, 13, 12, 30, 15), OffsetDateTime.of(2017, 6, 13, 12, 30, 15, 0, ZoneOffset.ofHours(9))},
                {OffsetDateTime.of(2017, 6, 13, 12, 30, 15, 0, ZoneOffset.ofHours(9)), OffsetDateTime.of(2017, 6, 13, 12, 30, 15, 0, ZoneOffset.ofHours(9))},
                {DateUtil.getParsedDate("20170621030530500", "yyyyMMddHHmmssSSS"), OffsetDateTime.of(2017, 6, 21, 3, 5, 30, 500000000, ZoneOffset.ofHours(9))},
                {newCalendar(DateUtil.getParsedDate("20170622235011300", "yyyyMMddHHmmssSSS")), OffsetDateTime.of(2017, 6, 22, 23, 50, 11, 300000000, ZoneOffset.ofHours(9))},
                {newSqlDate("20170623123015", "yyyyMMddHHmmss"), OffsetDateTime.of(2017, 6, 23, 0, 0, 0, 0, ZoneOffset.ofHours(9))}
        };

        @Theory
        public void test(Object[] testParams) {
            Object value = testParams[0];
            OffsetDateTime expected = (OffsetDateTime) testParams[1];

            Converter<OffsetDateTime> converter = new OffsetDateTimeConverter();
            assertThat(converter.convert(value), is(expected));
        }
    }

    @RunWith(Theories.class)
    public static class DateConvertParseFailTest {
        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @DataPoints
        public static Object[] params = {"abc", "20170625"};

        @Theory
        public void test(Object value) {
            expectedException.expect(DateTimeParseException.class);
            Converter<OffsetDateTime> converter = new OffsetDateTimeConverter();
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
            Converter<OffsetDateTime> converter = new OffsetDateTimeConverter();
            converter.convert(value);
        }
    }

    /**
     * 日付パターンのテスト。
     */
    public static class PatternTest {

        @Rule
        public ExpectedException expectedException = ExpectedException.none();

        @Test
        public void デフォルト() {
            final OffsetDateTimeConverter sut = new OffsetDateTimeConverter();
            assertEquals(OffsetDateTime.of(2018, 2, 21, 12, 34, 0, 0, ZoneOffset.UTC), sut.convert("2018-02-21T12:34:00Z"));
        }

        @Test
        public void パターン指定() {
            final OffsetDateTimeConverter sut = new OffsetDateTimeConverter(
                    Arrays.asList("yyyy/MM/dd HH:mmZ", "yyyy.MM.dd HHmmZ"));
            assertEquals(OffsetDateTime.of(2018, 2, 21, 12, 34, 0, 0, ZoneOffset.ofHours(9)), sut.convert("2018/02/21 12:34+0900"));
            assertEquals(OffsetDateTime.of(2018, 2, 21, 12, 34, 0, 0, ZoneOffset.ofHours(9)), sut.convert("2018.02.21 1234+0900"));
        }

        @Test
        public void 変換失敗() {
            expectedException.expect(IllegalArgumentException.class);
            final OffsetDateTimeConverter sut = new OffsetDateTimeConverter(
                    Arrays.asList("yyyy/MM/dd HH:mm", "yyyy.MM.dd HHmm"));
            sut.convert("201802211234");
        }
    }
}
