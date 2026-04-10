package com.mokakbob.common.init;

import com.mokakbob.domain.member.domain.Member;
import com.mokakbob.domain.member.domain.vo.MemberPreference;
import com.mokakbob.domain.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@Profile("prod")
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) {
        log.info("⏳ Initializing seed data...");

        createMember("a@naver.com", "tkdgnlrk1!", "A", MemberPreference.TALK);
        createMember("aa@naver.com", "tkdgnlrk1!", "B", MemberPreference.TALK);
        createMember("aaa@naver.com", "tkdgnlrk1!", "C", MemberPreference.TALK);
        createMember("aaaa@naver.com", "tkdgnlrk1!", "D", MemberPreference.TALK);

        log.info("✅ Seed data initialization completed!");
    }

    private void createMember(String email, String password, String nickname, MemberPreference preference) {
        // MemberRepository 인터페이스의 메서드명과 일치시킴 (existsByNickname)
        if (memberRepository.existsByEmail(email)) {
            log.info("⏭️ Member with email {} already exists, skipping.", email);
            return;
        }

        if (memberRepository.existsByNickname(nickname)) {
            log.info("⏭️ Member with nickname {} already exists, skipping.", nickname);
            return;
        }

        Member member = Member.builder()
                .email(email)
                .passwordEnc(passwordEncoder.encode(password))
                .nickname(nickname)
                .preference(preference)
                .profileImage("https://mokakbob-bucket.s3.amazonaws.com/default/profile.png")
                .build();

        memberRepository.save(member);
        log.info("👤 Created seed member: {} ({})", nickname, email);
    }
}
