package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import nablarch.core.beans.Converter;
import nablarch.core.beans.ConverterFactory;
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

/**
 * {@link ConverterFactory}の基本実装クラス。
 * 
 * @author Naoki Yamamoto
 */
public class BasicConverterFactory implements ConverterFactory {

    @Override
    public Map<Class<?>, Converter<?>> create() {
        return new ConcurrentHashMap<Class<?>, Converter<?>>() {
            {
                put(Boolean.class, new BooleanConverter());
                put(boolean.class, new BooleanConverter());
                put(Integer.class, new IntegerConverter());
                put(int.class, new IntegerConverter());
                put(Short.class, new ShortConverter());
                put(short.class, new ShortConverter());
                put(Long.class, new LongConverter());
                put(long.class, new LongConverter());
                put(BigDecimal.class, new BigDecimalConverter());
                put(String.class, new StringConverter());
                put(String[].class, new StringArrayConverter());
                put(Object[].class, new ObjectArrayConverter());
                put(Date.class, new DateConverter());
                put(java.sql.Date.class, new SqlDateConverter());
                put(Timestamp.class, new SqlTimestampConverter());
            }
        };
    }
}
