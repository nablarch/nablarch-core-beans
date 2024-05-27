package nablarch.core.beans.converter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
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
 * 日付パターンが設定されている場合は日付パターンに従ってパースされた値を返却する。
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code java.util.Date}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>日付型({@code java.time.LocalDate})</b>：<br>
 * 同一日付を表す{@code java.util.Date}オブジェクトを返却する。
 * <p/>
 * <b>日時型({@code java.time.LocalDateTime})</b>：<br>
 * 同一日付を表す{@code java.util.Date}オブジェクトを返却する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class DateConverter implements Converter<Date> {

    /** 日付パターン */
    private final List<String> patterns;

    /**
     * デフォルトコンストラクタ
     */
    public DateConverter() {
        this.patterns = Collections.emptyList();
    }

    /**
     * 日付パターンを設定してインスタンスを構築する。
     * 
     * @param patterns 日付パターン
     */
    public DateConverter(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Date convert(Object value) {
        if (value instanceof Date date) {
            return new Date(date.getTime());
        } else if (value instanceof Calendar cal) {
            return cal.getTime();
        } else if (value instanceof String str) {
            return convertFromString(str);
        } else if (value instanceof String[] strArray) {
            return SingleValueExtracter.toSingleValue(strArray, this, Date.class);
        } else if (value instanceof LocalDateTime localDateTime) {
            return DateTimeConverterUtil.getDate(localDateTime);
        } else if (value instanceof LocalDate localDate) {
            return DateTimeConverterUtil.getDate(localDate);
        } else {
            throw new ConversionException(Date.class, value);
        }
    }

    /**
     * {@link String}型の値を変換する。
     * 
     * <p>
     * 日付パターンが設定されている場合は日付パターンによる変換を試行する。
     * 日付パターンは複数設定でき、1つずつ試行をして変換が出来た最初の値を返す。
     * 全ての日付パターンで変換が失敗した場合は{@link IllegalArgumentException}をスローする。
     * </p>
     * 
     * <p>
     * 日付パターンが設定されていない場合は{@link DateUtil#getDate(String)}に処理を委譲する。
     * </p>
     * 
     * @param value 変換前の値
     * @return 変換された値
     */
    Date convertFromString(String value) {
        if (!patterns.isEmpty()) {
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
