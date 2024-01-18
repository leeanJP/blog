package com.it.blog.config.jwt;

import com.it.blog.domain.User;
import com.it.blog.repository.UserRepository;
import io.jsonwebtoken.Jwts;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Duration;
import java.util.Date;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class TokenProviderTest {
    @Autowired
    private TokenProvider tokenProvider;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private JwtProperties jwtProperties;

    @Test
    void generateToken() {
        //given
        //토큰에 유저정보를 추가하기 위한 테스트 유저 생성
        User testUser = userRepository.save(User.builder()
                .email("user@gmail.com")
                .password("test")
                .build());


        //when
        //토큰 제공자의 generateToken() 메소드를 호출해 토큰 생성
        String token = tokenProvider.generateToken(testUser, Duration.ofDays(14));
        //then
        //jjwt 라이브러리를 사용해 토큰을 복호화
        //클레임에 넣어둔 id 값이 given에서 만든 유저id와 동일한지 확인
        Long userId = Jwts.parser()
                .setSigningKey(jwtProperties.getSecretKey())
                .parseClaimsJws(token)
                .getBody()
                .get("id", Long.class);

        assertThat(userId).isEqualTo(testUser.getId());
    }

    //만료된 토큰으로 검증 실패 테스트
    @DisplayName("validToken() :만료된 토큰인 때에 유효성 검증 실패")
    @Test
    void validToken_invalidToken() {
       //givne
        String token = JwtFactory.builder()
                    .expiration(new Date(new Date().getTime() - Duration.ofDays(7).toMillis()))
                    .build().createToken(jwtProperties);
        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isFalse();

    }

    @Test
    void validToken() {
        //given
        String token = JwtFactory.withDefaultValues().createToken(jwtProperties);
        //when
        boolean result = tokenProvider.validToken(token);
        //then
        assertThat(result).isTrue();

    }

    @Test
    void getAuthentication() {
        //given
        //jjwt 라이브러리 사용해서 토큰 생성
        //토큰 제목은 user@email.com

        String userEmail = "user@email.com";
        String token = JwtFactory.builder().subject(userEmail).build().createToken(jwtProperties);
        //when
        //토큰 제공자의 getAuthentication() 메서드를 호출해서 인증 객체를 받음
        Authentication authentication = tokenProvider.getAuthentication(token);

        //then
        //반환받은 인증객체의 이름과 given에서 설정한 subject 값이 같은지 확인
        assertThat(((UserDetails) authentication.getPrincipal()).getUsername()).isEqualTo(userEmail);

    }

    @Test
    void getUserId() {
        //given
        //토큰 생성 클레임 추가 키는 id 값은 1(유저ID)
        Long userId = 1L;
        String token = JwtFactory.builder()
                .claims(Map.of("id", userId))
                .build().createToken(jwtProperties);

        //when
        //토큰 제공자 getUserId() 메소드를 호출해서 유저 ID 받음
        Long userIdByToken = tokenProvider.getUserId(token);

        //then
        //반환받은 유저 ID가 given에서 설정한 유저 ID 값 1과 같은지 확인
        assertThat(userIdByToken).isEqualTo(userId);

    }
}