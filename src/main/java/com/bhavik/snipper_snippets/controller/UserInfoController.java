package com.bhavik.snipper_snippets.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.Map;

@RestController
public class UserInfoController {
    @GetMapping("/me")
    public Map<String, Object> me(@AuthenticationPrincipal OidcUser user) {
        if (user == null) {
            return Map.of("authenticated", false);
        }
        return Map.of(
            "authenticated", true,
            "name", user.getFullName(),
            "email", user.getEmail(),
            "claims", user.getClaims()
        );
    }
}
