package nablarch.core.beans.converter;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * {@code Boolean}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>真偽値</b>：<br>
 * 何もしない。
 * <p/>
 * <b>数値型</b>：<br>
 * 変換元の値が{@code 0}なら{@code false}、それ以外なら{@code true}を返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * {@literal true}、{@literal on}、{@literal 1}なら{@code true}、それ以外なら{@code false}を返却する。
 * (大文字・小文字は区別しない)
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code Boolean}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class BooleanConverter implements Converter<Boolean> {
    @Override
    public Boolean convert(final Object value) {
        if (value instanceof Boolean) {
            return Boolean.class.cast(value);
        } else if (value instanceof Number) {
            final int intVal = Number.class.cast(value).intValue();
            return intVal != 0;
        } else if (value instanceof String) {
            final String strVal = String.class.cast(value);
            if (strVal.isEmpty()) {
                return null;
            }
            return strVal.equalsIgnoreCase("true")
                    || strVal.equals("1")
                    || strVal.equalsIgnoreCase("on");
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, Boolean.class);
        } else {
            throw new ConversionException(Boolean.class, value);
        }
    }

    /**
     * {@code boolean}に変換するための{@link Converter}。
     * <p>
     * プリミティブへの変換を行うので、{@link BooleanConverter}で変換後の値が{@code null}の場合には、
     * {@code false}に変換し返却する。
     */
    public static class Primitive extends BooleanConverter {

        @Override
        public Boolean convert(final Object value) {
            final Boolean result = super.convert(value);
            return result == null ? Boolean.FALSE : result;
        }
    }
}
