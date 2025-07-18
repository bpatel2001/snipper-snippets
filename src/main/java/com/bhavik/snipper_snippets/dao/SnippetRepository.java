package com.bhavik.snipper_snippets.dao;

import com.bhavik.snipper_snippets.entity.Snippets;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SnippetRepository extends JpaRepository<Snippets, Long> {
}
