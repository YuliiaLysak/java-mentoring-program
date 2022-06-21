package edu.lysak.sport;

import edu.lysak.sport.setup.SportSetup;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import java.text.NumberFormat;
import java.util.Arrays;

@Slf4j
@SpringBootApplication
public class SportApplication {

    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(SportApplication.class, args);

        String[] activeProfiles = context.getEnvironment().getActiveProfiles();
        if (Arrays.asList(activeProfiles).contains("docker")) {
            printMemoryLogs();
        } else {
            SportSetup sportSetup = context.getBean(SportSetup.class);
            sportSetup.initSportsWithBackPressure();
        }

        // First Attempt - don't need to use anymore
        // TODO: uncomment next lines if run application for the first time
//        sportSetup.getSportDataAndSaveToDb();
    }

    private static void printMemoryLogs() {
        Runtime runtime = Runtime.getRuntime();

        NumberFormat format = NumberFormat.getInstance();

        long maxMemory = runtime.maxMemory();
        long allocatedMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long mb = 1024 * 1024;
        String mega = " MB";

        log.info("========================== Memory Info ==========================");
        log.info("Free memory: {}", format.format(freeMemory / mb) + mega);
        log.info("Allocated memory: {}", format.format(allocatedMemory / mb) + mega);
        log.info("Max memory: {}", format.format(maxMemory / mb) + mega);
        log.info("Total free memory: {}", format.format((freeMemory + (maxMemory - allocatedMemory)) / mb) + mega);
        log.info("=================================================================\n");
    }
}
