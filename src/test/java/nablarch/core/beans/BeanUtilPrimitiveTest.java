package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.beans.HasPropertyWithValue.hasProperty;

import org.junit.Assert;
import org.junit.Test;

/**
 * {@link nablarch.core.beans.BeanUtil}のプリミティブに関するテスト。
 */
public class BeanUtilPrimitiveTest {

    private static class Src {

        private final String intNum;

        private final String longNum;

        private final String shortNum;

        private final String booleanValue;

        public Src(final String intNum, final String longNum, final String shortNum, final String booleanValue) {
            this.intNum = intNum;
            this.longNum = longNum;
            this.shortNum = shortNum;
            this.booleanValue = booleanValue;
        }

        public String getIntNum() {
            return intNum;
        }

        public String getLongNum() {
            return longNum;
        }

        public String getShortNum() {
            return shortNum;
        }

        public String getBooleanValue() {
            return booleanValue;
        }
    }

    public static class PrimitiveBean {

        private int intNum;

        private long longNum;

        private short shortNum;
        
        private boolean booleanValue;

        public PrimitiveBean() {
        }

        public void setIntNum(final int intNum) {
            this.intNum = intNum;
        }

        public int getIntNum() {
            return intNum;
        }

        public long getLongNum() {
            return longNum;
        }

        public void setLongNum(final long longNum) {
            this.longNum = longNum;
        }

        public short getShortNum() {
            return shortNum;
        }

        public void setShortNum(final short shortNum) {
            this.shortNum = shortNum;
        }

        public boolean isBooleanValue() {
            return booleanValue;
        }

        public void setBooleanValue(final boolean booleanValue) {
            this.booleanValue = booleanValue;
        }
    }

    @Test
    public void copyPrimitive() throws Exception {
        final Src src = new Src("12345", "54321", "99", "ON");
        final PrimitiveBean actual = BeanUtil.createAndCopy(PrimitiveBean.class, src);

        Assert.assertThat(actual, allOf(
                hasProperty("intNum", is(12345)),
                hasProperty("longNum", is(54321L)),
                hasProperty("shortNum", is((short) 99)),
                hasProperty("booleanValue", is(true))
        ));
    }

    @Test
    public void copyFromEmptyString() throws Exception {
        final Src src = new Src("", "", "", "");

        final PrimitiveBean actual = new PrimitiveBean();
        actual.setIntNum(1);
        actual.setLongNum(2);
        actual.setShortNum((short) 3);
        actual.setBooleanValue(true);
        
        BeanUtil.copy(src, actual);

        Assert.assertThat(actual, allOf(
                hasProperty("intNum", is(0)),
                hasProperty("longNum", is(0L)),
                hasProperty("shortNum", is((short) 0)),
                hasProperty("booleanValue", is(false))
        ));
    }
}
