package nablarch.core.beans.factory;

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import nablarch.core.beans.BeansException;
import nablarch.core.beans.CopyOption;
import nablarch.core.beans.CopyOptions;

public final class AnnotationCopyOptionsFactory implements CopyOptionsFactory {

    private final boolean useSrc;

    public AnnotationCopyOptionsFactory(boolean useSrc) {
        this.useSrc = useSrc;
    }

    @Override
    public CopyOptions create(Object src, Object dest) {
        CopyOptions.Builder builder = CopyOptions.options();
        Object obj = useSrc ? src : dest;
        try {
            apply(builder, obj.getClass());
        } catch (IntrospectionException e) {
            throw new BeansException(e);
        }
        return builder.build();
    }

    private static void apply(CopyOptions.Builder builder, Class<?> clazz)
            throws IntrospectionException {
        Map<String, Field> fields = new HashMap<String, Field>();
        for (Class<?> c = clazz; c != Object.class; c = c.getSuperclass()) {
            for (Field field : c.getDeclaredFields()) {
                String name = field.getName();
                if (fields.containsKey(name) == false) {
                    fields.put(name, field);
                }
            }
        }
        BeanInfo beanInfo = Introspector.getBeanInfo(clazz, Object.class);
        for (PropertyDescriptor pd : beanInfo.getPropertyDescriptors()) {
            String propertyName = pd.getName();
            Field field = fields.get(propertyName);
            if (field == null) {
                continue;
            }
            CopyOption copyOption = field.getAnnotation(CopyOption.class);
            if (copyOption == null) {
                continue;
            }
            if (copyOption.datePattern().length > 0) {
                builder.datePatternsByName(propertyName,
                        Arrays.asList(copyOption.datePattern()));
            }
            if (copyOption.numberPattern().length > 0) {
                builder.numberPatternsByName(propertyName,
                        Arrays.asList(copyOption.numberPattern()));
            }
        }
    }
}
