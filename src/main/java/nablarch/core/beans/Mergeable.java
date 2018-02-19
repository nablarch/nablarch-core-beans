package nablarch.core.beans;

public interface Mergeable<T, U extends Mergeable<T, U>> extends Converter<T> {

    U merge(U other);
}
