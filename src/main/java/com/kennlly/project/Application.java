package com.kennlly.project;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Application {

   private static final Logger log = LoggerFactory.getLogger(Application.class);

   public static void main(String[] args) {
      SpringApplication.run(Application.class, args);

      log.debug("Logging Debug");
      log.info("Logging Info");
      log.warn("Logging Warn");
      log.error("Logging Error");
   }

}