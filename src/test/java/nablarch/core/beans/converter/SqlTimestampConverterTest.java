package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

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
    public void fromTimestamp() throws Exception {
        final Timestamp src = new Timestamp(System.currentTimeMillis());
        src.setNanos(100000001);
        assertThat(sut.convert(src), is(src));
    }

    @Test
    public void fromUtilDate() throws Exception {
        final Date src = new Date(System.currentTimeMillis());
        assertThat(sut.convert(src)
                      .getTime(), is(src.getTime()));
    }

    @Test
    public void fromSqlDate() throws Exception {
        final java.sql.Date src = new java.sql.Date(System.currentTimeMillis());
        assertThat(sut.convert(src)
                      .getTime(), is(src.getTime()));
    }

    @Test
    public void fromCalendar() throws Exception {
        final Calendar src = Calendar.getInstance();
        src.setTimeInMillis(System.currentTimeMillis());

        assertThat(sut.convert(src)
                      .getTime(), is(src.getTimeInMillis()));
    }

    @Test
    public void fromString() throws Exception {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final Timestamp expected = new Timestamp(format.parse("20170308")
                                                       .getTime());
        assertThat(sut.convert("20170308"), is(expected));
    }

    @Test
    public void fromStringArray() throws Exception {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        final Timestamp expected = new Timestamp(format.parse("20170309")
                                                       .getTime());
        assertThat(sut.convert(new String[] {"20170309"}), is(expected));
    }

    @Test
    public void fromLong_shouldThrowConvertError() throws Exception {
        final long src = System.currentTimeMillis();
        
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert " + src + " to Timestamp.");
        sut.convert(src);
    }
}