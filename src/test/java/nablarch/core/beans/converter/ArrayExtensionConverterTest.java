package nablarch.core.beans.converter;

import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.List;

import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link ArrayExtensionConverter}のテスト。
 */
public class ArrayExtensionConverterTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final ArrayExtensionConverter sut = new ArrayExtensionConverter();

    @Test
    public void 配列は変換対象になる事() throws Exception {
        assertThat(sut.isConvertible(int[].class), is(true));
        assertThat(sut.isConvertible(Obj[].class), is(true));
    }

    @Test
    public void 非配列は変換対象外になること() throws Exception {
        assertThat(sut.isConvertible(Obj.class), is(false));
        assertThat(sut.isConvertible(List.class), is(false));
    }

    @Test
    public void 配列から配列に変換できること() throws Exception {
        final Object[] actual = sut.convert(Obj[].class, new Obj[] {new Obj("あ"), new Obj("か")});

        assertThat(actual, arrayContaining(
                hasProperty("name", is("あ")),
                hasProperty("name", is("か"))
        ));
    }

    @Test
    public void Listから配列に変換できること() throws Exception {
        final Integer[] actual = (Integer[]) sut.convert(Integer[].class, Arrays.asList(1, 2, 3));
        assertThat(actual, arrayContaining(1, 2, 3));
    }

    @Test
    public void Listと配列以外の場合には例外が送出されること() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert invalid to Integer[].");
        sut.convert(Integer[].class, "invalid");
    }

    public static class Obj {
        
        private String name;

        public Obj() {
        }

        public Obj(final String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        public void setName(final String name) {
            this.name = name;
        }
    }

}