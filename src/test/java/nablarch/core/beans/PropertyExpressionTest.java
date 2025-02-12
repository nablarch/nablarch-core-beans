package nablarch.core.beans;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;

/**
 * {@link PropertyExpression}のテストクラス。
 *
 * @author T.Kawasaki
 */
public class PropertyExpressionTest {

    @Test
    public void testInvalid() {
        try {
            new PropertyExpression("");
            fail("IllegalArgumentExceptionがスローされるはず");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("expression is null or blank."));
        }
    }

    @Test
    public void testIterator() {
        PropertyExpression sut = new PropertyExpression("aaa.bbb.ccc");
        assertThat(sut.getRoot(), is("aaa"));
        assertThat(sut.isNested(), is(true));

        sut = sut.rest();
        assertThat(sut.getRoot(), is("bbb"));
        assertThat(sut.isNested(), is(true));
        assertThat(sut.getParentKey(), is("aaa"));
        assertThat(sut.getAbsoluteRawKey(), is("aaa.bbb.ccc"));
        assertThat(sut.getRawKey(), is("bbb.ccc"));

        sut = sut.rest();
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.isNode(), is(true));
        assertThat(sut.getParentKey(), is("aaa.bbb"));
        assertThat(sut.getAbsoluteRawKey(), is("aaa.bbb.ccc"));
        assertThat(sut.getRawKey(), is("ccc"));

        try {
            sut.rest();
            fail("IllegalArgumentExceptionがスローされるはず");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("invalid."));
        }

        sut = new PropertyExpression("ccc");
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.isNode(), is(true));
        assertThat(sut.getParentKey(), is(""));
        assertThat(sut.getAbsoluteRawKey(), is("ccc"));
        assertThat(sut.getRawKey(), is("ccc"));

    }

    @Test
    public void testLeaf() {
        PropertyExpression sut = new PropertyExpression("aaa.bbb.ccc");

        sut = sut.leaf();
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.getParentKey(), is("aaa.bbb"));
        assertThat(sut.getAbsoluteRawKey(), is("aaa.bbb.ccc"));
        assertThat(sut.getRawKey(), is("ccc"));

        sut = sut.leaf();
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.getParentKey(), is("aaa.bbb"));
        assertThat(sut.getAbsoluteRawKey(), is("aaa.bbb.ccc"));
        assertThat(sut.getRawKey(), is("ccc"));

        sut = new PropertyExpression("ccc");
        sut = sut.leaf();
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.getParentKey(), is(""));
        assertThat(sut.getAbsoluteRawKey(), is("ccc"));
        assertThat(sut.getRawKey(), is("ccc"));
    }

    @Test
    public void testSibling() {
        PropertyExpression sut = new PropertyExpression("aaa.bbb.ccc");
        PropertyExpression rest = sut.rest();

        PropertyExpression sibling = rest.sibling("xxx");
        assertThat(sibling.getRoot(), is("xxx"));
        assertThat(sibling.isNested(), is(false));
        assertThat(sibling.getParentKey(), is("aaa"));
        assertThat(sibling.getAbsoluteRawKey(), is("aaa.xxx"));
        assertThat(sibling.getRawKey(), is("xxx"));

        sibling = rest.sibling("xxx.yyy");
        assertThat(sibling.getRoot(), is("xxx"));
        assertThat(sibling.isNested(), is(true));
        assertThat(sibling.getParentKey(), is("aaa"));
        assertThat(sibling.getAbsoluteRawKey(), is("aaa.xxx.yyy"));
        assertThat(sibling.getRawKey(), is("xxx.yyy"));
    }
}