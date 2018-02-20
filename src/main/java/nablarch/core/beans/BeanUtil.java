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
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.core.beans.factory.CopyOptionsFactoryManager;
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
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            final PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            final PropertyDescriptor[] newPds = new PropertyDescriptor[pds.length - 1];

            int i = 0;
            for (PropertyDescriptor pd : pds) {
                if (!pd.getName().equals("class")) {
                    newPds[i++] = pd;
                }
            }
            return newPds;
        } catch (IntrospectionException e) {
            throw new BeansException(e);
        }
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
        try {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            return getPropertyDescriptor(beanInfo.getPropertyDescriptors(), propertyName);
        } catch (IntrospectionException e) {
            throw new BeansException(e);
        }
    }

    /**
     * {@link PropertyDescriptor}の配列から、指定したプロパティ名の{@link PropertyDescriptor}を取得する。
     *
     * @param pds プロパティディスクリプタの配列
     * @param propertyName プロパティ名
     * @return 指定したプロパティのプロパティディスクリプタ
     * @throws BeansException {@code propertyName} に対応するプロパティディスクリプタが見つからない場合。
     */
    private static PropertyDescriptor getPropertyDescriptor(final PropertyDescriptor[] pds, final String propertyName) {
        try {
            for (PropertyDescriptor pd : pds) {
                if (propertyName.equals(pd.getName())) {
                    return pd;
                }
            }
            throw new IntrospectionException("Unknown property: " + propertyName);
        } catch (IntrospectionException e) {
            throw new BeansException(e);
        }
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
            setPropertyValue(bean, pd, nested, true);
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
        setPropertyValue(bean, getPropertyDescriptor(bean.getClass(), propertyName), propertyValue, true);
    }

    /**
     * プロパティに値を設定する。
     * @param bean Beanオブジェクト
     * @param pd 値を設定するプロパティのプロパティディスクリプタ
     * @param propertyValue プロパティに設定する値
     * @param needsToConvert 値に{@link ConversionUtil#convert(Class, Object)}を適用する必要がある場合は{@code true}
     */
    private static void setPropertyValue(final Object bean, final PropertyDescriptor pd, final Object propertyValue, final boolean needsToConvert) {
        try {
            final Method setter = pd.getWriteMethod();
            if (setter == null) {
                return;
            }
            final Object value = needsToConvert ? ConversionUtil.convert(pd.getPropertyType(), propertyValue) : propertyValue;
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
     * @return プロパティに値が登録されたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタ実行時に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Map<String, ?> map) {
        final T bean = createInstance(beanClass);
        if (map == null) {
            return bean;
        }

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                setProperty(bean, entry.getKey(), entry.getValue());
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while writing to the property :" + entry.getKey());
            }
        }
        return bean;
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
        final T bean = createInstance(beanClass);
        if (map == null) {
            return bean;
        }

        for (String include : includes) {
            try {
                setProperty(bean, include, map.get(include));
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while writing to the property :" + include);
            }
        }
        return bean;
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
        final T bean = createInstance(beanClass);
        if (map == null) {
            return bean;
        }

        List<String> excludesList = Arrays.asList(excludes);
        for (Map.Entry<String, ?> entry : map.entrySet()) {
            try {
                if (excludesList.contains(entry.getKey())) {
                    continue;
                }
                setProperty(bean, entry.getKey(), entry.getValue());
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while writing to the property :" + entry.getKey());
            }
        }
        return bean;
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
        final T bean = createInstance(beanClass);
        if (srcBean == null) {
            return bean;
        }
        return copy(srcBean, bean);
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
        final T bean = createInstance(beanClass);
        if (srcBean == null) {
            return bean;
        }
        return copyIncludes(srcBean, bean, includes);
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
        final T bean = createInstance(beanClass);
        if (srcBean == null) {
            return bean;
        }
        return copyExcludes(srcBean, bean, excludes);
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

        CopyOptions mergedCopyOptions = CopyOptionsFactoryManager.getInstance()
                .createAndMergeCopyOptions(srcBean, destBean, copyOptions);

        final PropertyDescriptor[] srcPds = getPropertyDescriptors(srcBean.getClass());
        final PropertyDescriptor[] destPds = getPropertyDescriptors(destBean.getClass());

        for (PropertyDescriptor pd : srcPds) {
            final String propertyName = pd.getName();
            if (copyOptions.isTargetProperty(propertyName) == false) {
                continue;
            }

            final Method getter = pd.getReadMethod();
            if (getter == null) {
                continue;
            }

            try {
                final Object val = getter.invoke(srcBean);
                if (!(copyOptions.isExcludesNull() && val == null)) {
                    final PropertyDescriptor destPd = getPropertyDescriptor(destPds, propertyName);
                    if (mergedCopyOptions.hasNamedConverter(propertyName, destPd.getPropertyType())) {
                        setPropertyValue(destBean, destPd, mergedCopyOptions.convertByName(propertyName, destPd.getPropertyType(), val), false);
                    } else if (mergedCopyOptions.hasTypedConverter(destPd.getPropertyType())) {
                        setPropertyValue(destBean, destPd, mergedCopyOptions.convertByType(destPd.getPropertyType(), val), false);
                    } else if (ConversionUtil.hasConverter(destPd.getPropertyType())) {
                        setPropertyValue(destBean, destPd, val, true);
                    } else {
                        if (val != null) {
                            Object innerDestBean = getProperty(destBean, destPd);
                            if (innerDestBean == null) {
                                innerDestBean = createInstance(destPd.getPropertyType());
                            }
                            CopyOptions.Builder builder = CopyOptions.options();
                            if (copyOptions.isExcludesNull()) {
                                builder.excludesNull();
                            }
                            setPropertyValue(destBean, destPd, copyInner(val, innerDestBean, builder.build()), true);
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
        return copyInner(srcBean, destBean, CopyOptions.options().build());
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
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopy(SRC srcBean) {
        return createMapInner(srcBean, "", new PropertyFilter() {
            @Override
            public boolean test(String prefix, PropertyDescriptor propertyDescriptor) {
                // 全てのプロパティがコピー対象
                return true;
            }
        });
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
        final List<String> excludes = Arrays.asList(excludeProperties);
        return createMapInner(srcBean, "", new PropertyFilter() {
            @Override
            public boolean test(String prefix, PropertyDescriptor propertyDescriptor) {
                // プロパティ名が除外対象以外のものがコピーた対象
                return !excludes.contains(propertyDescriptor.getName());
            }
        });
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
        final List<String> includes = Arrays.asList(includesProperties);
        return createMapInner(srcBean, "", new PropertyFilter() {
            @Override
            public boolean test(final String prefix, final PropertyDescriptor propertyDescriptor) {
                // 子供の階層のBeanかコピー対象の場合は、コピー対象とする。
                return !StringUtil.isNullOrEmpty(prefix) || includes.contains(propertyDescriptor.getName());
            }
        });
    }

    /**
     * Mapを作成しBeanのプロパティ値をコピーする。
     *
     * @param srcBean コピー元のBean
     * @param prefix プロパティ名のプレフィックス
     * @param filter フィルター(このフィルターが {@code true}を返すプロパティがコピー対象)
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    private static <SRC> Map<String, Object> createMapInner(
            final SRC srcBean, final String prefix, final PropertyFilter filter) {

        final Map<String, Object> result = new HashMap<String, Object>();
        for (PropertyDescriptor descriptor : getPropertyDescriptors(srcBean.getClass())) {
            if (!filter.test(prefix, descriptor)) {
                continue;
            }
            final String propertyName = descriptor.getName();
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
                    result.putAll(createMapInner(propertyValue, key, filter));
                }
            }
        }
        return result;
    }

    /**
     * コピー対象のプロパティをフィルタするインタフェース。
     */
    private interface PropertyFilter {

        /**
         * テストメソッド。
         * <p>
         * コピー対象のプロパティの場合に{@code true}を返す。
         *
         * @param prefix プレフィックス(階層構造の場合に、親のBeanまでのプロパティ名がプレフィックスとなる)
         * @param propertyDescriptor {@code PropertyDescriptor}
         * @return コピー対象のプロパティの場合は、{@code true}
         */
        boolean test(String prefix, PropertyDescriptor propertyDescriptor);
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
}
