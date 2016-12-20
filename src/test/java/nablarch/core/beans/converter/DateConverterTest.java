package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertThat;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link DateConverter}のテスト。
 */
public class DateConverterTest {

    private final DateConverter sut = new DateConverter();
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void fromDate() throws Exception {
        final Date input = new Date();
        assertThat(sut.convert(input), is(input));
    }

    @Test
    public void fromCalender() throws Exception {
        final Calendar input = Calendar.getInstance();
        input.set(2016, 0, 2, 0, 0, 0);
        input.set(Calendar.MILLISECOND, 0);

        assertThat(sut.convert(input), is(toDate("20160102")));
    }

    @Test
    public void fromString() throws Exception {
        assertThat(sut.convert("20160101"), is(toDate("20160101")));
    }

    @Test
    public void fromEmptyString() throws Exception {
        assertThat(sut.convert(""), is(nullValue()));
    }

    @Test
    public void fromSingleStringArray() throws Exception {
        assertThat(sut.convert(new String[] {"20160103"}), is(toDate("20160103")));
    }

    @Test
    public void fromMultipleStringArray() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new String[] {"20160103", "20160104"});
    }

    @Test
    public void fromOtherObject() throws Exception {
        expectedException.expect(ConversionException.class);
        sut.convert(new Object());
    }

    private Date toDate(String yyyymmdd) throws Exception {
        final SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.parse(yyyymmdd);
    }
}