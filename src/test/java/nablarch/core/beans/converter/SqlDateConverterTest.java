package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Date;
import java.util.Arrays;
import java.util.Collections;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

public class SqlDateConverterTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Test
    public void デフォルトパターン() {
        SqlDateConverter sut = new SqlDateConverter();
        assertThat(sut.convert("20180213"), is(Date.valueOf("2018-02-13")));
    }

    @Test
    public void デフォルトパターン_配列() {
        SqlDateConverter sut = new SqlDateConverter();
        assertThat(sut.convert(new String[] { "20180213" }), is(Date.valueOf("2018-02-13")));
    }

    @Test
    public void カスタムパターン() {
        SqlDateConverter sut = new SqlDateConverter(Collections.singletonList("yyyy/MM/dd"));
        assertThat(sut.convert("2018/02/13"), is(Date.valueOf("2018-02-13")));
    }

    @Test
    public void カスタムパターン_配列() {
        SqlDateConverter sut = new SqlDateConverter(Collections.singletonList("yyyy/MM/dd"));
        assertThat(sut.convert(new String[] { "2018/02/13" }), is(Date.valueOf("2018-02-13")));
    }

    @Test
    public void 複数のカスタムパターン() {
        SqlDateConverter sut = new SqlDateConverter(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"));
        assertThat(sut.convert("2018/02/14"), is(Date.valueOf("2018-02-14")));
        assertThat(sut.convert("2018-02-14"), is(Date.valueOf("2018-02-14")));
    }

    @Test
    public void デフォルトパターンに合わない場合() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException
                .expectMessage("the string was not formatted yyyyMMdd. date = 2018/02/14.");
        SqlDateConverter sut = new SqlDateConverter();
        sut.convert("2018/02/14");
    }

    @Test
    public void カスタムパターンに合わない場合() {
        expectedException.expect(IllegalArgumentException.class);
        expectedException.expectMessage(
                "the string was not formatted [yyyy/MM/dd, yyyy.MM.dd]. date = 2018-02-14.");
        SqlDateConverter sut = new SqlDateConverter(Arrays.asList("yyyy/MM/dd", "yyyy.MM.dd"));
        sut.convert("2018-02-14");
    }
}
