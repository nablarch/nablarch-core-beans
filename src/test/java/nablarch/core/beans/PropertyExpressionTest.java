package nablarch.core.beans;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

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

        sut = sut.rest();
        assertThat(sut.getRoot(), is("ccc"));
        assertThat(sut.isNested(), is(false));
        assertThat(sut.isNode(), is(true));

        try {
            sut.rest();
            fail("IllegalArgumentExceptionがスローされるはず");
        } catch (IllegalArgumentException e) {
            assertThat(e.getMessage(), is("invalid."));
        }
    }

}