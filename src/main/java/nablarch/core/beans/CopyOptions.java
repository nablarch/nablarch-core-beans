package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.beans.converter.StringConverter;
import nablarch.core.util.annotation.Published;

public final class CopyOptions {

    private final Map<Class<?>, Converter<?>> typedConverters;
    private final Map<String, Map<Class<?>, Converter<?>>> namedConverters;
    private final boolean excludesNull;
    private final Collection<String> excludesProperties;
    private final Collection<String> includesProperties;

    private CopyOptions(
            Map<Class<?>, Converter<?>> typedConverters,
            Map<String, Map<Class<?>, Converter<?>>> namedConverters,
            boolean excludesNull,
            Collection<String> excludesProperties,
            Collection<String> includesProperties) {
        this.typedConverters = Collections.unmodifiableMap(typedConverters);
        this.namedConverters = Collections.unmodifiableMap(namedConverters);
        this.excludesNull = excludesNull;
        this.excludesProperties = Collections.unmodifiableCollection(excludesProperties);
        this.includesProperties = Collections.unmodifiableCollection(includesProperties);
    }

    @Published
    public static Builder options() {
        return new Builder();
    }

    public CopyOptions merge(CopyOptions other) {
        return new CopyOptions(
                merge(typedConverters, other.typedConverters),
                merge(namedConverters, other.namedConverters),
                excludesNull,
                merge(excludesProperties, other.excludesProperties),
                merge(includesProperties, other.includesProperties));
    }

    private static <K, V> Map<K, V> merge(Map<K, V> main, Map<K, V> sub) {
        HashMap<K, V> merged = new HashMap<K, V>(main);
        for (K key : sub.keySet()) {
            if (merged.containsKey(key) == false) {
                merged.put(key, sub.get(key));
            }
        }
        return merged;
    }

    private static Collection<String> merge(Collection<String> main, Collection<String> sub) {
        HashSet<String> merged = new HashSet<String>();
        merged.addAll(main);
        merged.addAll(sub);
        return merged;
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

    public boolean isExcludesNull() {
        return excludesNull;
    }

    public boolean isTargetProperty(String propertyName) {
        if (excludesProperties.contains(propertyName)) {
            return false;
        }
        if (includesProperties.isEmpty() == false
                && includesProperties.contains(propertyName) == false) {
            return false;
        }
        return true;
    }

    public static class Builder {

        private final Map<Class<?>, Converter<?>> typedConverters = new HashMap<Class<?>, Converter<?>>();
        private final Map<String, Map<Class<?>, Converter<?>>> namedConverters = new HashMap<String, Map<Class<?>, Converter<?>>>();
        private boolean excludesNull;
        private final Collection<String> excludesProperties = new HashSet<String>();
        private final Collection<String> includesProperties = new HashSet<String>();

        @Published
        public Builder datePattern(String pattern) {
            return datePatterns(Collections.singletonList(pattern));
        }

        @Published
        public Builder datePatterns(List<String> patterns) {
            //FIXME nablarch-jsr310-adaptor
            converter(String.class, new StringConverter(patterns.get(0), null));
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
            converterByName(propertyName, String.class, new StringConverter(patterns.get(0), null));
            converterByName(propertyName, java.util.Date.class, new DateConverter(patterns));
            converterByName(propertyName, java.sql.Date.class, new SqlDateConverter(patterns));
            converterByName(propertyName, Timestamp.class, new SqlTimestampConverter(patterns));
            return this;
        }

        @Published
        public Builder numberPattern(String pattern) {
            return numberPatterns(Collections.singletonList(pattern));
        }

        @Published
        public Builder numberPatterns(List<String> patterns) {
            converter(String.class, new StringConverter(null, patterns.get(0)));
            converter(Integer.class, new IntegerConverter(patterns));
            converter(Long.class, new LongConverter(patterns));
            converter(BigDecimal.class, new BigDecimalConverter(patterns));
            return this;
        }

        @Published
        public Builder numberPatternByName(String propertyName, String pattern) {
            return numberPatternsByName(propertyName, Collections.singletonList(pattern));
        }

        @Published
        public Builder numberPatternsByName(String propertyName, List<String> patterns) {
            converterByName(propertyName, String.class, new StringConverter(null, patterns.get(0)));
            converterByName(propertyName, Integer.class, new IntegerConverter(patterns));
            converterByName(propertyName, Long.class, new LongConverter(patterns));
            converterByName(propertyName, BigDecimal.class, new BigDecimalConverter(patterns));
            return this;
        }

        @Published(tag = "architect")
        public <T> Builder converter(Class<T> clazz, Converter<T> converter) {
            addOrMergeConverter(typedConverters, clazz, converter);
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
            addOrMergeConverter(converters, clazz, converter);
            return this;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static <T> void addOrMergeConverter(Map<Class<?>, Converter<?>> converters,
                Class<T> clazz, Converter<T> converter) {
            Converter<T> newConverter = converter;
            Converter<?> registered = converters.get(clazz);
            if (registered != null) {
                if (registered instanceof Mergeable
                        && registered.getClass() == converter.getClass()) {
                    newConverter = ((Mergeable) registered).merge((Mergeable) converter);
                }
            }
            converters.put(clazz, newConverter);
        }

        public Builder excludesNull() {
            this.excludesNull = true;
            return this;
        }

        public Builder excludes(String... properties) {
            for (String property : properties) {
                this.excludesProperties.add(property);
            }
            return this;
        }

        public Builder includes(String... properties) {
            for (String property : properties) {
                this.includesProperties.add(property);
            }
            return this;
        }

        @Published
        public CopyOptions build() {
            return new CopyOptions(typedConverters, namedConverters, excludesNull,
                    excludesProperties, includesProperties);
        }
    }
}
