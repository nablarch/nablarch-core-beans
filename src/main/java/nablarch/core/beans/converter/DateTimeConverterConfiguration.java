package nablarch.core.beans.converter;

import nablarch.core.util.annotation.Published;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * Date and Time APIに関する共通的なフォーマッタ、タイムゾーンを扱うためのインターフェース。
 *
 * @author TIS
 */
@Published(tag = "architect")
public interface DateTimeConverterConfiguration {
    /**
     * 日付向けのフォーマッタ
     *
     * @return 日付向けの{@code java.time.format.DateTimeFormatter}のインスタンス
     */
    default DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.BASIC_ISO_DATE;
    }

    /**
     * 日時向けのフォーマッタ
     *
     * @return 日時向けの{@code java.time.format.DateTimeFormatter}のインスタンス
     */
    default DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ISO_INSTANT;
    }

    /**
     * オフセット付き日時向けのフォーマッタ
     *
     * @return オフセット付き日時向けの{@code java.time.format.DateTimeFormatter}のインスタンス
     */
    default DateTimeFormatter getOffsetDateTimeFormatter() {
        return DateTimeFormatter.ISO_OFFSET_DATE_TIME;
    }

    /**
     * システムが依存する{@code java.time.ZoneId}を取得する
     *
     * @return システムが依存するで管理している{@code java.time.ZoneId}
     */
    ZoneId getSystemZoneId();
}
