package nablarch.core.beans.converter;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.Date;

/**
 * Date and Time API向けのユーティリティ。
 * <p>
 * 本ユーティリティが使用する日付文字列の形式などは、{@link SystemRepository}より取得する。
 * {@link SystemRepository}からキー名:dateTimeConfigurationで{@link DateTimeConverterConfiguration}が取得出来た場合はそのオブジェクトを、
 * 取得出来ない場合は{@link BasicDateTimeConverterConfiguration}を使用する。
 *
 * @author TIS
 * @see DateTimeConverterConfiguration
 */
@Published
public final class DateTimeConverterUtil {

    /** 日付変換に使用する設定 */
    private static final DateTimeConverterConfiguration DEFAULT_DATE_TIME_CONFIGURATION = new BasicDateTimeConverterConfiguration();

    /**
     * 隠蔽コンストラクタ。
     */
    private DateTimeConverterUtil() {
    }

    /**
     * 日付変換に使用する設定を返す。
     * <p>
     * リポジトリに{@link DateTimeConverterConfiguration}が設定されている場合はそのオブジェクトを、
     * 設定されていない場合は{@link #DEFAULT_DATE_TIME_CONFIGURATION}を返す。
     *
     * @return 日付変換の設定
     */
    static DateTimeConverterConfiguration getDateTimeConverterConfiguration() {
        final DateTimeConverterConfiguration dateTimeConfiguration = SystemRepository.get("dateTimeConfiguration");

        if (dateTimeConfiguration != null) {
            return dateTimeConfiguration;
        } else {
            return DEFAULT_DATE_TIME_CONFIGURATION;
        }
    }

    /**
     * 日付文字列を{@link LocalDate}に変換する。
     * <p>
     * 日付文字列のフォーマットは、{@link DateTimeConverterConfiguration#getDateFormatter()} より取得する。
     *
     * @param date 日付文字列(yyyyMMdd形式)
     * @return 日付文字列をパースして生成した{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDate getLocalDate(final String date) {
        return LocalDate.parse(date, getDateTimeConverterConfiguration().getDateFormatter());
    }

    /**
     * {@code java.util.Date}のインスタンスを、{@code java.time.LocalDate}に変換する。
     * <p>
     * ゾーンIDは、{@link DateTimeConverterConfiguration#getSystemZoneId()}から取得する。
     *
     * @param date 変換対象の{@code java.util.Date}のインスタンス
     * @return 変換後の{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDate getLocalDate(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), getDateTimeConverterConfiguration().getSystemZoneId())
                            .toLocalDate();
    }

    /**
     * {@code java.sql.Date}のインスタンスを、{@code java.time.LocalDate}に変換する。
     * <p>
     * ※{@code java.sql.Date}は、toInstantメソッドをサポートしていないため
     *
     * @param date 変換対象の{@code java.sql.Date}のインスタンス
     * @return 変換後の{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDate getLocalDateAsSqlDate(final java.sql.Date date) {
        return date.toLocalDate();
    }

    /**
     * {@code java.util.Calendar}のインスタンスを、{@code java.time.LocalDate}に変換する。
     *
     * @param calendar 変換対象の{@code java.util.Calendar}のインスタンス
     * @return 変換後の{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDate getLocalDate(final Calendar calendar) {
        return getLocalDate(calendar.getTime());
    }

    /**
     * {@code java.time.OffsetDateTime}のインスタンスを、{@code java.time.LocalDate}に変換する。
     *
     * @param dateTime 変換対象の{@code java.time.OffsetDateTime}のインスタンス
     * @return 変換後の{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDate getLocalDate(final OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(getDateTimeConverterConfiguration().getSystemZoneId()).toLocalDate();
    }

    /**
     * 日時文字列を{@link LocalDateTime}に変換する。
     *
     * @param date 変換対象の日時文字列
     * @return 変換後の値
     */
    public static LocalDateTime getLocalDateTime(final String date) {
        if (date.endsWith("Z")) {
            final Instant instant = Instant.from(
                    getDateTimeConverterConfiguration().getDateTimeFormatter().parse(date));
            return LocalDateTime.ofInstant(instant, ZoneOffset.UTC);
        } else {
            return LocalDateTime.parse(date, getDateTimeConverterConfiguration().getDateTimeFormatter());
        }
    }

    /**
     * {@code java.util.Date}のインスタンスを、{@code java.time.LocalDateTime}に変換する
     *
     * @param date 変換対象の{@code java.util.Date}のインスタンス
     * @return 変換後の{@code java.time.LocalDate}のインスタンス
     */
    public static LocalDateTime getLocalDateTime(final Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), getDateTimeConverterConfiguration().getSystemZoneId());
    }

    /**
     * {@code java.sql.Date}のインスタンスを、{@code java.time.LocalDateTime}に変換する
     * <p>
     * ※{@code java.sql.Date}は、toInstantメソッドをサポートしていないため
     *
     * @param date 変換対象の{@code java.sql.Date}のインスタンス
     * @return 変換後の{@code java.time.LocalDateTime}のインスタンス
     */
    public static LocalDateTime getLocalDateTimeAsSqlDate(final java.sql.Date date) {
        return date.toLocalDate()
                   .atStartOfDay();
    }

    /**
     * {@code java.util.Calendar}のインスタンスを、{@code java.time.LocalDateTime}に変換する
     *
     * @param calendar 変換対象の{@code java.util.Calendar}のインスタンス
     * @return 変換後の{@code java.time.LocalDateTime}のインスタンス
     */
    public static LocalDateTime getLocalDateTime(final Calendar calendar) {
        return getLocalDateTime(calendar.getTime());
    }

    /**
     * {@code java.time.OffsetDateTime}のインスタンスを、{@code java.time.LocalDateTime}に変換する。
     *
     * @param dateTime 変換対象の{@code java.time.OffsetDateTime}のインスタンス
     * @return 変換後の{@code java.time.LocalDateTime}のインスタンス
     */
    public static LocalDateTime getLocalDateTime(final OffsetDateTime dateTime) {
        return dateTime.atZoneSameInstant(getDateTimeConverterConfiguration().getSystemZoneId()).toLocalDateTime();
    }

    /**
     * 日時文字列を{@link OffsetDateTime}に変換する。
     *
     * @param date 変換対象の日時文字列
     * @return 変換後の値
     */
    public static OffsetDateTime getOffsetDateTime(String date) {
        return OffsetDateTime.parse(date, getDateTimeConverterConfiguration().getOffsetDateTimeFormatter());
    }

    /**
     * {@code java.util.Date}のインスタンスを、{@code java.time.OffsetDateTime}に変換する
     *
     * @param date 変換対象の{@code java.util.Date}のインスタンス
     * @return 変換後の{@code java.time.OffsetDateTime}のインスタンス
     */
    public static OffsetDateTime getOffsetDateTime(Date date) {
        return OffsetDateTime.ofInstant(date.toInstant(), getDateTimeConverterConfiguration().getSystemZoneId());
    }

    /**
     * {@code java.sql.Date}のインスタンスを、{@code java.time.OffsetDateTime}に変換する
     *
     * @param date 変換対象の{@code java.sql.Date}のインスタンス
     * @return 変換後の{@code java.time.OffsetDateTime}のインスタンス
     */
    public static OffsetDateTime getOffsetDateTimeAsSqlDate(java.sql.Date date) {
        return date.toLocalDate().atStartOfDay(getDateTimeConverterConfiguration().getSystemZoneId()).toOffsetDateTime();
    }

    /**
     * {@code java.util.Calendar}のインスタンスを、{@code java.time.OffsetDateTime}に変換する
     *
     * @param calendar 変換対象の{@code java.util.Calendar}のインスタンス
     * @return 変換後の{@code java.time.OffsetDateTime}のインスタンス
     */
    public static OffsetDateTime getOffsetDateTime(Calendar calendar) {
        return getOffsetDateTime(calendar.getTime());
    }

    /**
     * {@code java.time.LocalDate}のインスタンスを、{@code java.time.OffsetDateTime}に変換する
     *
     * @param date 変換対象の{@code java.time.LocalDate}のインスタンス
     * @return 変換後の{@code java.time.OffsetDateTime}のインスタンス
     */
    public static OffsetDateTime getOffsetDateTime(LocalDate date) {
        return date.atStartOfDay(getDateTimeConverterConfiguration().getSystemZoneId()).toOffsetDateTime();
    }

    /**
     * {@code java.time.LocalDateTime}のインスタンスを、{@code java.time.OffsetDateTime}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.LocalDateTime}のインスタンス
     * @return 変換後の{@code java.time.OffsetDateTime}のインスタンス
     */
    public static OffsetDateTime getOffsetDateTime(LocalDateTime dateTime) {
        return dateTime.atZone(getDateTimeConverterConfiguration().getSystemZoneId()).toOffsetDateTime();
    }

    /**
     * {@code java.time.LocalDateTime}のインスタンスを{@code java.util.Date}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.LocalDateTime}のインスタンス
     * @return 変換後の{@code java.util.Date}のインスタンス
     */
    public static Date getDate(final LocalDateTime dateTime) {
        return Date.from(dateTime.atZone(getDateTimeConverterConfiguration().getSystemZoneId())
                                 .toInstant());
    }

    /**
     * {@code java.time.LocalDateTime}のインスタンスを{@code java.sql.Timestamp}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.LocalDateTime}のインスタンス
     * @return 変換後の{@code Timestamp}のインスタンス
     */
    public static Timestamp getTimestamp(final LocalDateTime dateTime) {
        return Timestamp.from(dateTime.atZone(getDateTimeConverterConfiguration().getSystemZoneId())
                                      .toInstant());
    }

    /**
     * {@code java.time.OffsetDateTime}のインスタンスを{@code java.sql.Timestamp}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.OffsetDateTime}のインスタンス
     * @return 変換後の{@code Timestamp}のインスタンス
     */
    public static Timestamp getTimestamp(final OffsetDateTime dateTime) {
        return Timestamp.from(dateTime.atZoneSameInstant(getDateTimeConverterConfiguration().getSystemZoneId())
                                      .toInstant());
    }

    /**
     * {@code java.time.LocalDate}のインスタンスを{@code java.util.Date}に変換する
     *
     * @param date 変換対象の{@code java.time.LocalDate}のインスタンス
     * @return 変換後の{@code java.util.Date}のインスタンス
     */
    public static Date getDate(final LocalDate date) {
        return Date.from(date.atStartOfDay(getDateTimeConverterConfiguration().getSystemZoneId())
                                      .toInstant());
    }

    /**
     * {@code java.time.OffsetDateTime}のインスタンスを{@code java.util.Date}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.LocalDate}のインスタンス
     * @return 変換後の{@code java.util.Date}のインスタンス
     */
    public static Date getDate(final OffsetDateTime dateTime) {
        return Date.from(dateTime.atZoneSameInstant(getDateTimeConverterConfiguration().getSystemZoneId())
                                 .toInstant());
    }

    /**
     * {@code java.time.OffsetDateTime}のインスタンスを{@code java.sql.Date}に変換する
     *
     * @param dateTime 変換対象の{@code java.time.OffsetDateTime}のインスタンス
     * @return 変換後の{@code java.sql.Date}のインスタンス
     */
    public static java.sql.Date getSqlDate(final OffsetDateTime dateTime) {
        return java.sql.Date.valueOf(dateTime.atZoneSameInstant(getDateTimeConverterConfiguration().getSystemZoneId())
                .toLocalDate());
    }
}
