package nablarch.core.beans;

import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertThrows;

/**
 * Nablarch 6における、BeanUtilでのnullプロパティの挙動を確認するテスト。
 */
@SuppressWarnings("NonAsciiCharacters")
public class BeanUtilForNullPropertyTest {

    public static class SrcBean {
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class DestBean {
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    public static class SrcParentBean {
        private ChildBean childBean;
        public ChildBean getChildBean() {
            return childBean;
        }
        public void setChildBean(ChildBean childBean) {
            this.childBean = childBean;
        }
    }

    public static class DestParentBean {
        private ChildBean childBean;
        public ChildBean getChildBean() {
            return childBean;
        }
        public void setChildBean(ChildBean childBean) {
            this.childBean = childBean;
        }
    }

    public static class ChildBean {
        private String name;
        public String getName() {
            return name;
        }
        public void setName(String name) {
            this.name = name;
        }
    }

    @Test
    public void setProperty_propertyNameがnullの場合は実行時例外が送出されること() {
        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                BeanUtil.setProperty(new DestBean(), null, "value"));
        assertThat(result.getMessage(), is("expression is null or blank."));
    }

    @Test
    public void setProperty_propertyValueがnullの場合は成功すること() {
        DestBean dest = new DestBean();
        dest.setName("value");

        BeanUtil.setProperty(dest, "name", null);
        assertThat(dest.getName(), is(nullValue()));
    }

    @Test
    public void 移送元をMapとするcopy_keyがnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put(null, "value");}};

        IllegalArgumentException result = assertThrows(IllegalArgumentException.class, () ->
                BeanUtil.copy(DestBean.class, new DestBean(), srcMap, CopyOptions.empty()));
        assertThat(result.getMessage(), is("expression is null or blank."));
    }

    @Test
    public void 移送元をMapとするcopy_valueがnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("name", null);}};
        DestBean dest = new DestBean();
        dest.setName("value");

        BeanUtil.copy(DestBean.class, dest, srcMap, CopyOptions.empty());
        assertThat(dest.getName(), is(nullValue()));
    }

    @Test
    public void 移送元をBeanとするcopy_valueがnullの場合は成功すること() {
        SrcBean src = new SrcBean();
        src.setName(null);
        DestBean dest = new DestBean();
        dest.setName("value");

        BeanUtil.copy(src, dest, CopyOptions.empty());
        assertThat(dest.getName(), is(nullValue()));
    }

    @Test
    public void 移送元をBeanとするcopy_コピー元beanがnullの場合はNPEが送出されること() {
        DestBean dest = new DestBean();
        dest.setName("value");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.copy(null, dest, CopyOptions.empty()));
    }

    @Test
    public void 移送元をBeanとするcopy_コピー先beanがnullの場合はNPEが送出されること() {
        SrcBean src = new SrcBean();
        src.setName("value");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.copy(src, null, CopyOptions.empty()));
    }

    @Test
    public void 移送元をBeanとするcopy_コピー元beanの子Beanがnullの場合はコピーされないこと() {
        SrcParentBean src = new SrcParentBean();
        src.setChildBean(null);
        DestParentBean dest = new DestParentBean();
        dest.setChildBean(new ChildBean());

        BeanUtil.copy(src, dest, CopyOptions.empty());
        // CopyOptions.empty().isExcludesNull() == false なので、nullのプロパティもコピーされるはずだが、Nablarch6時点ではではコピーされない。
        assertThat(dest.getChildBean(), instanceOf(ChildBean.class));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyIncludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("name", "value");}};

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyIncludes(DestBean.class, srcMap, (String[]) null));

    }

    @Test
    public void 移送元をMapとするcreateAndCopyIncludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("name", "value");}};

        DestBean dest = BeanUtil.createAndCopyIncludes(DestBean.class, srcMap, (String) null);
        assertThat(dest.getName(), is(nullValue()));
    }

    @Test
    public void 移送元をMapとするcreateAndCopyExcludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("name", "value");}};

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyExcludes(DestBean.class, srcMap, (String[]) null));

    }

    @Test
    public void 移送元をMapとするcreateAndCopyExcludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        Map<String, Object> srcMap = new HashMap<>(){{put("name", "value");}};

        DestBean dest = BeanUtil.createAndCopyExcludes(DestBean.class, srcMap, (String) null);
        assertThat(dest.getName(), is("value"));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyIncludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        SrcBean src = new SrcBean();
        src.setName("value");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyIncludes(DestBean.class, src, (String[]) null));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyIncludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        SrcBean src = new SrcBean();
        src.setName("value");

        DestBean dest = BeanUtil.createAndCopyIncludes(DestBean.class, src, (String) null);
        assertThat(dest.getName(), is(nullValue()));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyExcludes_プロパティの指定がStringの配列にキャストされたnullの場合は実行時例外が送出されること() {
        SrcBean src = new SrcBean();
        src.setName("value");

        assertThrows(NullPointerException.class, () ->
                BeanUtil.createAndCopyExcludes(DestBean.class, src, (String[]) null));

    }

    @Test
    public void 移送元をBeanとするcreateAndCopyExcludes_プロパティの指定がStringにキャストされたnullの場合は成功すること() {
        SrcBean src = new SrcBean();
        src.setName("value");

        DestBean dest = BeanUtil.createAndCopyExcludes(DestBean.class, src, (String) null);
        assertThat(dest.getName(), is("value"));

    }
}
