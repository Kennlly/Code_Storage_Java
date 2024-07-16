package com.kennlly.project.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class HttpClient {
   @Value("${genesys.tokenUrl}")
   private String tokenUrl;


   @Bean
   RestClient tokenHttpClient() {
      return RestClient.builder()
                       .baseUrl(tokenUrl)
                       .build();
   }
}