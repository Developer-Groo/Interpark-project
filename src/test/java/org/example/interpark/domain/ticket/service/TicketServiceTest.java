package org.example.interpark.domain.ticket.service;

import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.lock.repository.RedisLockRepository;
import org.example.interpark.domain.lock.service.LockService;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
class TicketServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceTest.class);

    @Autowired
    ConcertRepository concertRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    TicketRepository ticketRepository;
    @Autowired
    private TicketService ticketService;
    @Autowired
    private RedisLockRepository lockRepository;
    @Autowired
    private LockService lockService;

    private Concert concert;
    private User user;

    @BeforeEach
    void setUp() {
        concert = new Concert("콘서트", 1);
        concertRepository.save(concert);
        user = new User("gege", "1234", "gege@naver.com");
        userRepository.save(user);
    }

    @AfterEach
    void tearDown() {
        ticketRepository.deleteAll();
        userRepository.deleteAll();
        concertRepository.deleteAll();
    }

    @Test
    void 티켓생성() throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10));

        CountDownLatch latch = new CountDownLatch(10);

        for (int i = 1; i <= 10; i++) {
            executorService.execute(() -> {
                try {
                    ticketService.createAsync(new TicketRequestDto(user.getId(), concert.getId())).join();
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executorService.shutdown();

        Concert updatedConcert = concertRepository.findById(concert.getId())
                .orElseThrow(() -> new RuntimeException("Concert not found"));
        assertEquals(0, updatedConcert.getAvailableAmount());
    }
}