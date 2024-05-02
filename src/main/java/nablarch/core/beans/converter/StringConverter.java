package nablarch.core.beans.converter;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;
import nablarch.core.beans.Mergeable;
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
 * <b>日付型（日付パターンが設定されている場合）</b>：<br>
 * パターンに従ってフォーマットして返却する。
 * <p/>
 * <b>数値型（数値パターンが設定されている場合）</b>：<br>
 * パターンに従ってフォーマットして返却する。
 * <p/>
 * <b>上記以外</b>：<br>
 * 変換元の値を表す文字列を返却する。
 *
 * @author kawasima
 * @author tajima
 */
public class StringConverter implements Mergeable<String, StringConverter> {

    /** 日付パターン */
    private final String datePattern;
    /** 数値パターン */
    private final String numberPattern;

    private final DateTimeFormatter formatter;

    /**
     * デフォルトコンストラクタ。
     */
    public StringConverter() {
        this.datePattern = null;
        this.numberPattern = null;
        this.formatter = null;
    }

    /**
     * 日付パターンか数値パターン、もしくはその両方を設定してインスタンスを構築する。
     * 
     * @param datePattern 日付パターン
     * @param numberPattern 数値パターン
     */
    public StringConverter(String datePattern, String numberPattern) {
        this(datePattern, numberPattern,
        datePattern != null ? DateTimeFormatter.ofPattern(datePattern) : null);
    }

    private StringConverter(String datePattern, String numberPattern, DateTimeFormatter formatter) {
        this.datePattern = datePattern;
        this.numberPattern = numberPattern;
        this.formatter = formatter;
    }

    @Override
    public String convert(Object value) {
        if (value instanceof String) {
            return (String) value;
        } else if (value instanceof Boolean) {
            return (Boolean) value ? "1" : "0";
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, String.class);
        } else if (datePattern != null && value instanceof Date) {
            return DateUtil.formatDate((Date) value, datePattern);
        } else if (numberPattern != null && value instanceof Number) {
            return new DecimalFormat(numberPattern).format(value);
        } else if (formatter != null && value instanceof LocalDate) {
            return ((LocalDate) value).format(formatter);
        } else if (formatter != null && value instanceof LocalDateTime) {
            return ((LocalDateTime) value).format(formatter);
        }
        return StringUtil.toString(value);
    }

    @Override
    public StringConverter merge(StringConverter other) {
        return new StringConverter(
                datePattern != null ? datePattern : other.datePattern,
                numberPattern != null ? numberPattern : other.numberPattern,
                formatter != null ? formatter : other.formatter);
    }
}
