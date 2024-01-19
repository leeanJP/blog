package com.it.blog.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.it.blog.config.jwt.JwtFactory;
import com.it.blog.config.jwt.JwtProperties;
import com.it.blog.domain.RefreshToken;
import com.it.blog.domain.User;
import com.it.blog.dto.CreateAccessTokenRequest;
import com.it.blog.repository.RefreshTokenRepository;
import com.it.blog.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class TokenApiControllerTest {

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper; // 직렬화 , 역직렬화를 위한 클래스
    @Autowired
    private WebApplicationContext context;
    @Autowired
    JwtProperties jwtProperties;
    @Autowired
    UserRepository userRepository;
    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @BeforeEach
    public void mockMvcSetUp(){
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context).build();
        userRepository.deleteAll();
    }

    @DisplayName("새로운 액세스 토큰 발급")
    @Test
    public void createNewAccessToken() throws Exception{
        //given
        /* 테스트 유저 생성하고 jjwt 라이브러리를 활용해
            리프레시 토큰을 만들어서 DB에 저장
            토큰 생성 API의 요청 본문에 리프레시 토큰을 포함해
            요청 객체 생성
        * */
        final String url = "/api/token";

        User testUser = userRepository.save(
                User.builder()
                        .email("test@email.com")
                        .password("test")
                        .build());

        String refreshToken = JwtFactory.builder()
                .claims(Map.of("id", testUser.getId()))
                .build()
                .createToken(jwtProperties);

        refreshTokenRepository.save(new RefreshToken(testUser.getId(), refreshToken));

        CreateAccessTokenRequest request = new CreateAccessTokenRequest();
        request.setRefreshToken(refreshToken);

        final String requestBody = objectMapper.writeValueAsString(request);

        //when
        /*  토큰 추가 API 요청 보내기
            요청 타입은 JSON
        * */
        ResultActions resultActions = mockMvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(requestBody));
        //then
        /*  응답코드가 201 created 인지 확인
            액세스토큰이 비어있지 않은지 확인
        * */
        resultActions
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.accessToken").isNotEmpty());

    }


}
