package nablarch.core.beans;

import nablarch.core.util.annotation.Published;

/**
 * BeanUtilsが提供するJavaBeansの処理において何らかの問題が
 * 発生した場合に送出される実行時例外。
 *
 * @author kawasima
 * @author tajima
 */
@Published(tag = "architect")
public class ConversionException extends RuntimeException {
    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * コンストラクタ。
     *
     * @param type 変換先の型
     * @param value 変換対象の値
     */
    public ConversionException(Class<?> type, Object value) {
        super(String.format("Can't convert %s to %s.", value, type.getSimpleName()));
    }
}
