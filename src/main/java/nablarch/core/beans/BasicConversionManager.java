package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.BooleanConverter;
import nablarch.core.beans.converter.BytesConverter;
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
 * {@link ConversionManager}の基本実装クラス。
 * <p/>
 * フレームワークでデフォルトで用意しているコンバータを生成して提供する。
 * 
 * @author Naoki Yamamoto
 */
public class BasicConversionManager implements ConversionManager {

    /** 型変換に使用する{@link Converter}を格納したMap */
    private final Map<Class<?>, Converter<?>> converters;

    /**
     * コンストラクタ。
     */
    public BasicConversionManager() {
        Map<Class<?>, Converter<?>> convertMap = new HashMap<Class<?>, Converter<?>>();
        convertMap.put(Boolean.class, new BooleanConverter());
        convertMap.put(boolean.class, new BooleanConverter.Primitive());
        convertMap.put(Integer.class, new IntegerConverter());
        convertMap.put(int.class, new IntegerConverter.Primitive());
        convertMap.put(Short.class, new ShortConverter());
        convertMap.put(short.class, new ShortConverter.Primitive());
        convertMap.put(Long.class, new LongConverter());
        convertMap.put(long.class, new LongConverter.Primitive());
        convertMap.put(BigDecimal.class, new BigDecimalConverter());
        convertMap.put(String.class, new StringConverter());
        convertMap.put(String[].class, new StringArrayConverter());
        convertMap.put(Object[].class, new ObjectArrayConverter());
        convertMap.put(Date.class, new DateConverter());
        convertMap.put(java.sql.Date.class, new SqlDateConverter());
        convertMap.put(Timestamp.class, new SqlTimestampConverter());
        convertMap.put(byte[].class, new BytesConverter());
        converters = Collections.unmodifiableMap(convertMap);
    }

    @Override
    public Map<Class<?>, Converter<?>> getConverters() {
        return converters;
    }
}
