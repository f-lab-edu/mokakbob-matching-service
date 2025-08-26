package com.mokakbob.matching.service;

import com.mokakbob.matching.service.request.NotificationRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class NotificationService {

    public void send(NotificationRequest request) {
        log.info("[알림] memberId={}, type={}, payload={}",
                request.memberId(), request.type(), request.payload());
    }
}
