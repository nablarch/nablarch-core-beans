package nablarch.core.beans.factory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;

import nablarch.core.beans.CopyOptions;
import nablarch.core.repository.ObjectLoader;
import nablarch.core.repository.SystemRepository;
import nablarch.test.support.SystemRepositoryResource;

public class GlobalCopyOptionsFactoryTest {

    //SystemRepositoryのクリーンアップをしたいだけなのでnullを渡してインスタンス化している
    @Rule
    public SystemRepositoryResource resource = new SystemRepositoryResource(null);

    @Test
    public void defaultCopyOptions() {
        GlobalCopyOptionsFactory sut = new GlobalCopyOptionsFactory();
        CopyOptions copyOptions = sut.create(null, null);

        assertThat(copyOptions.hasTypedConverter(Timestamp.class), is(false));
        assertThat(copyOptions.hasTypedConverter(Integer.class), is(false));
    }

    @Test
    public void setDatePatterns() {
        GlobalCopyOptionsFactory sut = new GlobalCopyOptionsFactory();
        sut.setDatePatterns(Collections.singletonList("yyyy/MM/dd"));
        CopyOptions copyOptions = sut.create(null, null);

        assertThat(copyOptions.hasTypedConverter(Timestamp.class), is(true));
        assertThat((Timestamp) copyOptions.convertByType(Timestamp.class, "2018/02/19"),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
    }

    @Test
    public void setNumberPatterns() {
        GlobalCopyOptionsFactory sut = new GlobalCopyOptionsFactory();
        sut.setNumberPatterns(Collections.singletonList("#,###"));
        CopyOptions copyOptions = sut.create(null, null);

        assertThat(copyOptions.hasTypedConverter(Integer.class), is(true));
        assertThat((String) copyOptions.convertByType(String.class, 1234567890),
                is("1,234,567,890"));
    }

    @Test
    public void getInstanceFromSystemRepository() {
        final GlobalCopyOptionsFactory factory = new GlobalCopyOptionsFactory();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return Collections.<String, Object> singletonMap("globalCopyOptionsFactory",
                        factory);
            }
        });
        GlobalCopyOptionsFactory sut = GlobalCopyOptionsFactory.getInstance();
        assertThat(sut, is(sameInstance(factory)));
    }

    @Test
    public void getInstanceByNew() {
        final GlobalCopyOptionsFactory factory = new GlobalCopyOptionsFactory();
        SystemRepository.load(new ObjectLoader() {
            @Override
            public Map<String, Object> load() {
                return Collections.<String, Object> singletonMap("globalCopyOptionsFactory",
                        factory);
            }
        });
        //登録されているGlobalCopyOptionsFactoryをクリアする
        SystemRepository.clear();
        GlobalCopyOptionsFactory sut = GlobalCopyOptionsFactory.getInstance();
        assertThat(sut, is(not(sameInstance(factory))));
    }
}
