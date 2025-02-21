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
     * 未処理かどうか
     */
    private boolean notHandled;

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
     * @param notHandled 未処理かどうか
     */
    public BeansException(boolean notHandled) {
        this.notHandled = notHandled;
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
     * 例外がすでに処理済みかどうか
     * @return 未処理の場合はtrue/処理済みの場合はfalse
     */
    public boolean hasNotBeenHandled() {
        return notHandled;
    }
}
