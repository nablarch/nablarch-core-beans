package nablarch.core.beans;

import java.util.Map;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.annotation.Published;

/**
 * JavaBeansのプロパティ転送時に必要となる型変換を行うユーティリティクラス。
 * <p/>
 * 
 * 変換に使用する{@link Converter}は、{@link SystemRepository}から"conversionManager"という名前で
 * {@link ConversionManager}経由で取得する。
 * {@link ConversionManager}の呼び出しは初回の変換時のみである。
 * 
 * {@link ConversionManager}が{@link SystemRepository}に登録されていない場合は、
 * {@link BasicConversionManager}を使用する。
 * 
 * @author kawasima
 * @author tajima
 */
@Published(tag = "architect")
public final class ConversionUtil {

    /**
     * 隠蔽コンストラクタ。
     */
    private ConversionUtil() {
    }

    /**
     * {@link Converter}を用いて型変換する。
     * <p/>
     * 変換元のオブジェクトが{@code null}だった場合、{@code null}を返す。<br/>
     * 指定した型に対応する{@link Converter}が見つからなかった場合、
     * 変換元のオブジェクトを指定した型にキャストして返す。
     *
     * @param type 変換する型
     * @param value 変換元のオブジェクト
     * @param <T> 変換する型
     * @return 変換後のオブジェクト
     */
    @SuppressWarnings("unchecked")
    public static <T> T convert(final Class<T> type, final Object value) {
        if (value == null) {
            return null;
        }
        final Converter<T> converter = (Converter<T>) getConverters().get(type);
        if (converter != null) {
            return converter.convert(value);
        } else {
            return (T) value;
        }
    }

    /**
     * 指定された型に対応する{@link Converter}が存在するか判定する。
     *
     * @param type 変換したい型
     * @return {@link Converter}が存在する場合、{@code true}
     */
    public static boolean hasConverter(final Class<?> type) {
        return getConverters().containsKey(type);
    }

    /** デフォルトの{@link ConversionManager} */
    private static final ConversionManager DEFAULT_CONVERT_MANAGER = new BasicConversionManager();

    /**
     * コンバータを取得する。
     * @return コンバータ
     */
    private static Map<Class<?>, Converter<?>> getConverters() {
        ConversionManager manager = SystemRepository.get("conversionManager");
        return manager != null ? manager.getConverters() : DEFAULT_CONVERT_MANAGER.getConverters();
    }
}
