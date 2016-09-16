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
}
