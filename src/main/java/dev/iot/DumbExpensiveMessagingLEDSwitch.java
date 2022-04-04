package dev.iot;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
@Slf4j
public class DumbExpensiveMessagingLEDSwitch {

    public static void main(String[] args) {
        SpringApplication.run(DumbExpensiveMessagingLEDSwitch.class, args);
    }

}
