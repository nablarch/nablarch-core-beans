package nablarch.core.beans.converter;

import java.lang.reflect.Array;
import java.util.Collection;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.ExtensionConverter;

/**
 * {@link Collection}への型変換をサポートするクラス。
 *
 * @author siosio
 * @param <T> 変換対象の型
 */
@SuppressWarnings("rawtypes")
public abstract class CollectionExtensionConverterSupport<T extends Collection> implements ExtensionConverter<T> {

    @SuppressWarnings("unchecked")
    @Override
    public T convert(final Class<? extends T> type, final Object src) {
        final T instance = createInstance(type);
        if (src instanceof Collection) {
            instance.addAll(Collection.class.cast(src));
        } else if (src.getClass()
                      .isArray()) {
            final int length = Array.getLength(src);
            for (int i = 0; i < length; i++) {
                instance.add(Array.get(src, i));
            }
        } else {
            throw new ConversionException(type, src);
        }
        return instance;
    }

    /**
     * インスタンスを生成する。
     *
     * @param type 型
     * @return 生成したインスタンス
     */
    protected abstract T createInstance(final Class<? extends T> type);
}
