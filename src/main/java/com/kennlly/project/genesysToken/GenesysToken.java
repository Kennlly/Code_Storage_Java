package com.kennlly.project.genesysToken;

public record GenesysToken(
   String access_token,
   String token_type,
   int expires_in
) {
}