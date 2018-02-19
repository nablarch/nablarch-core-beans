package nablarch.core.beans.factory;

import nablarch.core.beans.CopyOptions;

public interface CopyOptionsFactory {

    CopyOptions create(Object src, Object dest);
}
