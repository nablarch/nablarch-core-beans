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

}