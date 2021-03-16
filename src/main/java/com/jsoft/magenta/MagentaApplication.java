package com.jsoft.magenta;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
public class MagentaApplication {

    public static void main(String[] args) {
        SpringApplication.run(MagentaApplication.class, args);
    }

}
