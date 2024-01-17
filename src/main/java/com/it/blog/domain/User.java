package com.it.blog.domain;

import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Table(name = "users")
@NoArgsConstructor
@Getter
@Entity
@EntityListeners(AuditingEntityListener.class)
public class User implements UserDetails {
    //UserDetails 상속받아 인증 객체로 사용

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", updatable = false)
    private Long id;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password")
    private String password;

    @Column(name = "created_at")
    @CreatedDate  //엔티티가 생성될 때 생성 시간 저장
    private LocalDateTime createdAt;


    @Column(name = "updated_at")
    @LastModifiedDate  //엔티티가 생성될 때 생성 시간 저장
    private LocalDateTime updatedAt;
    @Builder
    public User(String email , String password, String auth){
        this.email = email;
        this.password = password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("user"));
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        //만료되었는지 확인하는 로직
        return true; //true > 만료안됨
    }

    @Override
    public boolean isAccountNonLocked() {
        //계정이 잠금되었는지 확인하는 로직
        return true;  //true > 잠금안됨
    }

    @Override
    public boolean isCredentialsNonExpired() {
        //패스워드 만료여부
        return true; // true > 만료안됨
    }

    @Override
    public boolean isEnabled() {
        //계정이 사용 가능한지 확인하는 로직
        return true; // true > 사용가능
    }
}
