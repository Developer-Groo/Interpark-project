package org.example.interpark.domain.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
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


@SpringBootTest
class TicketServiceTest {

    private static final Logger log = LoggerFactory.getLogger(TicketServiceTest.class);

    @Autowired
    TicketService ticketService;


    @Autowired
    ConcertRepository concertRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TicketRepository ticketRepository;

    Concert concert;

    User user;

    @BeforeEach
    void setup() {
        concert = new Concert("콘서트", 1000);
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
        ExecutorService executorService = new ThreadPoolExecutor(20, 20, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(20000));

        CountDownLatch latch = new CountDownLatch(1500);

        for (int i = 1; i <=1500; i++) {
            executorService.execute(() -> {
                try {
                    ticketService.create(new TicketRequestDto(user.getId(), concert.getId()));
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
        List<Ticket> tickets = ticketRepository.findAll();
        Ticket firstTicket = ticketRepository.findById(1).orElseThrow();
        Ticket endTicket = ticketRepository.findById(1000).orElseThrow();
        Duration duration = Duration.between(firstTicket.getCreatedAt(),endTicket.getCreatedAt());
        log.info("---------------------------------------------");
        log.info("총 티켓 수 : " + updatedConcert.getTotalAmount());
        log.info("남은 티켓 수 : " + updatedConcert.getAvailableAmount());
        log.info("총 발급된 티켓 수 : " + tickets.size());
        log.info("티켓 소진까지 걸린 시간: {}초", String.format("%.2f", duration.toMillis()/1000.0));
        log.info("---------------------------------------------");

        assertEquals(1000, tickets.size());
        assertEquals(0, updatedConcert.getAvailableAmount());
    }
}