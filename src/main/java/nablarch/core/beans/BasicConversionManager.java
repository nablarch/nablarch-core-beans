package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.beans.converter.ArrayExtensionConverter;
import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.BooleanConverter;
import nablarch.core.beans.converter.BytesConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.ListExtensionConverter;
import nablarch.core.beans.converter.LocalDateConverter;
import nablarch.core.beans.converter.LocalDateTimeConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.ObjectArrayConverter;
import nablarch.core.beans.converter.OffsetDateTimeConverter;
import nablarch.core.beans.converter.SetExtensionConverter;
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
    private Map<Class<?>, Converter<?>> converters;

    /** 拡張型変換のList */
    private final List<ExtensionConverter<?>> extensionConverters;

    /**
     * コンストラクタ。
     */
    public BasicConversionManager() {
        final Map<Class<?>, Converter<?>> convertMap = new HashMap<>();
        convertMap.put(Boolean.class, new BooleanConverter());
        convertMap.put(boolean.class, new BooleanConverter());
        convertMap.put(Integer.class, new IntegerConverter());
        convertMap.put(int.class, new IntegerConverter());
        convertMap.put(Short.class, new ShortConverter());
        convertMap.put(short.class, new ShortConverter());
        convertMap.put(Long.class, new LongConverter());
        convertMap.put(long.class, new LongConverter());
        convertMap.put(BigDecimal.class, new BigDecimalConverter());
        convertMap.put(String.class, new StringConverter());
        convertMap.put(String[].class, new StringArrayConverter());
        convertMap.put(Object[].class, new ObjectArrayConverter());
        convertMap.put(Date.class, new DateConverter());
        convertMap.put(java.sql.Date.class, new SqlDateConverter());
        convertMap.put(Timestamp.class, new SqlTimestampConverter());
        convertMap.put(LocalDate.class, new LocalDateConverter());
        convertMap.put(LocalDateTime.class, new LocalDateTimeConverter());
        convertMap.put(OffsetDateTime.class, new OffsetDateTimeConverter());
        convertMap.put(byte[].class, new BytesConverter());
        converters = Collections.unmodifiableMap(convertMap);

        final List<ExtensionConverter<?>> extensionConverterList = new ArrayList<>();
        extensionConverterList.add(new ListExtensionConverter());
        extensionConverterList.add(new SetExtensionConverter());
        extensionConverterList.add(new ArrayExtensionConverter());

        extensionConverters = Collections.unmodifiableList(extensionConverterList);
    }

    @Override
    public Map<Class<?>, Converter<?>> getConverters() {
        return converters;
    }

    @Override
    public List<ExtensionConverter<?>> getExtensionConvertor() {
        return extensionConverters;
    }

    /**
     * 日付パターンを設定する。
     * 
     * @param patterns 日付パターン
     */
    public void setDatePatterns(List<String> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        HashMap<Class<?>, Converter<?>> convertMap = new HashMap<>(
                converters);
        convertMap.put(LocalDate.class, new LocalDateConverter(patterns));
        convertMap.put(LocalDateTime.class, new LocalDateTimeConverter(patterns));
        convertMap.put(OffsetDateTime.class, new OffsetDateTimeConverter(patterns));
        convertMap.put(Date.class, new DateConverter(patterns));
        convertMap.put(java.sql.Date.class, new SqlDateConverter(patterns));
        convertMap.put(Timestamp.class, new SqlTimestampConverter(patterns));

        StringConverter stringConverter = new StringConverter(patterns.get(0), null);
        Converter<?> converter = convertMap.get(String.class);
        if (converter instanceof StringConverter) {
            stringConverter = ((StringConverter) converter).merge(stringConverter);
        }
        convertMap.put(String.class, stringConverter);

        converters = Collections.unmodifiableMap(convertMap);
    }

    /**
     * 数値パターンを設定する。
     * 
     * @param patterns 数値パターン
     */
    public void setNumberPatterns(List<String> patterns) {
        if (patterns.isEmpty()) {
            return;
        }
        HashMap<Class<?>, Converter<?>> convertMap = new HashMap<>(
                converters);
        convertMap.put(Integer.class, new IntegerConverter(patterns));
        convertMap.put(int.class, new IntegerConverter(patterns));
        convertMap.put(Short.class, new ShortConverter(patterns));
        convertMap.put(short.class, new ShortConverter(patterns));
        convertMap.put(Long.class, new LongConverter(patterns));
        convertMap.put(long.class, new LongConverter(patterns));
        convertMap.put(BigDecimal.class, new BigDecimalConverter(patterns));

        StringConverter stringConverter = new StringConverter(null, patterns.get(0));
        Converter<?> converter = convertMap.get(String.class);
        if (converter instanceof StringConverter) {
            stringConverter = ((StringConverter) converter).merge(stringConverter);
        }
        convertMap.put(String.class, stringConverter);

        converters = Collections.unmodifiableMap(convertMap);
    }
}
