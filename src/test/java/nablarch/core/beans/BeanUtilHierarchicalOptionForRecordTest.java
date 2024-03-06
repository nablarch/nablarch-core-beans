package nablarch.core.beans;

import nablarch.test.support.SystemRepositoryResource;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;
import java.util.Date;

import static nablarch.core.beans.BeanUtilConversionCustomizedTest.date;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

/**
 * {@link BeanUtil#copy(Object, Object, CopyOptions)}で全体設定、
 * アノテーションによる設定、メソッド引数に渡した設定の適用を確認するテスト。
 * レコードでも正しく動作することを確認する。
 * 
 * @author Taichi Uragami
 *
 */
public class BeanUtilHierarchicalOptionForRecordTest {

    @Rule
    public SystemRepositoryResource resource = new SystemRepositoryResource(
            "nablarch/core/beans/sample/global-copy-options-test.xml");

    @Test
    public void 文字列から日付と数値への変換_copy() {
        SrcRecord src = new SrcRecord(
                "2018/02/19",
                "1,234,567,890",
                "2018-02-19",
                "2018.02.19",
                "2018_02_19");

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        DestRecord dest = BeanUtil.createAndCopy(DestRecord.class, src, copyOptions);

        assertThat(dest.globalOptionDate(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.globalOptionNumber(), is(1234567890));
        assertThat(dest.annotatedOptionAtSrcBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.annotatedOptionAtDestBean(),
                is(Timestamp.valueOf("2018-02-19 00:00:00")));
        assertThat(dest.methodArgOption(), is(Timestamp.valueOf("2018-02-19 00:00:00")));
    }

    @Test
    public void 日付と数値から文字列への変換_copy() {
        DestRecord dest = new DestRecord(
                Timestamp.valueOf("2018-02-19 00:00:00"),
                1234567890,
                Timestamp.valueOf("2018-02-19 00:00:00"),
                Timestamp.valueOf("2018-02-19 00:00:00"),
                Timestamp.valueOf("2018-02-19 00:00:00"));

        CopyOptions copyOptions = CopyOptions.options()
                .datePatternByName("methodArgOption", "yyyy_MM_dd").build();

        SrcRecord src = BeanUtil.createAndCopy(SrcRecord.class, dest, copyOptions);

        assertThat(src.globalOptionDate(), is("2018/02/19"));
        assertThat(src.globalOptionNumber(), is("1,234,567,890"));
        assertThat(src.annotatedOptionAtSrcBean(), is("2018-02-19"));
        assertThat(src.annotatedOptionAtDestBean(), is("2018.02.19"));
        assertThat(src.methodArgOption(), is("2018_02_19"));
    }

    public record SrcRecord(
            String globalOptionDate,
            String globalOptionNumber,
            @CopyOption(datePattern = "yyyy-MM-dd")
            String annotatedOptionAtSrcBean,
            String annotatedOptionAtDestBean,
            String methodArgOption) {}

    public record DestRecord(
            Timestamp globalOptionDate,
            Integer globalOptionNumber,
            Timestamp annotatedOptionAtSrcBean,
            @CopyOption(datePattern = "yyyy.MM.dd")
            Timestamp annotatedOptionAtDestBean,
            Timestamp methodArgOption) {}


    @Test
    public void CopyOptionアノテーションのテスト_Beanからレコード() {
        SourceBeanWithAnnotation src = new SourceBeanWithAnnotation();
        src.dateAtSrc = "2024_02_29";
        src.dateAtDest = "2024~03~01";
        src.numberAtSrc = "12,3456,7890";
        src.numberAtDest = "12345,67890";

        DestRecordWithAnnotation dest = BeanUtil.createAndCopy(DestRecordWithAnnotation.class, src,CopyOptions.empty());

        assertThat(dest.dateAtSrc, is(date("2024-02-29 00:00:00")));
        assertThat(dest.dateAtDest, is(date("2024-03-01 00:00:00")));
        assertThat(dest.numberAtSrc, is(1234567890));
        assertThat(dest.numberAtDest, is(1234567890));

    }

    @Test
    public void CopyOptionアノテーションのテスト_レコードからBean() {
        SourceRecordWithAnnotation src = new SourceRecordWithAnnotation(
                "2024_02_29",
                "2024~03~01",
                "12,3456,7890",
                "12345,67890");

        DestBeanWithAnnotation dest = BeanUtil.createAndCopy(DestBeanWithAnnotation.class, src,CopyOptions.empty());

        assertThat(dest.dateAtSrc, is(date("2024-02-29 00:00:00")));
        assertThat(dest.dateAtDest, is(date("2024-03-01 00:00:00")));
        assertThat(dest.numberAtSrc, is(1234567890));
        assertThat(dest.numberAtDest, is(1234567890));

    }

    @Test
    public void CopyOptionアノテーションのテスト_レコードからレコード() {
        SourceRecordWithAnnotation src = new SourceRecordWithAnnotation(
                "2024_02_29",
                "2024~03~01",
                "12,3456,7890",
                "12345,67890");

        DestRecordWithAnnotation dest = BeanUtil.createAndCopy(DestRecordWithAnnotation.class, src,CopyOptions.empty());

        assertThat(dest.dateAtSrc, is(date("2024-02-29 00:00:00")));
        assertThat(dest.dateAtDest, is(date("2024-03-01 00:00:00")));
        assertThat(dest.numberAtSrc, is(1234567890));
        assertThat(dest.numberAtDest, is(1234567890));

    }


    public static class SourceBeanWithAnnotation {
        @CopyOption(datePattern = "yyyy_MM_dd")
        private String dateAtSrc;

        private String dateAtDest;

        @CopyOption(numberPattern = "#,####")
        private String numberAtSrc;

        private String numberAtDest;

        @SuppressWarnings("unused")
        public String getDateAtSrc() {
            return dateAtSrc;
        }

        @SuppressWarnings("unused")
        public void setDateAtSrc(String dateAtSrc) {
            this.dateAtSrc = dateAtSrc;
        }

        @SuppressWarnings("unused")
        public String getDateAtDest() {
            return dateAtDest;
        }

        @SuppressWarnings("unused")
        public void setDateAtDest(String dateAtDest) {
            this.dateAtDest = dateAtDest;
        }

        @SuppressWarnings("unused")
        public String getNumberAtSrc() {
            return numberAtSrc;
        }

        @SuppressWarnings("unused")
        public void setNumberAtSrc(String numberAtSrc) {
            this.numberAtSrc = numberAtSrc;
        }

        @SuppressWarnings("unused")
        public String getNumberAtDest() {
            return numberAtDest;
        }

        @SuppressWarnings("unused")
        public void setNumberAtDest(String numberAtDest) {
            this.numberAtDest = numberAtDest;
        }
    }

    public static class DestBeanWithAnnotation {
        @CopyOption(datePattern = "yyyy~MM~dd")
        private Date dateAtSrc;

        @CopyOption(datePattern = "yyyy~MM~dd")
        private Date dateAtDest;

        @CopyOption(numberPattern = "#,#####")
        private Integer numberAtSrc;

        @CopyOption(numberPattern = "#,#####")
        private Integer numberAtDest;

        @SuppressWarnings("unused")
        public Date getDateAtSrc() {
            return dateAtSrc;
        }

        @SuppressWarnings("unused")
        public void setDateAtSrc(Date dateAtSrc) {
            this.dateAtSrc = dateAtSrc;
        }

        @SuppressWarnings("unused")
        public Date getDateAtDest() {
            return dateAtDest;
        }

        @SuppressWarnings("unused")
        public void setDateAtDest(Date dateAtDest) {
            this.dateAtDest = dateAtDest;
        }

        @SuppressWarnings("unused")
        public Integer getNumberAtSrc() {
            return numberAtSrc;
        }

        @SuppressWarnings("unused")
        public void setNumberAtSrc(Integer numberAtSrc) {
            this.numberAtSrc = numberAtSrc;
        }

        @SuppressWarnings("unused")
        public Integer getNumberAtDest() {
            return numberAtDest;
        }

        @SuppressWarnings("unused")
        public void setNumberAtDest(Integer numberAtDest) {
            this.numberAtDest = numberAtDest;
        }
    }

    public record SourceRecordWithAnnotation(
            @CopyOption(datePattern = "yyyy_MM_dd")
            String dateAtSrc,
            String dateAtDest,
            @CopyOption(numberPattern = "#,####")
            String numberAtSrc,
            String numberAtDest
    ) {}

    public record DestRecordWithAnnotation(
            @CopyOption(datePattern = "yyyy~MM~dd")
            Date dateAtSrc,
            @CopyOption(datePattern = "yyyy~MM~dd")
            Date dateAtDest,
            @CopyOption(numberPattern = "#,#####")
            Integer numberAtSrc,
            @CopyOption(numberPattern = "#,#####")
            Integer numberAtDest
    ) {}

}
