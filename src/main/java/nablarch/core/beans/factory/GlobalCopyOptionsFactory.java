package nablarch.core.beans.factory;

import java.util.List;

import nablarch.core.beans.CopyOptions;
import nablarch.core.repository.SystemRepository;

public final class GlobalCopyOptionsFactory implements CopyOptionsFactory {

    private static final String GLOBAL_COPY_OPTIONS_FACTORY_NAME = "globalCopyOptionsFactory";

    private List<String> datePatterns;
    private List<String> numberPatterns;

    public List<String> getDatePatterns() {
        return datePatterns;
    }

    public void setDatePatterns(List<String> datePatterns) {
        this.datePatterns = datePatterns;
    }

    public List<String> getNumberPatterns() {
        return numberPatterns;
    }

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

    public static GlobalCopyOptionsFactory getInstance() {
        GlobalCopyOptionsFactory factory = SystemRepository.get(GLOBAL_COPY_OPTIONS_FACTORY_NAME);
        if (factory != null) {
            return factory;
        }
        return new GlobalCopyOptionsFactory();
    }
}
