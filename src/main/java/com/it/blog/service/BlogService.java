package com.it.blog.service;

import com.it.blog.domain.Article;
import com.it.blog.dto.AddArticleRequest;
import com.it.blog.dto.UpdateArticleRequest;
import com.it.blog.repository.BlogRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor //final이 붙거나 @Notnull 이 붙은 필드의 생성자 추가
@Service
public class BlogService {

    private final BlogRepository blogRepository;

    //블로그 글 추가
    public Article save(AddArticleRequest request, String userName){
        return blogRepository.save(request.toEntity(userName));
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
        Article article = blogRepository.findById(id)
                        .orElseThrow(()-> new IllegalArgumentException("not found : " + id));

        authorizeArticleAuthor(article);
        blogRepository.deleteById(id);
    }

    @Transactional
    public Article update(long id, UpdateArticleRequest request){
        Article article = blogRepository.findById(id).orElseThrow(()->
                new IllegalArgumentException("not found : "+ id ));

        authorizeArticleAuthor(article);
        article.update(request.getTitle(), request.getContent());

        return article;
    }


    //게시글을 작성한 유저인지 확인하는 메소드
    private static void authorizeArticleAuthor(Article article){
        String userName = SecurityContextHolder.getContext().getAuthentication().getName();
        if(!article.getAuthor().equals(userName)){
            throw  new IllegalArgumentException("권한 없음");
        }

    }












}




