package nablarch.core.beans;

import nablarch.test.support.SystemRepositoryResource;
import org.junit.Rule;
import org.junit.Test;

import java.sql.Timestamp;

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
@SuppressWarnings("NonAsciiCharacters")
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
}
