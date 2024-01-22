package com.it.blog.config;

import com.it.blog.config.jwt.TokenProvider;
import com.it.blog.config.oauth.OAuth2AuthorizationRequestBasedOnCookieRepository;
import com.it.blog.config.oauth.OAuth2SuccessHandler;
import com.it.blog.config.oauth.OAuth2UserCustomService;
import com.it.blog.repository.RefreshTokenRepository;
import com.it.blog.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import static org.springframework.boot.autoconfigure.security.servlet.PathRequest.toH2Console;

@Configuration
@RequiredArgsConstructor
public class WebOAuthSecurityConfig {

    private final OAuth2UserCustomService oAuth2UserCustomService;
    private final TokenProvider tokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final UserService userService;

    @Bean
    public WebSecurityCustomizer configure(){
        return web -> web.ignoring()
                .requestMatchers(toH2Console())
                .requestMatchers("/img/**", "/css/**", "/js/**");
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
        //토큰 방식으로 인증하기 때문에 기존에 사용하던 폼로그인, 세션 비활성화
        http.csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        http.sessionManagement((session)->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));

        //헤더를 확인할 커스텀 필터 추가
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

        // 토큰 재발급 url은 인증없이 접근 가능하도록 설정
        // 나머지 API URL은 인증 필요
        http.authorizeRequests(auth -> auth
                .requestMatchers(new AntPathRequestMatcher("/api/token")).permitAll()
                .requestMatchers(new AntPathRequestMatcher("/api/**")).authenticated()
                .anyRequest().permitAll());

        http.oauth2Login(oauth2 -> oauth2
                .loginPage("/login")
                .authorizationEndpoint(authorization -> authorization
                        .authorizationRequestRepository(oAuth2AuthorizationRequestBasedOnCookieRepository()))
                        //Authrization 요청과 관련된 상태 저장
                .successHandler(oAuth2SuccessHandler())
                .userInfoEndpoint(userInfo -> userInfo.userService(oAuth2UserCustomService))
            ).logout(logout-> logout.logoutSuccessUrl("/login")
            ).exceptionHandling(exception-> exception
                    .defaultAuthenticationEntryPointFor(
                        new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                        new AntPathRequestMatcher("/api/**")
                    ));

        return http.build();











    }


    @Bean
    public OAuth2SuccessHandler oAuth2SuccessHandler() {
        return new OAuth2SuccessHandler(tokenProvider, refreshTokenRepository,
                oAuth2AuthorizationRequestBasedOnCookieRepository(),userService);
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    TokenAuthenticationFilter tokenAuthenticationFilter(){
        return new TokenAuthenticationFilter(tokenProvider);
    }

    @Bean
    public OAuth2AuthorizationRequestBasedOnCookieRepository oAuth2AuthorizationRequestBasedOnCookieRepository() {
        return new OAuth2AuthorizationRequestBasedOnCookieRepository();
    }


}
