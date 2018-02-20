package nablarch.core.beans.factory;

import java.util.List;

import nablarch.core.beans.CopyOptions;
import nablarch.core.repository.SystemRepository;

/**
 * コンポーネント設定ファイルで行った設定をもとに{@link CopyOptions}を構築する{@link CopyOptionsFactory}の実装。
 * 
 * @author Taichi Uragami
 *
 */
public final class GlobalCopyOptionsFactory implements CopyOptionsFactory {

    /** {@link GlobalCopyOptionsFactory}をリポジトリから取得する際に使用する名前 */
    private static final String GLOBAL_COPY_OPTIONS_FACTORY_NAME = "globalCopyOptionsFactory";
    /** 日付パターン */
    private List<String> datePatterns;
    /** 数値パターン */
    private List<String> numberPatterns;

    /**
     * 日付パターンを取得する。
     * 
     * @return 日付パターン
     */
    public List<String> getDatePatterns() {
        return datePatterns;
    }

    /**
     * 日付パターンを設定する。
     * 
     * @param datePatterns 日付パターン
     */
    public void setDatePatterns(List<String> datePatterns) {
        this.datePatterns = datePatterns;
    }

    /**
     * 数値パターンを取得する。
     * 
     * @return 数値パターン
     */
    public List<String> getNumberPatterns() {
        return numberPatterns;
    }

    /**
     * 数値パターンを設定する。
     * 
     * @param numberPatterns 数値パターン
     */
    public void setNumberPatterns(List<String> numberPatterns) {
        this.numberPatterns = numberPatterns;
    }

    @Override
    public CopyOptions create(Object src, Object dest) {
        CopyOptions.Builder builder = CopyOptions.options();
        if (datePatterns != null && datePatterns.isEmpty() == false) {
            builder.datePatterns(datePatterns);
        }
        if (numberPatterns != null && numberPatterns.isEmpty() == false) {
            builder.numberPatterns(numberPatterns);
        }
        return builder.build();
    }

    /**
     * {@link GlobalCopyOptionsFactory}のインスタンスを取得する。
     * 
     * @return {@link GlobalCopyOptionsFactory}のインスタンス
     */
    public static GlobalCopyOptionsFactory getInstance() {
        GlobalCopyOptionsFactory factory = SystemRepository.get(GLOBAL_COPY_OPTIONS_FACTORY_NAME);
        if (factory != null) {
            return factory;
        }
        return new GlobalCopyOptionsFactory();
    }
}
