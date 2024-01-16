package com.it.blog.repository;

import com.it.blog.domain.Article;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface BlogRepository extends JpaRepository<Article, Long> {
    List<Article> findAll();
}
