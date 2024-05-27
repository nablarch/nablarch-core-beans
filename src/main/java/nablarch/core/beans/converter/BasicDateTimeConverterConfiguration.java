package nablarch.core.beans.converter;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

/**
 * {@link DateTimeConverterConfiguration}のデフォルト実装クラス
 *
 * @author TIS
 */
public class BasicDateTimeConverterConfiguration implements DateTimeConverterConfiguration {

    @Override
    public DateTimeFormatter getDateFormatter() {
        return DateTimeFormatter.BASIC_ISO_DATE;
    }

    @Override
    public DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ISO_INSTANT;
    }

    @Override
    public ZoneId getSystemZoneId() {
        return ZoneId.systemDefault();
    }
}
