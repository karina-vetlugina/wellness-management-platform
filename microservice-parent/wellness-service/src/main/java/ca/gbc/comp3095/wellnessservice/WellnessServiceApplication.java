package ca.gbc.comp3095.wellnessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;


@SpringBootApplication
@EnableCaching
public class WellnessServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(WellnessServiceApplication.class, args);
    }

}
