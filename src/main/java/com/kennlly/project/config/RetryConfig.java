package com.kennlly.project.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryContext;
import org.springframework.retry.annotation.EnableRetry;
import org.springframework.retry.backoff.BackOffPolicy;
import org.springframework.retry.backoff.BackOffContext;
import org.springframework.retry.backoff.BackOffInterruptedException;
import org.springframework.retry.support.RetryTemplate;

@Configuration
@EnableRetry
public class RetryConfig {

   @Bean
   public RetryTemplate retryTemplate() {
      RetryTemplate retryTemplate = new RetryTemplate();
      retryTemplate.setBackOffPolicy(new CustomBackOffPolicy());
      return retryTemplate;
   }

   private static class CustomBackOffPolicy implements BackOffPolicy {
      @Override
      public BackOffContext start(RetryContext context) {
         return new CustomBackOffContext();
      }

      @Override
      public void backOff(BackOffContext backOffContext) throws BackOffInterruptedException {
         CustomBackOffContext context = (CustomBackOffContext) backOffContext;
         try {
            Thread.sleep(context.getInterval());
         } catch (InterruptedException e) {
            throw new BackOffInterruptedException("Thread interrupted while backing off", e);
         }
      }

      private static class CustomBackOffContext implements BackOffContext {
         private int attempt = 0;

         public long getInterval() {
            attempt++;
            return attempt * 10000L; // 10 seconds * attempt number
         }
      }
   }
}