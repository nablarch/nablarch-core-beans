package nablarch.core.beans.converter;

import java.util.HashSet;
import java.util.Set;

import nablarch.core.beans.BeansException;

/**
 * {@link Set}及びその具象サブクラスに型変換するクラス。
 *
 * @author siosio
 */
@SuppressWarnings("rawtypes")
public class SetExtensionConverter extends CollectionExtensionConverterSupport<Set> {

    @Override
    public boolean isConvertible(final Class<?> type) {
        return Set.class.isAssignableFrom(type);
    }

    @Override
    protected Set createInstance(final Class<? extends Set> type) {
        if (type.equals(Set.class)) {
            return new HashSet();
        } else {
            try {
                return type.getConstructor()
                           .newInstance();
            } catch (Exception e) {
                throw new BeansException(e);
            }
        }
    }
}
