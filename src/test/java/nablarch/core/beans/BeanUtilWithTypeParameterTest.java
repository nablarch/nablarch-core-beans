package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

import org.junit.Test;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

public class BeanUtilWithTypeParameterTest {

    @SuppressWarnings("unused")
    public static class ItemsForm<D extends Serializable> {
        private List<D> items;
        public List<D> getItems() {
            return items;
        }
        public void setItems(List<D> items) {
            this.items = items;
        }
    }

    @SuppressWarnings("unused")
    public static class Item implements Serializable {
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class BadSampleForm extends ItemsForm<Item> {
    }

    public static class GoodSampleForm extends ItemsForm<Item> {
        @Override
        public List<Item> getItems() {
            return super.getItems();
        }
    }

    public record ItemRecord<D extends Serializable>(List<D> items){}

    @Test
    public void testCreateAndCopyForBad() {
        Map<String, Object> map = Map.of(
                "items[0].name", "aaa",
                "items[1].name", "bbb");

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> {
            BeanUtil.createAndCopy(BadSampleForm.class, map);
        });
        assertThat(result.getMessage(), is(
                "BeanUtil does not support type parameter for List type, so the getter method in the concrete class must be overridden. "
                        + "getter method = [nablarch.core.beans.BeanUtilWithTypeParameterTest$BadSampleForm#getItems]"));
    }

    @Test
    public void testCreateAndCopyForGood() {
        Map<String, Object> map = Map.of(
                "items[0].name", "aaa",
                "items[1].name", "bbb");

        GoodSampleForm form = BeanUtil.createAndCopy(GoodSampleForm.class, map);
        assertThat(form.getItems().size(), is(2));
        assertThat(form.getItems().get(0).getName(), is("aaa"));
        assertThat(form.getItems().get(1).getName(), is("bbb"));
    }


    @Test
    public void testCreateAndCopyForBadRecord() {
        Map<String, Object> map = Map.of(
                "items[0].name", "aaa",
                "items[1].name", "bbb");

        IllegalStateException result = assertThrows(IllegalStateException.class, () -> {
            BeanUtil.createAndCopy(ItemRecord.class, map);
        });
        assertThat(result.getMessage(), is(
                "BeanUtil does not support type parameter for List type, so the accessor in the concrete class must be overridden. "
                        + "getter method = [nablarch.core.beans.BeanUtilWithTypeParameterTest$ItemRecord#items]"));
    }

}
