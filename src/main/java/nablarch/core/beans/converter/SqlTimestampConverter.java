package nablarch.core.beans.converter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.util.DateUtil;

/**
 * {@code java.sql.Timestamp}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>日付型</b>：<br>
 * 同一日付・時刻を表す{@code java.sql.Timestamp}オブジェクトを返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * 変換元の日付文字列と同一日付を表す{@code java.sql.Timestamp}オブジェクトを返却する。(時刻は切り捨て)
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code java.sql.Timestamp}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class SqlTimestampConverter implements Converter<Timestamp> {
    @Override
    public Timestamp convert(Object value) {
        if (value instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Date.class.cast(value));
            return new Timestamp(cal.getTimeInMillis());
        } else if (value instanceof Calendar) {
            Calendar cal = Calendar.class.cast(value);
            return new Timestamp(cal.getTimeInMillis());
        } else if (value instanceof String) {
            Date d = DateUtil.getDate(String.class.cast(value));
            Calendar cal = Calendar.getInstance();
            cal.setTime(d);
            return new Timestamp(cal.getTimeInMillis());
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, Timestamp.class);
        } else {
            throw new ConversionException(java.sql.Date.class, value);
        }
    }

}
