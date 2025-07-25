package com.bhavik.snipper_snippets.controller;

import com.bhavik.snipper_snippets.dao.SnippetRepository;
import com.bhavik.snipper_snippets.entity.Snippets;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import com.bhavik.snipper_snippets.security.JwtUtil;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.List;

@RestController
@RequestMapping("/snippets")
public class SnippetController {
    private final SnippetRepository repo;
    @Autowired
    private JwtUtil jwtUtil;

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
    public Snippets create(@RequestHeader("Authorization") String authHeader, @RequestBody Snippets snippets) throws Exception {
        validateToken(authHeader);
        snippets.setCode(encrypt(snippets.getCode()));
        return repo.save(snippets);
    }

    @GetMapping("/{id}")
    public Snippets get(@RequestHeader("Authorization") String authHeader, @PathVariable Long id) throws Exception {
        validateToken(authHeader);
        Snippets snippets = repo.findById(id).orElseThrow();
        snippets.setCode(decrypt(snippets.getCode()));
        return snippets;
    }

    @GetMapping
    public List<Snippets> getAll(@RequestHeader("Authorization") String authHeader) throws Exception {
        validateToken(authHeader);
        List<Snippets> snippets = repo.findAll();
        for (Snippets s : snippets) {
            s.setCode(decrypt(s.getCode()));
        }
        return snippets;
    }
    private void validateToken(String authHeader) {
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Missing or invalid Authorization header");
        }
        String token = authHeader.substring("Bearer ".length());
        if (jwtUtil.isTokenExpired(token)) {
            throw new RuntimeException("Token expired");
        }
        jwtUtil.extractEmail(token); // Throws if invalid
    }
}
