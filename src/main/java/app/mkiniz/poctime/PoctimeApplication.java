package app.mkiniz.poctime;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class PoctimeApplication {

    public static void main(String[] args) {
        SpringApplication.run(PoctimeApplication.class, args);
    }
}
