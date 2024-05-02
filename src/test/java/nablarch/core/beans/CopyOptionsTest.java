package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.hamcrest.MatcherAssert;

import nablarch.core.beans.CopyOptions.ConvertersProvider;
import nablarch.core.beans.converter.DateConverter;
import nablarch.core.beans.converter.StringConverter;
import nablarch.test.support.SystemRepositoryResource;

/**
 * {@link CopyOptions}のテスト。
 * @author Taichi Uragami
 *
 */
public class CopyOptionsTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();
    @Rule
    public SystemRepositoryResource resource = new SystemRepositoryResource(null);

    @Test
    public void datePatternsByName() {
        CopyOptions sut = CopyOptions.options()
                .datePatternsByName("foo", Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", LocalDate.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", LocalDateTime.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", java.util.Date.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", java.sql.Date.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", Timestamp.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", Object.class), is(false));

        MatcherAssert.assertThat(sut.hasNamedConverter("bar", java.util.Date.class), is(false));
        MatcherAssert.assertThat(sut.hasNamedConverter("baz", java.util.Date.class), is(false));
    }

    @Test
    public void numberPatternsByName() {
        CopyOptions sut = CopyOptions.options()
                .numberPatternsByName("foo", Arrays.asList("#,###", "#,####"))
                .build();
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", Integer.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", Long.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", BigDecimal.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", Object.class), is(false));

        MatcherAssert.assertThat(sut.hasNamedConverter("bar", Integer.class), is(false));
        MatcherAssert.assertThat(sut.hasNamedConverter("baz", Integer.class), is(false));
    }

    @Test
    public void converterByName() {
        CopyOptions sut = CopyOptions.options()
                .converterByName("bar", String.class, new StringConverter())
                .build();
        MatcherAssert.assertThat(sut.hasNamedConverter("foo", String.class), is(false));
        MatcherAssert.assertThat(sut.hasNamedConverter("bar", String.class), is(true));
        MatcherAssert.assertThat(sut.hasNamedConverter("bar", Object.class), is(false));
        MatcherAssert.assertThat(sut.hasNamedConverter("baz", String.class), is(false));
    }

    @Test
    public void convertByName() {
        final Object mockValue = new Object();
        Converter<Object> mockConverter = value -> mockValue;
        CopyOptions sut = CopyOptions.options()
                .converterByName("foo", Object.class, mockConverter)
                .build();
        MatcherAssert.assertThat(sut.convertByName("foo", Object.class, "Source value"),
                is(sameInstance(mockValue)));
    }

    @Test
    public void convertByNameToStringWithDatePattern() {
        CopyOptions sut = CopyOptions.options()
                .datePatternByName("foo", "yyyy/MM/dd")
                .build();
        MatcherAssert.assertThat((String) sut.convertByName("foo", String.class, date("2018-02-14 00:00:00")),
                is("2018/02/14"));
    }

    @Test
    public void convertByName失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatternByName("foo", "yyyyMMdd")
                .converterByName("bar", java.util.Date.class,
                        new DateConverter(Collections.singletonList("yyyyMMdd")))
                .build();
        sut.convertByName("baz", java.util.Date.class, "20180214");
    }

    @Test
    public void convertByNameプロパティ名に対応するコンバーターのMapが無い場合() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.empty();
        sut.convertByName("baz", java.util.Date.class, "20180214");
    }

    @Test
    public void convertByNameコンバーターが無い場合() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .converterByName("baz", Object.class, new MockConverter())
                .build();
        sut.convertByName("baz", java.util.Date.class, "20180214");
    }

    @Test
    public void datePatterns() {
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        MatcherAssert.assertThat(sut.hasTypedConverter(LocalDate.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(LocalDateTime.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(java.util.Date.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(java.sql.Date.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Timestamp.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(String.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void numberPatterns() {
        CopyOptions sut = CopyOptions.options()
                .numberPatterns(Arrays.asList("#,###", "#,###,###"))
                .build();
        MatcherAssert.assertThat(sut.hasTypedConverter(short.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(int.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(long.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Short.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Integer.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Long.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(BigDecimal.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(String.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void converter() {
        Converter<BigDecimal> converter = value -> null;
        CopyOptions sut = CopyOptions.options()
                .converter(BigDecimal.class, converter)
                .build();
        MatcherAssert.assertThat(sut.hasTypedConverter(BigDecimal.class), is(true));
        MatcherAssert.assertThat(sut.hasTypedConverter(Object.class), is(false));
    }

    @Test
    public void convertByType() {
        final Object mockValue = new Object();
        Converter<Object> mockConverter = value -> mockValue;
        CopyOptions sut = CopyOptions.options()
                .converter(Object.class, mockConverter)
                .build();
        MatcherAssert.assertThat(sut.convertByType(Object.class, "test"), is(sameInstance(mockValue)));
    }

    @Test
    public void convertByTypeToStringWithDatePattern() {
        CopyOptions sut = CopyOptions.options()
                .datePattern("yyyy/MM/dd")
                .build();
        MatcherAssert.assertThat((String) sut.convertByType(String.class, date("2018-02-14 00:00:00")),
                is("2018/02/14"));
    }

    @Test
    public void convertByTypeToStringWithNumberPattern() {
        CopyOptions sut = CopyOptions.options()
                .numberPattern("#,###")
                .build();
        MatcherAssert.assertThat((String) sut.convertByType(String.class, 1234567890), is("1,234,567,890"));
    }

    @Test
    public void convertByType失敗() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.options()
                .datePatterns(Arrays.asList("yyyy/MM/dd", "yyyy-MM-dd"))
                .build();
        sut.convertByType(java.util.Date.class, "20180214");
    }

    @Test
    public void convertByTypeコンバーターが無い場合() {
        expectedException.expect(IllegalArgumentException.class);
        CopyOptions sut = CopyOptions.empty();
        sut.convertByType(java.util.Date.class, "20180214");
    }

    @Test
    public void datePatternAndNumberPattern() {
        CopyOptions sut = CopyOptions.options()
                .datePattern("yyyy/MM/dd").numberPattern("#,###")
                .datePatternByName("foo", "yyyy.MM.dd").numberPatternByName("foo", "#,####")
                .build();

        MatcherAssert.assertThat(sut.hasTypedConverter(String.class), is(true));
        MatcherAssert.assertThat((String) sut.convertByType(String.class, date("2018-02-19 00:00:00")),
                is("2018/02/19"));
        MatcherAssert.assertThat((String) sut.convertByType(String.class, 1234567890), is("1,234,567,890"));

        MatcherAssert.assertThat(sut.hasNamedConverter("foo", String.class), is(true));
        MatcherAssert.assertThat((String) sut.convertByName("foo", String.class, date("2018-02-19 00:00:00")),
                is("2018.02.19"));
        MatcherAssert.assertThat((String) sut.convertByName("foo", String.class, 1234567890),
                is("12,3456,7890"));
    }

    @Test
    public void merge() {
        MockConverter mockConverter1 = new MockConverter();
        MockConverter mockConverter2 = new MockConverter();
        MockConverter mockConverter3 = new MockConverter();
        MockConverter mockConverter4 = new MockConverter();

        CopyOptions base = CopyOptions.options()
                .converterByName("foo", Object.class, mockConverter1)
                .converter(Object.class, mockConverter2)
                .build();

        CopyOptions other = CopyOptions.options()
                //fooのConverterはbaseにあるのでこちらは無視される
                .converterByName("foo", Object.class, mockConverter3)
                .converterByName("bar", Object.class, mockConverter3)
                //ObjectのConverterはbaseにあるのでこちらは無視される
                .converter(Object.class, mockConverter4)
                .build();

        CopyOptions sut = base.merge(other);

        MatcherAssert.assertThat(sut.convertByName("foo", Object.class, null),
                is(sameInstance(mockConverter1.mockValue)));
        MatcherAssert.assertThat(sut.convertByName("bar", Object.class, null),
                is(sameInstance(mockConverter3.mockValue)));
        MatcherAssert.assertThat(sut.convertByType(Object.class, null),
                is(sameInstance(mockConverter2.mockValue)));
    }

    @Test
    public void isExcludesNullデフォルト() {
        CopyOptions sut = CopyOptions.empty();
        MatcherAssert.assertThat(sut.isExcludesNull(), is(false));
    }

    @Test
    public void isExcludesNull() {
        CopyOptions sut = CopyOptions.options().excludesNull().build();
        MatcherAssert.assertThat(sut.isExcludesNull(), is(true));
    }

    @Test
    public void excludesNullはマージ元が優先される() {
        CopyOptions excludesNull = CopyOptions.options().excludesNull().build();
        CopyOptions includesNull = CopyOptions.empty();
        MatcherAssert.assertThat(excludesNull.merge(includesNull).isExcludesNull(), is(true));
        MatcherAssert.assertThat(includesNull.merge(excludesNull).isExcludesNull(), is(false));
    }

    @Test
    public void excludesProperties() {
        CopyOptions sut = CopyOptions.options().excludes("foo", "bar").build();
        MatcherAssert.assertThat(sut.isTargetProperty("foo"), is(false));
        MatcherAssert.assertThat(sut.isTargetProperty("bar"), is(false));
        MatcherAssert.assertThat(sut.isTargetProperty("baz"), is(true));
    }

    @Test
    public void includesProperties() {
        CopyOptions sut = CopyOptions.options().includes("foo", "bar").build();
        MatcherAssert.assertThat(sut.isTargetProperty("foo"), is(true));
        MatcherAssert.assertThat(sut.isTargetProperty("bar"), is(true));
        MatcherAssert.assertThat(sut.isTargetProperty("baz"), is(false));
    }

    @Test
    public void excludesPropertiesとincludesPropertiesはexcludesが優先される() {
        CopyOptions sut = CopyOptions.options()
                .excludes("foo", "bar")
                .includes("bar", "baz")
                .build();
        MatcherAssert.assertThat(sut.isTargetProperty("foo"), is(false));
        MatcherAssert.assertThat(sut.isTargetProperty("bar"), is(false));
        MatcherAssert.assertThat(sut.isTargetProperty("baz"), is(true));
    }

    @Test
    public void excludesPropertiesのマージ() {
        CopyOptions copyOptions1 = CopyOptions.options().excludes("foo").build();
        CopyOptions copyOptions2 = CopyOptions.options().excludes("bar").build();

        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("foo"), is(false));
        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("bar"), is(false));
        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("baz"), is(true));

        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("foo"), is(false));
        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("bar"), is(false));
        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("baz"), is(true));
    }

    @Test
    public void includesPropertiesのマージ() {
        CopyOptions copyOptions1 = CopyOptions.options().includes("foo").build();
        CopyOptions copyOptions2 = CopyOptions.options().includes("bar").build();

        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("foo"), is(true));
        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("bar"), is(true));
        MatcherAssert.assertThat(copyOptions1.merge(copyOptions2).isTargetProperty("baz"), is(false));

        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("foo"), is(true));
        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("bar"), is(true));
        MatcherAssert.assertThat(copyOptions2.merge(copyOptions1).isTargetProperty("baz"), is(false));
    }

    @Test
    public void ConvertersProviderをカスタマイズした日付パターン() {
        MockConvertersProvider provider = new MockConvertersProvider();
        resource.addComponent("convertersProvider", provider);

        CopyOptions sut = CopyOptions.options().datePattern("yyyy/MM/dd").build();
        MatcherAssert.assertThat(sut.convertByType(Object.class, "test"),
                is(sameInstance(provider.mockDateConverter.mockValue)));
    }

    @Test
    public void ConvertersProviderをカスタマイズした数値パターン() {
        MockConvertersProvider provider = new MockConvertersProvider();
        resource.addComponent("convertersProvider", provider);

        CopyOptions sut = CopyOptions.options().numberPattern("#,###").build();
        MatcherAssert.assertThat(sut.convertByType(Object.class, "test"),
                is(sameInstance(provider.mockNumberConverter.mockValue)));
    }

    @Test
    public void アノテーションからCopyOptionsを構築する() {
        CopyOptions copyOptions = CopyOptions.fromAnnotation(AnnotatedBean.class);
        MatcherAssert.assertThat(copyOptions.hasNamedConverter("foo", String.class), is(true));
        MatcherAssert.assertThat(copyOptions.hasNamedConverter("bar", String.class), is(true));
        MatcherAssert.assertThat(copyOptions.hasNamedConverter("baz", String.class), is(false));

        MatcherAssert.assertThat(
                (String) copyOptions.convertByName("foo", String.class,
                        Timestamp.valueOf("2018-02-19 00:00:00")),
                is("2018/02/19"));
        MatcherAssert.assertThat(
                (String) copyOptions.convertByName("bar", String.class,
                        1234567890),
                is("1,234,567,890"));
    }

    @Test
    public void アノテーションから構築されたCopyOptionsはキャッシュされる() {
        CopyOptions copyOptions1 = CopyOptions.fromAnnotation(AnnotatedBean.class);
        CopyOptions copyOptions2 = CopyOptions.fromAnnotation(AnnotatedBean.class);
        MatcherAssert.assertThat(copyOptions1 == copyOptions2, is(true));
    }

    @Test
    public void Converterの追加は先勝ちとする() {
        MockConverter converter1 = new MockConverter();
        MockConverter converter2 = new MockConverter();
        CopyOptions copyOptions = CopyOptions.options()
                .converter(Object.class, converter1)
                .converter(Object.class, converter2)
                .build();

        MatcherAssert.assertThat(copyOptions.convertByType(Object.class, null),
                is(sameInstance(converter1.mockValue)));
    }

    @Test
    public void 名前付きConverterの追加は先勝ちとする() {
        MockConverter converter1 = new MockConverter();
        MockConverter converter2 = new MockConverter();
        CopyOptions copyOptions = CopyOptions.options()
                .converterByName("foo", Object.class, converter1)
                .converterByName("foo", Object.class, converter2)
                .build();

        MatcherAssert.assertThat(copyOptions.convertByName("foo", Object.class, null),
                is(sameInstance(converter1.mockValue)));
    }

    private static java.util.Date date(String sqlTimestampPattern) {
        return new java.util.Date(Timestamp.valueOf(sqlTimestampPattern).getTime());
    }

    private static class MockConverter implements Converter<Object> {
        final Object mockValue = new Object();

        @Override
        public Object convert(Object value) {
            return mockValue;
        }
    }

    private static class MockConvertersProvider implements ConvertersProvider {

        final MockConverter mockDateConverter = new MockConverter();
        final MockConverter mockNumberConverter = new MockConverter();

        @Override
        public Map<Class<?>, Converter<?>> provideDateConverters(List<String> patterns) {
            return Collections.singletonMap(Object.class,
                    mockDateConverter);
        }

        @Override
        public Map<Class<?>, Converter<?>> provideNumberConverters(List<String> patterns) {
            return Collections.singletonMap(Object.class,
                    mockNumberConverter);
        }
    }

    public static class AnnotatedBean {

        @CopyOption(datePattern = "yyyy/MM/dd")
        private String foo;
        @CopyOption(numberPattern = "#,###")
        private String bar;
        private String baz;

        public String getFoo() {
            return foo;
        }

        public void setFoo(String foo) {
            this.foo = foo;
        }

        public String getBar() {
            return bar;
        }

        public void setBar(String bar) {
            this.bar = bar;
        }

        public String getBaz() {
            return baz;
        }

        public void setBaz(String baz) {
            this.baz = baz;
        }
    }
}