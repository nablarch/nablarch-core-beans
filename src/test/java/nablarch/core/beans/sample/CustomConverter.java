package nablarch.core.beans.sample;

import java.math.BigInteger;

import nablarch.core.beans.ConversionException;
import nablarch.core.beans.Converter;

public class CustomConverter implements Converter<BigInteger> {

    @Override
    public BigInteger convert(Object value) {
        // サンプルなので文字列のみ対応。
        if (value instanceof String) {
            try {
                return BigInteger.valueOf(Long.valueOf(String.class.cast(value)));
            } catch (NumberFormatException e) {
                throw new ConversionException(BigInteger.class, value);
            }
        } else {
            throw new ConversionException(BigInteger.class, value);
        }
    }

}
