package nablarch.core.beans.converter;

import java.lang.reflect.Array;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.ExtensionConverter;

/**
 * 配列に型変換するクラス。
 *
 * @author siosio
 */
@SuppressWarnings("rawtypes")
public class ArrayExtensionConverter implements ExtensionConverter<Object[]> {

    @Override
    public Object[] convert(final Class<? extends Object[]> type, final Object src) {
        final int length = getLength(type, src);
        final Object instance = Array.newInstance(type.getComponentType(), length);

        for (int i = 0; i < length; i++) {
            Array.set(instance, i, getValue(src, i));
        }
        return Object[].class.cast(instance);
    }

    /**
     * 変換元オブジェクトから指定の要素を返す。
     *
     * @param object 変換元オブジェクト(List or 配列)
     * @param position 要素
     * @return 指定要素の値
     */
    private static Object getValue(final Object object, final int position) {
        if (object instanceof List) {
            return ((List) object).get(position);
        } else {
            return Array.get(object, position);
        }
    }

    /**
     * 変換元オブジェクトから変換先の配列のサイズを導出する。
     *
     * @param type 変換先の型
     * @param src 変換元オブジェクト
     * @return 配列サイズ
     */
    @SuppressWarnings("OverlyStrongTypeCast")
    private static int getLength(final Class<? extends Object[]> type, final Object src) {
        if (src.getClass()
               .isArray()) {
            return Array.getLength(src);
        } else if (src instanceof List) {
            return ((List) src).size();
        } else {
            throw new ConversionException(type, src);
        }
    }

    @Override
    public boolean isConvertible(final Class<?> type) {
        return type.isArray();
    }
}
