package com.mokakbob.admin.controller;

import com.mokakbob.admin.controller.request.KafkaResendRequest;
import com.mokakbob.admin.service.AdminKafkaService;
import com.mokakbob.common.path.admin.AdminKafkaPath;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AdminKafkaController {

    private final AdminKafkaService adminKafkaService;

    @PostMapping(AdminKafkaPath.RESEND)
    public ResponseEntity<Void> resendTopic(@RequestBody KafkaResendRequest request) {
        adminKafkaService.resendParticipateTopic(
                request.key(),
                request.memberId(),
                request.lat(),
                request.lng(),
                request.category(),
                request.participantCount()
        );

        return ResponseEntity.ok()
                .build();
    }
}
