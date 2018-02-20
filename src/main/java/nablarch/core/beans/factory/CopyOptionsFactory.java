package nablarch.core.beans.factory;

import nablarch.core.beans.BeanUtil;
import nablarch.core.beans.CopyOptions;

/**
 * {@link BeanUtil#copy(Object, Object) Beanのコピー}時に{@link CopyOptions}を生成するために使用されるインターフェース。
 * @author Taichi Uragami
 *
 */
public interface CopyOptionsFactory {

    /**
     * {@link CopyOptions}のインスタンスを生成する。
     * 
     * @param src コピー元オブジェクト
     * @param dest コピー先オブジェクト
     * @return 生成された{@link CopyOptions}のインスタンス
     */
    CopyOptions create(Object src, Object dest);
}
