package nablarch.core.beans.converter;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.NumberUtil;

/**
 * {@code BigDecimal}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>真偽値</b>：<br>
 * {@code true}であれば{@link BigDecimal#ONE}、{@code false}であれば{@link BigDecimal#ZERO}を返却する。
 * <p/>
 * <b>数値型</b>：<br>
 * 変換元の数値を表す{@code BigDecimal}オブジェクトを返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * 変換元の数値文字列を表す{@code BigDecimal}オブジェクトを返却する。
 * 文字列が数値として不正であれば{@link ConversionException}を送出する。
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code BigDecimal}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class BigDecimalConverter implements Converter<BigDecimal> {

    private final List<String> patterns;

    public BigDecimalConverter() {
        this.patterns = Collections.emptyList();
    }

    public BigDecimalConverter(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public BigDecimal convert(final Object value) {
        if (value instanceof Number) {
            if (value instanceof BigDecimal) {
                return (BigDecimal) value; // an BigDecimal instance is immutable.
            } else if (value instanceof BigInteger) {
                return new BigDecimal((BigInteger) value);
            } else if (value instanceof Double || value instanceof Float) {
                return BigDecimal.valueOf(((Number) value).doubleValue());
            } else {
                return BigDecimal.valueOf(((Number) value).longValue());
            }
        } else if (value instanceof String) {
            try {
                final BigDecimal result = convertFromString(String.class.cast(value));
                NumberUtil.verifyBigDecimalScale(result);
                return result;
            } catch (NumberFormatException e) {
                throw new ConversionException(BigDecimal.class, value);
            }
        } else if (value instanceof Boolean) {
            return Boolean.class.cast(value) ? BigDecimal.ONE : BigDecimal.ZERO;
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, BigDecimal.class);
        } else {
            throw new ConversionException(BigDecimal.class, value);
        }
    }

    private BigDecimal convertFromString(String value) {
        if (patterns.isEmpty() == false) {
            ParseException lastThrownException = null;
            for (String pattern : patterns) {
                try {
                    return this.convert(new DecimalFormat(pattern).parse(value));
                } catch (ParseException ignore) {
                    //複数のパターンを順番に試すのでParseExceptionは無視する
                    lastThrownException = ignore;
                }
            }
            //すべてのパターンが失敗した場合は例外をスロー
            throw new IllegalArgumentException(
                    "the string was not formatted " + patterns + ". number = " + value + ".",
                    lastThrownException);
        }
        return new BigDecimal(value);
    }
}
