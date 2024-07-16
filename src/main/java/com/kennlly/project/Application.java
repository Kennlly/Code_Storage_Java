package com.kennlly.project;

import com.kennlly.project.token.TokenController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;

import java.io.IOException;

@SpringBootApplication
public class Application {

   private static final Logger logger = LoggerFactory.getLogger(Application.class);

   public static void main(String[] args) {
//      SpringApplication.run(Application.class, args);

      try {
         ApplicationContext context = SpringApplication.run(Application.class, args);

         TokenController tokenController = context.getBean(TokenController.class);
         String token = tokenController.getToken();

         logger.debug("token: {}", token);
      } catch (Error err) {
         logger.error("Application Error occurred: {}", err.getMessage(), err);
      } catch (IOException e) {
         throw new RuntimeException(e);
      }
   }
}