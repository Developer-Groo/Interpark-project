package org.example.interpark.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.user.Dto.UserRequestDto;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public void createUser(UserRequestDto requestDto) {
        User user = new User(
                requestDto.username(),
                requestDto.password(),
                requestDto.email()
        );
        userRepository.save(user);
    }

    public User getUser(int id) {
        return userRepository.findByIdOrElseThrow(id);
    }

}
