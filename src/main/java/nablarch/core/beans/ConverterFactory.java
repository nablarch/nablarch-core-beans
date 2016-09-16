package nablarch.core.beans;

import java.util.Map;

import nablarch.core.beans.Converter;

/**
 * {@link Converter}を格納したMapを生成するインタフェース。
 * 
 * @author Naoki Yamamoto
 */
public interface ConverterFactory {
    
    /**
     * {@link Converter}を格納したMapを作成する。
     * 
     * @return {@link Converter}を格納したMap
     */
    Map<Class<?>, Converter<?>> create();
}
