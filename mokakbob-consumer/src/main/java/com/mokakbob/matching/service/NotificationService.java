package com.mokakbob.matching.service;

import com.mokakbob.domain.matching.event.MatchingFoundEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void send(MatchingFoundEvent event) {
        // todo
    }
}
