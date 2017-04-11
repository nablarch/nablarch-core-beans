package nablarch.core.beans.converter;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;
import static org.junit.Assert.assertThat;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import nablarch.core.beans.BeansException;
import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link ListExtensionConverter}のテスト。
 */
public class ListExtensionConverterTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private final ListExtensionConverter sut = new ListExtensionConverter();

    @Test
    public void List及びそのサブタイプの場合は変換可能であること() throws Exception {
        assertThat(sut.isConvertible(List.class), is(true));
        assertThat(sut.isConvertible(ArrayList.class), is(true));
        assertThat(sut.isConvertible(LinkedList.class), is(true));
    }

    @Test
    public void Listのサブタイプでない場合は変換不可であること() throws Exception {
        assertThat(sut.isConvertible(String.class), is(false));
        assertThat(sut.isConvertible(List[].class), is(false));
    }

    @Test
    public void ListからListに変換できること() throws Exception {
        final List<String> actual = sut.convert(List.class, Arrays.asList("あ", "い"));
        assertThat(actual, contains("あ", "い"));
    }

    @Test
    public void ListからArrayListに変換できること() throws Exception {
        final List<String> actual = sut.convert(ArrayList.class, Arrays.asList("a", "b", "c"));
        assertThat(actual, contains("a", "b", "c"));
    }

    @Test
    public void SetからListに変換できること() throws Exception {
        final List<String> actual = sut.convert(List.class, new HashSet<String>(Arrays.asList("1", "a")));
        assertThat(actual, contains("1", "a"));
    }

    @Test
    public void 配列からListに変換できること() throws Exception {
        final List<Integer> actual = sut.convert(List.class, new int[] {1, 2, 3});
        assertThat(actual, contains(Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)));
    }

    @Test
    public void Objectを持つListを変換できること() throws Exception {
        final List<Obj> actual = sut.convert(List.class, new Obj[] {new Obj("a"), new Obj("b")});

        assertThat(actual, contains(
                hasProperty("name", is("a")),
                hasProperty("name", is("b"))
        ));
    }

    @Test
    public void Listや配列以外を指定した場合例外が送出されること() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert 100 to List");

        sut.convert(List.class, 100);
    }

    @Test
    public void ListのInstance生成に失敗した場合例外が送出されること() throws Exception {
        expectedException.expect(BeansException.class);

        sut.convert(AbstractList.class, Collections.emptyList());
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