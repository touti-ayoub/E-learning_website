package tn.esprit.tpfoyer17;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

@SpringBootApplication
@EnableScheduling
@EnableAspectJAutoProxy
public class TpFoyer17Application {

    public static void main(String[] args) {
        SpringApplication.run(TpFoyer17Application.class, args);
    }

}
