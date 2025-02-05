package org.example.interpark;

import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.user.Dto.UserRequestDto;
import org.example.interpark.domain.user.service.UserService;
import org.springframework.stereotype.Component;
import org.example.interpark.domain.user.entity.User;

import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class InitData {
    private final UserService userService;
    private final ConcertRepository concertRepository;

    @PostConstruct
    @Transactional
    public void init() {
        List<UserRequestDto> userList =
                List.of(new UserRequestDto("aaa","1234","aaa@naver.com"),
                        new UserRequestDto("bbb","1234","bbb@naver.com"),
                        new UserRequestDto("ccc","1234","ccc@naver.com"),
                        new UserRequestDto("ddd","1234","ddd@naver.com"),
                        new UserRequestDto("eee","1234","eee@naver.com")
                );

        for(UserRequestDto user : userList) {
            userService.createUser(user);
        }

        Concert concert = new Concert("ddd", 100);
        concertRepository.save(concert);

//        List<Concert> concertList =
//                List.of(new Concert("첫번째 콘서트",100),
//                        new Concert("두번째 콘서트",10),
//                        new Concert("세번째 콘서트",50)
//                        );
//        concertRepository.saveAll(concertList);

    }
}
