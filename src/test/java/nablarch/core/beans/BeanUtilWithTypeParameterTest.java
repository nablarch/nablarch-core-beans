package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import org.junit.Test;

import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanUtilWithTypeParameterTest {

    public static class ItemsForm<D extends Serializable> {
        private List<D> items;
        public List<D> getItems() {
            return items;
        }
        public void setItems(List<D> items) {
            this.items = items;
        }
    }

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

    @Test
    public void testCreateAndCopyForBad() {
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("items[0].name", "aaa");
                put("items[1].name", "bbb");
            }
        };
        try {
            BeanUtil.createAndCopy(BadSampleForm.class, map);
            fail("IllegalStateExceptionがスローされるはず");
        } catch (IllegalStateException e) {
            assertThat(e.getMessage(), is(
                    "BeanUtil does not support type parameter for List type, so the getter method in the concrete class must be overridden. "
            + "getter method = [nablarch.core.beans.BeanUtilWithTypeParameterTest$BadSampleForm#getItems]"));
        }
    }

    @Test
    public void testCreateAndCopyForGood() {
        Map<String, Object> map = new HashMap<String, Object>() {
            {
                put("items[0].name", "aaa");
                put("items[1].name", "bbb");
            }
        };
        GoodSampleForm form = BeanUtil.createAndCopy(GoodSampleForm.class, map);
        assertThat(form.getItems().size(), is(2));
        assertThat(form.getItems().get(0).getName(), is("aaa"));
        assertThat(form.getItems().get(1).getName(), is("bbb"));
    }
}
