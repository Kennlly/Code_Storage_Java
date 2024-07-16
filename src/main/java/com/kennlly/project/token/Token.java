package com.kennlly.project.token;

public record Token(
      String access_token,
      String token_type,
      int expires_in
) {
}