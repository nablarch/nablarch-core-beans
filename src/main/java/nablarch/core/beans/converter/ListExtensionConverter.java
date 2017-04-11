package nablarch.core.beans.converter;

import java.util.ArrayList;
import java.util.List;

import nablarch.core.beans.BeansException;

/**
 * {@link List}及びその具象サブクラスに型変換するクラス。
 *
 * @author siosio
 */
@SuppressWarnings("rawtypes")
public class ListExtensionConverter extends CollectionExtensionConverterSupport<List> {

    @Override
    public boolean isConvertible(final Class<?> type) {
        return List.class.isAssignableFrom(type);
    }

    @Override
    protected List createInstance(final Class<? extends List> type) {
        if (type.equals(List.class)) {
            return new ArrayList();
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

