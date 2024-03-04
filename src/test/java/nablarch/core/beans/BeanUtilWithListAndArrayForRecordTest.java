package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;

/**
 * [nablarch.core.beans.BeanUtil]の[java.util.List]及び配列に関するテスト。
 */
@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilWithListAndArrayForRecordTest {

    public record WithList(
            String name,
            List<String> strs,
            ArrayList<Integer> nums,
            LinkedList<Obj> objects) {}

    public record WithSet(
            String name,
            Set<String> strs,
            TreeSet<Integer> nums,
            Set<Obj> objects) {}

    public record Obj(String name){};

    @Before
    public void setUp() {
        SystemRepository.clear();
    }

    @Test
    public void createAndCopy_オブジェクトからSetをコピーできること() {
        WithList src = new WithList(
                "なまえ",
                List.of("a", "1", "aaa"),
                new ArrayList<>() {{
                    add(1);
                    add(10);
                    add(100);
                }},
                new LinkedList<>() {{
                    add(new Obj("a"));
                    add(new Obj("aa"));
                }});

        WithSet actual = BeanUtil.createAndCopy(WithSet.class, src);
        assertThat(actual.name(), is("なまえ"));
        assertThat(actual.strs(), containsInAnyOrder("a", "1", "aaa"));
        assertThat(actual.nums(), containsInAnyOrder(1, 10, 100));
        assertThat(actual.objects(), containsInAnyOrder(new Obj("a"), new Obj("aa")));

    }

    @Test
    public void createMapAndCopy_SetがMapにコピーできること() {
        WithSet src = new WithSet(
                "なまえ",
                Set.of("a", "1", "aaa"),
                new TreeSet<>(Set.of(1, 10, 100)),
                Set.of(new Obj("a"), new Obj("aa")));

        Map<String, ?> actual = BeanUtil.createMapAndCopy(src);
        assertThat(actual.get("name"), is("なまえ"));
        assertThat((Set<?>)actual.get("strs"), containsInAnyOrder("a", "1", "aaa"));
        assertThat((TreeSet<?>)actual.get("nums"), containsInAnyOrder(1, 10, 100));
        assertThat((Set<?>)actual.get("objects"), containsInAnyOrder(new Obj("a"), new Obj("aa")));
    }
}

