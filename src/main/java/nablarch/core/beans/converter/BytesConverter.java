package nablarch.core.beans.converter;

import java.util.Arrays;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * バイナリ({@code byte}配列)への変換を行う{@link Converter}.
 * <p>
 * コピー対象のオブジェクトが{@code byte}配列の場合、新しい配列に値をコピーし返却する。
 * それ以外のオブジェクトの場合には、{@link ConversionException}を送出する。
 *
 * @author Hisaaki Shioiri
 */
public class BytesConverter implements Converter<byte[]> {

    @Override
    public byte[] convert(Object value) {
        if (value instanceof byte[]) {
            final byte[] src = (byte[]) value;
            return Arrays.copyOf(src, src.length);
        } else {
            throw new ConversionException(byte[].class, value);
        }
    }
}
