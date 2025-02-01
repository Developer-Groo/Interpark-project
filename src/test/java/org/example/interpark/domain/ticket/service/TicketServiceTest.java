package org.example.interpark.domain.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.repository.RedisTicketLockRepository;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;


@SpringBootTest
class TicketServiceTest {

    @Autowired
    TicketService ticketService;

    @Autowired
    LettuceTicketLockService lettuceTicketLockService;

    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    @Autowired
    RedisTicketLockRepository redisTicketLockRepository;

    @Autowired
    RedisTemplate<String, String> redisTemplate;

    Concert concert;

    User user;

    @BeforeEach
    void setup() {
        concert = new Concert("콘서트", 1);
        concertRepository.save(concert);
        user = new User("gege", "1234", "gege@naver.com");
        userRepository.save(user);
    }

    @AfterEach
    void end() {
        ticketRepository.deleteAll();
        concertRepository.deleteAll();
        userRepository.deleteAll();
    }


    @Test
    void 티켓생성() throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10));

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 1; i <= 10; i++) {
            executorService.execute(() -> {
                try {
//                    ticketService.create(new TicketRequestDto(1, 3));
                    lettuceTicketLockService.decrease(
                        new TicketRequestDto(user.getId(), concert.getId()));
                    concert = concertRepository.findById(concert.getId())
                        .orElseThrow(() -> new RuntimeException("일단 봅시다."));
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Integer availableTicket = concert.getAvailableAmount();
        assertEquals(0, availableTicket);
    }
}