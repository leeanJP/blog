package com.it.blog.service;

import com.it.blog.config.jwt.TokenProvider;
import com.it.blog.domain.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class TokenService {

    private final TokenProvider tokenProvider;
    private final RefreshTokenService refreshTokenService;
    private final UserService userService;

    //createNewAccessToken
    //전달받은 리프레시 토큰으로 토큰 유효성 검사하고
    //유효한 토큰일 때 리프레시 토큰으로 사용자 ID 찾음
    //그 이후에 generateToken 메소드를 이용해 새로운 액세스 토큰 생성

    public String createNewAccessToken(String refreshToken){
        //토큰 유효성 검사 실패하면 예외 발생
        if(!tokenProvider.validToken(refreshToken)){
            throw new IllegalArgumentException("Token Service : Unexpected Token");
        }
        Long userId = refreshTokenService.findByRefreshToken(refreshToken).getUserId();
        User user = userService.findById(userId);

        return tokenProvider.generateToken(user, Duration.ofHours(2));

    }

}
