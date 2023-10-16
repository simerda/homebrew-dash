package cz.jansimerda.homebrewdash;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HomebrewDashApplication {

    public static void main(String[] args) {
        SpringApplication.run(HomebrewDashApplication.class, args);
    }

}
