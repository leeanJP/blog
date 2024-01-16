package com.it.blog.service;

import com.it.blog.domain.Article;
import com.it.blog.dto.AddArticleRequest;
import com.it.blog.dto.UpdateArticleRequest;
import com.it.blog.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor //final이 붙거나 @Notnull 이 붙은 필드의 생성자 추가
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    //블로그 글 추가
    public Article save(AddArticleRequest request){
        return blogRepository.save(request.toEntity());
    }

    //블로그 글 전체 조회
    public List<Article> findAll(){
        return blogRepository.findAll(Sort.by("createdAt").descending());
    }

    //블로그 글 ID로 조회
    public Article findById(Long id){
        return blogRepository.findById(id)
                .orElseThrow(()-> new IllegalArgumentException("not found : " + id));
    }

    //블로그 글 삭제
    public void delete(long id){
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("not found : "+ id ));

        article.update(request.getTitle(), request.getContent());

        return article;
    }














}




