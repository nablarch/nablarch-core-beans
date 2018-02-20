package nablarch.core.beans;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.beans.converter.StringConverter;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * {@link BeanUtil#copy(Object, Object) Beanのコピー}で使用される設定をまとめたクラス。
 * 
 * @author Taichi Uragami
 *
 */
public final class CopyOptions {

    /** クラスに紐づいたコンバーター */
    private final Map<Class<?>, Converter<?>> typedConverters;
    /** プロパティ名とクラスに紐づいたコンバーター */
    private final Map<String, Map<Class<?>, Converter<?>>> namedConverters;
    /** コピー元プロパティが{@code null}の場合にコピーしないかどうかを決定するフラグ */
    private final boolean excludesNull;
    /** コピー対象外のプロパティ名 */
    private final Collection<String> excludesProperties;
    /** コピー対象のプロパティ名 */
    private final Collection<String> includesProperties;

    /**
     * 当クラスは使用者が明示的にコンストラクタを呼び出すのではなく、
     * {@link Builder#build()}によってインスタンス化されることを想定しているため、
     * コンストラクタの可視性は{@code private}としている。
     * 
     * @param typedConverters クラスに紐づいたコンバーター
     * @param namedConverters プロパティ名とクラスに紐づいたコンバーター
     * @param excludesNull コピー元プロパティが{@code null}の場合にコピーしないかどうかを決定するフラグ
     * @param excludesProperties コピー対象外のプロパティ名
     * @param includesProperties コピー対象のプロパティ名
     */
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

    /**
     * ビルダーを取得する。
     * 
     * @return ビルダー
     */
    @Published
    public static Builder options() {
        return new Builder();
    }

    /**
     * 他の{@link CopyOptions}をマージする。
     * 
     * @param other 他の{@link CopyOptions}インスタンス
     * @return マージされたインスタンス
     */
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

    /**
     * 指定されたクラスに紐づいたコンバーターを保持しているかどうかを返す。
     * 
     * @param clazz クラス
     * @return 指定されたクラスに紐づいたコンバーターを保持していれば{@link true}
     */
    public boolean hasTypedConverter(Class<?> clazz) {
        return typedConverters.containsKey(clazz);
    }

    /**
     * 指定されたプロパティ名とクラスに紐づいたコンバーターを保持しているかどうかを判断して返す。
     * 
     * @param propertyName プロパティ名
     * @param clazz クラス
     * @return 指定されたプロパティ名とクラスに紐づいたコンバーターを保持していれば{@link true}
     */
    public boolean hasNamedConverter(String propertyName, Class<?> clazz) {
        return namedConverters.containsKey(propertyName)
                && namedConverters.get(propertyName).containsKey(clazz);
    }

    /**
     * クラスに紐づいたコンバーターを使用して値を変換する。
     * 
     * <p>
     * クラスに紐づいたコンバーターが見つからなければ{@link IllegalArgumentException}をスローする。
     * </p>
     * 
     * @param clazz クラス
     * @param value 変換前の値
     * @return 変換後の値
     */
    public Object convertByType(Class<?> clazz, Object value) {
        Converter<?> converter = typedConverters.get(clazz);
        if (converter == null) {
            throw new IllegalArgumentException(
                    "Converter is not found for " + clazz.getName() + ".");
        }
        Object converted = converter.convert(value);
        return converted;
    }

    /**
     * プロパティ名とクラスに紐づいたコンバーターを使用して値を変換する。
     * 
     * <p>
     * プロパティ名とクラスに紐づいたコンバーターが見つからなければ{@link IllegalArgumentException}をスローする。
     * </p>
     * 
     * @param propertyName プロパティ名
     * @param clazz クラス
     * @param value 変換前の値
     * @return 変換後の値
     */
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

    /**
     * コピー元プロパティが{@code null}の場合にコピーしないかどうかを返す。
     * 
     * @return コピー元プロパティが{@code null}の場合にコピーしない場合は{@link true}
     */
    public boolean isExcludesNull() {
        return excludesNull;
    }

    /**
     * 指定されたプロパティがコピー対象かどうかを返す。
     * 
     * @param propertyName プロパティ名
     * @return コピー対象なら{@link true}
     */
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

    /**
     * {@link CopyOptions}のビルダー。
     * 
     * <p>
     * {@link CopyOptions#options()}を起点としてメソッドチェーンでコピーの設定が行えるようにするためのクラス。
     * </p>
     *
     */
    @Published
    public static class Builder {

        /** {@link ConvertersProvider}をリポジトリから取得する際に使用する名前 */
        private static final String CONVERTERS_PROVIDER_NAME = "convertersProvider";
        /** デフォルトの{@link ConvertersProvider} */
        private static final ConvertersProvider DEFAULT_CONVERTERS_PROVIDER = new DefaultConvertersProvider();
        /** クラスに紐づいたコンバーター */
        private final Map<Class<?>, Converter<?>> typedConverters = new HashMap<Class<?>, Converter<?>>();
        /** プロパティ名とクラスに紐づいたコンバーター */
        private final Map<String, Map<Class<?>, Converter<?>>> namedConverters = new HashMap<String, Map<Class<?>, Converter<?>>>();
        /** コピー元プロパティが{@code null}の場合にコピーしないかどうかを決定するフラグ */
        private boolean excludesNull;
        /** コピー対象外のプロパティ名 */
        private final Collection<String> excludesProperties = new HashSet<String>();
        /** コピー対象のプロパティ名 */
        private final Collection<String> includesProperties = new HashSet<String>();

        /**
         * {@link CopyOptions#options()}でインスタンス化するためコンストラクタをprivateに設定している。
         */
        private Builder() {
            //nop
        }

        /**
         * 日付パターンを設定する。
         * 
         * @param pattern 日付パターン
         * @return 自分自身
         */
        public Builder datePattern(String pattern) {
            return datePatterns(Collections.singletonList(pattern));
        }

        /**
         * 日付パターンを設定する。
         * 
         * @param patterns 日付パターン
         * @return 自分自身
         */
        public Builder datePatterns(List<String> patterns) {
            addOrMergeConverters(typedConverters,
                    getConvertersProvider().provideDateConverters(patterns));
            return this;
        }

        /**
         * プロパティを指定して日付パターンを設定する。
         * 
         * @param propertyName 日付パターン適用対象のプロパティ名
         * @param pattern 日付パターン
         * @return 自分自身
         */
        public Builder datePatternByName(String propertyName, String pattern) {
            return datePatternsByName(propertyName, Collections.singletonList(pattern));
        }

        /**
         * プロパティを指定して日付パターンを設定する。
         * 
         * @param propertyName 日付パターン適用対象のプロパティ名
         * @param patterns 日付パターン
         * @return 自分自身
         */
        public Builder datePatternsByName(String propertyName, List<String> patterns) {
            addOrMergeConverters(getOrCreateConverters(propertyName),
                    getConvertersProvider().provideDateConverters(patterns));
            return this;
        }

        /**
         * 数値パターンを設定する。
         * 
         * @param pattern 数値パターン
         * @return 自分自身
         */
        public Builder numberPattern(String pattern) {
            return numberPatterns(Collections.singletonList(pattern));
        }

        /**
         * 数値パターンを設定する。
         * 
         * @param patterns 数値パターン
         * @return 自分自身
         */
        public Builder numberPatterns(List<String> patterns) {
            addOrMergeConverters(typedConverters,
                    getConvertersProvider().provideNumberConverters(patterns));
            return this;
        }

        /**
         * プロパティを指定して数値パターンを設定する。
         * 
         * @param propertyName 数値パターン適用対象のプロパティ名
         * @param pattern 数値パターン
         * @return 自分自身
         */
        public Builder numberPatternByName(String propertyName, String pattern) {
            return numberPatternsByName(propertyName, Collections.singletonList(pattern));
        }

        /**
         * プロパティを指定して数値パターンを設定する。
         * 
         * @param propertyName 数値パターン適用対象のプロパティ名
         * @param patterns 数値パターン
         * @return 自分自身
         */
        public Builder numberPatternsByName(String propertyName, List<String> patterns) {
            addOrMergeConverters(getOrCreateConverters(propertyName),
                    getConvertersProvider().provideNumberConverters(patterns));
            return this;
        }

        /**
         * クラスに対応するコンバーターを設定する。
         * 
         * @param clazz コンバーター適用対象のクラス
         * @param converter コンバーター
         * @return 自分自身
         */
        public <T> Builder converter(Class<T> clazz, Converter<T> converter) {
            addOrMergeConverter(typedConverters, clazz, converter);
            return this;
        }

        /**
         * プロパティを指定してクラスに対応するコンバーターを設定する。
         * 
         * @param propertyName コンバーター適用対象のプロパティ名
         * @param clazz コンバーター適用対象のクラス
         * @param converter コンバーター
         * @return 自分自身
         */
        public <T> Builder converterByName(String propertyName, Class<T> clazz,
                Converter<T> converter) {
            Map<Class<?>, Converter<?>> converters = getOrCreateConverters(propertyName);
            addOrMergeConverter(converters, clazz, converter);
            return this;
        }

        private Map<Class<?>, Converter<?>> getOrCreateConverters(String propertyName) {
            Map<Class<?>, Converter<?>> converters = this.namedConverters.get(propertyName);
            if (converters == null) {
                converters = new HashMap<Class<?>, Converter<?>>();
                this.namedConverters.put(propertyName, converters);
            }
            return converters;
        }

        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static void addOrMergeConverter(Map<Class<?>, Converter<?>> converters,
                Class<?> clazz, Converter<?> converter) {
            Converter<?> newConverter = converter;
            Converter<?> registered = converters.get(clazz);
            if (registered != null) {
                if (registered instanceof Mergeable
                        && registered.getClass() == converter.getClass()) {
                    newConverter = ((Mergeable) registered).merge((Mergeable) converter);
                }
            }
            converters.put(clazz, newConverter);
        }

        private static void addOrMergeConverters(Map<Class<?>, Converter<?>> converters,
                Map<Class<?>, Converter<?>> addMe) {
            for (Entry<Class<?>, Converter<?>> entry : addMe.entrySet()) {
                Class<?> clazz = entry.getKey();
                Converter<?> converter = entry.getValue();
                addOrMergeConverter(converters, clazz, converter);
            }
        }

        /**
         * コピー元のプロパティが{@code null}の場合はコピーしないよう設定する。
         * 
         * @return 自分自身
         */
        public Builder excludesNull() {
            this.excludesNull = true;
            return this;
        }

        /**
         * 指定されたプロパティをコピー対象外に設定する。
         * 
         * @param properties コピー対象外のプロパティ名
         * @return 自分自身
         */
        public Builder excludes(String... properties) {
            for (String property : properties) {
                this.excludesProperties.add(property);
            }
            return this;
        }

        /**
         * 指定されたプロパティをコピー対象に設定する。
         * 
         * @param properties コピー対象のプロパティ名
         * @return 自分自身
         */
        public Builder includes(String... properties) {
            for (String property : properties) {
                this.includesProperties.add(property);
            }
            return this;
        }

        /**
         * {@link CopyOptions}を構築する。
         * 
         * @return {@link CopyOptions}のインスタンス
         */
        public CopyOptions build() {
            return new CopyOptions(typedConverters, namedConverters, excludesNull,
                    excludesProperties, includesProperties);
        }

        private static ConvertersProvider getConvertersProvider() {
            ConvertersProvider provider = SystemRepository.get(CONVERTERS_PROVIDER_NAME);
            if (provider != null) {
                return provider;
            }
            return DEFAULT_CONVERTERS_PROVIDER;
        }
    }

    /**
     * 日付パターン・数値パターンをもとに{@link Converter}を提供するインターフェース。
     *
     */
    public interface ConvertersProvider {

        /**
         * 日付パターンをもとに{@link Converter}を提供する。
         * 
         * @param patterns 日付パターン
         * @return 日付パターンをもとにした{@link Converter}のマップ
         */
        Map<Class<?>, Converter<?>> provideDateConverters(List<String> patterns);

        /**
         * 数値パターンをもとに{@link Converter}を提供する。
         * 
         * @param patterns 数値パターン
         * @return 数値パターンをもとにした{@link Converter}のマップ
         */
        Map<Class<?>, Converter<?>> provideNumberConverters(List<String> patterns);
    }

    /**
     * {@link ConvertersProvider}のデフォルト実装。
     *
     */
    public static class DefaultConvertersProvider implements ConvertersProvider {

        @Override
        public Map<Class<?>, Converter<?>> provideDateConverters(List<String> patterns) {
            Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();
            converters.put(String.class, new StringConverter(patterns.get(0), null));
            converters.put(java.util.Date.class, new DateConverter(patterns));
            converters.put(java.sql.Date.class, new SqlDateConverter(patterns));
            converters.put(Timestamp.class, new SqlTimestampConverter(patterns));
            return converters;
        }

        @Override
        public Map<Class<?>, Converter<?>> provideNumberConverters(List<String> patterns) {
            Map<Class<?>, Converter<?>> converters = new HashMap<Class<?>, Converter<?>>();
            converters.put(String.class, new StringConverter(null, patterns.get(0)));
            converters.put(Integer.class, new IntegerConverter(patterns));
            converters.put(Long.class, new LongConverter(patterns));
            converters.put(BigDecimal.class, new BigDecimalConverter(patterns));
            return converters;
        }
    }
}
