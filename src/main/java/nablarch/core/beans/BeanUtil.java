package nablarch.core.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;

/**
 * JavaBeansに関する操作をまとめたユーティリティクラス。
 *
 * @author kawasima
 * @author tajima
 */
@Published
public final class BeanUtil {
    /**
     * 本クラスはインスタンスを生成しない。
     */
    private BeanUtil() {
    }

    /**
     * 指定したクラスに属する全てのプロパティの {@link PropertyDescriptor} を取得する。
     * ただし、classプロパティは取得対象外となる。
     *
     * @param beanClass プロパティを取得したいクラス
     * @return PropertyDescriptor[] 全てのプロパティの {@link PropertyDescriptor}
     * @throws BeansException プロパティの取得に失敗した場合。
     */
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> beanClass) {
        return PropertyDescriptors.get(beanClass).array;
    }

    /**
     * 指定したクラスから、特定のプロパティの{@link PropertyDescriptor} を取得する。<br/>
     *
     * @param beanClass プロパティを取得したいクラス
     * @param propertyName 取得したいプロパティ名
     * @return PropertyDescriptor 取得したプロパティ
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     */
    public static PropertyDescriptor getPropertyDescriptor(final Class<?> beanClass, final String propertyName) {
        return PropertyDescriptors.get(beanClass).getPropertyDescriptor(propertyName);
    }

    /**
     * 指定したオブジェクトから、特定のプロパティの値を取得する。
     * <p/>
     * {@code propertyName}には、{@code bean}のトップレベル要素のみ指定可能である。
     * <pre>
     *     {@code
     *     // ----------サンプルで使用するBean----------
     *     public class SampleBean {
     *         private String stringProp;
     *         private List<String> listProp;
     *         private String[] arrayProp;
     *         private NestedBean nestedBean;
     *         // setter及びgetterは省略
     *     }
     *
     *     public class NestedBean {
     *         private String nestedStringProp;
     *         // setter及びgetterは省略
     *     }
     *
     *     // ----------実装例----------
     *     // 問題のないコード1
     *     String stringProp = BeanUtil.getProperty(sampleBean, "stringProp");
     *
     *     // 問題のないコード2
     *     String stringProp = BeanUtil.getProperty(sampleBean.nestedBean, "nestedStringProp");
     *
     *     // 以下のコードは動作しない
     *     String nestedStringProp = BeanUtil.getProperty(sampleBean, "nestedBean.nestedStringProp");
     *
     *     }
     * </pre>
     *
     * @param bean プロパティの値を取得したいBeanオブジェクト
     * @param propertyName 取得したいプロパティ名
     * @return Object オブジェクトから取得したプロパティの値
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     */
    public static Object getProperty(final Object bean, final String propertyName) {
        return getProperty(bean, propertyName, null);
    }

    /**
     * 指定したオブジェクトのプロパティの値を、指定した型に変換して取得する。
     * </p>
     * 型変換の仕様は{@link ConversionUtil}を参照。
     * <p/>
     * {@code propertyName}の指定方法については{@link #getProperty(Object, String)}を参照。
     *
     * @param bean プロパティの値を取得したいBeanオブジェクト
     * @param propertyName 取得したいプロパティの名称
     * @param type 変換したい型 (nullを指定した場合は変換を行わず、プロパティの値をそのまま返す。)
     * @return Object 取得したプロパティを{@code type}に変換したオブジェクト
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     */
    public static Object getProperty(final Object bean, final String propertyName, final Class<?> type) {
        try {
            final PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
            final Method getter = pd.getReadMethod();
            Object value = getter.invoke(bean);
            if (type != null) {
                value = ConversionUtil.convert(type, value);
            }
            return value;
        } catch (Exception e) {
            throw new BeansException(e);
        }
    }

    /**
     * プロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param propertyValue プロパティに登録する値
     * @throws BeansException インスタンス生成に失敗した場合
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    private static void setProperty(Object bean, PropertyExpression expression, Object propertyValue) {
        if (expression.isSimpleProperty() && expression.isNode()) {
            setPropertyValue(bean, expression.getRoot(), propertyValue);
        } else if(expression.isListOrArray()) {
            PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), expression.getListPropertyName());
            Class<?> propertyType = pd.getPropertyType();
            if (propertyType.isArray()) {
                setArrayProperty(bean, expression, propertyValue, pd);
            } else if (List.class.isAssignableFrom(propertyType)) {
                setListProperty(bean, expression, propertyValue, pd);
            } else {
                throw new BeansException("property type must be List or Array.");
            }
        } else {
            setObjectProperty(bean, expression, propertyValue);
        }
    }

    /**
     * {@link Object}のプロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param propertyValue プロパティに設定する値
     */
    private static void setObjectProperty(Object bean, PropertyExpression expression, Object propertyValue) {
        String propertyName = expression.getRoot();
        Object nested = getProperty(bean, propertyName);
        if (nested == null) {
            PropertyDescriptor pd = getPropertyDescriptor(bean.getClass(), propertyName);
            nested = createInstance(pd.getPropertyType());
            setPropertyValue(bean, pd, nested, CopyOptions.empty());
        }
        setProperty(nested, expression.rest(), propertyValue);
    }

    /**
     * {@link List}のプロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param propertyValue プロパティに設定する値
     * @param pd プロパティに対する{@link PropertyDescriptor}
     */
    private static void setListProperty(Object bean, PropertyExpression expression, Object propertyValue, PropertyDescriptor pd) {

        String propertyName = expression.getListPropertyName();

        List nested = (List) getProperty(bean, propertyName);
        if (nested == null) {
            nested = new ArrayList();
        }

        int index = expression.getListIndex();
        if (index >= nested.size()) {
            for (int i = nested.size(); i <= index; i++) {
                // 間を埋める。
                nested.add(null);
            }
        }

        Class<?> genericType = getGenericType(bean, propertyName, pd);
        if (expression.isNode()) {
            // プリミティブ型の場合
            nested.set(index, ConversionUtil.convert(genericType, propertyValue));
        } else {
            // オブジェクト型の場合
            Object obj = nested.get(index);
            if (obj == null) {
                obj = createInstance(genericType);
                nested.set(index, obj);
            }
            setProperty(obj, expression.rest(), propertyValue);
        }

        setPropertyValue(bean, expression.getListPropertyName(), nested);
    }

    /**
     * 配列のプロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param propertyValue プロパティに設定する値
     * @param pd プロパティに対する{@link PropertyDescriptor}
     */
    private static void setArrayProperty(Object bean, PropertyExpression expression, Object propertyValue, PropertyDescriptor pd) {

        Class<?> componentType = pd.getPropertyType().getComponentType();
        String propertyName = expression.getListPropertyName();
        Object array = (Object) getProperty(bean, propertyName);
        if (array == null) {
            // 初期作成
            array = Array.newInstance(componentType, expression.getListIndex() + 1);
        } else if (Array.getLength(array) <= expression.getListIndex()) {
            // 長さが足りない場合、詰めなおす
            Object old = array;
            array = Array.newInstance(componentType, expression.getListIndex() + 1);
            System.arraycopy(old, 0, array, 0, Array.getLength(old));
        }

        int index = expression.getListIndex();
        if (expression.isNode()) {
            // プリミティブ型の場合
            Array.set(array, index, ConversionUtil.convert(componentType, propertyValue));
        } else {
            // オブジェクト型の場合
            Object obj = Array.get(array, index);
            if (obj == null) {
                obj = createInstance(componentType);
                Array.set(array, index, obj);
            }
            setProperty(obj, expression.rest(), propertyValue);
        }

        setPropertyValue(bean, propertyName, array);
    }

    /**
     * プロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param propertyName 値を設定するプロパティ名
     * @param propertyValue プロパティに設定する値
     */
    private static void setPropertyValue(final Object bean, final String propertyName, final Object propertyValue) {
        setPropertyValue(bean, getPropertyDescriptor(bean.getClass(), propertyName), propertyValue, CopyOptions.empty());
    }

    /**
     * プロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param pd 値を設定するプロパティのプロパティディスクリプタ
     * @param propertyValue プロパティに設定する値
     * @param copyOptions コピーの設定
     */
    private static void setPropertyValue(final Object bean, final PropertyDescriptor pd,
            final Object propertyValue, final CopyOptions copyOptions) {
        try {
            final Method setter = pd.getWriteMethod();
            if (setter == null) {
                return;
            }
            final Object value;
            Class<?> clazz = pd.getPropertyType();
            String propertyName = pd.getName();
            if (copyOptions.hasNamedConverter(propertyName, clazz)) {
                value = copyOptions.convertByName(propertyName, clazz, propertyValue);
            } else if (copyOptions.hasTypedConverter(clazz)) {
                value = copyOptions.convertByType(clazz, propertyValue);
            } else {
                value = ConversionUtil.convert(clazz, propertyValue);
            }
            setter.invoke(bean, value);
        } catch (Exception e) {
            throw new BeansException(e);
        }
    }

    /**
     * リスト、配列の要素の型を取得する.
     *
     * @param bean Beanオブジェクト
     * @param propertyName プロパティ名
     * @param pd プロパティディスクリプタ
     * @return リスト、配列の要素の型
     */
    private static Class<?> getGenericType(Object bean, String propertyName, PropertyDescriptor pd) {
        Method getter = pd.getReadMethod();
        Type type = getter.getGenericReturnType();
        if (!(type instanceof ParameterizedType)) {
            // Generics でない場合。
            throw new BeansException("must set generics type for property. class: "
                    + bean.getClass() + " property: " + propertyName);
        }
        ParameterizedType genericTypeParameter = (ParameterizedType) type;
        Class<?> genericType = null;
        genericType = (Class<?>) genericTypeParameter.getActualTypeArguments()[0];
        return genericType;
    }

    /**
     * 指定したオブジェクトのプロパティに値を登録する。
     * <p/>
     * 対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * {@code propertyValue}がnullの場合は、例外の送出やログ出力は行わずに、対象プロパティの値はnullになる。
     * <p/>
     * プロパティの指定方法<br/>
     *     {@code propertyName}にはプロパティ名を指定する。
     *     List型・配列型のプロパティでは、"プロパティ名[インデックス]"という形式で要素番号を指定して値を登録できる。
     *     ネストしたプロパティを指定することも可能である。ネストの深さに制限はない。
     *     ネストの親となるプロパティがnullである場合は、デフォルトコンストラクタを起動し
     *     インスタンスを生成してから値を格納する。
     * 実装例<br/>
     * <pre>
     * {@code
     *     // ----------サンプルで使用するBean----------
     *     public class SampleBean {
     *         private String stringProp;
     *         private List<String> listProp;
     *         private String[] arrayProp;
     *         private NestedBean nestedBean;
     *         // setter及びgetterは省略
     *     }
     *     public class NestedBean {
     *         private String nestedStringProp;
     *
     *         // setter及びgetterは省略
     *     }
     *
     *     // ----------実装例----------
     *     SampleBean sampleBean = new SampleBean();
     *
     *     // stringPropプロパティに"value"を登録
     *     BeanUtil.setProperty(sampleBean,"stringProp", "value");
     *
     *     // stringPropプロパティにnullを登録。stringPropはnullとなる
     *     BeanUtil.setProperty(sampleBean,"stringProp", null);
     *
     *     // listPropPropプロパティの要素番号0の位置に"list_value"を登録
     *     BeanUtil.setProperty(sampleBean,"listProp[0]", "list_value");
     *
     *     // 配列型のプロパティの要素番号0の位置に"array_value"を登録
     *     // 十分な要素数が自動で確保される
     *     BeanUtil.setProperty(sampleBean, "arrayProp[0]", "array_value");
     *
     *     // nestedBeanプロパティのnestedStringPropプロパティに"nested_value"を登録
     *     BeanUtil.setProperty(sampleBean, "nestedBean.nestedStringProp", "nested_value");
     * }
     * </pre>
     * <p/>
     *
     * @param bean 値を登録したいBeanオブジェクト
     * @param propertyName 値を登録したいプロパティ名
     * @param propertyValue 登録したい値
     * @throws BeansException
     *   <ul>
     *       <li>{@code propertyName}に対応するプロパティが定義されていない場合</li>
     *       <li>List型・配列型以外のプロパティに、"プロパティ名[インデックス]"という形式で指定した場合</li>
     *       <li>ネストの親となるプロパティのインスタンス生成に失敗した場合</li>
     *   </ul>
     */
    public static void setProperty(final Object bean, final String propertyName, final Object propertyValue) {
        setProperty(bean, new PropertyExpression(propertyName), propertyValue);
    }

    /**
     * {@link Map}からBeanを生成する。
     * <p/>
     * {@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * {@code map}にvalueがnullのエントリがある場合、対応するプロパティの値はnullとなる。
     * <p/>
     * 対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法<br/>
     *   {@code map}に格納するエントリのキー値には、値を登録したいプロパティ名を指定する。
     *   List型・配列型のプロパティでは、"プロパティ名[インデックス]"という形式で要素番号を指定して値を登録できる。
     *   ネストしたプロパティを指定することも可能である。ネストの深さに制限はない。
     *   ネストの親となるプロパティがnullである場合は、インスタンスを生成してから値を登録する。
     * 実装例<br/>
     * <pre>
     * {@code
     *     // ----------サンプルで使用するBean----------
     *     public class SampleBean {
     *         private String stringProp;
     *         private List<String> listProp;
     *         private String[] arrayProp;
     *         private NestedBean nestedBean;
     *
     *         // setter及びgetterは省略
     *     }
     *
     *     public class NestedBean {
     *         private String nestedStringProp;
     *
     *         // setter及びgetterは省略
     *     }
     *
     *     // ----------実装例----------
     *     // 格納したいプロパティ名をkeyに、値をvalueにもつMapを作成する
     *     Map<String,Object> map = new HashMap();
     *
     *     // String型のプロパティに"value"を登録
     *     map.put("stringProp", "value");
     *
     *     // stringPropプロパティにnullを登録。stringPropはnullとなる。
     *     map.put("stringProp", null);
     *
     *     // List型のプロパティの要素番号0の位置に"list_value"を登録
     *     map.put("listProp[0]", "list_value");
     *
     *     // 配列型のプロパティの要素番号0の位置に"array_value"を登録
     *     // 十分な要素数が自動で確保される
     *     map.put("arrayProp[0]", "array_value");
     *
     *     // ネストしたオブジェクトのプロパティに"nested_value"を登録
     *     map.put("nestedBean.nestedStringProp", "nested_value");
     *
     *     SampleBean sampleBean = BeanUtil.createAndCopy(SampleBean.class,map);
     * }
     * </pre>
     * <p/>
     *
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス
     * @param map
     *   JavaBeansのプロパティ名をエントリーのキー
     *   プロパティの値をエントリーの値とするMap
     * @param copyOptions コピーの設定
     * @return プロパティに値が登録されたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタ実行時に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Map<String, ?> map,
            final CopyOptions copyOptions) {
        final T bean = createInstance(beanClass);
        if (map == null) {
            return bean;
        }

        final CopyOptions mergedCopyOptions = copyOptions
                .merge(CopyOptions.fromAnnotation(beanClass));
        final Map<String, PropertyDescriptor> pdMap = PropertyDescriptors.get(beanClass).map;

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            final String propertyName = entry.getKey();
            if (mergedCopyOptions.isTargetProperty(propertyName) == false) {
                continue;
            }
            PropertyDescriptor pd = pdMap.get(propertyName);
            try {
                final Object value = entry.getValue();
                if (pd != null && hasConverter(pd, mergedCopyOptions)) {
                    setPropertyValue(bean, pd, value, mergedCopyOptions);
                } else {
                    setProperty(bean, propertyName, value);
                }
            } catch (BeansException bex) {
                LOGGER.logDebug(
                        "An error occurred while writing to the property :" + entry.getKey());
            }
        }
        return bean;
    }

    /**
     * {@link Map}からBeanを生成する。
     * 
     * <p>
     * 内部的には空の{@link CopyOptions}を渡して{@link #createAndCopy(Class, Map, CopyOptions)}を呼び出している。
     * </p>
     * 
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス
     * @param map
     *   JavaBeansのプロパティ名をエントリーのキー
     *   プロパティの値をエントリーの値とするMap
     * @return プロパティに値が登録されたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタ実行時に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Map<String, ?> map) {
        return createAndCopy(beanClass, map, CopyOptions.empty());
    }

    /**
     * {@link Map}から、指定したプロパティのみをコピーしたBeanを生成する。
     * <p/>
     * {@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * {@code map}でvalueがnullであるプロパティの値はnullになる。例外の送出やログ出力は行わない。
     * <p/>
     * 対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法については{@link #createAndCopy(Class, Map)}を参照。
     *
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス
     * @param map
     *   JavaBeansのプロパティ名をエントリーのキー
     *   プロパティの値をエントリーの値とするMap
     * @param includes コピー対象のプロパティ名
     * @return Beanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタ実行時に問題が発生した場合。
     */
    public static <T> T createAndCopyIncludes(final Class<T> beanClass, final Map<String, ?> map, final String... includes) {
        CopyOptions copyOptions = CopyOptions.options().includes(includes).build();
        return createAndCopy(beanClass, map, copyOptions);
    }

    /**
     * {@link Map}から指定されたプロパティ以外をコピーしてBeanを生成する。
     * <p/>
     * {@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * {@code map}でvalueがnullであるプロパティの値はnullになる。例外の送出やログ出力は行わない。
     * <p/>
     * 対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法については{@link #createAndCopy(Class, Map)}を参照。
     *
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス
     * @param map
     *   JavaBeansのプロパティ名をエントリーのキー
     *   プロパティの値をエントリーの値とするMap
     * @param excludes コピー対象外のプロパティ名
     * @return プロパティに値が登録されたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタ実行時に問題が発生した場合。
     */
    public static <T> T createAndCopyExcludes(final Class<T> beanClass, final Map<String, ?> map, final String... excludes) {
        CopyOptions copyOptions = CopyOptions.options().excludes(excludes).build();
        return createAndCopy(beanClass, map, copyOptions);
    }

    /**
     * Java Beansからプロパティをコピーして、別のBeanを作成する。
     * <p/>
     * {@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBean
     * @return コピーされたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタの実行中に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Object srcBean) {
        return createAndCopy(beanClass, srcBean, CopyOptions.empty());
    }

    /**
     * Java Beansからプロパティをコピーして、別のBeanを作成する。
     * <p/>
     * {@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBean
     * @param copyOptions コピーの設定
     * @return コピーされたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタの実行中に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Object srcBean, final CopyOptions copyOptions) {
        final T bean = createInstance(beanClass);
        if (srcBean == null) {
            return bean;
        }
        return copy(srcBean, bean, copyOptions);
    }

    /**
     * Java Beansから指定されたプロパティをコピーして、別のBeanを作成する。
     * <p/>
     * {@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBean
     * @param includes コピー対象のプロパティ名
     * @return コピーされたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のデフォルトコンストラクタの実行中に問題が発生した場合。
     */
    public static <T> T createAndCopyIncludes(final Class<T> beanClass, final Object srcBean, final String... includes) {
        CopyOptions copyOptions = CopyOptions.options().includes(includes).build();
        return createAndCopy(beanClass, srcBean, copyOptions);
    }

    /**
     * Java Beansから指定されたプロパティ以外をコピーして、別のBeanを作成する。
     * <p/>
     * {@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code beanClass}に存在しないプロパティはコピーされない。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBean
     * @param excludes コピー対象外のプロパティ名
     * @return プロパティに値がコピーされたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}のデフォルトコンストラクタの実行中に問題が発生した場合や、
     *   {@code beanClass}のプロパティのデフォルトコンストラクタの実行中に問題が発生した場合。
     */
    public static <T> T createAndCopyExcludes(final Class<T> beanClass, final Object srcBean, final String... excludes) {
        CopyOptions copyOptions = CopyOptions.options().excludes(excludes).build();
        return createAndCopy(beanClass, srcBean, copyOptions);
    }

    /**
     * BeanからBeanに値をコピーする。
     * <p/>
     * 内部で共通的に使用されるメソッド。
     *
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param copyOptions コピーの設定
     * @param <SRC> コピー元のBeanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     */
    protected static <SRC, DEST> DEST copyInner(final SRC srcBean, final DEST destBean, final CopyOptions copyOptions) {

        CopyOptions copyOptionsFromSrc = CopyOptions.fromAnnotation(srcBean.getClass());
        CopyOptions copyOptionsFromDest = CopyOptions.fromAnnotation(destBean.getClass());
        CopyOptions mergedCopyOptions = copyOptions.merge(copyOptionsFromSrc).merge(copyOptionsFromDest);

        final PropertyDescriptor[] srcPds = getPropertyDescriptors(srcBean.getClass());
        final Map<String, PropertyDescriptor> destPds = PropertyDescriptors.get(destBean.getClass()).map;

        for (PropertyDescriptor pd : srcPds) {
            final String propertyName = pd.getName();
            if (mergedCopyOptions.isTargetProperty(propertyName) == false) {
                continue;
            }

            final Method getter = pd.getReadMethod();
            if (getter == null) {
                continue;
            }

            try {
                final Object val = getter.invoke(srcBean);
                if (!(mergedCopyOptions.isExcludesNull() && val == null)) {
                    final PropertyDescriptor destPd = destPds.get(propertyName);
                    if (destPd == null) {
                        LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
                        continue;
                    }
                    if (hasConverter(destPd, mergedCopyOptions)) {
                        setPropertyValue(destBean, destPd, val, mergedCopyOptions);
                    } else {
                        if (val != null) {
                            Object innerDestBean = getProperty(destBean, destPd);
                            if (innerDestBean == null) {
                                innerDestBean = createInstance(destPd.getPropertyType());
                            }
                            CopyOptions.Builder builder = CopyOptions.options();
                            if (mergedCopyOptions.isExcludesNull()) {
                                builder.excludesNull();
                            }
                            setPropertyValue(destBean, destPd, copyInner(val, innerDestBean, builder.build()), CopyOptions.empty());
                        }
                    }
                }
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
            } catch (Exception e) {
                throw new BeansException(e);
            }
        }
        return destBean;
    }

    /**
     * 指定されたプロパティの情報をもとに有効な{@link Converter}または{@link ExtensionConverter}が存在するか判定する。
     * 
     * @param pd プロパティの情報
     * @param copyOptions コピーの設定
     * @return 有効な{@link Converter}または{@link ExtensionConverter}が存在する場合は{@code true}
     */
    private static boolean hasConverter(PropertyDescriptor pd, CopyOptions copyOptions) {
        String propertyName = pd.getName();
        Class<?> clazz = pd.getPropertyType();
        return copyOptions.hasNamedConverter(propertyName, clazz)
                || copyOptions.hasTypedConverter(clazz)
                || ConversionUtil.hasConverter(clazz);
    }

    /**
     * 指定したオブジェクトのプロパティの値を取得する。
     *
     * @param bean プロパティの値を取得したいBeanオブジェクト
     * @param pd 取得したいプロパティのプロパティディスクリプタ
     * @return オブジェクトから取得したプロパティの値
     * @throws BeansException 取得したいプロパティにgetterが存在しない場合
     */
    private static Object getProperty(final Object bean, final PropertyDescriptor pd) {
        try {
            final Method getter = pd.getReadMethod();
            return getter.invoke(bean);
        } catch (Exception e) {
            throw new BeansException(e);
        }
    }

    /**
     * BeanからBeanに値をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     *
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param <SRC>  コピー元のBeanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     */
    public static <SRC, DEST> DEST copy(final SRC srcBean, final DEST destBean) {
        return copyInner(srcBean, destBean, CopyOptions.empty());
    }

    /**
     * BeanからBeanに値をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * 
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param copyOptions コピーの設定
     * @param <SRC>  コピー元のBeanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     */
    public static <SRC, DEST> DEST copy(final SRC srcBean, final DEST destBean, final CopyOptions copyOptions) {
        return copyInner(srcBean, destBean, copyOptions);
    }

    /**
     * BeanからBeanに値をコピーする。ただしnullのプロパティはコピーしない。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     *
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param <SRC>  コピー元Beanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     */
    public static <SRC, DEST> DEST copyExcludesNull(final SRC srcBean, final DEST destBean) {
        return copyInner(srcBean, destBean, CopyOptions.options().excludesNull().build());
    }

    /**
     * BeanからBeanに、指定されたプロパティをコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * <p/>
     * {@code includes}には、{@code srcBean}のトップレベル要素のみ指定可能である。
     * それ以外を指定した場合はコピーされない。
     * <pre>
     *     {@code
     *     // aaa.bbbはコピーされない
     *     SampleBean copiedSampleBean = BeanUtil.createAndCopyIncludes(SampleBean.class, sampleBean, "aaa.bbb");
     *     }
     * </pre>
     *
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param includes コピー対象のプロパティ名
     * @param <SRC>  コピー元Beanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     */
    public static <SRC, DEST> DEST copyIncludes(final SRC srcBean, final DEST destBean, final String... includes) {
        return copyInner(srcBean, destBean, CopyOptions.options().includes(includes).build());
    }

    /**
     * BeanからBeanに、指定されたプロパティ以外をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     *
     * @param srcBean  コピー元のBeanオブジェクト
     * @param destBean コピー先のBeanオブジェクト
     * @param excludes コピー対象外のプロパティ名
     * @param <SRC>  コピー元Beanの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     */
    public static <SRC, DEST> DEST copyExcludes(final SRC srcBean, final DEST destBean, final String... excludes) {
        return copyInner(srcBean, destBean, CopyOptions.options().excludes(excludes).build());
    }

    /**
     * BeanからMapにプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     *
     * @param srcBean Bean
     * @param copyOptions コピーの設定
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopy(SRC srcBean, CopyOptions copyOptions) {
        return createMapInner(srcBean, "", copyOptions);
    }

    /**
     * BeanからMapにプロパティの値をコピーする。
     * 
     * <p>
     * 内部的には空の{@link CopyOptions}を渡して{@link #createMapAndCopy(Object, CopyOptions)}を呼び出している。
     * </p>
     * 
     * @param srcBean Bean
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopy(SRC srcBean) {
        return createMapAndCopy(srcBean, CopyOptions.empty());
    }

    /**
     * BeanからMapにプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     * <p>
     * 除外対象のプロパティ名が指定された場合は、そのプロパティがコピー対象から除外される。
     *
     * @param srcBean Bean
     * @param excludeProperties 除外対象のプロパティ名
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopyExcludes(final SRC srcBean,
            final String... excludeProperties) {
        return createMapAndCopy(srcBean, CopyOptions.options().excludes(excludeProperties).build());
    }

    /**
     * BeanからMapに指定されたプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     * <p>
     * コピー対象のプロパティ名として指定できるのは、トップ階層のBeanのプロパティ名となる。
     * このため、階層構造で子階層のBeanがinclude指定されていた場合、子階層のBeanのプロパティは全てコピーされる。
     *
     * @param srcBean Bean
     * @param includesProperties コピー対象のプロパティ名のリスト
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopyIncludes(SRC srcBean, String... includesProperties) {
        return createMapAndCopy(srcBean, CopyOptions.options().includes(includesProperties).build());
    }

    /**
     * Mapを作成しBeanのプロパティ値をコピーする。
     *
     * @param srcBean コピー元のBean
     * @param prefix プロパティ名のプレフィックス
     * @param copyOptions コピーの設定
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    private static <SRC> Map<String, Object> createMapInner(
            final SRC srcBean, final String prefix, final CopyOptions copyOptions) {

        final Map<String, Object> result = new HashMap<String, Object>();
        for (PropertyDescriptor descriptor : getPropertyDescriptors(srcBean.getClass())) {
            final String propertyName = descriptor.getName();
            if (copyOptions.isTargetProperty(propertyName) == false) {
                continue;
            }
            final String key = StringUtil.hasValue(prefix) ? prefix + '.' + propertyName : propertyName;
            final Method readMethod = descriptor.getReadMethod();
            if (readMethod == null) {
                continue;
            }
            final Object propertyValue;
            try {
                propertyValue = readMethod.invoke(srcBean);
            } catch (Exception e) {
                throw new BeansException(e);
            }
            if (ConversionUtil.hasConverter(descriptor.getPropertyType())) {
                result.put(key, propertyValue);
            } else {
                if (propertyValue == null) {
                    result.put(key, null);
                } else {
                    result.putAll(createMapInner(propertyValue, key, copyOptions.cloneForNestedObjectInCreateMapInner()));
                }
            }
        }
        return result;
    }

    /**
     * インスタンスを生成する.
     * @param clazz クラス
     * @return インスタンス
     */
    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException e) {
            throw new BeansException(e);
        } catch (IllegalAccessException e) {
            throw new BeansException(e);
        } catch (NoSuchMethodException e) {
            throw new BeansException(e);
        } catch (InvocationTargetException e) {
            throw new BeansException(e);
        }
    }

    /** ロガー */
    private static final Logger
        LOGGER = LoggerManager.get(BeanUtil.class);

    static void clearCache() {
        PropertyDescriptors.clearCache();
    }

    /**
     * クラスの{@link PropertyDescriptor}をまとめたもの。
     * ただしプロパティ名が{@literal class}のものは除かれている。
     * 
     * @author Taichi Uragami
     *
     */
    private static final class PropertyDescriptors {

        /** キャッシュ本体 */
        private static final WeakHashMap<Class<?>, PropertyDescriptors> CACHE = new WeakHashMap<Class<?>, BeanUtil.PropertyDescriptors>();
        /** {@link PropertyDescriptor}の配列表現 */
        final PropertyDescriptor[] array;
        /** {@link PropertyDescriptor}の{@link Map}表現 */
        final Map<String, PropertyDescriptor> map;

        /**
         * コンストラクタ。
         * 
         * <p>
         * クラスの{@link PropertyDescriptor}を取得して配列表現と{@link Map}表現を構築する。
         * なお、プロパティ名が{@literal class}となる{@link PropertyDescriptor}は無視する。
         * </p>
         * 
         * @param beanClass クラス
         * @throws IntrospectionException {@link Introspector#getBeanInfo(Class)}からスローされうる例外
         */
        PropertyDescriptors(Class<?> beanClass) throws IntrospectionException {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            map = new LinkedHashMap<String, PropertyDescriptor>(pds.length - 1);
            for (PropertyDescriptor pd : pds) {
                if (!pd.getName().equals("class")) {
                    map.put(pd.getName(), pd);
                }
            }
            array = map.values().toArray(new PropertyDescriptor[map.size()]);
        }

        /**
         * キャッシュをクリアする。
         * 
         * <p>
         * 主にテストコードからの利用を想定している。
         * </p>
         */
        static synchronized void clearCache() {
            CACHE.clear();
        }

        /**
         * 指定したプロパティ名の{@link PropertyDescriptor}を取得する。
         * 
         * @param propertyName プロパティ名
         * @return 指定したプロパティ名の{@link PropertyDescriptor}
         * @throws BeansException {@code propertyName} に対応する{@link PropertyDescriptor}が見つからない場合。
         */
        PropertyDescriptor getPropertyDescriptor(String propertyName) {
            PropertyDescriptor pd = map.get(propertyName);
            if (pd != null) {
                return pd;
            }
            throw new BeansException(
                    new IntrospectionException("Unknown property: " + propertyName));
        }

        /**
         * クラスに対応する{@link PropertyDescriptors}を取得する。
         * 
         * <p>
         * {@link PropertyDescriptors}はキャッシュされる。
         * </p>
         * 
         * @param beanClass クラス
         * @return キャッシュ
         */
        static synchronized PropertyDescriptors get(final Class<?> beanClass) {
            PropertyDescriptors beanDescCache = CACHE.get(beanClass);
            if (beanDescCache != null) {
                return beanDescCache;
            }
            try {
                beanDescCache = new PropertyDescriptors(beanClass);
            } catch (IntrospectionException e) {
                throw new BeansException(e);
            }
            CACHE.put(beanClass, beanDescCache);
            return beanDescCache;
        }
    }
}
