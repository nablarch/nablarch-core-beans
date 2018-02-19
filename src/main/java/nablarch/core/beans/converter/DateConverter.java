package nablarch.core.beans.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.DateUtil;

/**
 * {@code java.util.Date}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>日付型</b>：<br>
 * 同一日付を表す{@code java.util.Date}オブジェクトを返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * 変換元の日付文字列と同一日付を表す{@code java.util.Date}オブジェクトを返却する。
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code java.util.Date}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class DateConverter implements Converter<Date> {

    private final List<String> patterns;

    public DateConverter() {
        this.patterns = Collections.emptyList();
    }

    public DateConverter(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Date convert(Object value) {
        if (value instanceof Date) {
            return new Date(Date.class.cast(value).getTime());
        } else if (value instanceof Calendar) {
            return Calendar.class.cast(value).getTime();
        } else if (value instanceof String) {
            return convertFromString(String.class.cast(value));
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, Date.class);
        } else {
            throw new ConversionException(Date.class, value);
        }
    }

    Date convertFromString(String value) {
        if (patterns.isEmpty() == false) {
            ParseException lastThrownException = null;
            for (String pattern : patterns) {
                try {
                    return new SimpleDateFormat(pattern).parse(value);
                } catch (ParseException ignore) {
                    //複数のパターンを順番に試すのでParseExceptionは無視する
                    lastThrownException = ignore;
                }
            }
            //すべてのパターンが失敗した場合は例外をスロー
            throw new IllegalArgumentException(
                    "the string was not formatted " + patterns + ". date = " + value + ".",
                    lastThrownException);
        }
        return DateUtil.getDate(value);
    }
}
