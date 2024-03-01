package nablarch.core.beans;

import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.assertThrows;

@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilWithTypeParameterForRecordTest {

    @Before
    public void setUp() {
        OnMemoryLogWriter.clear();
    }

    public record ItemRecord<D extends Serializable>(List<D> items){}

    @Test
    public void コンポーネント内の型引数が具体型でないレコードを生成しようとした場合_実行時例外が発生すること() {
        Map<String, Object> map = Map.of(
                "items[0].name", "aaa",
                "items[1].name", "bbb");

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> {
            BeanUtil.createAndCopy(ItemRecord.class, map);
        });
        assertThat(result.getMessage(), is(
                "BeanUtil does not support type parameter for List type component, so the type parameter in the record class must be concrete type."));
    }

    @SuppressWarnings("rawtypes")
    public record NoGenericTypeRecord(List children){}

    @Test
    public void Generic型が未指定の場合_内部でBeansExceptionが送出されかつデバッグログが出力されること() {
        BeanUtil.createAndCopy(NoGenericTypeRecord.class, Map.of("children[0].name", "aaa"));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("must set generics type for property. class: " +
                        "class nablarch.core.beans.BeanUtilWithTypeParameterForRecordTest$NoGenericTypeRecord property: children"),
                containsString("nablarch.core.beans.BeansException"))));
    }



}
