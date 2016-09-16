package nablarch.core.beans.converter;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.not;
import static org.hamcrest.CoreMatchers.sameInstance;
import static org.junit.Assert.assertThat;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link BytesConverter}のテストクラス。
 */
public class BytesConverterTest {

    /** テスト対象 */
    private final BytesConverter sut = new BytesConverter();

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    /**
     * バイナリ(バイト配列)を指定した場合、新しい配列オブジェクトとして入力値が返されること。
     */
    @Test
    public void bytesType() throws Exception {
        final byte[] input = {0x30, 0x32};
        final byte[] actual = sut.convert(input);

        assertThat(actual, allOf(
                not(sameInstance(input)),       // インスタンスが変わっていること
                is(input)                       // 同値であること
        ));
    }

    /**
     * バイナリ（バイト配列）以外を指定した場合、例外が送出されること。
     *
     * @throws Exception
     */
    @Test
    public void notBytesType() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage(containsString("input to byte[]"));
        sut.convert("input");
    }
}
