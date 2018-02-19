package nablarch.core.beans.factory;

import java.util.Arrays;
import java.util.List;

import nablarch.core.beans.CopyOptions;
import nablarch.core.repository.SystemRepository;

public class CopyOptionsFactoryManager {

    private static final String COPY_OPTIONS_FACTORY_MANAGER = "copyOptionsFactoryManager";

    private List<CopyOptionsFactory> copyOptionsFactories;

    public List<CopyOptionsFactory> getCopyOptionsFactories() {
        return copyOptionsFactories;
    }

    public void setCopyOptionsFactories(List<CopyOptionsFactory> copyOptionsFactories) {
        this.copyOptionsFactories = copyOptionsFactories;
    }

    public CopyOptions createAndMergeCopyOptions(Object src, Object dest,
            CopyOptions copyOptionsAsCopyMethodArg) {
        CopyOptions copyOptions = copyOptionsAsCopyMethodArg;
        for (CopyOptionsFactory factory : copyOptionsFactories) {
            copyOptions = copyOptions.merge(factory.create(src, dest));
        }
        return copyOptions;
    }

    public static CopyOptionsFactoryManager getInstance() {
        CopyOptionsFactoryManager manager = SystemRepository.get(COPY_OPTIONS_FACTORY_MANAGER);
        if (manager != null) {
            return manager;
        }
        return createDefaultInstance();
    }

    private static CopyOptionsFactoryManager createDefaultInstance() {
        CopyOptionsFactoryManager instance = new CopyOptionsFactoryManager();
        instance.setCopyOptionsFactories(Arrays.asList(
                new AnnotationCopyOptionsFactory(true),
                new AnnotationCopyOptionsFactory(false),
                GlobalCopyOptionsFactory.getInstance()));
        return instance;
    }
}
