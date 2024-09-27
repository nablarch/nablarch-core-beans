package nablarch.core.beans;

import nablarch.core.util.annotation.Published;

/**
 * 拡張の型変換インタフェース。
 * <p>
 * 本インタフェースは、ロジックにより変換可能かを判断し型変換を行う場合に使用する。
 *
 * @param <T> 型変換後に返す型
 * @author siosio
 */
@Published(tag = "architect")
public interface ExtensionConverter<T> {

    /**
     * 型変換を行う。
     *
     * @param type 変換対象の型
     * @param src 変換元オブジェクト
     * @return 型変換を行った結果のオブジェクト
     */
    T convert(Class<? extends T> type, Object src);

    /**
     * このコンバータで値変換できるか否か。
     *
     * @param type 変換対象の型
     * @return 値変換出来る場合は{@code true}
     */
    boolean isConvertible(Class<?> type);

}
