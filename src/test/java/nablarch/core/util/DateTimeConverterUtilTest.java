package nablarch.core.util;

import nablarch.core.repository.SystemRepository;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link DateTimeConverterUtil}のテスト。
 */
public class DateTimeConverterUtilTest {

    @Before
    public void setUp() {
        SystemRepository.clear();
    }

    @After
    public void tearDown() {
        SystemRepository.clear();
    }

    @Test
    public void getLocalDateFromString() {
        assertThat(DateTimeConverterUtil.getLocalDate("19490403"), is(LocalDate.of(1949, Month.APRIL, 3)));
        
        SystemRepository.load(() -> Collections.singletonMap("dateTimeConfiguration", new BasicDateTimeConverterConfiguration() {
            @Override
            public DateTimeFormatter getDateFormatter() {
                return DateTimeFormatter.ofPattern("yyyy年MM月dd日");
            }
        }));

        assertThat(DateTimeConverterUtil.getLocalDate("2017年07月26日"), is(LocalDate.of(2017, Month.JULY, 26)));
    }

    @Test
    public void getLocalDateFromUtilDate() {
        final Date date = DateUtil.getDate("20170401");
        assertThat(DateTimeConverterUtil.getLocalDate(date), is(LocalDate.of(2017, Month.APRIL, 1)));
    }

    @Test
    public void getLocalDateAsSqlDate() {
        final Date date = DateUtil.getDate("20161231");
        assertThat(DateTimeConverterUtil.getLocalDateAsSqlDate(new java.sql.Date(date.getTime())),
                is(LocalDate.of(2016, Month.DECEMBER, 31)));
    }

    @Test
    public void getLocalDateFromCalendar() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 15);

        assertThat(DateTimeConverterUtil.getLocalDate(calendar), is(LocalDate.of(2017, 6, 15)));
    }

    @Test
    public void getLocalDateTimeFromString() {
        assertThat(DateTimeConverterUtil.getLocalDateTime("2017-01-02T03:04:05Z"),
                is(LocalDateTime.of(2017, Month.JANUARY, 2, 3, 4, 5)));

        
        SystemRepository.load(() -> Collections.singletonMap("dateTimeConfiguration", new BasicDateTimeConverterConfiguration() {
            @Override
            public DateTimeFormatter getDateTimeFormatter() {
                return DateTimeFormatter.ofPattern("uuuu/MM/dd HH:mm:ss");
            }
        }));
        assertThat(DateTimeConverterUtil.getLocalDateTime("2014/01/02 11:22:33"),
                is(LocalDateTime.of(2014, 1, 2, 11, 22, 33)));
    }

    @Test
    public void getLocalDateTimeFromUtilDate() {
        final Timestamp timestamp = Timestamp.valueOf("2017-01-02 03:04:05");
        assertThat(DateTimeConverterUtil.getLocalDateTime(new Date(timestamp.getTime())),
                is(LocalDateTime.of(2017, Month.JANUARY, 2, 3, 4, 5)));
    }

    @Test
    public void getLocalDateTimeAsSqlDate() {
        final Timestamp timestamp = Timestamp.valueOf("2017-12-02 03:04:05");
        assertThat(DateTimeConverterUtil.getLocalDateTimeAsSqlDate(new java.sql.Date(timestamp.getTime())),
                is(LocalDateTime.of(2017, 12, 2, 0, 0)));
    }

    @Test
    public void getLocalDAteTimeFromCalendar() {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2017);
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 15);
        calendar.set(Calendar.HOUR_OF_DAY, 22);
        calendar.set(Calendar.MINUTE, 30);
        calendar.set(Calendar.SECOND, 35);
        calendar.set(Calendar.MILLISECOND, 100);

        assertThat(DateTimeConverterUtil.getLocalDateTime(calendar),
                is(LocalDateTime.of(2017, 6, 15, 22, 30, 35, 100000000)));
    }

    @Test
    public void getDateFromLocalDate() {
        assertThat(DateTimeConverterUtil.getDate(LocalDate.of(2017, 11, 12)),
                is(DateUtil.getDate("20171112")));
    }

    @Test
    public void getDateFromLocalDateTime() {
        assertThat(DateTimeConverterUtil.getDate(LocalDateTime.of(2017, 1, 2, 3, 4, 5, 123456789)),
                is(DateUtil.getParsedDate("2017/01/02 03:04:05.123", "yyyy/MM/dd hh:mm:ss.SSS")));
    }
    
    @Test
    public void getTimestampFromLocalDateTime() {
        assertThat(DateTimeConverterUtil.getTimestamp(LocalDateTime.of(2017, 1, 2, 3, 4, 5, 123456789)),
                is(Timestamp.valueOf("2017-01-02 03:04:05.123456789")));
    }
}
