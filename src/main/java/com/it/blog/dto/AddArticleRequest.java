package com.it.blog.dto;

import com.it.blog.domain.Article;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AddArticleRequest {
    //서비스 계층에서 요청을 받을 객체

    private String title;
    private String content;


    public Article toEntity(){
        return Article.builder()
                .title(title)
                .content(content)
                .build();
    }
}
