package nablarch.core.beans.converter;

import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.Collections;
import java.util.List;

import nablarch.core.beans.Converter;

/**
 * 数値型の{@link Converter}の抽象基底クラス。
 * {@link String}型の値からの変換に使用できるメソッドを提供する。
 * @author Taichi Uragami
 *
 * @param <T> 変換後の型
 */
public abstract class AbstractNumberConverter<T extends Number> implements Converter<T> {

    /** 数値パターン */
    private final List<String> patterns;

    /**
     * デフォルトコンストラクタ
     */
    public AbstractNumberConverter() {
        this.patterns = Collections.emptyList();
    }

    /**
     * 数値パターンを設定してインスタンスを構築する。
     * 
     * @param patterns 数値パターン
     */
    public AbstractNumberConverter(List<String> patterns) {
        this.patterns = patterns;
    }

    /**
     * {@link String}型の値を変換する。
     * 
     * <p>
     * 数値パターンが設定されている場合は数値パターンによる変換を試行する。
     * 数値パターンは複数設定でき、1つずつ試行をして変換が出来た最初の値を返す。
     * 全ての数値パターンで変換が失敗した場合は{@link IllegalArgumentException}をスローする。
     * </p>
     * 
     * <p>
     * 数値パターンが設定されていない場合は{@link #convertFromStringWithoutPattern(String)}に処理を委譲する。
     * </p>
     * 
     * @param value 変換前の値
     * @return 変換された値
     */
    protected final T convertFromString(String value) {
        if (patterns.isEmpty() == false) {
            ParseException lastThrownException = null;
            for (String pattern : patterns) {
                try {
                    return this.convert(new DecimalFormat(pattern).parse(value));
                } catch (ParseException ignore) {
                    //複数のパターンを順番に試すのでParseExceptionは無視する
                    lastThrownException = ignore;
                }
            }
            //すべてのパターンが失敗した場合は例外をスロー
            throw new IllegalArgumentException(
                    "the string was not formatted " + patterns + ". number = " + value + ".",
                    lastThrownException);
        }
        return convertFromStringWithoutPattern(value);
    }

    /**
     * {@link String}型の値を数値パターンを伴わずに変換する。
     * 
     * @param value 変換前の値
     * @return 変換された値
     */
    protected abstract T convertFromStringWithoutPattern(String value);
}
