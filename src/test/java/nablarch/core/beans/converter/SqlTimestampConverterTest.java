package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.util.Calendar;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link SqlTimestampConverter}のテスト。
 */
public class SqlTimestampConverterTest {

    private final SqlTimestampConverter sut = new SqlTimestampConverter();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromSqlTimestamp() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 1, 2, 3, 4);
        calendar.set(Calendar.MILLISECOND, 5);
        final Timestamp input = new Timestamp(calendar.getTimeInMillis());

        assertThat(sut.convert(input), is(input));
    }

    @Test
    public void fromUtilDate() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1, 2, 3, 4);
        calendar.set(Calendar.MILLISECOND, 5);

        assertThat(sut.convert(calendar.getTime()), is(new Timestamp(calendar.getTimeInMillis())));
    }

    @Test
    public void fromCalendar() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 1, 1, 2, 3, 4);
        calendar.set(Calendar.MILLISECOND, 5);

        assertThat(sut.convert(calendar), is(new Timestamp(calendar.getTimeInMillis())));
    }

    @Test
    public void fromString() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 2, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        assertThat(sut.convert("20160102"), is(new Timestamp(calendar.getTimeInMillis())));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 2, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        assertThat(sut.convert(new String[] {"20160102"}), is(new Timestamp(calendar.getTimeInMillis())));
    }

    @Test
    public void fromMultipleStringArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"20160101", "20160102"});
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }
}