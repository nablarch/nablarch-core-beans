package nablarch.core.beans.converter;

import java.time.ZoneId;

/**
 * {@link DateTimeConverterConfiguration}のデフォルト実装クラス
 *
 * @author TIS
 */
public class BasicDateTimeConverterConfiguration implements DateTimeConverterConfiguration {
    @Override
    public ZoneId getSystemZoneId() {
        return ZoneId.systemDefault();
    }
}
