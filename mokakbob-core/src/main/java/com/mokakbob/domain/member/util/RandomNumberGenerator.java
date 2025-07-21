package com.mokakbob.domain.member.util;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final int BASE_DIGIT = 10;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        int max = (int) Math.pow(BASE_DIGIT, length);
        int min = max / BASE_DIGIT;

        return String.valueOf(min + random.nextInt(max - min));
    }
}
