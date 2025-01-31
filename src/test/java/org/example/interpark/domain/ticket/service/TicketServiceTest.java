package org.example.interpark.domain.ticket.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.concert.repository.ConcertRepositoryV1;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;


@ExtendWith(MockitoExtension.class)
class TicketServiceTest {

    @InjectMocks
    TicketService ticketService;

    @Mock
    ConcertRepository concertRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    TicketRepository ticketRepository;


    @Test
    void 티켓생성() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(30);
        CountDownLatch latch = new CountDownLatch(30);
        int concertId = 3;
        Concert concert = new Concert("콘서트",20);
        User user = new User("gege","1234","gege@naver.com");
        Ticket ticket = new Ticket(user, concert);

        when(concertRepository.findById(concertId)).thenReturn(Optional.of(concert));
        when(userRepository.findById(1)).thenReturn(Optional.of(user));
        when(ticketRepository.save(any(Ticket.class))).thenReturn(ticket);

        for (int i = 1; i <= 80; i++) {
            executor.execute(() -> {
                try {
                    ticketService.create(new TicketRequestDto(1, 3));
                } catch (Exception e) {
                    System.out.println(e);
                } finally {
                    latch.countDown();
                }
            });
        }

        latch.await();
        executor.shutdown();

        Integer availableTicket = concert.getAvailableAmount();
        assertEquals(0, availableTicket);
    }

}