package org.example.interpark.domain.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.RedisTicketLockRepository;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @Mock
    TicketService ticketService;

    @InjectMocks
    LettuceTicketLockService lettuceTicketLockService;

    @Mock
    ConcertRepository concertRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TicketRepository ticketRepository;

    @Mock
    RedisTicketLockRepository redisTicketLockRepository;

    @Mock
    RedisTemplate<String,String> redisTemplate;


    @Test
    void 티켓생성() throws InterruptedException {
        ExecutorService executorService = new ThreadPoolExecutor(10, 100, 60L, TimeUnit.SECONDS,
            new LinkedBlockingQueue<>(10));

        CountDownLatch latch = new CountDownLatch(10);
        int concertId = 3;
        Concert concert = new Concert("콘서트", 1);
        User user = new User("gege", "1234", "gege@naver.com");
        Ticket ticket = new Ticket(user, concert);

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        for (int i = 1; i <= 10; i++) {
            executorService.execute(() -> {
                try {
//                    ticketService.create(new TicketRequestDto(1, 3));
                    lettuceTicketLockService.decrease(new TicketRequestDto(1, 3));
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