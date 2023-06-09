package hello.core.singleton;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotatedBeanDefinitionReader;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Bean;

import static org.junit.jupiter.api.Assertions.*;

class StatefulServiceTest {

    @Test
    void statefulServiceSingleton() {

        ApplicationContext ac = new AnnotationConfigApplicationContext(TestConfig.class);

        StatefulService statefulService1 = ac.getBean(StatefulService.class);
        StatefulService statefulService2 = ac.getBean(StatefulService.class);

        //ThreadA : 사용자A가 10000원 주문
//        statefulService1.order("userA", 10000);
        int userA = statefulService1.order("userA", 10000);
        //ThreadB : 사용자A가 20000원 주문
//        statefulService1.order("userB", 20000);
        int userB = statefulService1.order("userB", 20000);


        //ThreadA : 사용자A의 주문 금액을 조회
//        int price = statefulService1.getPrice();
        Assertions.assertThat(userA).isEqualTo(10000);
        Assertions.assertThat(userB).isEqualTo(20000);

    }

    static class TestConfig {
        @Bean
        public StatefulService statefulService(){
            return new StatefulService();
        }
    }
}