package nablarch.core.beans.converter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * {@code Long}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>真偽値</b>：<br>
 * {@code true}であれば{@code 1}、{@code false}であれば{@code 0}を返却する。
 * <p/>
 * <b>数値型</b>：<br>
 * 変換元の数値を表す{@link Long}オブジェクトを返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * 変換元の数値文字列を表す{@link Long}オブジェクトを返却する。
 * 文字列が数値として不正な場合は{@link ConversionException}を送出する。
 * <p/>
 * <b>文字列型の配列</b>：<br>
 * 要素数が1であれば、その要素を{@code Long}オブジェクトに変換して返却する。
 * 要素数が1以外であれば、{@link ConversionException}を送出する。
 * <p/>
 * <b>上記以外</b>：<br>
 * {@link ConversionException}を送出する。
 *
 * @author kawasima
 * @author tajima
 */
public class LongConverter implements Converter<Long> {

    /** 数値パターン */
    private final List<String> patterns;

    /**
     * デフォルトコンストラクタ
     */
    public LongConverter() {
        this.patterns = Collections.emptyList();
    }

    /**
     * 数値パターンを設定してインスタンスを構築する。
     * 
     * @param patterns 数値パターン
     */
    public LongConverter(List<String> patterns) {
        this.patterns = patterns;
    }

    @Override
    public Long convert(Object value) {
        if (value instanceof Number) {
            return Number.class.cast(value).longValue();
        } else if (value instanceof String) {
            return convertFromString(String.class.cast(value));
        } else if (value instanceof Boolean) {
            return Boolean.class.cast(value) ? 1L : 0L;
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, Long.class);
        } else {
            throw new ConversionException(Long.class, value);
        }
    }

    private Long convertFromString(String value) {
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
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException e) {
            throw new ConversionException(Long.class, value);
        }
    }
}
