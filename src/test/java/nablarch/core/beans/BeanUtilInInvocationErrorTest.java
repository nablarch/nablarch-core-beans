package nablarch.core.beans;

import org.junit.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

public class BeanUtilInInvocationErrorTest {

    public static class NormalBean {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }
    }

    public record NormalRecord(
            String test
    ) {}

    public static class ErrorInGetterBean {
        public String getTest() {
            throw new RuntimeException("thrown by ErrorInGetterBean.");
        }

        public void setTest(String test) {
            // nop
        }
    }

    public static class ErrorInSetterBean {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            throw new RuntimeException("thrown by ErrorInSetterBean.");
        }
    }

    public static class ErrorInDefaultConstructorBean {
        private String test;

        public String getTest() {
            return test;
        }

        public void setTest(String test) {
            this.test = test;
        }

        public ErrorInDefaultConstructorBean() {
            throw new RuntimeException("thrown by ErrorInDefaultConstructorBean.");
        }

    }

    public record ErrorInAccessorRecord(
            String test
    ) {
        @Override
        public String test(){
            throw new RuntimeException("thrown by ErrorInAccessorRecord.");
        }
    }

    public record ErrorInCompactConstructorRecord(
            String test
    ) {
        public ErrorInCompactConstructorRecord {
            throw new RuntimeException("thrown by ErrorInCompactConstructorRecord.");
        }
    }

    @Test
    public void BeanからBeanへの移送_Beanからの値取得でエラーが発生するパターン() {
        ErrorInGetterBean src = new ErrorInGetterBean();
        src.setTest("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(NormalBean.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInGetterBean."));
    }

    @Test
    public void BeanからBeanへの移送_Bean生成でエラーが発生するパターン() {
        NormalBean src = new NormalBean();
        src.setTest("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(ErrorInDefaultConstructorBean.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInDefaultConstructorBean."));
    }

    @Test
    public void Beanへの値の設定_Beanへの値の設定でエラーが発生するパターン() {
        ErrorInSetterBean dest = new ErrorInSetterBean();

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.setProperty(dest, "test", "test"));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInSetterBean."));
    }

    @Test
    public void Mapからレコードへの移送_レコード生成でエラーが発生するパターン() {
        Map<String, Object> src = Map.of("test", "test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(ErrorInCompactConstructorRecord.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInCompactConstructorRecord."));

    }

    @Test
    public void BeanからMapへの移送_Beanからの値取得でエラーが発生するパターン() {
        ErrorInGetterBean src = new ErrorInGetterBean();

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createMapAndCopy(src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInGetterBean."));
    }

    @Test
    public void Beanからレコードへの移送_Beanからの値取得でエラーが発生するパターン() {
        ErrorInGetterBean src = new ErrorInGetterBean();

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(NormalRecord.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInGetterBean."));

    }

    @Test
    public void Beanからレコードへの移送_レコード生成でエラーが発生するパターン() {
        NormalBean src = new NormalBean();
        src.setTest("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(ErrorInCompactConstructorRecord.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInCompactConstructorRecord."));

    }

    @Test
    public void レコードからレコードへの移送_Beanからの値取得でエラーが発生するパターン() {
        ErrorInAccessorRecord src = new ErrorInAccessorRecord("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(NormalRecord.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInAccessorRecord."));

    }

    @Test
    public void レコードからレコードへの移送_レコード生成でエラーが発生するパターン() {
        NormalRecord src = new NormalRecord("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(ErrorInCompactConstructorRecord.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInCompactConstructorRecord."));

    }

    @Test
    public void レコードからBeanへの移送_レコードからの値取得でエラーが発生するパターン() {
        ErrorInAccessorRecord src = new ErrorInAccessorRecord("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(NormalBean.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInAccessorRecord."));
    }

    @Test
    public void レコードからBeanへの移送_Bean生成でエラーが発生するパターン() {
        NormalRecord src = new NormalRecord("test");

        BeansException result = assertThrows(BeansException.class, () -> BeanUtil.createAndCopy(ErrorInDefaultConstructorBean.class, src));
        assertThat(result.getCause(), instanceOf(InvocationTargetException.class));
        assertThat(result.getCause().getCause().getMessage(), is("thrown by ErrorInDefaultConstructorBean."));
    }



}
