package nablarch.core.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.beans.converter.DateConverter;
import nablarch.core.util.annotation.Published;

public final class CopyOptions {

    private final Map<Class<?>, Converter<?>> typedConverters;
    private final Map<String, Converter<?>> namedConverters;

    private CopyOptions(
            Map<Class<?>, Converter<?>> typedConverters,
            Map<String, Converter<?>> namedConverters) {
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

    public boolean hasNamedConverter(String propertyName) {
        return namedConverters.containsKey(propertyName);
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

    public Object convertByName(String propertyName, Object value) {
        Converter<?> converter = namedConverters.get(propertyName);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Converter is not found named by '" + propertyName + "'.");
        }
        Object converted = converter.convert(value);
        return converted;
    }

    public static class Builder {

        private final Map<Class<?>, Converter<?>> typedConverters = new HashMap<Class<?>, Converter<?>>();
        private final Map<String, Converter<?>> namedConverters = new HashMap<String, Converter<?>>();

        @Published
        public Builder datePattern(String pattern) {
            return datePatterns(Collections.singletonList(pattern));
        }

        @Published
        public Builder datePatterns(List<String> patterns) {
            return converter(java.util.Date.class, new DateConverter(patterns));
        }

        @Published
        public Builder datePatternByName(String propertyName, String pattern) {
            return datePatternsByName(propertyName, Collections.singletonList(pattern));
        }

        @Published
        public Builder datePatternsByName(String propertyName, List<String> patterns) {
            return converterByName(propertyName, new DateConverter(patterns));
        }

        @Published(tag = "architect")
        public <T> Builder converter(Class<T> clazz, Converter<T> converter) {
            this.typedConverters.put(clazz, converter);
            return this;
        }

        @Published(tag = "architect")
        public Builder converterByName(String propertyName, Converter<?> converter) {
            this.namedConverters.put(propertyName, converter);
            return this;
        }

        @Published
        public CopyOptions build() {
            return new CopyOptions(typedConverters, namedConverters);
        }
    }
}
