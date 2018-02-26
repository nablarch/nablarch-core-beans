package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class DateConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void デフォルトパターン() {
        DateConverter sut = new DateConverter();
        assertThat(sut.convert("20180213"), is(date("2018-02-13 00:00:00")));
    }

    @Test
    public void デフォルトパターン_配列() {
        DateConverter sut = new DateConverter();
        assertThat(sut.convert(new String[] { "20180213" }), is(date("2018-02-13 00:00:00")));
    }

    @Test
    public void カスタムパターン() {
        DateConverter sut = new DateConverter(Collections.singletonList("yyyy/MM/dd HH:mm"));
        assertThat(sut.convert("2018/02/13 17:35"), is(date("2018-02-13 17:35:00")));
    }

    @Test
    public void カスタムパターン_配列() {
        DateConverter sut = new DateConverter(Collections.singletonList("yyyy/MM/dd HH:mm"));
        assertThat(sut.convert(new String[] { "2018/02/13 17:35" }),
                is(date("2018-02-13 17:35:00")));
    }

    @Test
    public void 複数のカスタムパターン() {
        DateConverter sut = new DateConverter(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"));
        assertThat(sut.convert("2018/02/14"), is(date("2018-02-14 00:00:00")));
        assertThat(sut.convert("2018-02-14"), is(date("2018-02-14 00:00:00")));
    }

    @Test
    public void デフォルトパターンに合わない場合() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException
                .expectMessage("the string was not formatted yyyyMMdd. date = 2018/02/14.");
        DateConverter sut = new DateConverter();
        sut.convert("2018/02/14");
    }

    @Test
    public void カスタムパターンに合わない場合() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "the string was not formatted [yyyy/MM/dd, yyyy.MM.dd]. date = 2018-02-14.");
        DateConverter sut = new DateConverter(Arrays.asList("yyyy/MM/dd", "yyyy.MM.dd"));
        sut.convert("2018-02-14");
    }

    private static Date date(String sqlTimestampPattern) {
        return new Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }
}
