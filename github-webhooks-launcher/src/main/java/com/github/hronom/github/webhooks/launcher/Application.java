package com.github.hronom.github.webhooks.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {
    private static final Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        logger.info("Application.main");
        SpringApplication.run(new Class[] {Application.class}, args);
    }
}
