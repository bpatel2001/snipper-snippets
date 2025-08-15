package com.bhavik.snipper_snippets.controller;

import com.bhavik.snipper_snippets.dao.SnippetRepository;
import com.bhavik.snipper_snippets.entity.Snippets;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/snippets")
@PreAuthorize("isAuthenticated()")
public class SnippetController {
    private final SnippetRepository repo;

    public SnippetController(SnippetRepository repo) {
        this.repo = repo;
    }

    private static final String KEY = "1234567890123456";

    private String encrypt(String data) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        byte[] encrypted = cipher.doFinal(data.getBytes());
        return Base64.getEncoder().encodeToString(encrypted);
    }

    private String decrypt(String encrypted) throws Exception {
        Cipher cipher = Cipher.getInstance("AES");
        SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(), "AES");
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        byte[] decrypted = cipher.doFinal(Base64.getDecoder().decode(encrypted));
        return new String(decrypted);
    }

    @PostMapping
    public Snippets create(@AuthenticationPrincipal OidcUser principal, @RequestBody Snippets snippets) throws Exception {
        // You can access user info from principal if needed
        snippets.setCode(encrypt(snippets.getCode()));
        return repo.save(snippets);
    }

    @GetMapping("/{id}")
    public Snippets get(@AuthenticationPrincipal OidcUser principal, @PathVariable Long id) throws Exception {
        Snippets snippets = repo.findById(id).orElseThrow();
        snippets.setCode(decrypt(snippets.getCode()));
        return snippets;
    }

    @GetMapping
    public List<Snippets> getAll(@AuthenticationPrincipal OidcUser principal) throws Exception {
        List<Snippets> snippets = repo.findAll();
        for (Snippets s : snippets) {
            s.setCode(decrypt(s.getCode()));
        }
        return snippets;
    }
}