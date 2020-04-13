package nablarch.core.beans;

import org.junit.Test;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BeanUtilWithTypeParameterTest {

    public interface IMeisaiRowForm extends Serializable {
    }

    public interface IMeisaiForm<D extends IMeisaiRowForm> extends Serializable {
        List<D> getDetails();
        void setDetails(List<D> details);
    }

    public static class AbstractMeisaiForm<D extends IMeisaiRowForm> implements IMeisaiForm<D> {
        private List<D> details;
        @Override
        public List<D> getDetails() {
            return details;
        }
        @Override
        public void setDetails(List<D> details) {
            this.details = details;
        }
    }
    public static class MeisaiRowForm implements IMeisaiRowForm {
        private String value;
        public String getValue() {
            return value;
        }
        public void setValue(String value) {
            this.value = value;
        }
    }

    public static class BadMeisaiForm extends AbstractMeisaiForm<MeisaiRowForm> {
        public BadMeisaiForm() {
            setDetails(new ArrayList<MeisaiRowForm>());
        }
    }

    public static class GoodMeisaiForm extends AbstractMeisaiForm<MeisaiRowForm> {
        public GoodMeisaiForm() {
            setDetails(new ArrayList<MeisaiRowForm>());
        }
        @Override
        public List<MeisaiRowForm> getDetails() {
            return super.getDetails();
        }
    }

    @Test
    public void testCreateAndCopy() {
        Map<String, Object> paramMap = new HashMap<String, Object>() {
            {
                put("details[0].value", "1");
                put("details[1].value", "2");
            }
        };
        GoodMeisaiForm goodForm = BeanUtil.createAndCopy(GoodMeisaiForm.class, paramMap);
        BadMeisaiForm badForm = BeanUtil.createAndCopy(BadMeisaiForm.class, paramMap);
    }
}
