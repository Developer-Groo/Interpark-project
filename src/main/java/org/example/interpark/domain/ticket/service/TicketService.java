package org.example.interpark.domain.ticket.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.concert.entity.Concert;
import org.example.interpark.domain.concert.repository.ConcertRepository;
import org.example.interpark.domain.ticket.dto.TicketRequestDto;
import org.example.interpark.domain.ticket.dto.TicketResponseDto;
import org.example.interpark.domain.ticket.entity.Ticket;
import org.example.interpark.domain.ticket.repository.TicketRepository;
import org.example.interpark.domain.user.entity.User;
import org.example.interpark.domain.user.repository.UserRepository;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TicketService {

    private final TicketRepository ticketRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final StringRedisTemplate redisTemplate;

    public TicketResponseDto find(int id) {
        Ticket ticket = ticketRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Cannot find ticket id: " + id));
        return TicketResponseDto.from(ticket);
    }

//    @Transactional
//    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
//        if (!ticketRequestDto.isValid()) {
//            throw new RuntimeException("Invalid ticket request");
//        }
//
//        Concert concert = concertRepository.findById(ticketRequestDto.concertId()).orElseThrow(
//            () -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));
//
//        if (concert.getAvailableAmount() <= 0) {
//            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
//        }
//
//        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
//            () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));
//        concert.sellTicket();
//        concertRepository.save(concert);
//        Ticket ticket = new Ticket(user, concert);
//        ticket = ticketRepository.save(ticket);
//
//        return TicketResponseDto.from(ticket);
//    }

//    @Transactional
//    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
//
//        if (!ticketRequestDto.isValid()) {
//            throw new RuntimeException("Invalid ticket request");
//        }
//
//        Concert concert = concertRepository.findById(ticketRequestDto.concertId()).orElseThrow(
//            () -> new RuntimeException("Cannot find concert id: " + ticketRequestDto.concertId()));
//
//        User user = userRepository.findById(ticketRequestDto.userId()).orElseThrow(
//            () -> new RuntimeException("Cannot find user id: " + ticketRequestDto.userId()));
//
//        String key = "concertId:" + ticketRequestDto.concertId();
//        int availableTickets =
//            Boolean.TRUE.equals(redisTemplate.hasKey(key)) ? Integer.parseInt(
//                redisTemplate.opsForValue().get(key))
//                : concert.getAvailableAmount();
//
//        if (availableTickets <= 0) {
//            throw new RuntimeException("Cannot sell ticket. Available amount is less than 0.");
//        }
//
//        redisTemplate.opsForValue().setIfAbsent(key, String.valueOf(availableTickets-1));
//
//        concert.sellTicket();
//        concertRepository.save(concert);
//        Ticket ticket = new Ticket(user, concert);
//        ticket = ticketRepository.save(ticket);
//
//        return TicketResponseDto.from(ticket);
//    }


    @Transactional
    public TicketResponseDto create(TicketRequestDto ticketRequestDto) {
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
        concertRepository.save(concert);
        Ticket ticket = new Ticket(user, concert);
        ticket = ticketRepository.save(ticket);

        System.out.println("남은 개수 : "+ concert.getAvailableAmount());

        return TicketResponseDto.from(ticket);
    }

}