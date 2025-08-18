package com.mokakbob.matching.controller;

import com.mokakbob.common.path.matching.MatchingPath;
import com.mokakbob.global.resolver.annotation.MemberId;
import com.mokakbob.matching.controller.request.MatchingStartRequest;
import com.mokakbob.matching.service.MatchingStartService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class MatchingController {

    private final MatchingStartService startService;

    @PostMapping(MatchingPath.PARTICIPATE)
    public ResponseEntity<Void> startMatching(
            @RequestBody MatchingStartRequest request,
            @MemberId Long memberId
    ) {
        startService.participateMatchingWithLock(
                request.lat(),
                request.lng(),
                request.category(),
                request.participantCount(),
                memberId
        );

        return ResponseEntity.ok()
                .build();
    }
}
