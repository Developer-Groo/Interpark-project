package org.example.interpark.domain.ticket.service;

import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.repository.RedisTicketLockRepository;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class LettuceTicketLockService {

    private final RedisTicketLockRepository redisTicketLockRepository;
    private final TicketService ticketService;

    public TicketResponseDto decrease(TicketRequestDto requestDto) {

        /*
       락 획득 시도, Thread.sleep(100)은 0.1초에 한번 락 획득 시도하도록 설정한 것
        시도 제한 -> 10번? 0.1초에 한번씩이면 1초정도
         */
        String key = "concertId:" + requestDto.concertId();
        final int maxTry = 10;
        int currentTry = 0;
        while (!(redisTicketLockRepository.lock(key)) && currentTry < maxTry) {
            try {
                Thread.sleep(1000);
                log.info(Thread.currentThread().getId()+"  :   " +currentTry);

                currentTry++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if(currentTry >= maxTry) {
            throw new RuntimeException("ticket sell fail");
        }

        try {
            TicketResponseDto ticketResponseDto = ticketService.create(requestDto);
            return ticketResponseDto;
        } finally {
            redisTicketLockRepository.unlock(key);
        }
    }

}
