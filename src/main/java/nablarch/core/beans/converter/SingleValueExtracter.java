

package nablarch.core.beans.converter;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

/**
 * 設定対象の値がString[]の場合に、配列先頭の値を取得するクラス。
 * 任意の型のConverterを渡すことで、戻り値をその型に変換して取得することができる。
 * <pre><code>
 *     class IntegerConverter implements Converter<Integer> {
 *         // -- 中略 --
 *         return extractor.toSingleValue(value, this);  // Integerが返却される
 * </code></pre>
 *
 * @author T.Kawasaki
 * @author koichi asano
 */
public class SingleValueExtracter {

    /**
     * 隠蔽コンストラクタ。
     */
    private SingleValueExtracter() {
        
    }
    /**
     * 単一の値に変換する。
     *
     * @param array 配列
     * @param original 元の{@link Converter}
     * @param clazz 変換後の型のクラス
     * @param <T> 変換後の型
     * @return 配列先頭の値
     */
    public static <T> T toSingleValue(String[] array, Converter<T> original, Class<T> clazz) {
        if (array.length == 1) {
            return original.convert(array[0]);
        }
        throw new ConversionException(clazz, array);
    }
}


