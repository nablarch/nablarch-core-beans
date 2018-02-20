package nablarch.core.beans;

/**
 * 異なる2つの{@link Converter}}インスタンスをマージ可能であることを表すインターフェース。
 * 
 * @author Taichi Uragami
 *
 * @param <T> {@link Converter}にバインドされる型
 * @param <U> 自分自身の型
 */
public interface Mergeable<T, U extends Mergeable<T, U>> extends Converter<T> {

    /**
     * 自分自身と他のインスタンスをマージして返す。
     * 
     * @param other 他のインスタンス
     * @return マージされたインスタンス
     */
    U merge(U other);
}
