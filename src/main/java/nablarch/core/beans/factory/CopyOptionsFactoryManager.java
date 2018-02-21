package nablarch.core.beans.factory;

import java.util.Arrays;
import java.util.List;

import nablarch.core.beans.BeanUtil;
import nablarch.core.beans.CopyOptions;
import nablarch.core.repository.SystemRepository;

/**
 * {@link CopyOptionsFactory}を管理して設定を集約した{@link CopyOptions}を生成するクラス。
 * 
 * @author Taichi Uragami
 *
 */
public class CopyOptionsFactoryManager {

    /** {@link CopyOptionsFactoryManager}をリポジトリから取得する際に使用する名前 */
    private static final String COPY_OPTIONS_FACTORY_MANAGER_NAME = "copyOptionsFactoryManager";
    /** {@link CopyOptionsFactory}のリスト */
    private List<CopyOptionsFactory> copyOptionsFactories;

    /**
     * {@link CopyOptionsFactory}のリストを取得する。
     * 
     * @return {@link CopyOptionsFactory}のリスト
     */
    public List<CopyOptionsFactory> getCopyOptionsFactories() {
        return copyOptionsFactories;
    }

    /**
     * {@link CopyOptionsFactory}のリストを設定する。
     * 
     * @param copyOptionsFactories {@link CopyOptionsFactory}のリスト
     */
    public void setCopyOptionsFactories(List<CopyOptionsFactory> copyOptionsFactories) {
        this.copyOptionsFactories = copyOptionsFactories;
    }

    /**
     * 保持している全ての{@link CopyOptionsFactory}で{@link CopyOptions}を作り、
     * マージしたものを返す。
     * 
     * @param src コピー元オブジェクト
     * @param dest コピー先オブジェクト
     * @param copyOptionsAsCopyMethodArg {@link BeanUtil#copy(Object, Object, CopyOptions) コピーメソッド}に渡された{@link CopyOptions}
     * @return マージされた{@link CopyOptions}
     */
    public CopyOptions createAndMergeCopyOptions(Object src, Object dest,
            CopyOptions copyOptionsAsCopyMethodArg) {
        CopyOptions copyOptions = copyOptionsAsCopyMethodArg;
        for (CopyOptionsFactory factory : copyOptionsFactories) {
            copyOptions = copyOptions.merge(factory.create(src, dest));
        }
        return copyOptions;
    }

    /**
     * {@link CopyOptionsFactoryManager}のインスタンスを取得する。
     * 
     * @return {@link CopyOptionsFactoryManager}のインスタンス
     */
    public static CopyOptionsFactoryManager getInstance() {
        CopyOptionsFactoryManager manager = SystemRepository.get(COPY_OPTIONS_FACTORY_MANAGER_NAME);
        if (manager != null) {
            return manager;
        }
        return createDefaultInstance();
    }

    private static CopyOptionsFactoryManager createDefaultInstance() {
        CopyOptionsFactoryManager instance = new CopyOptionsFactoryManager();
        instance.setCopyOptionsFactories(Arrays.<CopyOptionsFactory> asList(
                new AnnotationCopyOptionsFactory(true),
                new AnnotationCopyOptionsFactory(false)));
        return instance;
    }
}
