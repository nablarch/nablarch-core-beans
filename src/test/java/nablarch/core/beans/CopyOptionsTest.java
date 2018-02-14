package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import nablarch.core.beans.converter.DateConverter;

public class CopyOptionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void hasConverter() {
        CopyOptions sut = CopyOptions.options()
                .datePattern("foo", "yyyy/MM/dd", "yyyy-MM-dd")
                .converter("bar", new DateConverter("yyyy.MM.dd"))
                .build();
        assertThat(sut.hasConverter("foo"), is(true));
        assertThat(sut.hasConverter("bar"), is(true));
        assertThat(sut.hasConverter("baz"), is(false));
    }

    @Test
    public void convert() {
        CopyOptions sut = CopyOptions.options()
                .datePattern("foo", "yyyy/MM/dd", "yyyy-MM-dd")
                .converter("bar", new DateConverter("yyyy.MM.dd"))
                .build();
        assertThat((Date) sut.convert("foo", "2018/02/14"), is(date("2018-02-14 00:00:00")));
        assertThat((Date) sut.convert("foo", "2018-02-14"), is(date("2018-02-14 00:00:00")));
        assertThat((Date) sut.convert("bar", "2018.02.14"), is(date("2018-02-14 00:00:00")));
    }

    @Test
    public void convert失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePattern("foo", "yyyy/MM/dd", "yyyy-MM-dd")
                .converter("bar", new DateConverter("yyyy.MM.dd"))
                .build();
        sut.convert("baz", "20180214");
    }

    private static Date date(String sqlTimestampPattern) {
        return new Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }
}