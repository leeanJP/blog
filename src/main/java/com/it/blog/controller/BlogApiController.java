package com.it.blog.controller;

import com.it.blog.domain.Article;
import com.it.blog.dto.AddArticleRequest;
import com.it.blog.dto.ArticleResponse;
import com.it.blog.dto.UpdateArticleRequest;
import com.it.blog.service.BlogService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.swing.text.html.parser.Entity;
import java.util.List;

@RequiredArgsConstructor
@RestController //HTTP Respone body 객체 데이터를 JSON 형식으로 반환하는 컨트롤러
public class BlogApiController {
    private final BlogService blogService;

    @GetMapping("/api/articles")
    public ResponseEntity<List<ArticleResponse>> findAllArticles(){
        List<ArticleResponse> articles = blogService.findAll()
                .stream()
                .map(ArticleResponse::new)
                .toList();

        return ResponseEntity.ok().body(articles);

    }

    // HTTP 메소드가 POST 방식일 때 전달받은 URL과 동일하면 메서드로 매핑
    @PostMapping("/api/articles")
    public ResponseEntity<Article> addArticle(@RequestBody AddArticleRequest request){
        Article savedArticle = blogService.save(request);

        //요청한 자원이 성공적으로 생성되고 저장된 블로그 글 정보를 응답 객체에 받아서 전송
        return ResponseEntity.status(HttpStatus.CREATED).body(savedArticle);

        /*
        *  알아두면 좋은 응답 코드
        *  200 OK : 요청이 성공적으로 수행됨
        *  201 Created : 요청이 성공적으로 수행되고, 새로운 리소스 생성
        *  400 Bad Req : 요청 값이 잘못되어서 요청 실패
        *  403 Forbidden : 권한이 없어서 요청 실패
        *  404 Not Found : 요청 값으로 찾은 리소스가 없어서 요청 실패
        *  500 Internal Server Error : 서버 상의 문제로 인해 요청 실패
        * */


        /* Spring Boot 설정  파일
        *  application.properties
        *  application.yml
        *
        *  properties 파일은 key = value 형태
        *  spring.datasource.url = 데이터베이스 url
        *  spring.datasource.username = DB 유저 ID
        *
        *  yml 파일은 들여쓰기로 구분함
        *
        * spring:
        *   datasource:
        *       url: 데이터베이스 url
        *       username : DB 유저 ID
        * */
    }

    @GetMapping("/api/articles/{id}")
    //URL 경로에서 값 추출
    public ResponseEntity<ArticleResponse> findArticle(@PathVariable long id){
        Article article = blogService.findById(id);

        return ResponseEntity.ok().body(new ArticleResponse(article));
    }

    @DeleteMapping("/api/articles/{id}")
    public ResponseEntity<Void> deleteArticle(@PathVariable long id){
        blogService.delete(id);

        return ResponseEntity.ok().build();
    }


    @PutMapping("/api/articles/{id}")
    public ResponseEntity<Article> updateArticle(@PathVariable long id,
                                                 @RequestBody UpdateArticleRequest request){

        Article updateArticle = blogService.update(id,request);

        return ResponseEntity.ok().body(updateArticle);
    }


}
