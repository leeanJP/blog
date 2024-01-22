package com.it.blog.service;

import com.it.blog.domain.User;
import com.it.blog.dto.AddUserRequest;
import com.it.blog.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Long save(AddUserRequest dto){
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        return userRepository.save(User.builder()
                                        .email(dto.getEmail())
                                        .password(encoder.encode(dto.getPassword()))
                                        .build()
                                ).getId();
    }

    public User findById(Long userId){
        return userRepository.findById(userId)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected User"));
    }

    public User findByEmail(String email){
        return userRepository.findByEmail(email)
                .orElseThrow(()-> new IllegalArgumentException("Unexpected User email"));
    }

}
