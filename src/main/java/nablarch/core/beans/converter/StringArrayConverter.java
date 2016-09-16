package nablarch.core.beans.converter;

import java.lang.reflect.Array;
import java.util.List;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * {@code String}の配列型への変換を行う {@link Converter} 。
 * <p/>
 * 変換元のオブジェクトがList、配列型であれば、
 * List、配列内の各要素を{@link StringConverter#convert(Object)}の変換結果を格納した配列を返却する。
 * <p/>
 * それ以外の場合は、{@link ConversionException}を送出する。
 *
 * @author tajima
 */
public class StringArrayConverter implements Converter<String[]> {

    /** String型への変換を行うコンバータ。 */
    private final StringConverter converter = new StringConverter();

    @Override
    public String[] convert(Object value) {
        String[] result = null;

        // List to String[]
        if (value instanceof List) {
            List<?> list = (List<?>) value;
            result = new String[list.size()];
            for (int i = 0; i < list.size(); i++) {
                result[i] = converter.convert(list.get(i));
            }

        // Array to String[]
        } else if (value.getClass().isArray()) {
            result = new String[Array.getLength(value)];
            for (int i = 0; i < Array.getLength(value); i++) {
                Object obj = Array.get(value, i);
                if (obj != null) {
                    result[i] = converter.convert(obj);
                }
            }

        } else {
            throw new ConversionException(String[].class, value);
        }

        return result;
    }
}
