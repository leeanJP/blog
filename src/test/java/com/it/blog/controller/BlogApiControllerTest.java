package com.it.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.blog.domain.Article;
import com.it.blog.dto.AddArticleRequest;
import com.it.blog.dto.UpdateArticleRequest;
import com.it.blog.repository.BlogRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MockMvcBuilder;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc // MockMVC 생성 및 자동 구성  HTTP GET POST 등에 대한 API테스트도 가능함
class BlogApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper; // 직렬화 , 역직렬화를 위한 클래스
    @Autowired
    private WebApplicationContext context;

    @Autowired
    BlogRepository blogRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        blogRepository.deleteAll();
    }

    @Test
    void addArticle() throws Exception {
        //given
        final String url = "/api/articles";
        final String title = "테스트";
        final String content = "본문 테스트";
        final AddArticleRequest userReq = new AddArticleRequest(title, content);

        //객체 JSON 직렬화
        final String reqBody = objectMapper.writeValueAsString(userReq);
        //직렬화 역직렬화란???
        /*
         *   HTTP에서는 JSON을 사용하고, JAVA에서는 객체를 사용한다.
         *   서로 형식이 다르기때문에 형식에 맞게 변환 해주는 작업이 필요함
         *
         *   직렬화는 자바 객체를 외부에서 사용하도록 데이터를 변환하는 작업
         *           자바 객체 > JSON
         * * 역직렬화 JSON > 자바객체로 변환하는 작업
         * */


        //when

        //설정한 내용으로 요청 전송
        ResultActions result = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(reqBody));

        //then
        result.andExpect(status().isCreated());

        List<Article> articleList = blogRepository.findAll();

        assertThat(articleList.size()).isEqualTo(1); //크기가 1인지 검증
        assertThat(articleList.get(0).getTitle()).isEqualTo(title);
        assertThat(articleList.get(0).getContent()).isEqualTo(content);

    }



    //전체 조회
    @Test
    public void findAllArticles() throws Exception{
        //given
        final String url = "/api/articles";
        final String title = "제목";
        final String content = "본문";

        blogRepository.save(Article.builder()
                        .title(title)
                        .content(content)
                        .build());
        //when
        final  ResultActions resultActions = mockMvc.perform(get(url)
                .accept(MediaType.APPLICATION_JSON));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].content").value(content))
                .andExpect(jsonPath("$[0].title").value(title));

    }


    //단건 조회
    @Test
    public void findArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        final String title = "findArticle Test";
        final String content = "Test1";

        Article savedArticle = blogRepository.save(Article.builder()
                                            .title(title)
                                            .content(content)
                                            .build());

        //when
        final ResultActions resultActions = mockMvc.perform(get(url, savedArticle.getId()));
        //then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.content").value(content))
                .andExpect(jsonPath("$.title").value(title));
    }

    @Test
    public void deleteArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        final String title = "findArticle Test";
        final String content = "Test1";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());
        //when
        mockMvc.perform(delete(url, savedArticle.getId()))
                .andExpect(status().isOk());

        //then
        List<Article> articles = blogRepository.findAll();
        assertThat(articles).isEmpty();
    }


    @Test
    public void updateArticle() throws Exception{
        //given
        final String url = "/api/articles/{id}";
        final String title = "findArticle Test";
        final String content = "Test1";

        Article savedArticle = blogRepository.save(Article.builder()
                .title(title)
                .content(content)
                .build());

        final String newTitle = "new title";
        final String newContent = "new content";
        UpdateArticleRequest request = new UpdateArticleRequest(newTitle,newContent);

        //when
        ResultActions resultActions = mockMvc.perform(put(url, savedArticle.getId())
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(objectMapper.writeValueAsString(request)));
        //then
        resultActions.andExpect(status().isOk());
        Article article = blogRepository.findById(savedArticle.getId()).get();

        assertThat(article.getTitle()).isEqualTo(newTitle);
        assertThat(article.getContent()).isEqualTo(newContent);


    }


}