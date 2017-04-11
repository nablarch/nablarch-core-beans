package nablarch.core.beans;

import java.util.List;
import java.util.Map;

import nablarch.core.util.annotation.Published;

/**
 * 型変換機能を管理するインタフェース。
 * 
 * @author Naoki Yamamoto
 */
@Published(tag = "architect")
public interface ConversionManager {
    
    /**
     * 型変換に使用する{@link Converter}を格納したMapを取得する。
     * <p/>
     * Mapのキーには変換先の型、値にはキーで指定した型に対応する{@link Converter}を設定する。
     * 
     * @return {@link Converter}を格納したMap
     */
    Map<Class<?>, Converter<?>> getConverters();

    /**
     * 拡張の型変換リストを返す。
     * <p>
     * 優先順位が高いものをリストのより先頭に設定する必要がある。
     *
     * @return 拡張型変換のリスト
     */
    List<ExtensionConverter<?>> getExtensionConvertor();
}
