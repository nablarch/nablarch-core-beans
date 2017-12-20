package nablarch.core.beans;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.arrayContaining;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.isEmptyOrNullString;
import static org.hamcrest.collection.IsMapContaining.hasKey;
import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import nablarch.test.support.log.app.OnMemoryLogWriter;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsMapContaining;

import nablarch.core.repository.SystemRepository;
import nablarch.core.util.StringUtil;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import mockit.Expectations;
import mockit.Mocked;

/**
 * @author Iwauo Tajima
 */
public class BeanUtilTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Before
    public void setUp() throws Exception {
        // デフォルト設定で動作させるよう、リポジトリをクリアする。
        SystemRepository.clear();
        OnMemoryLogWriter.clear();
    }

    public static class UserDto {
        private int age;
        private String firstName;
        private String lastName;
        private String[] phones;

        private byte[] bin;
        private Address address;

        private int height;

        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String name) {
            this.firstName = name;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String name) {
            this.lastName = name;
        }
        // read-only property
        public String getFullName() {
            return this.firstName + " " + this.lastName;
        }
        // wirte-only property
        public void setAgeInDays(int days) {
            this.age = days / 365; //てきとう
        }
        public String[] getPhoneNumbers() {
            return this.phones;
        }
        public void setPhoneNumbers(String[] phones) {
            this.phones = phones;
        }
        public Address getAddress() {
            return address;
        }
        public void setAddress(Address address) {
            this.address = address;
        }

        public byte[] getBin() {
            return bin;
        }

        public void setBin(byte[] bin) {
            this.bin = bin;
        }
    }

    public static class Address {
        private String postCode;
        private String addr;

        public Address() {
        }

        public Address(String postCode, String addr) {
            super();
            this.postCode = postCode;
            this.addr = addr;
        }

        public String getPostCode() {
            return postCode;
        }
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
        public String getAddr() {
            return addr;
        }
        public void setAddr(String addr) {
            this.addr = addr;
        }
    }

    public static class UserEntity {
        private int age;
        private String firstName;
        private String lastName;
        private String[] phones;
        private String ssn;
        private int height;

        private byte[] bin;

        private AddressEntity address;

        public int getAge() {
            return age;
        }
        public void setAge(int age) {
            this.age = age;
        }
        public String getFirstName() {
            return firstName;
        }
        public void setFirstName(String name) {
            this.firstName = name;
        }
        public String getLastName() {
            return lastName;
        }
        public void setLastName(String name) {
            this.lastName = name;
        }
        public String getFullName() {
            return this.firstName + " " + this.lastName;
        }
        public String getSsn() {
            return this.ssn;
        }
        public void setSsn(String ssn) {
            this.ssn = ssn;
        }
        public String[] getPhoneNumbers() {
            return this.phones;
        }
        public void setPhoneNumbers(String[] phones) {
            this.phones = phones;
        }
        public AddressEntity getAddress() {
            return address;
        }
        public void setAddress(AddressEntity address) {
            this.address = address;
        }

        public byte[] getBin() {
            return bin;
        }

        public void setBin(byte[] bin) {
            this.bin = bin;
        }
    }

    public static class AddressEntity {
        private String postCode;
        private String addr;
        public AddressEntity(String postCode, String addr) {
            super();
            this.postCode = postCode;
            this.addr = addr;
        }
        public String getPostCode() {
            return postCode;
        }
        public void setPostCode(String postCode) {
            this.postCode = postCode;
        }
        public String getAddr() {
            return addr;
        }
        public void setAddr(String addr) {
            this.addr = addr;
        }
    }

    public static class SrcBean {
        private String sample;
        private List<String> strList;

        public SrcBean(String sample){
            this.sample = sample;
        }

        public String getSample() {
            return sample;
        }
        public void setSample(String sample) {
            this.sample = sample;
        }
        public void setStrList(List<String> strList) {
            this.strList = strList;
        }
    }

    public static class DestBean {
        private Integer sample;
        private List<String> strList;
        private List<Address> addressList;
        private String[] strArray;
        private Address[] addressArray;

        public Integer getSample() {
            return sample;
        }
        public void setSample(Integer sample) {
            this.sample = sample;
        }
        public List<String> getStrList() {
            return strList;
        }
        public void setStrList(List<String> strList) {
            this.strList = strList;
        }
        public String[] getStrArray() {
            return strArray;
        }
        public void setStrArray(String[] strArray) {
            this.strArray = strArray;
        }
        public Address[] getAddressArray() {
            return addressArray;
        }
        public void setAddressArray(Address[] addressArray) {
            this.addressArray = addressArray;
        }
        public List<Address> getAddressList() {
            return addressList;
        }
        public void setAddressList(List<Address> addressList) {
            this.addressList = addressList;
        }
    }

    @Test
    public void testThatItCanRetrievePropertyDescriptorsOfAClass() {
        PropertyDescriptor[] pds = BeanUtil.getPropertyDescriptors(UserDto.class);
        assertEquals(8, pds.length);
    }

    @Test
    public void testThatItCanReadPropertyValueFromBeans() {
        UserDto bean = new UserDto();
        bean.setFirstName("Hogeo");
        String name = (String) BeanUtil.getProperty(bean, "firstName");
        assertEquals("Hogeo", name);
    }

    @Test
    public void testThatItCanReadPropertyValueConvertingAssignedType() {
        UserDto bean = new UserDto();
        bean.setAge(22);
        String ageStr = (String) BeanUtil.getProperty(bean, "age", String.class);
        assertEquals("22", ageStr);

        Integer age = (Integer) BeanUtil.getProperty(bean, "age", null);
        assertEquals(22, age.intValue());
    }

    @Test
    public void testThatItCanReadPropertyWhoseTypeIsArrayOfString() {
        UserDto bean = new UserDto();
        bean.setPhoneNumbers(new String[]{"09012345678", "0311112222"});
        String[] phones = (String[]) BeanUtil.getProperty(bean, "phoneNumbers");

        assertEquals("09012345678", phones[0]);
        assertEquals("0311112222", phones[1]);
    }


    @Test
    public void testThatItCanWritePrperty() {
        UserDto bean = new UserDto();
        BeanUtil.setProperty(bean, "age", 22);
        assertEquals(22, bean.getAge());
    }

    @Test
    public void testThatItRaisesAnErrorWhenAccessingToUndefinedProperty() {
        UserDto dto = new UserDto();
        try {
            BeanUtil.setProperty(dto, "undefinedProperty", "999999999");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof BeansException);
        }

        try {
            BeanUtil.getProperty(dto, "undefinedProperty");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof BeansException);
        }
    }

    @Test
    public void testThatItDoNothingWhenWritingToAReadOnlyProperty() {
        UserDto dto = new UserDto();
        dto.setFirstName("Hogeo");
        dto.setLastName("Hogeta");
        assertEquals("Hogeo Hogeta", dto.getFullName());
        BeanUtil.setProperty(dto, "fullName", "999999999");
        assertEquals("Hogeo Hogeta", dto.getFullName());
    }

    @Test
    public void testThatItRaisesAnErrorWhenWrittenPropertyCouldNotBeenConverted() {
        UserDto dto = new UserDto();
        try {
            BeanUtil.setProperty(dto, "age", "HOHOHOHOHOHO");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof BeansException);
            assertTrue(e.getCause() instanceof ConversionException);
        }
    }

    @Test
    public void testThatItRaisesAnErrorWhenReadingAWriteOnlyProperty() {
        UserDto dto = new UserDto();
        try {
            BeanUtil.getProperty(dto, "ageInDays");
            fail();
        } catch (Throwable e) {
            assertTrue(e instanceof BeansException);
        }
    }

    @SuppressWarnings("serial")
    @Test
    public void testThatItCanCreateABeanFromMap() {

        UserDto dto = BeanUtil.createAndCopy(UserDto.class, new HashMap<String, Object>() {{
            put("firstName", "Rakutaro");
            put("lastName", "Nabu");
            put("age", 34);
            put("unknownProperty", "UNKNOWN");
            put("bin", new byte[] {0x00, 0x30});
        }});

        assertEquals("Rakutaro Nabu", dto.getFullName());
        assertEquals(34, dto.getAge());
        assertArrayEquals(new byte[] {0x00, 0x30}, dto.bin);
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while writing to the property :unknownProperty"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));
    }

    @Test
    public void testThatItCanCreateABeanFromAnotherBean() {
        UserEntity entity = new UserEntity();
        entity.setAge(34);
        entity.setFirstName("Rakutaro");
        entity.setLastName("Nabu");
        entity.setSsn("123456789");
        entity.setBin(new byte[] {0x30, 0x40});

        UserDto dto = BeanUtil.createAndCopy(UserDto.class, entity);

        assertEquals("Rakutaro Nabu", dto.getFullName());
        assertEquals(34, dto.getAge());
        assertArrayEquals(new byte[] {0x30, 0x40}, dto.getBin());

        // サロゲートペア対応
        dto = BeanUtil.createAndCopy(UserDto.class, new HashMap<String, Object>() {{
            put("firstName", "𠀃𠀄𠀅");
            put("lastName", "😁");
        }});
        assertEquals("𠀃𠀄𠀅 😁", dto.getFullName());
    }

    @Test
    public void testThatItCanNotCreateInnerBeanPropertiesFromAnotherBean() {

        UserEntity src;
        UserDto dest;

        // srcのInnerBeanがnullの場合
        src = new UserEntity();

        dest = BeanUtil.createAndCopy(UserDto.class, src);

        assertNull(dest.getAddress());

        //  srcのInnerBeanがnot nullの場合
        src = new UserEntity();
        src.setAddress(new AddressEntity("123-4567", null));

        dest = BeanUtil.createAndCopy(UserDto.class, src);

        assertThat(dest.address.postCode, is("123-4567"));
        assertThat(dest.address.addr, is(nullValue()));
    }

    @Test
    public void testThatItCanCopyPropertiesFromABeanToAnotherOne() {
        UserEntity entity = new UserEntity();
        entity.setAge(34);
        entity.setFirstName("Rakutaro");
        entity.setLastName("Nabu");
        entity.setSsn("123456789");
        entity.setPhoneNumbers(new String[]{"09011112222", "08033334444"});

        UserDto dto = new UserDto();
        dto.setAge(0);
        dto.setFirstName("-");
        dto.setLastName("-");

        BeanUtil.copy(entity, dto);

        assertEquals("Rakutaro Nabu", dto.getFullName());
        assertEquals(34, dto.getAge());
        assertEquals("09011112222", dto.getPhoneNumbers()[0]);
        assertEquals("08033334444", dto.getPhoneNumbers()[1]);

        // サロゲートペア対応
        entity.setFirstName("𠀃𠀄𠀅");
        entity.setLastName("😁");

        BeanUtil.copy(entity, dto);

        assertEquals("𠀃𠀄𠀅 😁", dto.getFullName());

        entity.setAge(34);
        entity.setFirstName(null);
        entity.setLastName(null);
        entity.setSsn("123456789");

        dto.setAge(0);
        dto.setFirstName("-");
        dto.setLastName("-");

        BeanUtil.copyExcludesNull(entity, dto);

        assertEquals("- -", dto.getFullName());
        assertEquals(34, dto.getAge());

        // サロゲートペア対応
        dto.setFirstName("𠀃𠀄𠀅");
        dto.setLastName("😁");

        BeanUtil.copyExcludesNull(entity, dto);
        assertEquals("𠀃𠀄𠀅 😁", dto.getFullName());
    }

    @Test
    public void testThatItCanNotCopyInnerBeanPropertiesFromABeanToAnotherOne() {

        UserEntity src;
        UserDto dest;

        // srcのInnerBeanがnull、destのInnerBeanがnullの場合
        src = new UserEntity();
        dest = new UserDto();

        BeanUtil.copy(src, dest);

        assertNull(dest.getAddress());

        //  srcのInnerBeanがnot null、destのInnerBeanがnullの場合
        src = new UserEntity();
        src.setAddress(new AddressEntity("123-4567", null));
        dest = new UserDto();

        BeanUtil.copy(src, dest);

        assertThat(dest.address.postCode, is("123-4567"));
        assertThat(dest.address.addr, is(nullValue()));

        // srcのInnerBeanがnot null、destのInnerBeanがnot nullの場合
        src = new UserEntity();
        src.setAddress(new AddressEntity("123-4567", null));
        dest = new UserDto();
        dest.setAddress(new Address(null, "shinjuku tokyo"));

        BeanUtil.copy(src, dest);

        assertNotNull(dest.getAddress());
        assertEquals("123-4567", dest.getAddress().getPostCode()); // Dest側のInnerBeanがnot nullの場合はコピーされる。
        assertNull(dest.getAddress().getAddr()); // Dest側のInnerBeanがnot nullの場合はコピーされる。

        // srcのInnerBeanがnull、destのInnerBeanがnull、ExcludesNullの場合
        src = new UserEntity();
        dest = new UserDto();

        BeanUtil.copyExcludesNull(src, dest);

        assertNull(dest.getAddress());

        //  srcのInnerBeanがnot null、destのInnerBeanがnull、ExcludesNullの場合
        src = new UserEntity();
        src.setAddress(new AddressEntity("123-4567", null));
        dest = new UserDto();

        BeanUtil.copyExcludesNull(src, dest);

        assertThat(dest.address.postCode, is("123-4567"));
        assertThat(dest.address.addr, is(nullValue()));

        // srcのInnerBeanがnot null、destのInnerBeanがnot null、ExcludesNullの場合
        src = new UserEntity();
        src.setAddress(new AddressEntity("123-4567", null));
        dest = new UserDto();
        dest.setAddress(new Address(null, "shinjuku tokyo"));

        BeanUtil.copyExcludesNull(src, dest);

        assertNotNull(dest.getAddress());
        assertEquals("123-4567", dest.getAddress().getPostCode()); // Dest側がnullでないため、コピーされる。
        assertEquals("shinjuku tokyo", dest.getAddress().getAddr()); // Dest側がnullのため、コピーされない。
    }

    @Test
    public void testCreateAndCopyWhenNullSrc() {
        Map<String, Object> map = null;
        assertNotNull(BeanUtil.createAndCopy(Object.class, map));
        Object obj = null;
        assertNotNull(BeanUtil.createAndCopy(Object.class, obj));
    }

    @Test
    public void testCreateAndCopyWhenInvalidBeanClass() {
        Map<String, Object> map = new HashMap<String, Object>();
        try {
            BeanUtil.createAndCopy(StringUtil.class, map);
            fail("must be thrown BeansException");
        } catch (BeansException e) {
            assertNotNull(e.getCause());
        }
        Object obj = new Object();
        try {
            BeanUtil.createAndCopy(StringUtil.class, obj);
            fail("must be thrown BeansException");
        } catch (BeansException e) {
            assertNotNull(e.getCause());
        }
    }

    @Test
    public void testCopyIncludes() {

        UserEntity src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.bin = new byte[] {0x00};

        UserDto dest = new UserDto();
        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.bin = new byte[] {0x01};

        BeanUtil.copyIncludes(src, dest, "age", "firstName", "phoneNumbers", "address", "bin");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("田中"));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address.addr, is("東京都新宿区"));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.bin, is(new byte[] {0x00}));


        // null値のプロパティを指定するケース
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = null;
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.bin = null;

        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.address = new Address("333-4444", "兵庫県神戸市");
        dest.bin = new byte[] {0x00};

        BeanUtil.copyIncludes(src, dest, "age", "firstName", "lastName", "bin");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is(nullValue()));
        assertThat(dest.phones, is(new String[]{"777-8888-9999"}));
        assertThat(dest.address.addr, is("兵庫県神戸市"));
        assertThat(dest.address.postCode, is("333-4444"));
        assertThat(dest.bin, is(nullValue()));

        // コピー先に存在しないプロパティを指定するケース
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.ssn = "123456789";

        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.address = new Address("333-4444", "兵庫県神戸市");

        BeanUtil.copyIncludes(src, dest, "lastName", "ssn");

        assertThat(dest.age, is(20));
        assertThat(dest.firstName, is("花子"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(new String[]{"777-8888-9999"}));
        assertThat(dest.address.addr, is("兵庫県神戸市"));
        assertThat(dest.address.postCode, is("333-4444"));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while copying the property :ssn"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));

        // コピー元に存在しないプロパティを指定するケース
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.ssn = "123456789";

        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.address = new Address("333-4444", "兵庫県神戸市");

        BeanUtil.copyIncludes(src, dest, "undefined", "age");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("花子"));
        assertThat(dest.lastName, is("田中"));
        assertThat(dest.phones, is(new String[]{"777-8888-9999"}));
        assertThat(dest.address.addr, is("兵庫県神戸市"));
        assertThat(dest.address.postCode, is("333-4444"));

        // サロゲートペアを扱うテストケース
        src.firstName = "😁";
        src.lastName = "😁";
        src.address = new AddressEntity("111-2222", "😁");

        dest.firstName = "𠀃𠀄𠀅";
        dest.lastName = "𠀃𠀄𠀅";
        BeanUtil.copyIncludes(src, dest,  "firstName", "lastName", "address");

        assertThat(dest.firstName, is("😁"));
        assertThat(dest.lastName, is("😁"));
        assertThat(dest.address.addr, is("😁"));
    }

    @Test
    public void testCopyExcludes() {

        UserEntity src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.bin = new byte[] {0x30};

        UserDto dest = new UserDto();
        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.bin = new byte[] {0x00};

        BeanUtil.copyExcludes(src, dest, "age", "bin");

        assertThat(dest.age, is(20));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address.addr, is("東京都新宿区"));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.bin, is(new byte[] {0x00}));

        // コピー先に存在しないプロパティを指定するケース
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.ssn = "123456789";

        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.address = new Address("333-4444", "兵庫県神戸市");

        BeanUtil.copyExcludes(src, dest, "lastName", "ssn");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("田中"));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address.addr, is("東京都新宿区"));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while copying the property :ssn"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));

        // コピー元に存在しないプロパティを指定するケース
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");
        src.ssn = "123456789";

        dest.age = 20;
        dest.firstName = "花子";
        dest.lastName = "田中";
        dest.phones = new String[]{"777-8888-9999"};
        dest.address = new Address("333-4444", "兵庫県神戸市");

        BeanUtil.copyExcludes(src, dest, "undefined", "age");

        assertThat(dest.age, is(20));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address.addr, is("東京都新宿区"));
        assertThat(dest.address.postCode, is("111-2222"));

        // サロゲートペアを扱うテストケース
        src.firstName = "𠀃𠀄𠀅";
        src.lastName = "𠀃𠀄𠀅";
        src.address = new AddressEntity("111-2222", "𠀃𠀄𠀅");

        dest.firstName = "😁";
        dest.lastName = "😁";

        BeanUtil.copyExcludes(src, dest, "lastName");

        assertThat(dest.firstName, is("𠀃𠀄𠀅"));
        assertThat(dest.lastName, is("😁"));
        assertThat(dest.address.addr, is("𠀃𠀄𠀅"));
    }

    @Test
    public void testCreateAndCopyIncludesForBean() {

        UserEntity src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};

        UserDto dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "phoneNumbers");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is(nullValue()));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address, is(nullValue()));

        // null値のプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "phoneNumbers");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(nullValue()));

        // コピー先に存在しないプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.ssn = "123456789";

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "ssn");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // コピー元に存在しないプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "undefined");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // getter,setterが存在しないプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.height = 180;

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "height");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.height, is(0));

        // コピー元がnullのケース
        src = null;
        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is(nullValue()));
        assertThat(dest.lastName, is(nullValue()));


        // サロゲートペアを扱うテストケース
        src = new UserEntity();
        src.firstName = "😁";
        src.lastName = "😁";

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "firstName", "lastName");

        assertThat(dest.firstName, is("😁"));
        assertThat(dest.lastName, is("😁"));
    }

    @Test
    public void testCreateAndCopyExcludesForBean() {

        UserEntity src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.phones = new String[]{"111-2222-3333", "444-5555-6666"};
        src.address = new AddressEntity("111-2222", "東京都新宿区");

        UserDto dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "lastName");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is(nullValue()));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address.postCode, is("111-2222"));
        assertThat(dest.address.addr, is("東京都新宿区"));

        // コピー先に存在しないプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";
        src.ssn = "123456789";

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address", "ssn");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // コピー元に存在しないプロパティを指定するケース
        src = new UserEntity();
        src.age = 10;
        src.firstName = "太朗";
        src.lastName = "山田";

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address", "undefined");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // コピー元がnullのケース
        src = null;
        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is(nullValue()));
        assertThat(dest.lastName, is(nullValue()));

        // サロゲートペアを扱うテストケース
        src = new UserEntity();
        src.firstName = "😁";
        src.lastName = "😁";
        src.address = new AddressEntity("111-2222", "😁");

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src);

        assertThat(dest.firstName, is("😁"));
        assertThat(dest.lastName, is("😁"));
        assertThat(dest.address.addr, is("😁"));
    }

    @Test
    public void testCreateAndCopyIncludesForMap() {

        Map<String, Object> src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("phoneNumbers", new String[]{"111-2222-3333", "444-5555-6666"});
            put("bin", new byte[] {0x30});
        }};

        UserDto dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "phoneNumbers", "bin");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is(nullValue()));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address, is(nullValue()));
        assertThat(dest.bin, is(new byte[] {0x30}));

        // null値のプロパティを指定するケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("bin", null);
        }};

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "phoneNumbers", "bin");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(nullValue()));
        assertThat(dest.bin, is(nullValue()));

        // コピー先に存在しないプロパティを指定するケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("ssn", "123456789");
        }};

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "ssn");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while writing to the property :ssn"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));

        // コピー元に存在しないプロパティを指定するケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
        }};

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName", "undefined");

        assertThat(dest.age, is(10));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // コピー元がnullのケース
        src = null;
        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "age", "firstName", "lastName");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is(nullValue()));
        assertThat(dest.lastName, is(nullValue()));

        // コピー先のクラスにデフォルトコンストラクタが存在しないケース
        try {
            BeanUtil.createAndCopyIncludes(AddressEntity.class, src);
        } catch (Exception e) {
            assertThat(e, instanceOf(BeansException.class));
        }

        // サロゲートペアを扱うテストケース
        src = new HashMap<String, Object>(){{
            put("firstName", "😁");
            put("lastName", "😁");
        }};

        dest = BeanUtil.createAndCopyIncludes(UserDto.class, src, "firstName", "lastName");

        assertThat(dest.firstName, is("😁"));
        assertThat(dest.lastName, is("😁"));
    }

    @Test
    public void testCreateAndCopyExcludesForMap() {

        Map<String, Object> src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("phoneNumbers", new String[]{"111-2222-3333", "444-5555-6666"});
        }};

        UserDto dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "adress");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(dest.phones, is(new String[]{"111-2222-3333", "444-5555-6666"}));
        assertThat(dest.address, is(nullValue()));

        // コピー先に存在しないプロパティを除外指定するケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("ssn", "123456789");
        }};

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address", "ssn");

        // コピー先に存在しないプロパティを除外指定しないケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
            put("ssn", "123456789");
        }};

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));
        assertThat(OnMemoryLogWriter.getMessages("writer.memory"), contains(allOf(
                containsString("An error occurred while writing to the property :ssn"),
                not(containsString("nablarch.core.beans.BeansException"))
        )));

        // コピー元に存在しないプロパティを指定するケース
        src = new HashMap<String, Object>(){{
            put("age", 10);
            put("firstName", "太朗");
            put("lastName", "山田");
        }};

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address", "undefined");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is("太朗"));
        assertThat(dest.lastName, is("山田"));

        // コピー元がnullのケース
        src = null;
        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src, "age", "address");

        assertThat(dest.age, is(0));
        assertThat(dest.firstName, is(nullValue()));
        assertThat(dest.lastName, is(nullValue()));

        // コピー先のクラスにデフォルトコンストラクタが存在しないケース
        try {
            BeanUtil.createAndCopyExcludes(AddressEntity.class, src);
        } catch (Exception e) {
            assertThat(e, instanceOf(BeansException.class));
        }

        // サロゲートペアを扱うテストケース
        src = new HashMap<String, Object>(){{
            put("firstName", "😁");
            put("lastName", "😁");
        }};

        dest = BeanUtil.createAndCopyExcludes(UserDto.class, src);

        assertThat(dest.firstName, is("😁"));
        assertThat(dest.lastName, is("😁"));
    }

    @Test(expected = BeansException.class)
    public void testGetPropertyDescriptorsForException(@Mocked final Introspector intro) throws IntrospectionException {
        // PropertyDescriptor取得時に例外が発生するケース
        new Expectations() {{
            Introspector.getBeanInfo(UserEntity.class); result = new IntrospectionException("test");
        }};
        BeanUtil.getPropertyDescriptors(UserEntity.class);
    }

    @Test(expected = BeansException.class)
    public void testGetPropertyDescriptorForException(@Mocked final Introspector intro) throws IntrospectionException {
        // PropertyDescriptor取得時に例外が発生するケース
        new Expectations() {{
            Introspector.getBeanInfo(UserEntity.class); result = new IntrospectionException("test");
        }};
        BeanUtil.getPropertyDescriptor(UserEntity.class, "age");
    }

    /** ネストしたプロパティにコピーできること。*/
    @Test
    public void testNestedProperties() {
        Map<String, String[]> req = new HashMap<String, String[]>();

        req.put("address.addr", new String[]{"tokyo"});
        req.put("phoneNumbers", new String[]{"012", "3456", "7890"});

        UserDto actual = BeanUtil.createAndCopy(UserDto.class, req);
        assertThat(actual.getAddress().getAddr(), is("tokyo"));
        assertThat(actual.getPhoneNumbers(), is(new String[] {"012", "3456", "7890"}));


    }

    /**
     * BeanからMapが作成できること
     */
    @Test
    public void testCreateMapAndCopy() throws Exception {
        final UserDto input = new UserDto();
        input.setFirstName("first_name");
        input.setLastName("last_name");
        input.setAge(10);
        input.setBin(new byte[] {0x31, 0x33, 0x35});
        input.setPhoneNumbers(new String[] {"01", "02", "03"});

        Map<String, Object> actual = BeanUtil.createMapAndCopy(input);

        assertThat(actual, allOf(
                hasEntry("firstName", "first_name"),                            // そのまま移送
                hasEntry("lastName", "last_name"),
                hasEntry("fullName", "first_name last_name"),                   // getterでの編集結果が移送
                hasEntry("phoneNumbers", new String[] {"01", "02", "03"}),      // 配列も移送出来る
                hasEntry("age", Integer.valueOf(10)),                           // プリミティブを返すgetter
                hasEntry("address", null),                                      // 値が設定されていないproperty
                hasEntry("bin", new byte[] {0x31, 0x33, 0x35})                  // バイト配列もコピーできること
        ));
    }

    /**
     * ネストしたプロパティは、キーが「parent.child」となること。
     */
    @Test
    public void testCreateMapAndCopy_nestedProperty() throws Exception {
        final UserDto input = new UserDto();
        final Address address = new Address();
        address.setPostCode("1111234");
        address.setAddr("住所");
        input.setAddress(address);
        input.setFirstName("first");
        input.setLastName("last");

        final Map<String, Object> actual = BeanUtil.createMapAndCopy(input);

        assertThat(actual, allOf(
                hasEntry("firstName", "first"),
                hasEntry("lastName", "last"),
                hasEntry("address.postCode", "1111234"),
                hasEntry("address.addr", "住所")
        ));

        // サロゲートペアを扱うテストケース
        address.setAddr("😁");
        input.setAddress(address);
        input.setFirstName("😁");
        input.setLastName("😁");

        final Map<String, Object> surrogate = BeanUtil.createMapAndCopy(input);
        assertThat(surrogate, allOf(
                hasEntry("firstName", "😁"),
                hasEntry("lastName", "😁"),
                hasEntry("address.addr", "😁")
        ));
    }

    /**
     * 指定したプロパティを除いてMapにコピーできること。
     */
    @Test
    public void testCreateMapAndCopy_excludeProperty() throws Exception {
        final UserDto input = new UserDto();
        final Address address = new Address();
        address.setPostCode("1111234");
        address.setAddr("住所");
        input.setAddress(address);
        input.setFirstName("first");
        input.setLastName("last");
        input.setBin(new byte[] {0x31});

        final Map<String, Object> actual = BeanUtil.createMapAndCopyExcludes(input, "addr", "fullName", "bin");

        assertThat(actual, allOf(
                hasEntry("firstName", "first"),
                hasEntry("lastName", "last"),
                hasEntry("address.postCode", "1111234"),
                not(hasKey("fullName")),            // コピー対象外
                not(hasKey("address.addr")),        // コピー対象外
                not(hasKey("bin"))                  // コピー対象外
        ));

        // サロゲートペアを扱うテストケース
        address.setAddr("😁");
        input.setAddress(address);
        input.setFirstName("😁");
        input.setLastName("😁");

        final Map<String, Object> surrogate = BeanUtil.createMapAndCopyExcludes(input,"addr");
        assertThat(surrogate, allOf(
                hasEntry("firstName", "😁"),
                hasEntry("lastName", "😁"),
                not(hasKey("address.addr"))        // コピー対象外
        ));
    }

    /**
     * 指定したプロパティのみMapにコピーできること。
     */
    @Test
    public void testCreateMapAndCopy_includesProperty() throws Exception {
        final UserDto input = new UserDto();
        final Address address = new Address();
        address.setPostCode("1111234");
        address.setAddr("住所");
        input.setAddress(address);
        input.setFirstName("first");
        input.setPhoneNumbers(new String[] {"1", "3", "5"});
        input.setLastName("last");
        input.setBin(new byte[] {0x32, 0x33, 0x00});

        final Map<String, Object> actual = BeanUtil.createMapAndCopyIncludes(input, "address", "fullName",
                "phoneNumbers", "bin");

        assertThat(actual, allOf(
                hasEntry("fullName", "first last"),
                hasEntry("address.addr", "住所"),
                hasEntry("address.postCode", "1111234"),
                hasEntry("phoneNumbers", new String[] {"1", "3", "5"}),
                hasEntry("bin", new byte[] {0x32, 0x33, 0x00}),
                not(hasKey("firstName")),
                not(hasKey("lastName"))
        ));

        // サロゲートペアを扱うテストケース
        address.setAddr("😁");
        input.setAddress(address);
        input.setFirstName("😁");

        final Map<String, Object> surrogate = BeanUtil.createMapAndCopyIncludes(input, "address");
        assertThat(surrogate, allOf(
                hasEntry("address.addr", "😁"),
                not(hasKey("firstName")),
                not(hasKey("lastName"))
        ));
    }

    /**
     * ネストしているBeanのプロパティ名をコピー対象としなかった場合、
     * そのBeanの属性はコピーされないこと。
     */
    @Test
    public void testCreateMapAndCopy_includesProperty_topLevelOnly() throws Exception {
        final UserDto input = new UserDto();
        final Address address = new Address();
        address.setPostCode("1111234");
        address.setAddr("住所");
        input.setAddress(address);
        input.setFirstName("first");
        input.setLastName("last");

        final Map<String, Object> actual = BeanUtil.createMapAndCopyIncludes(input, "fullName");

        assertThat(actual, allOf(
                hasEntry("fullName", "first last"),
                not(hasKey("address.addr")),                // addressがコピー対象ではないのでキーは存在しない
                not(hasKey("address.postCode"))             // addressがコピー対象ではないのでキーは存在しない
        ));
    }

    /**
     * MapとBean間の相互変換が出来ること。
     */
    @Test
    public void testInterconversionOfMapAndBean() throws Exception {
        final UserDto user = new UserDto();
        final Address address = new Address();
        address.setPostCode("1111222");
        address.setAddr("住所");
        user.setAddress(address);
        user.setFirstName("first");
        user.setLastName("last");
        user.setAge(50);
        user.setBin(new byte[] {0x00, 0x01, 0x02});

        final Map<String, Object> map = BeanUtil.createMapAndCopy(user);
        final UserDto actual = BeanUtil.createAndCopyExcludes(UserDto.class, map);

        assertThat(actual, allOf(
                hasProperty("firstName", is("first")),
                hasProperty("lastName", is("last")),
                hasProperty("age", is(50)),
                hasProperty("phoneNumbers", nullValue()),
                hasProperty("bin", is(new byte[] {0x00, 0x01, 0x02})),
                hasProperty("address", allOf(
                        hasProperty("postCode", is("1111222")),
                        hasProperty("addr", is("住所"))
                ))
        ));

        address.setAddr("😁");
        user.setAddress(address);
        user.setFirstName("😁");
        user.setLastName("😁");

        final Map<String, Object> map2 = BeanUtil.createMapAndCopy(user);
        final UserDto surrogate = BeanUtil.createAndCopyExcludes(UserDto.class, map2);

        assertThat(surrogate, allOf(
                hasProperty("firstName", is("😁")),
                hasProperty("lastName", is("😁")),
                hasProperty("address", allOf(
                        hasProperty("postCode", is("1111222")),
                        hasProperty("addr", is("😁"))
                ))
        ));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * 配列の指定したインデックスに値をセットできるか。
     */
    @Test
    public void testSetPropertyForArrayCopyToSpecifiedIndex(){
        DestBean dest = new DestBean();
        BeanUtil.setProperty(dest, "strArray[1]", "A");

        assertThat(dest.getStrArray(), arrayContaining(null, "A"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * 配列の長さを超えたインデックスを指定しても値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexWhenArrayLengthIsNotEnough(){
        DestBean dest = new DestBean();
        dest.setStrArray(new String[1]);
        BeanUtil.setProperty(dest, "strArray[1]", "A");

        assertThat(dest.getStrArray(), arrayContaining(null, "A"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * 配列の指定したインデックスにあるオブジェクトに値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexOfArrayWhenValueTypeIsObject(){
        DestBean dest = new DestBean();
        dest.setAddressArray(new Address[]{new Address("1", "Tokyo")});
        BeanUtil.setProperty(dest, "addressArray[0].postCode", "2");

        Address address = (Address) dest.getAddressArray()[0];
        assertThat(address.getPostCode(), is("2"));
        assertThat(address.getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * 配列の指定したインデックスにオブジェクトが存在しない場合、
     * 新しくインスタンスを生成して値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexWhenArrayNotInstantiationAndValueTypeIsObject(){
        DestBean dest = new DestBean();
        dest.setAddressArray(new Address[]{});
        BeanUtil.setProperty(dest, "addressArray[0].postCode", "1");

        Address address = (Address) dest.getAddressArray()[0];
        assertThat(address.getPostCode(), is("1"));
        assertThat(address.getAddr(), isEmptyOrNullString());
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * リストの指定したインデックスに値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndex(){
        DestBean dest = new DestBean();
        BeanUtil.setProperty(dest, "strList[1]", "A");

        assertThat(dest.getStrList(), contains(null, "A"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * リストのサイズを超えたインデックスを指定しても値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexWhenListSizeIsNotEnough(){
        DestBean dest = new DestBean();
        dest.setStrList(new ArrayList<String>(1));
        BeanUtil.setProperty(dest, "strList[1]", "B");

        assertThat(dest.getStrList(), contains(null, "B"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * リストの指定したインデックスにあるオブジェクトに値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexOfListWhenValueTypeIsObject(){
        DestBean dest = new DestBean();
        List<Address> list = new ArrayList<Address>();
        list.add(new Address("1", "Tokyo"));
        dest.setAddressList(list);
        BeanUtil.setProperty(dest, "addressList[0].postCode", "2");

        Address address = (Address) dest.getAddressList().get(0);
        assertThat(address.getPostCode(), is("2"));
        assertThat(address.getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * リストの指定したインデックスにオブジェクトが存在しない場合、
     * 新しく生成して値をセットできるか。
     */
    @Test
    public void testSetPropertyToSpecifiedIndexWhenValueNotInstantiationAndValueTypeIsObject(){
        DestBean dest = new DestBean();
        dest.setAddressList(new ArrayList<Address>());
        BeanUtil.setProperty(dest, "addressList[0].postCode", "2");

        Address address = (Address) dest.getAddressList().get(0);
        assertThat(address.getPostCode(), is("2"));
        assertThat(address.getAddr(), isEmptyOrNullString());
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Map)}のテスト。
     * <p/>
     * 配列の指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strArray[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopy(dest.getClass(), src);

        assertThat(dest.getStrArray().length, is(2));
        assertThat(dest.getStrArray(), arrayContaining(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Map)}のテスト。
     * <p/>
     * 配列の指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyNestedPropertyToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressArray[1].postCode", "123-1234");
        src.put("addressArray[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopy(dest.getClass(), src);

        Address[] address = Arrays.asList(dest.getAddressArray()).toArray(new Address[dest.getAddressArray().length]);
        assertThat(dest.getAddressArray().length, is(2));
        assertThat(address[0], nullValue());
        assertThat(address[1].getPostCode(), is("123-1234"));
        assertThat(address[1].getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Map)}のテスト。
     * <p/>
     * リストの指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strList[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopy(dest.getClass(), src);

        assertThat(dest.getStrList().size(), is(2));
        assertThat(dest.getStrList(), contains(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Map)}のテスト。
     * <p/>
     * リストの指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyNestedPropertyToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressList[1].postCode", "123-1234");
        src.put("addressList[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopy(dest.getClass(), src);

        List<Address> list = dest.getAddressList();
        assertThat(list.size(), is(2));
        assertThat(list.get(0), nullValue());
        assertThat(list.get(1).getPostCode(), is("123-1234"));
        assertThat(list.get(1).getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * リストの指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyIncludesToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strList[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyIncludes(dest.getClass(), src, "strList[1]");

        assertThat(dest.getStrList().size(), is(2));
        assertThat(dest.getStrList(), contains(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * 配列の指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyIncludesToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strArray[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyIncludes(dest.getClass(), src, "strArray[1]");

        assertThat(dest.getStrArray().length, is(2));
        assertThat(dest.getStrArray(), arrayContaining(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * リストの指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyIncludesNestedPropertyToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressList[1].postCode", "123-1234");
        src.put("addressList[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyIncludes(dest.getClass(), src, "addressList[1].postCode");

        List<Address> list = dest.getAddressList();
        assertThat(list.size(), is(2));
        assertThat(list.get(0), nullValue());
        assertThat(list.get(1).getPostCode(), is("123-1234"));
        assertThat(list.get(1).getAddr(), isEmptyOrNullString());
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * 配列の指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyIncludesNestedPropertyToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressArray[1].postCode", "123-1234");
        src.put("addressArray[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyIncludes(dest.getClass(), src, "addressArray[1].postCode");

        Address[] address = Arrays.asList(dest.getAddressArray()).toArray(new Address[dest.getAddressArray().length]);
        assertThat(dest.getAddressArray().length, is(2));
        assertThat(address[0], nullValue());
        assertThat(address[1].getPostCode(), is("123-1234"));
        assertThat(address[1].getAddr(), isEmptyOrNullString());
    }

    /**
     * {@link BeanUtil#createAndCopyExcludes(Class, Map, String...)}のテスト。
     * <p/>
     * リストの指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyExcludesToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strList[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyExcludes(dest.getClass(), src, "sample");

        assertThat(dest.getStrList().size(), is(2));
        assertThat(dest.getStrList(), contains(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopyExcludes(Class, Map, String...)}のテスト。
     * <p/>
     * 配列の指定したインデックスの値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyExcludesToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("strArray[1]", "A");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyExcludes(dest.getClass(), src, "sample");

        assertThat(dest.getStrArray().length, is(2));
        assertThat(dest.getStrArray(), arrayContaining(null, "A"));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * 配列の指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyExcludesNestedPropertyToSpecifiedIndexOfArray(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressArray[1].postCode", "123-1234");
        src.put("addressArray[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyExcludes(dest.getClass(), src, "sample");

        Address[] address = Arrays.asList(dest.getAddressArray()).toArray(new Address[dest.getAddressArray().length]);
        assertThat(dest.getAddressArray().length, is(2));
        assertThat(address[0], nullValue());
        assertThat(address[1].getPostCode(), is("123-1234"));
        assertThat(address[1].getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#createAndCopyExcludes(Class, Map, String...)}のテスト。
     * <p/>
     * リストの指定したインデックスにあるネストした値をbeanにコピーできるか。
     */
    @Test
    public void testCreateAndCopyExcludesNestedPropertyToSpecifiedIndexOfList(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("addressList[1].postCode", "123-1234");
        src.put("addressList[1].addr", "Tokyo");
        DestBean dest = new DestBean();
        dest = BeanUtil.createAndCopyExcludes(dest.getClass(), src, "sample");

        List<Address> list = dest.getAddressList();
        assertThat(list.size(), is(2));
        assertThat(list.get(0), nullValue());
        assertThat(list.get(1).getPostCode(), is("123-1234"));
        assertThat(list.get(1).getAddr(), is("Tokyo"));
    }

    /**
     * {@link BeanUtil#setProperty(Object, String, Object)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してセットできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testSetPropertyForConvertingProperty(){
        DestBean dest = new DestBean();
        BeanUtil.setProperty(dest, "sample", "123");

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#copy(Object, Object)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCopyForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = new DestBean();
        BeanUtil.copy(src, dest);

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#copyIncludes(Object, Object, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCopyIncludesForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = new DestBean();
        BeanUtil.copyIncludes(src, dest, "sample", "strList");

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#copyExcludes(Object, Object, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCopyExcludesForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = new DestBean();
        BeanUtil.copyExcludes(src, dest);

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#copyExcludesNull(Object, Object)} (Object, Object, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCopyExcludesNullForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = new DestBean();
        BeanUtil.copyExcludesNull(src, dest);

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Map)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyMapToBeanForConvertingProperty(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("sample", "123");
        DestBean dest = BeanUtil.createAndCopy(DestBean.class, src);

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopy(Class, Object)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyBeanToBeanForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = BeanUtil.createAndCopy(DestBean.class, src);

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Map, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyIncludesMapToBeanForConvertingProperty(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("sample", 123);
        DestBean dest = BeanUtil.createAndCopyIncludes(DestBean.class, src, "sample");

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopyIncludes(Class, Object, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyIncludesBeanToBeanForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = BeanUtil.createAndCopyIncludes(DestBean.class, src, "sample");

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopyExcludes(Class, Map, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyExcludesMapToBeanForConvertingProperty(){
        Map<String, Object> src = new HashMap<String, Object>();
        src.put("sample", "123");
        DestBean dest = BeanUtil.createAndCopyExcludes(DestBean.class, src, "");

        assertThat(dest.getSample(), is(123));
    }

    /**
     * {@link BeanUtil#createAndCopyExcludes(Class, Object, String...)}のテスト。
     * <p/>
     * 異なる型のプロパティを変換してコピーできるか。
     * このテストではString→Integerに変換する。
     */
    @Test
    public void testCreateAndCopyExcludesBeanToBeanForConvertingProperty(){
        SrcBean src = new SrcBean("123");
        DestBean dest = BeanUtil.createAndCopyExcludes(DestBean.class, src, "");

        assertThat(dest.getSample(), is(123));
    }

    @Test
    public void testCopyNullOnlyStringArray() throws Exception {
        final HashMap<String, Object> input = new HashMap<String, Object>();
        input.put("firstName", new String[] {null});
        input.put("lastName", new String[] {"なまえ"});

        final UserDto actual = BeanUtil.createAndCopy(UserDto.class, input);
        assertThat(actual, allOf(
                hasProperty("firstName", is(nullValue())),
                hasProperty("lastName", is("なまえ"))
        ));
        
    }

    /**
     * マイクロ秒を持つTimestampがコピー出来ることを検証するケース。
     */
    @Test
    public void copyTimestampWithMicroSeconds() throws Exception {
        final WithTimestamp src = new WithTimestamp();
        final Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        timestamp.setNanos(100000001);
        src.timestamp = timestamp;
        final WithTimestamp actual = BeanUtil.createAndCopy(WithTimestamp.class, src);
        assertThat(actual.timestamp, is(timestamp));
    }

    public static class WithTimestamp {

        private Timestamp timestamp;

        public Timestamp getTimestamp() {
            return timestamp;
        }

        public void setTimestamp(final Timestamp timestamp) {
            this.timestamp = timestamp;
        }
    }


    private static Matcher<Map<? extends String, ?>> hasEntry(String key, Object value) {
        return IsMapContaining.<String, Object>hasEntry(key, value);
    }
}
