package nablarch.core.beans;

import nablarch.core.repository.SystemRepository;
import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.junit.Before;
import org.junit.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class BeanUtilExecUnitTest {

    @Before
    public void setUp() {
        // デフォルト設定で動作させるよう、リポジトリをクリアする。
        SystemRepository.clear();
        OnMemoryLogWriter.clear();
        //BeanDescriptorのキャッシュをクリアしておく
        BeanUtil.clearCache();
    }

    @Test
    public void test_NoNest_Simple_Property() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("name", "Taro"),
                Map.entry("age", 20),
                Map.entry("birthday", LocalDate.of(2000, 1, 1))
        );

        Person person = new Person();

        BeanUtil.copy(Person.class, person, srcMap, CopyOptions.empty());

        assertEquals("Taro", person.getName());
        assertEquals(20, person.getAge().intValue());
        assertEquals(LocalDate.of(2000, 1, 1), person.getBirthday());
    }

    @Test
    public void test_NoNest_Array_Property() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("torishimariyaku[0]", "Taro"),
                Map.entry("torishimariyaku[1]", "Jiro"),
                Map.entry("torishimariyaku[2]", "Saburo"),
                Map.entry("sales[0]", "100"),
                Map.entry("sales[1]", "200"),
                Map.entry("sales[2]", "300"),
                Map.entry("holidays[0]", "20210101"),
                Map.entry("holidays[1]", "20210102"),
                Map.entry("holidays[2]", "20210103")
        );

        Company company = new Company();

        BeanUtil.copy(Company.class, company, srcMap, CopyOptions.empty());

        assertEquals("Taro", company.torishimariyaku[0]);
        assertEquals("Jiro", company.torishimariyaku[1]);
        assertEquals("Saburo", company.torishimariyaku[2]);
        assertEquals(100, company.sales[0].intValue());
        assertEquals(200, company.sales[1].intValue());
        assertEquals( 300, company.sales[2].intValue());
        assertEquals(LocalDate.of(2021, 1, 1), company.holidays[0]);
        assertEquals(LocalDate.of(2021, 1, 2), company.holidays[1]);
        assertEquals(LocalDate.of(2021, 1, 3), company.holidays[2]);

    }

    @Test
    public void test_NoNest_List_Property() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("torishimariyaku[0]", "Taro"),
                Map.entry("torishimariyaku[1]", "Jiro"),
                Map.entry("torishimariyaku[2]", "Saburo"),
                Map.entry("sales[0]", "100"),
                Map.entry("sales[1]", "200"),
                Map.entry("sales[2]", "300"),
                Map.entry("holidays[0]", "20210101"),
                Map.entry("holidays[1]", "20210102"),
                Map.entry("holidays[2]", "20210103")
        );

        CompanyY companyY = new CompanyY();

        BeanUtil.copy(CompanyY.class, companyY, srcMap, CopyOptions.empty());

        assertEquals("Taro", companyY.getTorishimariyaku().get(0));
        assertEquals("Jiro", companyY.getTorishimariyaku().get(1));
        assertEquals("Saburo", companyY.getTorishimariyaku().get(2));
        assertEquals(100, companyY.getSales().get(0).intValue());
        assertEquals(200, companyY.getSales().get(1).intValue());
        assertEquals(300, companyY.getSales().get(2).intValue());
        assertEquals(LocalDate.of(2021, 1, 1), companyY.getHolidays().get(0));
        assertEquals(LocalDate.of(2021, 1, 2), companyY.getHolidays().get(1));
        assertEquals(LocalDate.of(2021, 1, 3), companyY.getHolidays().get(2));

    }

    @Test
    public void test_Nest_Class_Property() {

        Map<String, Object> srcMap = Map.ofEntries(
                Map.entry("category.categoryName", "Fruit")
        );

        Item item = new Item();

        BeanUtil.copy(Item.class, item, srcMap, CopyOptions.empty());

        assertEquals("Fruit", item.getCategory().getCategoryName());
    }

    @Test
    public void testThatItCanCopyPropertiesFromABeanToAnotherOne2() {

    }

    @Test
    public void testPropertyExpression() {

    }

    @Test
    public void testCopy_new() {

    }

    public static class Person {

        private String name;

        private int age;

        private LocalDate birthday;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(Integer age) {
            this.age = age;
        }

        public LocalDate getBirthday() {
            return birthday;
        }

        public void setBirthday(LocalDate birthday) {
            this.birthday = birthday;
        }
    }

    public static class Company {

        private String[] torishimariyaku;

        private Integer[] sales;

        private LocalDate[] holidays;

        public String[] getTorishimariyaku() {
            return torishimariyaku;
        }

        public void setTorishimariyaku(String[] torishimariyaku) {
            this.torishimariyaku = torishimariyaku;
        }

        public Integer[] getSales() {
            return sales;
        }

        public void setSales(Integer[] sales) {
            this.sales = sales;
        }

        public LocalDate[] getHolidays() {
            return holidays;
        }

        public void setHolidays(LocalDate[] holidays) {
            this.holidays = holidays;
        }
    }

    public static class CompanyY {

        private List<String> torishimariyaku;

        private List<Integer> sales;

        private List<LocalDate> holidays;

        public List<String> getTorishimariyaku() {
            return torishimariyaku;
        }

        public void setTorishimariyaku(List<String> torishimariyaku) {
            this.torishimariyaku = torishimariyaku;
        }

        public List<Integer> getSales() {
            return sales;
        }

        public void setSales(List<Integer> sales) {
            this.sales = sales;
        }

        public List<LocalDate> getHolidays() {
            return holidays;
        }

        public void setHolidays(List<LocalDate> holidays) {
            this.holidays = holidays;
        }
    }

    public static class Category {
        String categoryName;

        public String getCategoryName() {
            return categoryName;
        }

        public void setCategoryName(String categoryName) {
            this.categoryName = categoryName;
        }
    }

    public static class Item {
        Category  category;

        public Category getCategory() {
            return category;
        }

        public void setCategory(Category category) {
            this.category = category;
        }
    }
}
