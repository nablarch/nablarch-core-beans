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
     * 例外の原因が、ネストした（グルーピングされた）プロパティの一括操作に失敗したことを表す
     */
    private boolean nestedPropertyOperationFailure = false;

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
     * ネストした（グルーピングされた）プロパティ操作に失敗したことを表すインスタンスを生成する
     *
     * @param nestedPropertyOperationFailure ネストした（グルーピングされた）プロパティ操作に失敗したかどうか
     */
    private BeansException(boolean nestedPropertyOperationFailure) {
        this.nestedPropertyOperationFailure = nestedPropertyOperationFailure;
    }

    /**
     * ネストした（グルーピングされた）プロパティ操作に失敗したことを表すインスタンスを生成する
     *
     * @return ネストした（グルーピングされた）プロパティ操作に失敗したことを表すインスタンス
     */
    static BeansException createNestedPropertiesOperationFailure() {
        return new BeansException(true);
    }

    /**
     * この例外の原因がネストした（グルーピングされた）プロパティ操作に失敗したものである場合{@code true}を返す
     *
     * @return この例外の原因がネストした（グルーピングされた）プロパティ操作に失敗したものである場合{@code true}
     */
    boolean isNestedPropertyOperationFailure() {
        return nestedPropertyOperationFailure;
    }

    /**
     * この例外の原因がシンプルなプロパティ操作に失敗したものである場合{@code true}を返す
     *
     * @return この例外の原因がシンプルなプロパティ操作に失敗したものである場合{@code true}
     */
    boolean isNodePropertyOperationFailure() {
        return !isNestedPropertyOperationFailure();
    }
}
