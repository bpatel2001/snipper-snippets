package com.bhavik.snipper_snippets.entity;

import jakarta.persistence.*;

@Entity
public class Snippets {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

    @Lob
    private String code;

    public Snippets() {}

    public Snippets(String title, String code) {
        this.title = title;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
