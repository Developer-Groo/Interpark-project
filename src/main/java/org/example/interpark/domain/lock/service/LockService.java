package org.example.interpark.domain.lock.service;

import lombok.RequiredArgsConstructor;
import org.example.interpark.domain.lock.repository.LockRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LockService {

    private final LockRepository lockRepository;

    final int MAX_TRY = 10;
    int CURRENT_TRY = 0;

    public void trylock(String key) {
        while(!(lockRepository.lock(key)) && CURRENT_TRY < MAX_TRY) {
            try {
                Thread.sleep(100);
                CURRENT_TRY++;
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        if (CURRENT_TRY >= MAX_TRY) {
            throw new RuntimeException("ticket sell fail");
        }
    }

    public void unlock(String key) {
        lockRepository.unlock(key);
    }


}
