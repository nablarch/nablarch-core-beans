package nablarch.core.beans.converter;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import nablarch.core.beans.BeansException;
import nablarch.core.beans.ConversionException;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * {@link SetExtensionConverter}のテスト。
 */
public class SetExtensionConverterTest {
    
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private SetExtensionConverter sut = new SetExtensionConverter();

    @Test
    public void Set及びそのサブクラスには変換可能であること() throws Exception {
        assertThat(sut.isConvertible(Set.class), is(true));
        assertThat(sut.isConvertible(HashSet.class), is(true));
        assertThat(sut.isConvertible(SortedSet.class), is(true));
    }

    @Test
    public void Set以外の場合には変換不可であること() throws Exception {
        assertThat(sut.isConvertible(List.class), is(false));
        assertThat(sut.isConvertible(String.class), is(false));
    }

    @Test
    public void SetからSetに変換できること() throws Exception {
        final Set<String> actual = sut.convert(Set.class, Collections.singleton("a"));
        assertThat(actual, contains("a"));
    }

    @Test
    public void ListからSetに変換できること() throws Exception {
        final Set<Integer> actual = sut.convert(TreeSet.class, Arrays.asList(3, 2, 1));
        assertThat(actual, contains(1, 2, 3));
    }

    @Test
    public void 配列からSetに変換できること() throws Exception {
        final Set<Integer> actual = sut.convert(TreeSet.class, new int[] {3, 2, 1});
        assertThat(actual, contains(1, 2, 3));
    }


    @Test
    public void Setや配列以外を指定した場合例外が送出されること() throws Exception {
        expectedException.expect(ConversionException.class);
        expectedException.expectMessage("Can't convert 100 to Set");
        sut.convert(Set.class, 100);
    }
    
    @Test
    public void SetのInstance生成に失敗した場合例外が送出されること() throws Exception {
        expectedException.expect(BeansException.class);
        sut.convert(SortedSet.class, Collections.emptyList());
    }
}