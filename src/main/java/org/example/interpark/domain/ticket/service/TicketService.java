package org.example.interpark.domain.ticket.service;

import jakarta.persistence.OptimisticLockException;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.hibernate.StaleObjectStateException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }


    @Retryable(maxAttempts = 3, retryFor = {ObjectOptimisticLockingFailureException.class,
        OptimisticLockException.class,
        StaleObjectStateException.class}, backoff = @Backoff(delay = 2000))
    @Transactional
    public TicketResponseDto create(TicketRequestDto ticketRequestDto) throws Exception {
        if (!ticketRequestDto.isValid()) {
            throw new RuntimeException("Invalid ticket request");
        }

        Concert concert = concertRepository.findById(ticketRequestDto.concertId()).orElseThrow(
            () -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));

        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
        }

        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
            () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));

        concert.sellTicket();
        Ticket ticket = new Ticket(user, concert);

//            ticketRepository.save(ticket);

        //여기서 save가 문제임 Transactional 문제인건 알겠지만 어떻게 해결할 방법이 떠오르지 않아 남겨둠
        //이문제때문에 티켓이 하나만 팔리거나, 티켓이 저장되지 않는 문제 존재(ticketRepository.save를 지울 경우)

        return TicketResponseDto.from(ticket);
    }

    /*
    Retryable에 대한 내용 > 최신이랑 사용법 달라서 AI 망가짐
    https://github.com/spring-projects/spring-retry >> 공식 Readme 참고
    retry 안되는거같은데 어떻게하지...
     */


    /*
    concert.findbyId > 현재 version값을 1차캐시에 저장
    트랜잭션 종료, 커밋 시점에 DB의 version값과 1차 캐시에 저장된 값을 비교함
    버전 일치 > 버전 값 증가 / 버전 불일치 -> OptimisticLockException 발생 -> Retry
    Example)
    Thread 1 > 버전이 맞고 문제 없어서 version = 2 로 업데이트
    Thread 2 > 커밋 시점에서 version이 1이어야되는데 version이 2인 걸 확인 -> Retry
             -> 버전 값 새로 가져와서 캐시 저장
     */

}