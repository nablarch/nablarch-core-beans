package nablarch.core.beans;

import nablarch.core.util.annotation.Published;

/**
 * Java Beansの操作中に発生した例外を非検査例外でthrowするためのExceptionラッパー。
 *
 * @author kawasima
 * @author tajima
 */
@Published(tag = "architect")
public class BeansException extends RuntimeException {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    /**
     * Beanに対する{@code Map}からのあるプロパティのコピー操作に失敗したことを表す
     */
    private boolean copyPropertyFromMapInternalError = false;

    /**
     * コンストラクタ。
     *
     * @param message メッセージ
     */
    public BeansException(String message) {
        super(message);
    }

    /**
     * コンストラクタ。
     *
     * @param t 起因となった元例外
     */
    public BeansException(Throwable t) {
        super(t);
    }

    /**
     * コンストラクタ。
     *
     * @param msg メッセージ
     * @param t 起因となった元例外
     */
    public BeansException(String msg, Throwable t) {
        super(msg, t);
    }


    /**
     * Beanに対する{@code Map}からのあるプロパティのコピー操作に失敗したことを表すインスタンスを生成する
     *
     * @return Beanに対する{@code Map}からのあるプロパティのコピー操作に失敗したことを表すインスタンス
     */
    static BeansException createCopyPropertyFromMapInternalError() {
        BeansException e = new BeansException("An error occurred while copying a property from the map");
        e.copyPropertyFromMapInternalError = true;
        return e;
    }

    /**
     * この例外の原因がBeanに対する{@code Map}からのあるプロパティのコピー操作に失敗したことを表すものの場合{@code true}を返す
     *
     * @return の例外の原因がBeanに対する{@code Map}からのあるプロパティのコピー操作に失敗したことを表すものの場合{@code true}
     */
    boolean isNotCopyPropertyFromMapInternalError() {
        return !copyPropertyFromMapInternalError;
    }
}
