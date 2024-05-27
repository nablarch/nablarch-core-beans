package nablarch.core.beans.converter;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * {@code java.time.LocalDateTime}型への変換を行う {@link Converter} 。
 * <p>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p>
 * <b>日付型({@code java.time.LocalDate})</b>：<br>
 * 同一日付を表す{@code java.time.LocalDateTime}オブジェクトを返却する。
 * <p>
 * <b>日時型({@code java.time.LocalDateTime})</b>：<br>
 * 同一日時を表す{@code java.time.LocalDateTime}オブジェクトを返却する。
 * <p>
 * <b>日付型</b>：<br>
 * 同一日付を表す{@code java.time.LocalDateTime}オブジェクトを返却する。
 * <p>
 * <b>文字列型</b>：<br>
 * 日付文字列と同一日付を表す{@code java.time.LocalDateTime}オブジェクトを返却する。
 * <p>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code java.time.LocalDateTime}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 * @author TIS
 */
public class LocalDateTimeConverter implements Converter<LocalDateTime> {

    /** 日付パターン */
    private final List<DateTimeFormatter> formatters;

    /**
     * デフォルトコンストラクタ
     */
    public LocalDateTimeConverter() {
        this.formatters = Collections.emptyList();
    }

    /**
     * 日付パターンを設定してインスタンスを構築する。
     * 
     * @param patterns 日付パターン
     */
    public LocalDateTimeConverter(List<String> patterns) {
        this.formatters = patterns.stream()
                .map(DateTimeFormatter::ofPattern)
                .collect(Collectors.toList());
    }

    @Override
    public LocalDateTime convert(final Object value) {
        if (value instanceof LocalDate localDate) {
            return LocalDateTime.of(localDate, LocalTime.of(0, 0, 0));
        } else if (value instanceof LocalDateTime localDateTime) {
            return localDateTime;
        } else if (value instanceof java.sql.Date sqlDate) {
            return DateTimeConverterUtil.getLocalDateTimeAsSqlDate(sqlDate);
        } else if (value instanceof Date date) {
            return DateTimeConverterUtil.getLocalDateTime(date);
        } else if (value instanceof Calendar cal) {
            return DateTimeConverterUtil.getLocalDateTime(cal);
        } else if (value instanceof String str) {
            return convertFromString(str);
        } else if (value instanceof String[] strArray) {
            return SingleValueExtracter.toSingleValue(strArray, this, LocalDateTime.class);
        } else {
            throw new ConversionException(LocalDate.class, value);
        }
    }

    private LocalDateTime convertFromString(String value) {
        if (!formatters.isEmpty()) {
            DateTimeParseException lastThrownException = null;
            for (DateTimeFormatter formatter : formatters) {
                try {
                    return LocalDateTime.parse(value, formatter);
                } catch (DateTimeParseException ignore) {
                    //複数のパターンを順番に試すのでParseExceptionは無視する
                    lastThrownException = ignore;
                }
            }
            //すべてのパターンが失敗した場合は例外をスロー
            throw new IllegalArgumentException(
                    "the string was not formatted " + formatters + ". date = " + value + ".",
                    lastThrownException);
        }
        return DateTimeConverterUtil.getLocalDateTime(value);
    }
}
