package nablarch.core.beans;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.util.annotation.Published;

public final class CopyOptions {

    private final Map<Class<?>, Converter<?>> typedConverters;
    private final Map<String, Map<Class<?>, Converter<?>>> namedConverters;

    private CopyOptions(
            Map<Class<?>, Converter<?>> typedConverters,
            Map<String, Map<Class<?>, Converter<?>>> namedConverters) {
        this.typedConverters = Collections.unmodifiableMap(typedConverters);
        this.namedConverters = Collections.unmodifiableMap(namedConverters);
    }

    @Published
    public static Builder options() {
        return new Builder();
    }

    public boolean hasTypedConverter(Class<?> clazz) {
        return typedConverters.containsKey(clazz);
    }

    public boolean hasNamedConverter(String propertyName, Class<?> clazz) {
        return namedConverters.containsKey(propertyName)
                && namedConverters.get(propertyName).containsKey(clazz);
    }

    public Object convertByType(Class<?> clazz, Object value) {
        Converter<?> converter = typedConverters.get(clazz);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Converter is not found for " + clazz.getName() + ".");
        }
        Object converted = converter.convert(value);
        return converted;
    }

    public Object convertByName(String propertyName, Class<?> clazz, Object value) {
        Map<Class<?>, Converter<?>> converters = namedConverters.get(propertyName);
        if (converters == null) {
            throw new IllegalArgumentException(
                    "Converter is not found named by '" + propertyName + "'.");
        }
        Converter<?> converter = converters.get(clazz);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Converter is not found named by '" + propertyName + "'.");
        }
        Object converted = converter.convert(value);
        return converted;
    }

    public static class Builder {

        private final Map<Class<?>, Converter<?>> typedConverters = new HashMap<Class<?>, Converter<?>>();
        private final Map<String, Map<Class<?>, Converter<?>>> namedConverters = new HashMap<String, Map<Class<?>, Converter<?>>>();

        @Published
        public Builder datePattern(String pattern) {
            return datePatterns(Collections.singletonList(pattern));
        }

        @Published
        public Builder datePatterns(List<String> patterns) {
            //FIXME nablarch-jsr310-adaptor
            converter(java.util.Date.class, new DateConverter(patterns));
            converter(java.sql.Date.class, new SqlDateConverter(patterns));
            converter(Timestamp.class, new SqlTimestampConverter(patterns));
            return this;
        }

        @Published
        public Builder datePatternByName(String propertyName, String pattern) {
            return datePatternsByName(propertyName, Collections.singletonList(pattern));
        }

        @Published
        public Builder datePatternsByName(String propertyName, List<String> patterns) {
            //FIXME nablarch-jsr310-adaptor
            converterByName(propertyName, java.util.Date.class, new DateConverter(patterns));
            converterByName(propertyName, java.sql.Date.class, new SqlDateConverter(patterns));
            converterByName(propertyName, Timestamp.class, new SqlTimestampConverter(patterns));
            return this;
        }

        @Published(tag = "architect")
        public <T> Builder converter(Class<T> clazz, Converter<T> converter) {
            this.typedConverters.put(clazz, converter);
            return this;
        }

        @Published(tag = "architect")
        public <T> Builder converterByName(String propertyName, Class<T> clazz,
                Converter<T> converter) {
            Map<Class<?>, Converter<?>> converters = this.namedConverters.get(propertyName);
            if (converters == null) {
                converters = new HashMap<Class<?>, Converter<?>>();
                this.namedConverters.put(propertyName, converters);
            }
            converters.put(clazz, converter);
            return this;
        }

        @Published
        public CopyOptions build() {
            return new CopyOptions(typedConverters, namedConverters);
        }
    }
}
