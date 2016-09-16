package nablarch.core.beans;

import nablarch.core.util.annotation.Published;

/**
 * JavaBeans間のプロパティ転送の際、型の変換を行うモジュールが実装する
 * インターフェース。
 *
 * @param <T> 転送先プロパティの型
 *
 * @author kawasima
 * @author tajima
 */
@Published(tag = "architect")
public interface Converter<T> {

    /**
     * 転送先プロパティの型に指定された値を変換する。
     *
     * @param value 値
     * @return T
     */
    T convert(Object value);
}
