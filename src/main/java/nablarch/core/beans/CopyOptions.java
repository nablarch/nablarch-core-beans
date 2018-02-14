package nablarch.core.beans;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.beans.converter.DateConverter;
import nablarch.core.util.annotation.Published;

public final class CopyOptions {

    final Map<String, Converter<?>> converters;

    private CopyOptions(Map<String, Converter<?>> converters) {
        this.converters = Collections.unmodifiableMap(converters);
    }

    @Published
    public static Builder options() {
        return new Builder();
    }

    public boolean hasConverter(String propertyName) {
        return converters.containsKey(propertyName);
    }

    public Object convert(String propertyName, Object value) {
        Converter<?> converter = converters.get(propertyName);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Converter is not found named by '" + propertyName + "'.");
        }
        Object converted = converter.convert(value);
        return converted;
    }

    public static class Builder {

        private final Map<String, Converter<?>> converters = new HashMap<String, Converter<?>>();

        @Published
        public Builder datePattern(String propertyName, String... patterns) {
            return converter(propertyName, new DateConverter(patterns));
        }

        @Published(tag = "architect")
        public Builder converter(String propertyName, Converter<?> converter) {
            this.converters.put(propertyName, converter);
            return this;
        }

        @Published
        public CopyOptions build() {
            return new CopyOptions(converters);
        }
    }
}
