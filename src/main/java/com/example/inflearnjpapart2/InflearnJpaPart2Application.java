package com.example.inflearnjpapart2;

//import com.fasterxml.jackson.datatype.hibernate5.Hibernate5Module;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class InflearnJpaPart2Application {

    public static void main(String[] args) {
        SpringApplication.run(InflearnJpaPart2Application.class, args);
    }

    // Lazy loading 되어 있는 것을 강제로 로딩 시키는 방법 -> 엔티티를 외부에 직접 노출시키지 않는다면 할 필요 없다. DTO를 사용하는 것이 좋다.
    // 결론 : 엔티티를 외부에 노출시키지 말자!!
//    @Bean
//    Hibernate5Module hibernate5Module() {
//        Hibernate5Module hibernate5Module = new Hibernate5Module();
////         hibernate5Module.configure(Hibernate5Module.Feature.FORCE_LAZY_LOADING, true);
//        return hibernate5Module;
//    }
}
