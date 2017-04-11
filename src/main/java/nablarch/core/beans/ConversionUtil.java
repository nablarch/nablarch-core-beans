package nablarch.core.beans;

import java.util.List;
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
            final ExtensionConverter<T> extensionConverter = (ExtensionConverter<T>) getExtensionConverter(type);
            return extensionConverter != null ? extensionConverter.convert(type, value) : (T) value;
        }
    }

    /**
     * 指定された型に対応する{@link Converter}または、{@link ExtensionConverter}が存在するか判定する。
     *
     * @param type 変換したい型
     * @return {@link Converter}または{@link ExtensionConverter}が存在する場合、{@code true}
     */
    public static boolean hasConverter(final Class<?> type) {
        final boolean result = getConverters().containsKey(type);
        return result || getExtensionConverter(type) != null;
    }

    /** デフォルトの{@link ConversionManager} */
    private static final ConversionManager DEFAULT_CONVERT_MANAGER = new BasicConversionManager();

    /**
     * コンバータを取得する。
     * @return コンバータ
     */
    private static Map<Class<?>, Converter<?>> getConverters() {
        return getConversionManager().getConverters();
    }

    /**
     * 指定の型に変換する拡張コンバータを取得する。
     *
     * @param type 型
     * @return 拡張コンバータ(存在しない場合はnull)
     */
    private static ExtensionConverter<?> getExtensionConverter(final Class<?> type) {
        final List<ExtensionConverter<?>> convertor = getConversionManager().getExtensionConvertor();
        if (convertor == null) {
            return null;
        }
        for (final ExtensionConverter<?> converter : convertor) {
            if (converter.isConvertible(type)) {
                return converter;
            }
        }
        return null;
    }

    /**
     * {@link ConversionManager}を取得する。
     *
     * @return ConversionManager
     */
    private static ConversionManager getConversionManager() {
        final ConversionManager manager = SystemRepository.get("conversionManager");
        return manager != null ? manager : DEFAULT_CONVERT_MANAGER;
    }
}
