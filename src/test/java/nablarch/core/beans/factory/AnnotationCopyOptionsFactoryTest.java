package nablarch.core.beans.factory;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.sql.Timestamp;

import org.junit.Test;

import nablarch.core.beans.CopyOption;
import nablarch.core.beans.CopyOptions;

/**
 * {@link AnnotationCopyOptionsFactory}のテスト。
 * 
 * @author Taichi Uragami
 *
 */
public class AnnotationCopyOptionsFactoryTest {

    @Test
    public void srcのアノテーションからCopyOptionsを構築する() {
        AnnotationCopyOptionsFactory sut = new AnnotationCopyOptionsFactory(true);
        SrcBean src = new SrcBean();
        DestBean dest = new DestBean();

        CopyOptions copyOptions = sut.create(src, dest);

        assertThat(copyOptions.hasNamedConverter("foo", String.class), is(true));
        assertThat(copyOptions.hasNamedConverter("bar", String.class), is(false));
        assertThat(copyOptions.hasNamedConverter("baz", String.class), is(false));

        assertThat(
                (String) copyOptions.convertByName("foo", String.class,
                        Timestamp.valueOf("2018-02-19 00:00:00")),
                is("2018/02/19"));
    }

    @Test
    public void destのアノテーションからCopyOptionsを構築する() {
        AnnotationCopyOptionsFactory sut = new AnnotationCopyOptionsFactory(false);
        SrcBean src = new SrcBean();
        DestBean dest = new DestBean();

        CopyOptions copyOptions = sut.create(src, dest);

        assertThat(copyOptions.hasNamedConverter("foo", String.class), is(false));
        assertThat(copyOptions.hasNamedConverter("bar", String.class), is(true));
        assertThat(copyOptions.hasNamedConverter("baz", String.class), is(false));

        assertThat(
                (String) copyOptions.convertByName("bar", String.class,
                        Timestamp.valueOf("2018-02-19 00:00:00")),
                is("2018.02.19"));
    }

    public static class SrcBean {

        @CopyOption(datePattern = "yyyy/MM/dd")
        private String foo;
        private String bar;
        private String baz;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBaz() {
            return baz;
        }

        public void setBaz(String baz) {
            this.baz = baz;
        }
    }

    public static class DestBean {

        private Timestamp foo;
        @CopyOption(datePattern = "yyyy.MM.dd")
        private Timestamp bar;
        private Timestamp baz;

        public Timestamp getFoo() {
            return foo;
        }

        public void setFoo(Timestamp foo) {
            this.foo = foo;
        }

        public Timestamp getBar() {
            return bar;
        }

        public void setBar(Timestamp bar) {
            this.bar = bar;
        }

        public Timestamp getBaz() {
            return baz;
        }

        public void setBaz(Timestamp baz) {
            this.baz = baz;
        }
    }
}
