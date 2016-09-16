package nablarch.core.beans;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class BeanUtilPerformanceTest {

    private static final int EXECUTE_COUNT = 1000000;

    /**
     * createCopyAndMapの処理時間をBeanUtilとJacksonで計測するメソッド
     */
    @Test
    public void createMapAndCopy() throws Exception {
        final InputBean inputBean = new InputBean("firstName", "lastName", "1234567", "address", 30, new Date());

        System.out.println("------------------------------ nablarch");
        final PerformanceTestExecutor executor = new PerformanceTestExecutor();
        executor.execute(EXECUTE_COUNT, new PerformanceTestStrategy<InputBean>() {
            @Override
            public void doPerformanceTest(final InputBean src) {
                BeanUtil.createMapAndCopy(src);
            }

            @Override
            public InputBean createSrc() {
                return inputBean;
            }
        });

        System.out.println("------------------------------ jackson");
        executor.execute(EXECUTE_COUNT, new PerformanceTestStrategy<InputBean>() {
            ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void doPerformanceTest(final InputBean src) {
                objectMapper.convertValue(src, Map.class);
            }

            @Override
            public InputBean createSrc() {
                return inputBean;
            }
        });

    }

    /**
     * copyIncludesの処理時間をBeanUtilとJacksonで計測するメソッド
     */
    @Test
    public void copyIncludes() throws Exception {
        final InputBean inputBean = new InputBean("firstName", "lastName", "1234567", "address", 30, new Date());

        System.out.println("------------------------------ nablarch");
        final PerformanceTestExecutor executor = new PerformanceTestExecutor();
        executor.execute(EXECUTE_COUNT, new PerformanceTestStrategy<InputBean>() {
            @Override
            public void doPerformanceTest(final InputBean src) {
                BeanUtil.copyIncludes(src, new OutputBean(), "firstName", "postalNo", "age", "birthDay");
            }

            @Override
            public InputBean createSrc() {
                return inputBean;
            }
        });

        System.out.println("------------------------------ jackson");
        executor.execute(EXECUTE_COUNT, new PerformanceTestStrategy<InputBean>() {
            ObjectMapper objectMapper = new ObjectMapper();

            @Override
            public void doPerformanceTest(final InputBean src) {
                objectMapper.convertValue(src, OutputBean.class);
            }

            @Override
            public InputBean createSrc() {
                return inputBean;
            }
        });

    }

    private static class InputBean {

        private String firstName;
        
        private String lastName;

        private String postalNo;

        private String address;

        private Integer age;
        
        private Date birthDay;

        public InputBean() {
        }

        public InputBean(final String firstName, final String lastName, final String postalNo, final String address,
                final Integer age, final Date birthDay) {
            this.firstName = firstName;
            this.postalNo = postalNo;
            this.address = address;
            this.age = age;
        }

        public String getFirstName() {
            return firstName;
        }

        public void setFirstName(final String firstName) {
            this.firstName = firstName;
        }

        public String getLastName() {
            return lastName;
        }

        public void setLastName(final String lastName) {
            this.lastName = lastName;
        }

        public String getPostalNo() {
            return postalNo;
        }

        public void setPostalNo(final String postalNo) {
            this.postalNo = postalNo;
        }

        public String getAddress() {
            return address;
        }

        public void setAddress(final String address) {
            this.address = address;
        }

        public Integer getAge() {
            return age;
        }

        public void setAge(final Integer age) {
            this.age = age;
        }

        public Date getBirthDay() {
            return birthDay;
        }

        public void setBirthDay(final Date birthDay) {
            this.birthDay = birthDay;
        }
    }

    private static class OutputBean extends InputBean {}

    interface PerformanceTestStrategy<T> {

        void doPerformanceTest(T src);

        T createSrc();
    }

    private static class PerformanceTestExecutor {

        private <T> void execute(int count, PerformanceTestStrategy<T> strategy) {
            final T src = strategy.createSrc();
            // これはすてるやつ
            for (int i = 0; i < count; i++) {
                strategy.doPerformanceTest(src);
            }

            // これが計測用
            for (int i = 0; i < 5; i++) {
                long start = System.nanoTime();
                for (int j = 0; j < count; j++) {
                    strategy.doPerformanceTest(src);
                }
                long end = System.nanoTime();
                System.out.println("execute time(ms) = " + TimeUnit.NANOSECONDS.toMillis(end - start));
            }
        }
    }
}
