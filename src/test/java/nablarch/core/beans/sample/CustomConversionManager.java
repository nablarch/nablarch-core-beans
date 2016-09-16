package nablarch.core.beans.sample;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.beans.ConversionManager;
import nablarch.core.beans.Converter;
import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.BooleanConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.ObjectArrayConverter;
import nablarch.core.beans.converter.ShortConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.beans.converter.StringArrayConverter;
import nablarch.core.beans.converter.StringConverter;

public class CustomConversionManager implements ConversionManager {

    /** 型変換に使用する{@link Converter}を格納したMap */
    private Map<Class<?>, Converter<?>> converters;

    /**
     * コンストラクタ。
     */
    public CustomConversionManager() {

        Map<Class<?>, Converter<?>> converterMap = new HashMap<Class<?>, Converter<?>>();

        // フレームワーク提供のコンバータ
        converterMap.put(Boolean.class, new BooleanConverter());
        converterMap.put(boolean.class, new BooleanConverter());
        converterMap.put(Integer.class, new IntegerConverter());
        converterMap.put(int.class, new IntegerConverter());
        converterMap.put(Short.class, new ShortConverter());
        converterMap.put(short.class, new ShortConverter());
        converterMap.put(Long.class, new LongConverter());
        converterMap.put(long.class, new LongConverter());
        converterMap.put(BigDecimal.class, new BigDecimalConverter());
        converterMap.put(String.class, new StringConverter());
        converterMap.put(String[].class, new StringArrayConverter());
        converterMap.put(Object[].class, new ObjectArrayConverter());
        converterMap.put(Date.class, new DateConverter());
        converterMap.put(java.sql.Date.class, new SqlDateConverter());
        converterMap.put(Timestamp.class, new SqlTimestampConverter());

        // PJ固有のコンバータ
        converterMap.put(BigInteger.class, new CustomConverter());

        converters = Collections.unmodifiableMap(converterMap);
    }

    @Override
    public Map<Class<?>, Converter<?>> getConverters() {
        return converters;
    }
}
