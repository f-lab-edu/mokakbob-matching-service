package com.mokakbob.member.controller;

import com.mokakbob.member.service.ProfileImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class ProfileImageController {

    private final ProfileImageService profileImageService;
}
