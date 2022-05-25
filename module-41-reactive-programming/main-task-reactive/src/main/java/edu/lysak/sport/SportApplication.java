package edu.lysak.sport;

import edu.lysak.sport.setup.SportSetup;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class SportApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SportApplication.class, args);

        // TODO: uncomment next lines if run application for the first time
//        SportSetup sportSetup = context.getBean(SportSetup.class);
//        sportSetup.getSportDataAndSaveToDb();
    }
}
