package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link SqlDateConverter}のテスト。
 */
public class SqlDateConverterTest {

    private final SqlDateConverter sut = new SqlDateConverter();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromSqlDate() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 1);

        assertThat(sut.convert(new java.sql.Date(calendar.getTimeInMillis())), is(toDate("20160101")));
    }

    @Test
    public void fromUtilDate() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 2);

        assertThat(sut.convert(calendar.getTime()), is(toDate("20160102")));
    }

    @Test
    public void fromCalendar() throws Exception {
        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, 0, 3);

        assertThat(sut.convert(calendar), is(toDate("20160103")));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("20160104"), is(toDate("20160104")));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"20160105"}), is(toDate("20160105")));
    }

    @Test
    public void fromMultipleSingleArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"20160106", "20160107"});
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }

    private java.sql.Date toDate(String yyyymmdd) throws Exception {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return new java.sql.Date(format.parse(yyyymmdd).getTime());
    }
}