package nablarch.core.beans;

import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * {@link BeansException}のテスト
 * @author Kiyohito Itoh
 */
public class BeansExceptionTest {

    /**
     * 指定した引数で例外が生成されること。
     */
    @Test
    public void testConstructor() {

        BeansException e;
        Throwable cause = new RuntimeException();

        e = new BeansException(cause);
        assertEquals(cause, e.getCause());

        e = new BeansException("test-message", cause);
        assertEquals("test-message", e.getMessage());
        assertEquals(cause, e.getCause());
    }
}
