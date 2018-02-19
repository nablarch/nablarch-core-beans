package nablarch.core.beans.converter;

import java.text.DecimalFormat;
import java.util.Date;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.DateUtil;
import nablarch.core.util.StringUtil;

/**
 * {@code String}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>真偽値</b>：<br>
 * {@code true}であれば{@literal 1}、{@code false}であれば{@literal 0}を返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * 何もせずにそのまま返却する。
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素をそのまま返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * 変換元の値を表す文字列を返却する。
 *
 * @author kawasima
 * @author tajima
 */
public class StringConverter implements Converter<String> {

    private final String datePattern;
    private final String numberPattern;

    public StringConverter() {
        this.datePattern = null;
        this.numberPattern = null;
    }

    public StringConverter(String datePattern, String numberPattern) {
        this.datePattern = datePattern;
        this.numberPattern = numberPattern;
    }

    @Override
    public String convert(Object value) {
        if (value instanceof String) {
            return String.class.cast(value);
        } else if (value instanceof Boolean) {
            return Boolean.class.cast(value) ? "1" : "0";
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, String.class);
        } else if (datePattern != null && value instanceof Date) {
            return DateUtil.formatDate(Date.class.cast(value), datePattern);
        } else if (numberPattern != null && value instanceof Number) {
            return new DecimalFormat(numberPattern).format(value);
        }
        return StringUtil.toString(value);
    }

}
