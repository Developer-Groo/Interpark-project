package org.example.interpark.domain.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.lock.service.LockService;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final LockService lockService;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }

    @Transactional
    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
        if (!ticketRequestDto.isValid()) {
            throw new RuntimeException("Invalid ticket request");
        }

        Concert concert = concertRepository.findById(ticketRequestDto.concertId()).orElseThrow(
            () -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));

        String key = "concertId:" + concert.getId();

        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
        }

        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
            () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));

        ticketRock(concert.getId(),key);
        try{
            concert.sellTicket();
            concertRepository.save(concert);
            Ticket ticket = new Ticket(user, concert);
            ticket = ticketRepository.save(ticket);
            return TicketResponseDto.from(ticket);
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            ticketUnlock(key);
        }

    }

    public void ticketRock(int concertId,String key) {

        Concert concert = concertRepository.findById(concertId).orElseThrow(
            () -> new RuntimeException("Cannot find concert id: " + concertId));

        if (concert.getAvailableAmount() <= 0) {
            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
        }
        lockService.trylock(key);
    }

    public void ticketUnlock(String key) {
        lockService.unlock(key);
    }
}