package nablarch.core.beans.converter;

import java.lang.reflect.Array;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * {@code Object}の配列型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元のオブジェクトがList、配列型であれば、
 * List、配列内の各要素をそのまま格納した{@code Object}型の配列を返却する。
 * <p/>
 * それ以外の場合は、{@link ConversionException}を送出する。
 *
 * @author tajima
 */
public class ObjectArrayConverter implements Converter<Object[]> {
    @Override
    public Object[] convert(Object value) {


        // List to Object[]
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            return list.toArray(new Object[list.size()]);

        // Array to Object[]
        } else if (value.getClass().isArray()) {
            Object[] result = new Object[Array.getLength(value)];
            for (int i = 0; i < Array.getLength(value); i++) {
                result[i] = Array.get(value, i);
            }
            return result;

        } else {
            throw new ConversionException(Object[].class, value);
        }
    }
}
