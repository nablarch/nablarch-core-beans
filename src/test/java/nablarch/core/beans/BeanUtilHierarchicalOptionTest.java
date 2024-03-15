package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

import java.sql.Timestamp;

import org.junit.Rule;
import org.junit.Test;

import nablarch.test.support.SystemRepositoryResource;

/**
 * {@link BeanUtil#copy(Object, Object, CopyOptions)}で全体設定、
 * アノテーションによる設定、メソッド引数に渡した設定の適用を確認するテスト。
 * 
 * @author Taichi Uragami
 *
 */

public class BeanUtilHierarchicalOptionTest {

    @Rule
    public SystemRepositoryResource resource = new SystemRepositoryResource(
            "nablarch/core/beans/sample/global-copy-options-test.xml");

    @Test
    public void 文字列から日付と数値への変換_copy() {
        SrcBean src = new SrcBean();
        src.setGlobalOptionDate("2018/02/19");
        src.setGlobalOptionNumber("1,234,567,890");
        src.setAnnotatedOptionAtSrcBean("2018-02-19");
        src.setAnnotatedOptionAtDestBean("2018.02.19");
        src.setMethodArgOption("2018_02_19");

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        DestBean dest = new DestBean();
        BeanUtil.copy(src, dest, copyOptions);

        assertThat(dest.getGlobalOptionDate(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getGlobalOptionNumber(), is(1234567890));
        assertThat(dest.getAnnotatedOptionAtSrcBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getAnnotatedOptionAtDestBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getMethodArgOption(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
    }

    @Test
    public void 日付と数値から文字列への変換_copy() {
        DestBean dest = new DestBean();
        dest.setGlobalOptionDate(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setGlobalOptionNumber(1234567890);
        dest.setAnnotatedOptionAtSrcBean(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setAnnotatedOptionAtDestBean(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setMethodArgOption(Timestamp.valueOf("2018-02-19 00:00:00"));

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        SrcBean src = new SrcBean();
        BeanUtil.copy(dest, src, copyOptions);

        assertThat(src.getGlobalOptionDate(), is("2018/02/19"));
        assertThat(src.getGlobalOptionNumber(), is("1,234,567,890"));
        assertThat(src.getAnnotatedOptionAtSrcBean(), is("2018-02-19"));
        assertThat(src.getAnnotatedOptionAtDestBean(), is("2018.02.19"));
        assertThat(src.getMethodArgOption(), is("2018_02_19"));
    }

    @Test
    public void 文字列から日付と数値への変換_createAndCopy() {
        SrcBean src = new SrcBean();
        src.setGlobalOptionDate("2018/02/19");
        src.setGlobalOptionNumber("1,234,567,890");
        src.setAnnotatedOptionAtSrcBean("2018-02-19");
        src.setAnnotatedOptionAtDestBean("2018.02.19");
        src.setMethodArgOption("2018_02_19");

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        DestBean dest = BeanUtil.createAndCopy(DestBean.class, src, copyOptions);

        assertThat(dest.getGlobalOptionDate(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getGlobalOptionNumber(), is(1234567890));
        assertThat(dest.getAnnotatedOptionAtSrcBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getAnnotatedOptionAtDestBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.getMethodArgOption(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
    }

    @Test
    public void 日付と数値から文字列への変換_createAndCopy() {
        DestBean dest = new DestBean();
        dest.setGlobalOptionDate(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setGlobalOptionNumber(1234567890);
        dest.setAnnotatedOptionAtSrcBean(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setAnnotatedOptionAtDestBean(Timestamp.valueOf("2018-02-19 00:00:00"));
        dest.setMethodArgOption(Timestamp.valueOf("2018-02-19 00:00:00"));

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        SrcBean src = BeanUtil.createAndCopy(SrcBean.class, dest, copyOptions);

        assertThat(src.getGlobalOptionDate(), is("2018/02/19"));
        assertThat(src.getGlobalOptionNumber(), is("1,234,567,890"));
        assertThat(src.getAnnotatedOptionAtSrcBean(), is("2018-02-19"));
        assertThat(src.getAnnotatedOptionAtDestBean(), is("2018.02.19"));
        assertThat(src.getMethodArgOption(), is("2018_02_19"));
    }

    public static class SrcBean {

        private String globalOptionDate;
        private String globalOptionNumber;
        @CopyOption(datePattern = "yyyy-MM-dd")
        private String annotatedOptionAtSrcBean;
        private String annotatedOptionAtDestBean;
        private String methodArgOption;

        public String getGlobalOptionDate() {
            return globalOptionDate;
        }

        public void setGlobalOptionDate(String globalOptionDate) {
            this.globalOptionDate = globalOptionDate;
        }

        public String getGlobalOptionNumber() {
            return globalOptionNumber;
        }

        public void setGlobalOptionNumber(String globalOptionNumber) {
            this.globalOptionNumber = globalOptionNumber;
        }

        public String getAnnotatedOptionAtSrcBean() {
            return annotatedOptionAtSrcBean;
        }

        public void setAnnotatedOptionAtSrcBean(String annotatedOptionAtSrcBean) {
            this.annotatedOptionAtSrcBean = annotatedOptionAtSrcBean;
        }

        public String getAnnotatedOptionAtDestBean() {
            return annotatedOptionAtDestBean;
        }

        public void setAnnotatedOptionAtDestBean(String annotatedOptionAtDestBean) {
            this.annotatedOptionAtDestBean = annotatedOptionAtDestBean;
        }

        public String getMethodArgOption() {
            return methodArgOption;
        }

        public void setMethodArgOption(String methodArgOption) {
            this.methodArgOption = methodArgOption;
        }
    }

    public static class DestBean {

        private Timestamp globalOptionDate;
        private Integer globalOptionNumber;
        private Timestamp annotatedOptionAtSrcBean;
        @CopyOption(datePattern = "yyyy.MM.dd")
        private Timestamp annotatedOptionAtDestBean;
        private Timestamp methodArgOption;

        public Timestamp getGlobalOptionDate() {
            return globalOptionDate;
        }

        public void setGlobalOptionDate(Timestamp globalOptionDate) {
            this.globalOptionDate = globalOptionDate;
        }

        public Integer getGlobalOptionNumber() {
            return globalOptionNumber;
        }

        public void setGlobalOptionNumber(Integer globalOptionNumber) {
            this.globalOptionNumber = globalOptionNumber;
        }

        public Timestamp getAnnotatedOptionAtSrcBean() {
            return annotatedOptionAtSrcBean;
        }

        public void setAnnotatedOptionAtSrcBean(Timestamp annotatedOptionAtSrcBean) {
            this.annotatedOptionAtSrcBean = annotatedOptionAtSrcBean;
        }

        public Timestamp getAnnotatedOptionAtDestBean() {
            return annotatedOptionAtDestBean;
        }

        public void setAnnotatedOptionAtDestBean(Timestamp annotatedOptionAtDestBean) {
            this.annotatedOptionAtDestBean = annotatedOptionAtDestBean;
        }

        public Timestamp getMethodArgOption() {
            return methodArgOption;
        }

        public void setMethodArgOption(Timestamp methodArgOption) {
            this.methodArgOption = methodArgOption;
        }
    }
}
