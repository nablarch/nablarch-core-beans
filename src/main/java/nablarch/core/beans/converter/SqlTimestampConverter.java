package nablarch.core.beans.converter;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * {@code java.sql.Timestamp}型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元の型に応じて、以下のとおり変換を行う。
 * <p/>
 * <b>Timestamp型</b>：<br>
 * 同一の値となる{@code java.sql.Timestamp}オブジェクトを返却する。
 * <p/>
 * <b>日付型</b>：<br>
 * 同一日付・時刻を表す{@code java.sql.Timestamp}オブジェクトを返却する。
 * <p/>
 * <b>文字列型</b>：<br>
 * {@link DateConverter}へ処理を委譲して取得した{@link java.util.Date}オブジェクトから
 * {@code java.sql.Timestamp}オブジェクトを生成して返却する。(時刻は切り捨て)
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

    /** 日付コンバーター */
    private final DateConverter dateConverter;

    /**
     * デフォルトコンストラクタ
     */
    public SqlTimestampConverter() {
        this.dateConverter = new DateConverter();
    }

    /**
     * 日付パターンを設定してインスタンスを構築する。
     * 
     * @param patterns 日付パターン
     */
    public SqlTimestampConverter(List<String> patterns) {
        this.dateConverter = new DateConverter(patterns);
    }

    @Override
    public Timestamp convert(final Object value) {
        if (value instanceof Timestamp) {
            final Timestamp src = Timestamp.class.cast(value);
            final Timestamp dest = new Timestamp(src.getTime());
            dest.setNanos(src.getNanos());
            return dest;
        } else if (value instanceof Date) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(Date.class.cast(value));
            return new Timestamp(cal.getTimeInMillis());
        } else if (value instanceof Calendar) {
            Calendar cal = Calendar.class.cast(value);
            return new Timestamp(cal.getTimeInMillis());
        } else if (value instanceof String) {
            Date d = dateConverter.convert(String.class.cast(value));
            return new Timestamp(d.getTime());
        } else if (value instanceof String[]) {
            return SingleValueExtracter.toSingleValue((String[]) value, this, Timestamp.class);
        } else {
            throw new ConversionException(Timestamp.class, value);
        }
    }

}
