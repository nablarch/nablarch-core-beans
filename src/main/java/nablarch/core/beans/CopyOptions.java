package nablarch.core.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.WeakHashMap;
import java.util.stream.Collectors;

import nablarch.core.beans.converter.BigDecimalConverter;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.IntegerConverter;
import nablarch.core.beans.converter.LongConverter;
import nablarch.core.beans.converter.ShortConverter;
import nablarch.core.beans.converter.SqlDateConverter;
import nablarch.core.beans.converter.SqlTimestampConverter;
import nablarch.core.beans.converter.StringConverter;
import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * {@link BeanUtil#copy(Object, Object) Beanのコピー}で使用される設定をまとめたクラス。
 * 
 * <p>
 * 当クラスのインスタンスは{@link #options() options}メソッドを起点としたビルダーパターンで構築する。
 * </p>
 * 
 * <p>
 * 例えば次のコードは日付パターン{@code yyyy/MM/dd}を設定して、
 * さらにプロパティ{@code createdDate}に対しては{@code yyyy/MM/dd HH:mm}を設定している。
 * </p>
 * <pre>{@code
 * CopyOptions copyOptions = CopyOptions.options()
 *         .datePattern("yyyy/MM/dd")
 *         .datePatternByName("createdDate", "yyyy/MM/dd HH:mm")
 *         .build();
 * }</pre>
 * 
 * <p>
 * {@link Builder#datePattern(String) datePattern}メソッドと{@link Builder#numberPattern(String) numberPattern}は
 * 内部的には{@link Builder#converter(Class, Converter) converter}メソッドを呼び出している。
 * </p>
 * 
 * <p>
 * {@link Builder#datePattern(String) datePattern}メソッドは次のクラスに対する{@link Converter}を追加する。
 * </p>
 * <ul>
 * <li>{@link java.util.Date}</li>
 * <li>{@link java.sql.Date}</li>
 * <li>{@link java.sql.Timestamp}</li>
 * <li>{@link java.lang.String}</li>
 * </ul>
 * 
 * <p>
 * {@link Builder#numberPattern(String) numberPattern}メソッドは次のクラスに対する{@link Converter}を追加する。
 * </p>
 * <ul>
 * <li>{@code short}とそのラッパークラス</li>
 * <li>{@code int}とそのラッパークラス</li>
 * <li>{@code long}とそのラッパークラス</li>
 * <li>{@link java.math.BigDecimal}</li>
 * <li>{@link java.lang.String}</li>
 * </ul>
 * 
 * <p>
 * 同じクラスに対して{@link Builder#converter(Class, Converter) converter}メソッドが複数回呼び出されると、
 * 先に登録されたものが有効となる。
 * つまり次のコードで{@link java.util.Date}に対するフォーマットは{@code yyyy/MM/dd}が有効となり、
 * {@code CustomDateConverter}は無視される。
 * </p>
 * 
 * <pre>{@code
 * CopyOptions copyOptions = CopyOptions.options()
 *         .datePattern("yyyy/MM/dd")
 *         .converter(java.util.Date.class, new CustomDateConverter())
 *         .build();
 * }</pre>
 * 
 * @author Taichi Uragami
 *
 */
public final class CopyOptions {

    /** {@link CopyOption}アノテーションから構築される{@link CopyOptions}のキャッシュ */
    private static final WeakHashMap<Class<?>, CopyOptions> FROM_ANNOTATION_CACHE = new WeakHashMap<Class<?>, CopyOptions>();
    /** 空の{@link CopyOptions} */
    private static final CopyOptions EMPTY = options().build();
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
     * {@link #cloneForNestedObjectInCreateMapInner()}から呼び出されるコンストラクタ。
     * 
     * <p>
     * 深くネストされたオブジェクトがコピーされる場合、
     * {@link Collections#unmodifiableMap(Map)}および{@link Collections#unmodifiableCollection(Collection)}で多重にラップされることを避けるために、
     * このコンストラクタは定義されている。
     * </p>
     * 
     * <p>
     * <strong>このコンストラクタの呼び出し元は{@link #cloneForNestedObjectInCreateMapInner()}メソッドだけを想定している。
     * その他のメソッドからは呼び出してはいけない。</strong>
     * </p>
     * 
     * @param typedConverters クラスに紐づいたコンバーター
     * @param namedConverters プロパティ名とクラスに紐づいたコンバーター
     * @param excludesNull コピー元プロパティが{@code null}の場合にコピーしないかどうかを決定するフラグ
     * @param excludesProperties コピー対象外のプロパティ名
     */
    private CopyOptions(Map<Class<?>, Converter<?>> typedConverters,
            Map<String, Map<Class<?>, Converter<?>>> namedConverters, boolean excludesNull,
            Collection<String> excludesProperties) {
        this.typedConverters = typedConverters;
        this.namedConverters = namedConverters;
        this.excludesNull = excludesNull;
        this.excludesProperties = excludesProperties;
        this.includesProperties = Collections.emptyList();
    }

    /**
     * {@link BeanUtil}の{@code createMapInner}メソッドでネストしたプロパティのコピーをするため、
     * {@code createMapInner}メソッドを再帰的に呼び出す際に渡す{@link CopyOptions}を生成して返す。
     * 
     * <p>
     * 具体的には{@link #includesProperties}が空で他のフィールドは元の{@link CopyOptions}の値を引き継ぐ。
     * </p>
     * 
     * <p>
     * <strong>このメソッドの呼び出し元は{@link BeanUtil}の{@code createMapInner}メソッドだけを想定している。
     * その他のメソッドからは呼び出してはいけない。</strong>
     * </p>
     * 
     * @return {@link #includesProperties}以外を引き継いで作られた{@link CopyOptions}のインスタンス
     */
    CopyOptions cloneForNestedObjectInCreateMapInner() {
        return new CopyOptions(
                typedConverters,
                namedConverters,
                excludesNull,
                excludesProperties);
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
     * 空の{@link CopyOptions}を取得する。
     *  
     * @return 空の{@link CopyOptions}
     */
    public static CopyOptions empty() {
        return EMPTY;
    }

    /**
     * {@link CopyOption}アノテーションを読み取って構築された{@link CopyOptions}を取得する。
     * 
     * @param clazz アノテーションを読み取る対象のクラス
     * @return {@link CopyOption}アノテーションを読み取って構築された{@link CopyOptions}
     */
    public static CopyOptions fromAnnotation(Class<?> clazz) {
        synchronized (FROM_ANNOTATION_CACHE) {
            CopyOptions maybeCached = FROM_ANNOTATION_CACHE.get(clazz);
            if (maybeCached != null) {
                return maybeCached;
            }
        }
        CopyOptions.Builder builder = CopyOptions.options();
        try {
            Map<String, Field> fields = new HashMap<String, Field>();
            for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
                for (Field field : c.getDeclaredFields()) {
                    String name = field.getName();
                    if (fields.containsKey(name) == false) {
                        fields.put(name, field);
                    }
                }
            }
            BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
            for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
                String propertyName = pd.getName();
                Field field = fields.get(propertyName);
                if (field == null) {
                    continue;
                }
                CopyOption copyOption = field.getAnnotation(CopyOption.class);
                if (copyOption == null) {
                    continue;
                }
                if (copyOption.datePattern().length > 0) {
                    builder.datePatternsByName(propertyName,
                            Arrays.asList(copyOption.datePattern()));
                }
                if (copyOption.numberPattern().length > 0) {
                    builder.numberPatternsByName(propertyName,
                            Arrays.asList(copyOption.numberPattern()));
                }
            }
        } catch (IntrospectionException e) {
            throw new BeansException(e);
        }
        CopyOptions copyOptions = builder.build();
        synchronized (FROM_ANNOTATION_CACHE) {
            FROM_ANNOTATION_CACHE.put(clazz, copyOptions);
        }
        return copyOptions;
    }


    CopyOptions reduce(String propertyName) {
        Collection<String> tmpIncludeProperties = includesProperties.stream().map(pn -> pn.replace(propertyName + ".", "")).collect(Collectors.toCollection(HashSet::new));
        Collection<String> tmpExcludeProperties = excludesProperties.stream().map(pn -> pn.replace(propertyName + ".", "")).collect(Collectors.toCollection(HashSet::new));
        return new CopyOptions(
                typedConverters,
                namedConverters,
                excludesNull,
                tmpExcludeProperties,
                tmpIncludeProperties);
    }


    /**
     * 他の{@link CopyOptions}をマージする。
     * 
     * <p>
     * マージ処理は{@code this}をベースにして差分を{@code other}から持ってくる。
     * 例えば同一のプロパティに紐づいた{@link Converter}が{@code this}と{@code other}の両方にあった場合、
     * マージ後の{@link CopyOptions}には{@code this}が持つ{@link Converter}が残る。
     * </p>
     * 
     * @param other 他の{@link CopyOptions}インスタンス
     * @return マージされたインスタンス
     */
    public CopyOptions merge(CopyOptions other) {
        //this と other のどちらか片方が EMPTY の場合、マージは不要。
        //ただし this の性質を優先してマージする仕様のため excludesNull を比較している。
        //(excludesNull が異なる場合、単純に other を返せない)
        if (this == EMPTY && excludesNull == other.excludesNull) {
            return other;
        } else if (other == EMPTY) {
            return this;
        }
        return new CopyOptions(
                merge(typedConverters, other.typedConverters),
                merge(namedConverters, other.namedConverters),
                excludesNull,
                merge(excludesProperties, other.excludesProperties),
                merge(includesProperties, other.includesProperties));
    }

    /**
     * ふたつの{@link Map}をマージして作られた新しい{@link Map}を返す。
     * 
     * @param <K> キーの型
     * @param <V> 値の型
     * @param main ベースとなる{@link Map}
     * @param sub マージされる{@link Map}
     * @return マージされた{@link Map}
     */
    private static <K, V> Map<K, V> merge(Map<K, V> main, Map<K, V> sub) {
        //this と other のどちらか片方が empty の場合、マージは不要。
        if (main.isEmpty()) {
            return sub;
        } else if (sub.isEmpty()) {
            return main;
        }
        HashMap<K, V> merged = new HashMap<K, V>(main.size() + sub.size());
        merged.putAll(sub);
        merged.putAll(main);
        return merged;
    }

    /**
     * ふたつの{@link Collection}をマージして作られた新しい{@link Collection}を返す。
     * 
     * @param main ベースとなる{@link Collection}
     * @param sub マージされる{@link Collection}
     * @return マージされた{@link Collection}
     */
    private static Collection<String> merge(Collection<String> main, Collection<String> sub) {
        //this と other のどちらか片方が empty の場合、マージは不要。
        if (main.isEmpty()) {
            return sub;
        } else if (sub.isEmpty()) {
            return main;
        }
        HashSet<String> merged = new HashSet<String>(main.size() + sub.size());
        merged.addAll(main);
        merged.addAll(sub);
        return merged;
    }

    /**
     * 指定されたクラスに紐づいたコンバーターを保持しているかどうかを返す。
     * 
     * @param clazz クラス
     * @return 指定されたクラスに紐づいたコンバーターを保持していれば{@code true}
     */
    public boolean hasTypedConverter(Class<?> clazz) {
        return typedConverters.containsKey(clazz);
    }

    /**
     * 指定されたプロパティ名とクラスに紐づいたコンバーターを保持しているかどうかを判断して返す。
     * 
     * @param propertyName プロパティ名
     * @param clazz クラス
     * @return 指定されたプロパティ名とクラスに紐づいたコンバーターを保持していれば{@code true}
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
     * @return コピー元プロパティが{@code null}の場合にコピーしない場合は{@code true}
     */
    public boolean isExcludesNull() {
        return excludesNull;
    }

    /**
     * 指定されたプロパティがコピー対象かどうかを返す。
     * 
     * @param propertyName プロパティ名
     * @return コピー対象なら{@code true}
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
         * <p>
         * このメソッドは次のコードと等価である。
         * </p>
         * <pre>{@code
         * datePatterns(Collections.<String>singletonList(pattern))
         * }</pre>
         * 
         * @param pattern 日付パターン
         * @return 自分自身
         */
        public Builder datePattern(String pattern) {
            return datePatterns(Collections.singletonList(pattern));
        }

        /**
         * 複数の日付パターンを設定する。
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
         * <p>
         * このメソッドは次のコードと等価である。
         * </p>
         * <pre>{@code
         * datePatternsByName(propertyName, Collections.<String>singletonList(pattern))
         * }</pre>
         * 
         * @param propertyName 日付パターン適用対象のプロパティ名
         * @param pattern 日付パターン
         * @return 自分自身
         */
        public Builder datePatternByName(String propertyName, String pattern) {
            return datePatternsByName(propertyName, Collections.singletonList(pattern));
        }

        /**
         * プロパティを指定して複数の日付パターンを設定する。
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
         * <p>
         * このメソッドは次のコードと等価である。
         * </p>
         * <pre>{@code
         * numberPatterns(Collections.<String>singletonList(pattern))
         * }</pre>
         * 
         * @param pattern 数値パターン
         * @return 自分自身
         */
        public Builder numberPattern(String pattern) {
            return numberPatterns(Collections.singletonList(pattern));
        }

        /**
         * 複数の数値パターンを設定する。
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
         * <p>
         * このメソッドは次のコードと等価である。
         * </p>
         * <pre>{@code
         * numberPatternsByName(propertyName, Collections.<String>singletonList(pattern))
         * }</pre>
         * 
         * @param propertyName 数値パターン適用対象のプロパティ名
         * @param pattern 数値パターン
         * @return 自分自身
         */
        public Builder numberPatternByName(String propertyName, String pattern) {
            return numberPatternsByName(propertyName, Collections.singletonList(pattern));
        }

        /**
         * プロパティを指定して複数の数値パターンを設定する。
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

        /**
         * プロパティ名を指定して{@link #namedConverters}から{@link Map}を取得する。
         * 取得できない場合は新しい{@link Map}を生成して{@link #namedConverters}へ登録する。
         * 
         * @param propertyName プロパティ名
         * @return {@link #namedConverters}から取得された{@link Map}
         */
        private Map<Class<?>, Converter<?>> getOrCreateConverters(String propertyName) {
            Map<Class<?>, Converter<?>> converters = this.namedConverters.get(propertyName);
            if (converters == null) {
                converters = new HashMap<Class<?>, Converter<?>>();
                this.namedConverters.put(propertyName, converters);
            }
            return converters;
        }

        /**
         * 指定された{@link Map}に{@link Converter}を登録する。
         * 
         * <p>
         * 既に登録されている{@link Converter}があり、
         * それが{@link Mergeable}の実装クラスであり、
         * 渡された{@link Converter}と同じクラスであればマージした結果が登録される。
         * </p>
         * 
         * <p>
         * 既に登録されている{@link Converter}があり、
         * それが{@link Mergeable}の実装クラスでなければ何もせずメソッドを終了する。
         * </p>
         * 
         * @param converters 登録される{@link Map}
         * @param clazz {@link Map}のキーとなるクラス
         * @param converter {@link Map}の値となる{@link Converter}
         */
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static void addOrMergeConverter(Map<Class<?>, Converter<?>> converters,
                Class<?> clazz, Converter<?> converter) {
            Converter<?> newConverter = converter;
            Converter<?> registered = converters.get(clazz);
            if (registered != null) {
                if (registered instanceof Mergeable
                        && registered.getClass() == converter.getClass()) {
                    newConverter = ((Mergeable) registered).merge((Mergeable) converter);
                } else {
                    return;
                }
            }
            converters.put(clazz, newConverter);
        }

        /**
         * 指定された{@link Map}にもう一方の{@link Map}の内容を登録する。
         * 
         * @param converters ベースとなる{@link Map}
         * @param addMe マージされる{@link Map}
         */
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

        /**
         * {@link ConvertersProvider}インスタンスを取得する。
         * 
         * <p>
         * {@link SystemRepository}から取得を試みて、取得できればそれを返す。
         * 取得できなければ{@link #DEFAULT_CONVERTERS_PROVIDER}を返す。
         * </p>
         * 
         * @return {@link ConvertersProvider}インスタンス
         */
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
            converters.put(short.class, new ShortConverter(patterns));
            converters.put(int.class, new IntegerConverter(patterns));
            converters.put(long.class, new LongConverter(patterns));
            converters.put(Short.class, new ShortConverter(patterns));
            converters.put(Integer.class, new IntegerConverter(patterns));
            converters.put(Long.class, new LongConverter(patterns));
            converters.put(BigDecimal.class, new BigDecimalConverter(patterns));
            return converters;
        }
    }
}
