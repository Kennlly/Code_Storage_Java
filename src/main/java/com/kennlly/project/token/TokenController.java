package com.kennlly.project.token;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;

@Component
public class TokenController {
   private final RestClient tokenClient;
   private final RetryTemplate retryTemplate;
   private static final Logger logger = LoggerFactory.getLogger(TokenController.class);
   private final ObjectMapper objectMapper;
   @Value("${genesys.clientId}")
   private String clientId;
   @Value("${genesys.clientSecret}")
   private String clientSecret;


   public TokenController(RestClient tokenClient, RetryTemplate retryTemplate, ObjectMapper objectMapper) {
      this.tokenClient = tokenClient;
      this.retryTemplate = retryTemplate;
      this.objectMapper = objectMapper;
   }

   public String getToken() throws IOException {
      try {
         getTokenPathReady();

         try (InputStream inputStream = TypeReference.class.getResourceAsStream("/data/token.json")) {
            if (inputStream != null && inputStream.available() > 0) {
               Token token = objectMapper.readValue(inputStream, new TypeReference<>() {
               });

               return token.access_token();
            }
         }

         // Refresh Token
         Token token = fetchToken();
         objectMapper.writeValue(Files.newOutputStream(Paths.get("src/main/resources/data/token.json")), token);

         return token.access_token();
      } catch (IOException e) {
         logger.error("Getting token from file failed: {}", e.getMessage(), e);

         throw e;
      }
   }

   private Token fetchToken() {
      String originalInput = this.clientId + ":" + this.clientSecret;
      String encodedString = Base64.getEncoder()
                                   .encodeToString(originalInput.getBytes());

      return retryTemplate.execute(_ -> {
         try {
            return tokenClient.post()
                              .uri(uriBuilder -> uriBuilder
                                    .path("/oauth/token")
                                    .queryParam("grant_type", "client_credentials")
                                    .build())
                              .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                              .header(HttpHeaders.AUTHORIZATION, "Basic " + encodedString)
                              .retrieve()
                              .body(Token.class);
         } catch (RestClientException e) {
            logger.error("Error fetching token: {}", e.getMessage(), e);

            throw e;
         }
      });
   }


   private void getTokenPathReady() throws IOException {
      try {
         // Define the package (directory) and file paths
         Path packagePath = Paths.get("src/main/resources/data");
         Path filePath = packagePath.resolve("token.json");

         // Check if the package (directory) exists
         if (!Files.exists(packagePath)) Files.createDirectories(packagePath);

         // Check if the file exists
         if (!Files.exists(filePath)) Files.createFile(filePath);
      } catch (IOException e) {
         logger.error("Creating token file failed: {}", e.getMessage(), e);

         throw e;
      }
   }
}