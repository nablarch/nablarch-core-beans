package nablarch.core.beans;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Array;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.RecordComponent;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.WeakHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import nablarch.core.log.Logger;
import nablarch.core.log.LoggerManager;
import nablarch.core.util.StringUtil;
import nablarch.core.util.annotation.Published;

/**
 * JavaBeansおよびレコードに関する操作をまとめたユーティリティクラス。
 * <p>
 * レコードはJavaBeanではないものの、JavaBeansと同様に取り扱えると便利なため、
 * 本ユーティリティにてレコードに関する操作もサポートしている。
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
     * <p>
     * ただし、classプロパティは取得対象外となる。
     * <p>
     * 本メソッドの引数にレコードクラスを指定した場合、実行時例外が送出される。
     *
     * @param beanClass プロパティを取得したいクラス
     * @return PropertyDescriptor[] 全てのプロパティの {@link PropertyDescriptor}
     * @throws BeansException プロパティの取得に失敗した場合。
     * @throws IllegalArgumentException 引数の{@code beanClass}がレコードの場合
     */
    public static PropertyDescriptor[] getPropertyDescriptors(final Class<?> beanClass) {
        return PropertyDescriptors.get(beanClass).array;
    }

    /**
     * 指定したクラスから、特定のプロパティの{@link PropertyDescriptor} を取得する。
     * <p>
     * 本メソッドの引数にレコードクラスを指定した場合、実行時例外が送出される。
     *
     * @param beanClass プロパティを取得したいクラス
     * @param propertyName 取得したいプロパティ名
     * @return PropertyDescriptor 取得したプロパティ
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     * @throws IllegalArgumentException 引数の{@code beanClass}がレコードの場合
     */
    public static PropertyDescriptor getPropertyDescriptor(final Class<?> beanClass, final String propertyName) {
        return PropertyDescriptors.get(beanClass).getPropertyDescriptor(propertyName);
    }

    /**
     * 指定したレコードに属する全てのプロパティの {@link RecordComponent} を取得する。
     * <p>
     * 本メソッドの引数にレコードクラスでないクラスを指定した場合、実行時例外が送出される。
     *
     * @param recordClass プロパティを取得したいクラス
     * @return RecordComponent[] 全てのプロパティの {@link RecordComponent}
     * @throws BeansException プロパティの取得に失敗した場合。
     * @throws IllegalArgumentException 引数の{@code recordClass}がレコードでない場合
     */
    static RecordComponent[] getRecordComponents(Class<?> recordClass) {
        return RecordComponents.get(recordClass).array;
    }

    /**
     * 指定したクラスから、特定のプロパティの{@link RecordComponent} を取得する。
     * <p>
     * 本メソッドの引数にレコードクラスでないクラスを指定した場合、実行時例外が送出される。
     *
     * @param recordClass プロパティを取得したいクラス
     * @param propertyName 取得したいプロパティ名
     * @return RecordComponent 取得したプロパティ
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     * @throws IllegalArgumentException 引数の{@code recordClass}がレコードでない場合
     */
    static RecordComponent getRecordComponent(Class<?> recordClass, String propertyName) {
        return RecordComponents.get(recordClass).getRecordComponent(propertyName);
    }

    /**
     * 指定したクラスに属する全てのプロパティの名前を取得する。
     *
     * @param beanClass プロパティ名を取得したいクラス
     * @return Set<String> 全てのプロパティの名前
     */
    static Set<String> getPropertyNames(Class<?> beanClass) {
        if (beanClass.isRecord()) {
            return RecordComponents.get(beanClass).properties;
        }
        return PropertyDescriptors.get(beanClass).properties;
    }

    /**
     * 指定したクラスから、特定プロパティの型を取得する。
     *
     * @param beanClass プロパティの型を取得したいクラス
     * @param propertyName 取得したいプロパティ名
     * @return Class<?> プロパティの型
     */
    static Class<?> getPropertyType(Class<?> beanClass, String propertyName) {
        if (beanClass.isRecord()) {
            return getRecordComponent(beanClass, propertyName).getType();
        }
        return getPropertyDescriptor(beanClass, propertyName).getPropertyType();
    }

    /**
     * 指定したクラスから、特定プロパティの読み取りメソッドを取得する。
     *
     * @param beanClass プロパティの読み取りメソッドを取得したいクラス
     * @param propertyName 取得したいプロパティ名
     * @return Method プロパティの読み取りメソッド
     */
    static Method getReadMethod(Class<?> beanClass, String propertyName) {
        if (beanClass.isRecord()) {
            return getRecordComponent(beanClass, propertyName).getAccessor();
        }
        return getPropertyDescriptor(beanClass, propertyName).getReadMethod();
    }

    /**
     * 指定したJavaBeansオブジェクトもしくはレコードから、特定のプロパティの値を取得する。
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
     * @param bean プロパティの値を取得したいBeanオブジェクトもしくはレコード
     * @param propertyName 取得したいプロパティ名
     * @return Object オブジェクトから取得したプロパティの値
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     */
    public static Object getProperty(final Object bean, final String propertyName) {
        return getProperty(bean, propertyName, null);
    }

    /**
     * 指定したJavaBeansオブジェクトもしくはレコードのプロパティの値を、指定した型に変換して取得する。
     * </p>
     * 型変換の仕様は{@link ConversionUtil}を参照。
     * <p/>
     * {@code propertyName}の指定方法については{@link #getProperty(Object, String)}を参照。
     *
     * @param bean プロパティの値を取得したいBeanオブジェクトもしくはレコード
     * @param propertyName 取得したいプロパティの名称
     * @param type 変換したい型 (nullを指定した場合は変換を行わず、プロパティの値をそのまま返す。)
     * @return Object 取得したプロパティを{@code type}に変換したオブジェクト
     * @throws BeansException {@code propertyName} に対応するプロパティが定義されていない場合。
     */
    public static Object getProperty(final Object bean, final String propertyName, final Class<?> type) {
        try {
            final Method getter = getReadMethod(bean.getClass(), propertyName);
            Object value = getter.invoke(bean);
            if (type != null) {
                value = ConversionUtil.convert(type, value);
            }
            return value;
        } catch (Exception e) {
            throw new BeansException("The property does not exist in the bean or record. property name: " + propertyName, e);
        }
    }

    /**
     * JavaBeansのプロパティに値を設定する。
     * <p>
     * 引数の{@code bean}がレコードの場合、実行時例外が送出される。
     *
     * @param bean Beanオブジェクト
     * @param propertyExpression プロパティを表すオブジェクト
     * @throws BeansException インスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code bean}がレコードの場合
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setProperty(Object bean, PropertyExpression propertyExpression, Map<String, ?> map, CopyOptions copyOptions) {

        if (bean.getClass().isRecord()) {
            throw new IllegalArgumentException("The target bean must not be a record.");
        }
        final String propertyName = propertyExpression.isListOrArray() ?
                propertyExpression.getListPropertyName() : propertyExpression.getRoot();

        Map<String, Map<String, Object>> groupMap = new HashMap<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {

            PropertyExpression expression = new PropertyExpression(entry.getKey());

            if (expression.isSimpleProperty() && expression.isNode()) {
                setPropertyValue(bean, expression.getRoot(), map.get(expression.getRawKey()));
            } else if (expression.isListOrArray()) {
                Class<?> propertyType = getPropertyType(bean.getClass(), propertyName);
                if (propertyType.isArray()) {
                    if (expression.isNode()) {
                        setArrayProperty(bean, expression, map);
                    }
                } else if (List.class.isAssignableFrom(propertyType)) {
                    if (expression.isNode()) {
                        setListProperty(bean, expression, map);
                    }
                } else {
                    throw new BeansException("property type must be List or Array.");
                }
            }

            if (!expression.isNode()) {
                groupMap.computeIfAbsent(expression.rest().getRoot(), (key) -> new HashMap<>())
                        .put(expression.rest().getRawKey(), entry.getValue());
            }
        }

        if (groupMap.isEmpty()) {
            return;
        }

        Object propertyObject = getProperty(bean, propertyName);
        Object destObject;

        Class<?> propertyType = getPropertyType(bean.getClass(), propertyName);
        if (propertyType.isArray()) {
            Class<?> componentType = propertyType.getComponentType();
            propertyObject = initializeArray(propertyObject, componentType, propertyExpression.getListIndex());
            if (componentType.isRecord()) {
                Map<String, Object> recordMap = new HashMap<>();
                groupMap.forEach((key, value) -> recordMap.putAll(value));
                Array.set(propertyObject, propertyExpression.getListIndex(),
                        createRecord(componentType, recordMap, copyOptions.reduce(propertyExpression.getRoot())));
                setPropertyValue(bean, propertyName, propertyObject, CopyOptions.empty());
                return;
            } else {
                destObject = Array.get(propertyObject, propertyExpression.getListIndex());
                if (destObject == null) {
                    destObject = createInstance(componentType);
                    Array.set(propertyObject, propertyExpression.getListIndex(), destObject);
                }
            }
        } else if (List.class.isAssignableFrom(propertyType)) {

            int index = propertyExpression.getListIndex();
            List list = initializeList(propertyObject, index);
            propertyObject = list;

            Class<?> genericType = getGenericType(bean, propertyName);
            if (genericType.isRecord()) {
                Map<String, Object> recordMap = new HashMap<>();
                groupMap.forEach((key, value) -> recordMap.putAll(value));
                list.set(index, createRecord(genericType, recordMap, copyOptions.reduce(propertyExpression.getRoot())));
                setPropertyValue(bean, propertyName, list, CopyOptions.empty());
                return;
            } else {
                destObject = list.get(index);
                if (destObject == null) {
                    destObject = createInstance(genericType);
                    list.set(index, destObject);
                }
            }
        } else if (propertyType.isRecord()) {
            Map<String, Object> recordMap = new HashMap<>();
            groupMap.forEach((key, value) -> recordMap.putAll(value));
            setRecordProperty(bean, propertyName, recordMap, copyOptions);
            return;
        } else {
            propertyObject = Objects.requireNonNullElse(propertyObject, createInstance(propertyType));
            destObject = propertyObject;
            setPropertyValue(bean, propertyName, propertyObject, CopyOptions.empty());
        }

        for (Map.Entry<String, Map<String, Object>> entry : groupMap.entrySet()) {
            try {
                PropertyExpression nestedPropertyExpression = new PropertyExpression(entry.getKey());
                setProperty(destObject, nestedPropertyExpression, entry.getValue(), copyOptions.reduce(nestedPropertyExpression.getRoot()));
                setPropertyValue(bean, propertyName, propertyObject, CopyOptions.empty());
            } catch (BeansException bex) {
                LOGGER.logDebug(
                        "An error occurred while writing to the property :" + entry.getKey());
            }
        }
    }

    /**
     * 配列を初期化する。
     *
     * @param array 配列
     * @param componentType 配列の型
     * @param index 配列の要素数
     * @return 初期化した配列
     */
    @SuppressWarnings("SuspiciousSystemArraycopy")
    private static Object initializeArray(Object array, Class<?> componentType, int index) {
        if (array == null) {
            return Array.newInstance(componentType, index + 1);
        } else if (Array.getLength(array) <= index) {
            // 長さが足りない場合、詰めなおす
            Object retArray = Array.newInstance(componentType, index + 1);
            System.arraycopy(array, 0, retArray, 0, Array.getLength(array));
            return retArray;
        }

        return array;
    }

    /**
     * リストを初期化する。
     *
     * @param list リスト
     * @param index リストの要素数
     * @return 初期化したリスト
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static List initializeList(Object list, int index) {
        List retList = list == null ? new ArrayList<>() : (List)list;
        if (index >= retList.size()) {
            for (int i = retList.size(); i <= index; i++) {
                // 間を埋める。
                retList.add(null);
            }
        }

        return retList;
    }

    /**
     * {@link Object}のプロパティにレコード型の値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param propertyName プロパティ名
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     */
    private static void setRecordProperty(Object bean, String propertyName, Map<String, ?> map, CopyOptions copyOptions) {
        Class<?> propertyType = getPropertyType(bean.getClass(), propertyName);

        Object nested = getProperty(bean, propertyName);
        if (propertyType.isRecord()) {
            if (nested != null) {
                return;
            }
            setPropertyValue(bean, propertyName, createRecord(propertyType, map, copyOptions.reduce(propertyName)));
        }
    }

    /**
     * {@link List}のプロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setListProperty(Object bean, PropertyExpression expression, Map<String, ?> map) {

        String propertyName = expression.getListPropertyName();

        List list = (List) getProperty(bean, propertyName);
        int index = expression.getListIndex();

        list = initializeList(list, index);

        Class<?> genericType = getGenericType(bean, propertyName);

        Object propertyValue = map.get(expression.getRawKey());
        list.set(index, ConversionUtil.convert(genericType, propertyValue));

        setPropertyValue(bean, expression.getListPropertyName(), list);
    }

    /**
     * 配列のプロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param expression プロパティを表すオブジェクト
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     */
    private static void setArrayProperty(Object bean, PropertyExpression expression, Map<String, ?> map) {

        Class<?> componentType = getPropertyType(bean.getClass(), expression.getListPropertyName()).getComponentType();
        String propertyName = expression.getListPropertyName();
        Object array = getProperty(bean, propertyName);
        array = initializeArray(array, componentType, expression.getListIndex());

        int index = expression.getListIndex();

        Object propertyValue = map.get(expression.getRawKey());
        Array.set(array, index, ConversionUtil.convert(componentType, propertyValue));

        setPropertyValue(bean, propertyName, array);
    }

    /**
     * プロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param propertyName 値を設定するプロパティ名
     * @param propertyValue プロパティに設定する値
     */
    private static void setPropertyValue(final Object bean, final String propertyName, final Object propertyValue) {
        setPropertyValue(bean, propertyName, propertyValue, CopyOptions.empty());
    }

    /**
     * プロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param propertyName 値を設定するプロパティ名
     * @param propertyValue プロパティに設定する値
     * @param copyOptions コピーの設定
     * @throws BeansException プロパティの設定に失敗した場合。
     */
    private static void setPropertyValue(Object bean, String propertyName, Object propertyValue, CopyOptions copyOptions) {
        try {
            final Method setter = getPropertyDescriptor(bean.getClass(), propertyName).getWriteMethod();
            if (setter == null) {
                return;
            }
            final Object value;
            Class<?> clazz = getPropertyType(bean.getClass(), propertyName);
            if (copyOptions.hasNamedConverter(propertyName, clazz)) {
                value = copyOptions.convertByName(propertyName, clazz, propertyValue);
            } else if (copyOptions.hasTypedConverter(clazz)) {
                value = copyOptions.convertByType(clazz, propertyValue);
            } else {
                value = ConversionUtil.convert(clazz, propertyValue);
            }
            setter.invoke(bean, value);
        } catch (Exception e) {
            throw new BeansException("Failed to convert property. property name: " + propertyName, e);
        }
    }

    /**
     * JavaBeansのプロパティから、リスト要素の型を取得する.
     * <p>
     * 本メソッドは、JavaBeansのList型のプロパティの構築時に、型変数に指定された型を取得するために使用することを想定している。
     * したがって、プロパティがList型とならないような引数を指定してはならない。
     *
     * @param bean Beanオブジェクト
     * @param propertyName プロパティ名
     * @return リストの要素の型
     * @throws BeansException Listプロパティが原型である場合
     * @throws IllegalStateException コンポーネントの型が型変数である場合
     */
    private static Class<?> getGenericType(Object bean, String propertyName) {
        Method getter = getReadMethod(bean.getClass(), propertyName);
        Type type = getter.getGenericReturnType();

        if (!(type instanceof ParameterizedType genericTypeParameter)) {
            // Generics でない場合。
            throw new BeansException("must set generics type for property. class: "
                    + bean.getClass() + " property: " + propertyName);
        }

        Object genericType = genericTypeParameter.getActualTypeArguments()[0];
        if (genericType instanceof TypeVariable<?>) {
            throw new IllegalStateException(
                    "BeanUtil does not support type parameter for List type, so the getter method in the concrete class must be overridden. "
                            + "getter method = [" + bean.getClass().getName() + "#" + getter.getName() + "]");
        }
        return (Class<?>) genericType;
    }


    /**
     * レコードのコンポーネントから、リストの要素の型を取得する.
     * <p>
     * 本メソッドは、レコードのList型のコンポーネントの構築時に、型変数に指定された型を取得するために使用することを想定している。
     * したがって、コンポーネントがList型とならないような引数を指定してはならない。
     *
     * @param beanClass Beanオブジェクト
     * @param propertyName プロパティ名
     * @return リストの要素の型
     * @throws BeansException Listコンポーネントの型が原型である場合
     * @throws IllegalStateException コンポーネントの型が型変数である場合
     */
    private static Class<?> getGenericTypeForRecord(Class<?> beanClass, String propertyName) {
        Type type = getRecordComponent(beanClass, propertyName).getGenericType();

        if (!(type instanceof ParameterizedType genericTypeParameter)) {
            // Generics でない場合。
            throw new BeansException("must set generics type for property. class: "
                    + beanClass + " property: " + propertyName);
        }
        Object genericType = genericTypeParameter.getActualTypeArguments()[0];
        if (genericType instanceof TypeVariable<?>) {
            throw new IllegalStateException("BeanUtil does not support type parameter for List type component," +
                    " so the type parameter in the record class must be concrete type.");
        }
        return (Class<?>) genericType;
    }

    /**
     * 指定したJavaBeansオブジェクトのプロパティに値を登録する。
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
     * <p>
     * 実装例
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
     * 引数の{@code bean}がレコードの場合、実行時例外が送出される。
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
     * @throws IllegalArgumentException 引数の{@code bean}がレコードの場合
     */
    public static void setProperty(final Object bean, final String propertyName, final Object propertyValue) {
        Map<String, Object> parameterMap = new HashMap<>();
        parameterMap.put(propertyName, propertyValue);
        setProperty(bean, propertyName, parameterMap, CopyOptions.empty());
    }

    /**
     * プロパティに値を設定する。
     *
     * @param bean Beanオブジェクト
     * @param propertyName 値を設定するプロパティ名
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     * @throws IllegalArgumentException 引数の{@code bean}がレコードの場合
     */
    private static void setProperty(Object bean, String propertyName, Map<String, ?> map, CopyOptions copyOptions) {
        setProperty(bean, new PropertyExpression(propertyName), map, copyOptions);
    }

    /**
     * {@link Map}からBeanもしくはレコードを生成する。
     * <p/>
     * 生成対象がBeanであり、かつ{@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code map}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     * <p/>
     * {@code map}にvalueがnullのエントリがある場合、対応するプロパティの値はnullとなる。
     * <p/>
     * 生成対象がBeanで、かつ対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法<br/>
     *   {@code map}に格納するエントリのキー値には、値を登録したいプロパティ名を指定する。
     *   List型・配列型のプロパティでは、"プロパティ名[インデックス]"という形式で要素番号を指定して値を登録できる。
     *   ネストしたプロパティを指定することも可能である。ネストの深さに制限はない。
     *   ネストの親となるプロパティがnullである場合は、インスタンスを生成してから値を登録する。
     * <p>
     * 実装例
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

        if(beanClass.isRecord()) {
            return createRecord(beanClass, map, copyOptions);
        }

        final T bean = createInstance(beanClass);
        if (map == null) {
            return bean;
        }
        copy(beanClass, bean, map, copyOptions);
        return bean;
    }

    /**
     * {@link Map}からBeanインスタンスへコピーを行う。
     * <p>
     * 生成済みのインスタンスにコピーを行う点以外は、{@link #createAndCopy(Class, Map, CopyOptions)}と同じ動作である。
     *
     * @param beanClass 移送先BeanのClass
     * @param bean 移送先Beanインスタンス
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     * @param <T> 型引数
     * @throws IllegalArgumentException 引数の{@code beanClass}がレコードクラスの場合
     */
    public static <T> void copy(Class<? extends T> beanClass, final T bean, final Map<String, ?> map,
            final CopyOptions copyOptions) {

        if (beanClass.isRecord()) {
            throw new IllegalArgumentException("The target bean class must not be a record class.");
        }

        final CopyOptions mergedCopyOptions = copyOptions
                .merge(CopyOptions.fromAnnotation(beanClass));
        final Map<String, PropertyDescriptor> pdMap = PropertyDescriptors.get(beanClass).map;
        Map<String, Map<String, Object>> groupedMap = new HashMap<>();

        for (Map.Entry<String, ?> entry : map.entrySet()) {
            final String propertyName = entry.getKey();
            if (!mergedCopyOptions.isTargetProperty(propertyName)) {
                continue;
            }
            PropertyDescriptor pd = pdMap.get(propertyName);
            try {
                final Object value = entry.getValue();
                if (pd != null && hasConverter(beanClass, propertyName, mergedCopyOptions)) {
                    setPropertyValue(bean, propertyName, value, mergedCopyOptions);
                } else {
                    // mapに保持する
                    PropertyExpression property = new PropertyExpression(entry.getKey());
                    groupedMap.computeIfAbsent(property.getRoot(), (key) -> new HashMap<>())
                            .put(entry.getKey(), entry.getValue());
                }
            } catch (BeansException bex) {
                LOGGER.logDebug(
                        "An error occurred while writing to the property :" + entry.getKey());
            }
        }

        for (Map.Entry<String, Map<String, Object>> entry : groupedMap.entrySet()) {
            try {
                PropertyExpression expression = new PropertyExpression(entry.getKey());
                setProperty(bean, expression, entry.getValue(), mergedCopyOptions);
            } catch (BeansException bex) {
                LOGGER.logDebug(
                        "An error occurred while writing to the property :" + entry.getKey());
            }
        }
    }

    /**
     * {@link Map}からBeanインスタンスへコピーを行う。
     * パラメータmapにはネストしたプロパティが設定される想定。
     *
     * @param bean 移送先Beanインスタンス
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     */
    private static void setBean(Object bean, Map<String, ?> map, CopyOptions copyOptions) {

        Map<String, Map<String, Object>> groupMap = new HashMap<>();
        for (Map.Entry<String, ?> entry: map.entrySet()) {
            PropertyExpression expression = new PropertyExpression(entry.getKey());
            groupMap.computeIfAbsent(expression.getRoot(), (key)->new HashMap<>())
                    .put(entry.getKey(), entry.getValue());
        }
        int errorCount = 0;
        BeansException error = null;
        for (Map.Entry<String, Map<String, Object>> entry: groupMap.entrySet()) {
            PropertyExpression expression = new PropertyExpression(entry.getKey());
            try {
                setProperty(bean, expression, entry.getValue(), copyOptions);
            } catch (BeansException bex) {
                error = bex;
                errorCount++;
                LOGGER.logDebug(
                        "An error occurred while writing to the property :" + entry.getKey());
            }
        }
        if (error != null && groupMap.size() == errorCount) {
            throw error;
        }
    }

    /**
     * JavaBeansもしくはレコードからレコードを生成する。
     *
     * @param beanClass 生成するレコードのClass
     * @param srcBean 生成元のJavaBeansもしくはレコード
     * @param copyOptions コピーの設定
     * @param <T> 型引数
     * @return レコード
     * @throws BeansException レコードの生成に失敗した場合
     */
    private static <T> T createRecord(Class<? extends T> beanClass, Object srcBean, CopyOptions copyOptions) {
        CopyOptions copyOptionsFromSrc = CopyOptions.fromAnnotation(srcBean.getClass());
        CopyOptions copyOptionsFromDest = CopyOptions.fromAnnotation(beanClass);
        CopyOptions mergedCopyOptions = copyOptions.merge(copyOptionsFromSrc).merge(copyOptionsFromDest);

        final RecordComponent[] destRcs = getRecordComponents(beanClass);
        final Class<?>[] parameterTypes = new Class<?>[destRcs.length];
        final Object[] args = new Object[destRcs.length];

        for (int i = 0; i < destRcs.length; i++) {
            final String propertyName = destRcs[i].getName();
            parameterTypes[i] = destRcs[i].getType();

            if (!mergedCopyOptions.isTargetProperty(propertyName)) {
                continue;
            }

            // srcBeanに対応するプロパティが存在しないか、アクセサが存在しない場合はスキップ
            final Method accessor;
            try {
            accessor = getReadMethod(srcBean.getClass(), propertyName);
                if (accessor == null) {
                    if (parameterTypes[i].isPrimitive()) {
                        args[i] = PRIM_DEFAULT_VALUES.get(parameterTypes[i]);
                    }
                    continue;
                }
            } catch (BeansException bex) {
                if (parameterTypes[i].isPrimitive()) {
                    args[i] = PRIM_DEFAULT_VALUES.get(parameterTypes[i]);
                }
                continue;
            }

            try {
                Object val = accessor.invoke(srcBean);
                if (hasConverter(beanClass, propertyName, mergedCopyOptions)) {
                    args[i] = createPropertyValue(beanClass, propertyName, val, mergedCopyOptions);
                } else {
                    if (val != null) {
                        if (parameterTypes[i].isRecord()) {
                            args[i] = createRecord(parameterTypes[i], val, CopyOptions.empty());
                        } else {
                            args[i] = copyInner(val, createInstance(parameterTypes[i]), CopyOptions.empty());
                        }
                    }
                }
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while copying the property: " + propertyName);
            } catch (InvocationTargetException | IllegalAccessException e) {
                throw new BeansException("Failed to read property. property name: " + propertyName, e);
            }
        }

        // コピー対象であり、かつコピー元に存在してコピー先に存在しないプロパティのログ出力
        if(LOGGER.isDebugEnabled()) {
            Set<String> srcLeftProperties = new HashSet<>(getPropertyNames(srcBean.getClass()));
            srcLeftProperties.removeAll(getPropertyNames(beanClass));
            for (String propertyName : srcLeftProperties) {
                if (mergedCopyOptions.isTargetProperty(propertyName)) {
                    LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
                }
            }
        }

        try {
            return beanClass.getConstructor(parameterTypes).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BeansException("An error occurred while creating the record: " + beanClass.getName(), e);
        }
    }


    /**
     * {@link Map}からレコードを生成する。
     *
     * @param beanClass レコードのClass
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     * @param <T> 型引数
     * @return レコード
     * @throws BeansException レコードの生成に失敗した場合
     */
    private static <T> T createRecord(Class<? extends T> beanClass, Map<String, ?> map, CopyOptions copyOptions) {
        Map<String, ?> propertyMap = createPropertyMap(beanClass, map, copyOptions);

        final RecordComponent[] recordComponents = getRecordComponents(beanClass);
        final Class<?>[] parameterTypes = new Class<?>[recordComponents.length];
        final Object[] args = new Object[recordComponents.length];

        for(int i=0; i<recordComponents.length; i++) {
            parameterTypes[i] = recordComponents[i].getType();
            String propertyName = recordComponents[i].getName();

            if (!propertyMap.containsKey(propertyName)) {
                if (parameterTypes[i].isPrimitive()) {
                    args[i] = PRIM_DEFAULT_VALUES.get(parameterTypes[i]);
                }
                continue;
            }
            args[i] = propertyMap.get(propertyName);
        }

        try {
            return beanClass.getConstructor(parameterTypes).newInstance(args);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            throw new BeansException("An error occurred while creating the record: " + beanClass.getName(), e);
        }
    }

    /**
     * レコードを生成するためのプロパティ値を格納したマップを生成する。
     *
     * @param beanClass レコードのClass
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     * @param copyOptions コピーの設定
     * @return レコードを生成するためのプロパティ値を格納したマップ
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static Map<String, ?> createPropertyMap(Class<?> beanClass, Map<String, ?> map, CopyOptions copyOptions) {
        if (map == null) {
            return Collections.emptyMap();
        }

        Map<String, Object> propertyMap = new HashMap<>();
        Map<String, Map<String, Object>> groupMap = new HashMap<>();

        final CopyOptions mergedCopyOptions = copyOptions
                .merge(CopyOptions.fromAnnotation(beanClass));

        for(Map.Entry<String, ?> entry : map.entrySet()) {
            String propertyName = entry.getKey();
            Object propertyValue = entry.getValue();
            PropertyExpression expression = new PropertyExpression(propertyName);

            if (!mergedCopyOptions.isTargetProperty(propertyName)) {
                continue;
            }
            try {
                if (expression.isSimpleProperty() && expression.isNode()) {
                    String propertyRoot = expression.getRoot();
                    propertyMap.put(propertyRoot, createPropertyValue(beanClass, propertyRoot, propertyValue, copyOptions));
                } else if (expression.isListOrArray()) {
                    Class<?> propertyType = getPropertyType(beanClass, expression.getListPropertyName());
                    if (propertyType.isArray()) {
                        if (expression.isNode()) {
                            setArrayPropertyToMap(beanClass, expression, propertyMap, map);
                        }
                    } else if (List.class.isAssignableFrom(propertyType)) {
                        if (expression.isNode()) {
                            setListPropertyToMap(beanClass, expression, propertyMap, map);
                        }
                    } else {
                        throw new BeansException("property type must be List or Array.");
                    }
                }

                if (!expression.isNode()) {
                    groupMap.computeIfAbsent(expression.getRoot(), (key) -> new HashMap<>())
                            .put(expression.rest().getRawKey(), entry.getValue());
                }

            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while copying the property :" + propertyName + " original exception: " + bex);
            }
        }

        if (groupMap.isEmpty()) {
            return propertyMap;
        }

        for (Map.Entry<String, Map<String, Object>> entry: groupMap.entrySet()) {

            PropertyExpression expression = new PropertyExpression(entry.getKey());
            String propertyName = expression.isListOrArray() ? expression.getListPropertyName()
                    : expression.getRoot();
            try {
                Class<?> propertyType = getPropertyType(beanClass, propertyName);

                if (propertyType.isArray()) {
                    Class<?> componentType = propertyType.getComponentType();
                    Object array = propertyMap.getOrDefault(propertyName, Array.newInstance(componentType, expression.getListIndex() + 1));
                    array = initializeArray(array, componentType, expression.getListIndex());
                    int index = expression.getListIndex();
                    try {
                        if (componentType.isRecord()) {
                            Array.set(array, index, createRecord(componentType, entry.getValue(), copyOptions.reduce(expression.getRoot())));
                        } else {
                            Object nested = Array.get(array, index);
                            if (nested == null) {
                                nested = createInstance(componentType);
                            }
                            setBean(nested, entry.getValue(), copyOptions.reduce(expression.getRoot()));
                            Array.set(array, index, nested);
                        }
                        propertyMap.put(propertyName, array);

                    } catch (BeansException e) {
                        LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
                    }
                } else if (List.class.isAssignableFrom(propertyType)) {

                    List list = (List)propertyMap.getOrDefault(propertyName, new ArrayList<>());
                    int index = expression.getListIndex();

                    list = initializeList(list, index);

                    Class<?> genericType = getGenericTypeForRecord(beanClass, propertyName);

                    try {
                        if (genericType.isRecord()) {
                            list.set(index, createRecord(genericType, entry.getValue(), copyOptions.reduce(expression.getRoot())));
                        } else {
                            Object nested = list.get(index);
                            if (nested == null) {
                                nested = createInstance(genericType);
                            }
                            setBean(nested, entry.getValue(), copyOptions.reduce(expression.getRoot()));
                            list.set(index, nested);
                        }
                        propertyMap.put(propertyName, list);

                    } catch (BeansException e) {
                        LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
                    }
                } else if (propertyType.isRecord()) {
                    propertyMap.put(propertyName, createRecord(propertyType, entry.getValue(), copyOptions.reduce(expression.getRoot())));
                } else {
                    Object nested = propertyMap.getOrDefault(propertyName, createInstance(propertyType));
                    setBean(nested, entry.getValue(), copyOptions.reduce(expression.getRoot()));
                    propertyMap.put(propertyName, nested);
                }

            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while copying the property :" + propertyName + " original exception: " + bex);
            }
        }

        return propertyMap;
    }

    /**
     * プロパティ値を格納したマップに配列の値を設定する。
     *
     * @param beanClass レコードのClass
     * @param expression 設定する配列を表すPropertyExpression
     * @param propertyMap プロパティ値を格納したマップ
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     */
    private static void setArrayPropertyToMap(Class<?> beanClass, PropertyExpression expression, Map<String, Object> propertyMap, Map<String, ?> map) {
        String listPropertyName = expression.getListPropertyName();
        Class<?> propertyType = getPropertyType(beanClass, listPropertyName);
        Class<?> componentType = propertyType.getComponentType();

        Object array = propertyMap.getOrDefault(listPropertyName, Array.newInstance(componentType, expression.getListIndex() + 1));
        array = initializeArray(array, componentType, expression.getListIndex());
        int index = expression.getListIndex();

        Object propertyValue = map.get(expression.getRawKey());
        Array.set(array, index, ConversionUtil.convert(componentType, propertyValue));

        propertyMap.put(listPropertyName, array);

    }

    /**
     * プロパティ値を格納したマップにリストの値を設定する。
     *
     * @param beanClass レコードのClass
     * @param expression 設定するリストを表すPropertyExpression
     * @param propertyMap プロパティ値を格納したマップ
     * @param map JavaBeansのプロパティ名をエントリーのキー、プロパティの値をエントリーの値とする、移送元のMap
     */
    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void setListPropertyToMap(Class<?> beanClass, PropertyExpression expression, Map<String, Object> propertyMap, Map<String, ?> map) {
        String listPropertyName = expression.getListPropertyName();

        List list = (List) propertyMap.getOrDefault(listPropertyName, new ArrayList());
        int index = expression.getListIndex();

        list = initializeList(list, index);

        Class<?> genericType = getGenericTypeForRecord(beanClass, listPropertyName);
        Object propertyValue = map.get(expression.getRawKey());

        list.set(index, ConversionUtil.convert(genericType, propertyValue));

        propertyMap.put(listPropertyName, list);

    }


    /**
     * プロパティ値を変換して生成する。
     *
     * @param beanClass レコードのClass
     * @param propertyName プロパティ名
     * @param propertyValue プロパティ値
     * @param copyOptions コピーの設定
     * @return 変換済みのプロパティ値
     * @throws BeansException プロパティ値の変換に失敗した場合
     */
    private static Object createPropertyValue(Class<?> beanClass, String propertyName, Object propertyValue, CopyOptions copyOptions) {

        try {
            Class<?> clazz = getPropertyType(beanClass, propertyName);

            if (copyOptions.hasNamedConverter(propertyName, clazz)) {
                return copyOptions.convertByName(propertyName, clazz, propertyValue);
            }

            if (copyOptions.hasTypedConverter(clazz)) {
                return copyOptions.convertByType(clazz, propertyValue);
            }

            return ConversionUtil.convert(clazz, propertyValue);
        } catch (Exception e) {
            throw new BeansException("Failed to convert property. property name: " + propertyName, e);
        }
    }

    /**
     * プリミティブ型に対応するデフォルト値
     */
    private static final Map<Class<?>, Object> PRIM_DEFAULT_VALUES = Map.of(
            boolean.class, false,
            byte.class, (byte) 0,
            short.class, (short) 0,
            int.class, 0,
            long.class, 0L,
            float.class, 0.0f,
            double.class, 0.0d,
            char.class, '\u0000'
    );

    /**
     * {@link Map}からBeanもしくはレコードを生成する。
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
     * {@link Map}から、指定したプロパティのみをコピーしたBeanもしくはレコードを生成する。
     * <p/>
     * 生成対象がBeanであり、かつ{@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code map}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     * <p/>
     * {@code map}でvalueがnullであるプロパティの値はnullになる。例外の送出やログ出力は行わない。
     * <p/>
     * 生成対象がBeanで、かつ対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法については{@link #createAndCopy(Class, Map)}を参照。
     *
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス、もしくはレコードクラス
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
     * {@link Map}から指定されたプロパティ以外をコピーしてBeanもしくはレコードを生成する。
     * <p/>
     * 生成対象がBeanであり、かつ{@code map}がnullである場合は、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code map}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     * <p/>
     * 生成対象がBeanで、対象のプロパティにsetterが定義されていない場合はなにもしない。
     * <p/>
     * プロパティの指定方法については{@link #createAndCopy(Class, Map)}を参照。
     *
     * @param <T> 型引数
     * @param beanClass 生成したいBeanクラス、もしくはレコードクラス
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
     * Java Beansもしくはレコードからプロパティをコピーして、別のBeanを作成する。
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
     * Java Beansもしくはレコードからプロパティをコピーして、別のBeanを作成する。
     * <p/>
     * 生成対象がBeanであり、かつ{@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code srcBean}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBeanもしくはレコード
     * @param copyOptions コピーの設定
     * @return コピーされたBeanオブジェクト
     * @throws BeansException
     *   {@code beanClass}にデフォルトコンストラクタが定義されていない場合や、
     *   {@code beanClass}のコンストラクタの実行中に問題が発生した場合。
     */
    public static <T> T createAndCopy(final Class<T> beanClass, final Object srcBean, final CopyOptions copyOptions) {

        if(beanClass.isRecord()) {
            if(Objects.isNull(srcBean)) {
                return createRecord(beanClass, Collections.emptyMap(), copyOptions);
            }
            return createRecord(beanClass, srcBean, copyOptions);
        }

        final T bean = createInstance(beanClass);
        if (srcBean == null) {
            return bean;
        }
        return copy(srcBean, bean, copyOptions);
    }

    /**
     * Java Beansもしくはレコードから指定されたプロパティをコピーして、別のBeanを作成する。
     * <p/>
     * 生成対象がBeanであり、かつ{@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code srcBean}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBeanもしくはレコード
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
     * 生成対象がBeanであり、かつ{@code srcBean}がnullである場合、デフォルトコンストラクタで{@code beanClass}を生成して返却する。
     * <p/>
     * 生成対象がレコードであり、かつ{@code srcBean}がnullである場合は、各コンポーネントにnullもしくはプリミティブ型のデフォルト値を設定したレコードを生成して返却する。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code beanClass}に存在しないプロパティはコピーされない。
     *
     * @param <T> 型引数
     * @param beanClass コピー先のBeanクラス
     * @param srcBean  コピー元のBeanもしくはレコード
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
     * BeanもしくはレコードからBeanに値をコピーする。
     * <p/>
     * 内部で共通的に使用されるメソッド。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param copyOptions コピーの設定
     * @param <SRC> コピー元のBeanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException Beanのコピーに失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    static <SRC, DEST> DEST copyInner(final SRC srcBean, final DEST destBean, final CopyOptions copyOptions) {

        if (destBean.getClass().isRecord()) {
            throw new IllegalArgumentException("The destination bean must not be a record.");
        }

        CopyOptions copyOptionsFromSrc = CopyOptions.fromAnnotation(srcBean.getClass());
        CopyOptions copyOptionsFromDest = CopyOptions.fromAnnotation(destBean.getClass());
        CopyOptions mergedCopyOptions = copyOptions.merge(copyOptionsFromSrc).merge(copyOptionsFromDest);

        final PropertyDescriptor[] destPds = getPropertyDescriptors(destBean.getClass());

        for (PropertyDescriptor pd : destPds) {
            final String propertyName = pd.getName();
            if (!mergedCopyOptions.isTargetProperty(propertyName)) {
                continue;
            }

            // srcBeanに対応するプロパティが存在しないか、getterが存在しない場合はスキップ
            final Method getter;
            try {
                getter = getReadMethod(srcBean.getClass(), propertyName);
                if (getter == null) {
                    continue;
                }
            } catch (BeansException bex) {
                continue;
            }

            try {
                final Object val = getter.invoke(srcBean);
                if (!(mergedCopyOptions.isExcludesNull() && val == null)) {
                    if (hasConverter(destBean.getClass(), propertyName, mergedCopyOptions)) {
                        setPropertyValue(destBean, propertyName, val, mergedCopyOptions);
                    } else {
                        if (val != null) {
                            Class<?> propertyType = pd.getPropertyType();
                            CopyOptions.Builder builder = CopyOptions.options();
                            if (mergedCopyOptions.isExcludesNull()) {
                                builder.excludesNull();
                            }

                            if (propertyType.isRecord()) {
                                setPropertyValue(destBean, propertyName, createRecord(propertyType, val, builder.build()), CopyOptions.empty());

                            } else {
                                Object innerDestBean = getProperty(destBean, propertyName);
                                if (innerDestBean == null) {
                                    innerDestBean = createInstance(propertyType);
                                }
                                setPropertyValue(destBean, propertyName, copyInner(val, innerDestBean, builder.build()), CopyOptions.empty());
                            }
                        }
                    }
                }
            } catch (BeansException bex) {
                LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
            } catch (Exception e) {
                throw new BeansException("Failed to read property from source bean. property name: " + propertyName, e);
            }
        }

        // コピー対象であり、かつコピー元に存在してコピー先に存在しないプロパティのログ出力
        if(LOGGER.isDebugEnabled()) {
            Set<String> srcLeftProperties = new HashSet<>(getPropertyNames(srcBean.getClass()));
            srcLeftProperties.removeAll(getPropertyNames(destBean.getClass()));
            for (String propertyName : srcLeftProperties) {
                if (mergedCopyOptions.isTargetProperty(propertyName)) {
                    LOGGER.logDebug("An error occurred while copying the property :" + propertyName);
                }
            }
        }

        return destBean;
    }

    /**
     * 指定されたプロパティの情報をもとに有効な{@link Converter}または{@link ExtensionConverter}が存在するか判定する。
     * 
     * @param beanClass コピー先のbeanクラス
     * @param propertyName コピー先のプロパティ名
     * @param copyOptions コピーの設定
     * @return 有効な{@link Converter}または{@link ExtensionConverter}が存在する場合は{@code true}
     */
    private static boolean hasConverter(Class<?> beanClass, String propertyName, CopyOptions copyOptions) {
        Class<?> clazz = getPropertyType(beanClass, propertyName);
        return copyOptions.hasNamedConverter(propertyName, clazz)
                || copyOptions.hasTypedConverter(clazz)
                || ConversionUtil.hasConverter(clazz);
    }

    /**
     * BeanもしくはレコードからBeanに値をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * <p>
     * {@code destBean}にレコードが指定された場合は、{@link IllegalArgumentException}が送出される。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param <SRC>  コピー元のBeanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    public static <SRC, DEST> DEST copy(final SRC srcBean, final DEST destBean) {
        return copyInner(srcBean, destBean, CopyOptions.empty());
    }

    /**
     * BeanもしくはレコードからBeanに値をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * <p>
     * {@code destBean}にレコードが指定された場合は、{@link IllegalArgumentException}が送出される。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param copyOptions コピーの設定
     * @param <SRC>  コピー元のBeanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    public static <SRC, DEST> DEST copy(final SRC srcBean, final DEST destBean, final CopyOptions copyOptions) {
        return copyInner(srcBean, destBean, copyOptions);
    }

    /**
     * BeanもしくはレコードからBeanに値をコピーする。ただしnullのプロパティはコピーしない。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * <p>
     * {@code destBean}にレコードが指定された場合は、{@link IllegalArgumentException}が送出される。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param <SRC>  コピー元Beanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    public static <SRC, DEST> DEST copyExcludesNull(final SRC srcBean, final DEST destBean) {
        return copyInner(srcBean, destBean, CopyOptions.options().excludesNull().build());
    }

    /**
     * BeanもしくはレコードからBeanに、指定されたプロパティをコピーする。
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
     * <p>
     * {@code destBean}にレコードが指定された場合は、{@link IllegalArgumentException}が送出される。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param includes コピー対象のプロパティ名
     * @param <SRC>  コピー元Beanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    public static <SRC, DEST> DEST copyIncludes(final SRC srcBean, final DEST destBean, final String... includes) {
        return copyInner(srcBean, destBean, CopyOptions.options().includes(includes).build());
    }

    /**
     * BeanもしくはレコードからBeanに、指定されたプロパティ以外をコピーする。
     * <p/>
     * プロパティのコピーは{@code srcBean}に定義されたプロパティをベースに実行される。
     * {@code srcBean}に存在し、{@code destBean}に存在しないプロパティはコピーされない。
     * <p>
     * {@code destBean}にレコードが指定された場合は、{@link IllegalArgumentException}が送出される。
     *
     * @param srcBean  コピー元のBeanオブジェクトもしくはレコード
     * @param destBean コピー先のBeanオブジェクト
     * @param excludes コピー対象外のプロパティ名
     * @param <SRC>  コピー元Beanもしくはレコードの型
     * @param <DEST> コピー先のBeanの型
     * @return コピー先のBeanオブジェクト
     * @throws BeansException {@code destBean}のプロパティのインスタンス生成に失敗した場合
     * @throws IllegalArgumentException 引数の{@code destBean}がレコードの場合
     */
    public static <SRC, DEST> DEST copyExcludes(final SRC srcBean, final DEST destBean, final String... excludes) {
        return copyInner(srcBean, destBean, CopyOptions.options().excludes(excludes).build());
    }

    /**
     * BeanもしくはレコードからMapにプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     *
     * @param srcBean Beanもしくはレコード
     * @param copyOptions コピーの設定
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopy(SRC srcBean, CopyOptions copyOptions) {
        return createMapInner(srcBean, "", copyOptions);
    }

    /**
     * BeanもしくはレコードからMapにプロパティの値をコピーする。
     * 
     * <p>
     * 内部的には空の{@link CopyOptions}を渡して{@link #createMapAndCopy(Object, CopyOptions)}を呼び出している。
     * </p>
     * 
     * @param srcBean Beanもしくはレコード
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopy(SRC srcBean) {
        return createMapAndCopy(srcBean, CopyOptions.empty());
    }

    /**
     * BeanもしくはレコードからMapにプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     * <p>
     * 除外対象のプロパティ名が指定された場合は、そのプロパティがコピー対象から除外される。
     *
     * @param srcBean Beanもしくはレコード
     * @param excludeProperties 除外対象のプロパティ名
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopyExcludes(final SRC srcBean,
            final String... excludeProperties) {
        return createMapAndCopy(srcBean, CopyOptions.options().excludes(excludeProperties).build());
    }

    /**
     * BeanもしくはレコードからMapに指定されたプロパティの値をコピーする。
     * <p>
     * Mapのキーはプロパティ名で、値はプロパティ値となる。
     * 値の型変換は行わず、Beanのプロパティの値を単純にMapの値に設定する。
     * BeanがBeanを持つ構造の場合、Mapのキー値は「.」で連結された値となる。
     * <p>
     * コピー対象のプロパティ名として指定できるのは、トップ階層のBeanのプロパティ名となる。
     * このため、階層構造で子階層のBeanがinclude指定されていた場合、子階層のBeanのプロパティは全てコピーされる。
     *
     * @param srcBean Beanもしくはレコード
     * @param includesProperties コピー対象のプロパティ名のリスト
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     */
    public static <SRC> Map<String, Object> createMapAndCopyIncludes(SRC srcBean, String... includesProperties) {
        return createMapAndCopy(srcBean, CopyOptions.options().includes(includesProperties).build());
    }

    /**
     * Mapを作成しBeanもしくはレコードのプロパティ値をコピーする。
     *
     * @param srcBean コピー元のBeanもしくはレコード
     * @param prefix プロパティ名のプレフィックス
     * @param copyOptions コピーの設定
     * @param <SRC> Beanの型
     * @return BeanのプロパティをコピーしたMap
     * @throws BeansException Beanもしくはレコードのプロパティの読み取りに失敗した場合
     */
    private static <SRC> Map<String, Object> createMapInner(
            final SRC srcBean, final String prefix, final CopyOptions copyOptions) {

        final Map<String, Object> result = new HashMap<>();
        for (String propertyName : getPropertyNames(srcBean.getClass())) {
            if (!copyOptions.isTargetProperty(propertyName)) {
                continue;
            }
            final String key = StringUtil.hasValue(prefix) ? prefix + '.' + propertyName : propertyName;
            final Method readMethod = getReadMethod(srcBean.getClass(), propertyName);
            if (readMethod == null) {
                continue;
            }
            final Object propertyValue;
            try {
                propertyValue = readMethod.invoke(srcBean);
            } catch (Exception e) {
                throw new BeansException("Failed to read property. property name: " + propertyName, e);
            }
            if (ConversionUtil.hasConverter(getPropertyType(srcBean.getClass(), propertyName))) {
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
     *
     * @param clazz クラス
     * @return インスタンス
     * @throws BeansException インスタンスの生成に失敗した場合
     */
    private static <T> T createInstance(Class<T> clazz) {
        try {
            return clazz.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            throw new BeansException("Failed to create instance using default constructor. class name: " + clazz.getName(), e);
        }
    }

    /** ロガー */
    private static final Logger
        LOGGER = LoggerManager.get(BeanUtil.class);

    static void clearCache() {
        PropertyDescriptors.clearCache();
        RecordComponents.clearCache();
    }

    /**
     * クラスの{@link PropertyDescriptor}をまとめたもの。
     * <p>
     * ただしプロパティ名が{@literal class}のものは除かれている。
     * 
     * @author Taichi Uragami
     *
     */
    private static final class PropertyDescriptors {

        /** キャッシュ本体 */
        private static final WeakHashMap<Class<?>, PropertyDescriptors> CACHE = new WeakHashMap<>();
        /** {@link PropertyDescriptor}の配列表現 */
        final PropertyDescriptor[] array;
        /** {@link PropertyDescriptor}の{@link Map}表現 */
        final Map<String, PropertyDescriptor> map;
        /** プロパティ名の {@link Set} */
        final Set<String> properties;

        /**
         * コンストラクタ。
         * 
         * <p>
         * クラスの{@link PropertyDescriptor}を取得して配列表現と{@link Map}表現、プロパティ名の{@link Set}を構築する。
         * なお、プロパティ名が{@literal class}となる{@link PropertyDescriptor}は無視する。
         * </p>
         * 
         * @param beanClass クラス
         * @throws IntrospectionException {@link Introspector#getBeanInfo(Class)}からスローされうる例外
         */
        PropertyDescriptors(Class<?> beanClass) throws IntrospectionException {
            final BeanInfo beanInfo = Introspector.getBeanInfo(beanClass);
            PropertyDescriptor[] pds = beanInfo.getPropertyDescriptors();
            map = Arrays.stream(pds)
                    .filter(pd -> !"class".equals(pd.getName()))
                    .collect(Collectors.toMap(PropertyDescriptor::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new));
            array = map.values().toArray(new PropertyDescriptor[0]);
            properties = Arrays.stream(array).map(PropertyDescriptor::getName).collect(Collectors.toSet());
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
            return map.computeIfAbsent(propertyName, k -> {
                throw new BeansException(
                        new IntrospectionException("Unknown property: " + propertyName));
            });
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
         * @throws BeansException {@link Introspector} によるBeanの解析に失敗した場合
         */
        static synchronized PropertyDescriptors get(final Class<?> beanClass) {
            if(beanClass.isRecord()) {
                throw new IllegalArgumentException("The target bean class must not be a record class.");
            }

            return CACHE.computeIfAbsent(beanClass, k -> {
                try {
                    return new PropertyDescriptors(beanClass);
                } catch (IntrospectionException e) {
                    throw new BeansException("Failed to introspect bean class. class name: " + beanClass.getName(), e);
                }
            });
        }
    }

    /**
     * レコードの{@link RecordComponent}をまとめたもの。
     *
     * @author Takayuki Uchida
     *
     */
    private static final class RecordComponents {

        /** キャッシュ本体 */
        private static final WeakHashMap<Class<?>, RecordComponents> CACHE = new WeakHashMap<>();
        /** {@link RecordComponent}の配列表現 */
        final RecordComponent[] array;
        /** {@link RecordComponent}の{@link Map}表現 */
        final Map<String, RecordComponent> map;
        /** プロパティ名の {@link Set} */
        final Set<String> properties;

        /**
         * コンストラクタ。
         *
         * <p>
         * クラスの{@link RecordComponent}を取得して配列表現と{@link Map}表現、プロパティ名の{@link Set}を構築する。
         * </p>
         *
         * @param beanClass クラス
         */
        RecordComponents(Class<?> beanClass) {
            RecordComponent[] rcs = beanClass.getRecordComponents();
            map = Arrays.stream(rcs).collect(Collectors.toMap(RecordComponent::getName, Function.identity(), (a, b) -> b, LinkedHashMap::new));
            array = map.values().toArray(new RecordComponent[0]);
            properties = Arrays.stream(array).map(RecordComponent::getName).collect(Collectors.toSet());
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
         * 指定したプロパティ名の{@link RecordComponent}を取得する。
         *
         * @param propertyName プロパティ名
         * @return 指定したコンポーネント名の {@link RecordComponent}
         * @throws BeansException {@code propertyName} に対応する{@link RecordComponent}が見つからない場合。
         */
        RecordComponent getRecordComponent(String propertyName) {
            return map.computeIfAbsent(propertyName, k -> {
                throw new BeansException("Unknown property: " + propertyName);
            });
        }

        /**
         * クラスに対応する{@link RecordComponents}を取得する。
         *
         * <p>
         * {@link RecordComponents}はキャッシュされる。
         * </p>
         *
         * @param recordClass クラス
         * @return キャッシュ
         */
        static synchronized RecordComponents get(Class<?> recordClass) {
            if(!recordClass.isRecord()) {
                throw new IllegalArgumentException("The target bean class must be a record class.");
            }

            return CACHE.computeIfAbsent(recordClass, k -> new RecordComponents(recordClass));

        }
    }
}
