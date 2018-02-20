package nablarch.core.beans;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * プロパティ単位のコピーの設定をするアノテーション。
 * 
 * @author Taichi Uragami
 *
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CopyOption {

    /**
     * 日付パターン。
     * 
     * @return 日付パターン
     */
    String[] datePattern() default {};

    /**
     * 数値パターン。
     * 
     * @return 数値パターン
     */
    String[] numberPattern() default {};
}
