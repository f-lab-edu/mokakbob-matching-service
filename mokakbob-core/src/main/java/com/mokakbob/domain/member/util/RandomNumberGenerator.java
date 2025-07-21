package com.mokakbob.domain.member.util;

import java.security.SecureRandom;

public class RandomNumberGenerator {

    private static final int NUMBER_LENGTH_DEFINE_NUMBER = 10;

    private static final SecureRandom random = new SecureRandom();

    public static String generate(int length) {
        int max = (int) Math.pow(NUMBER_LENGTH_DEFINE_NUMBER, length);
        int min = max / NUMBER_LENGTH_DEFINE_NUMBER;

        return String.valueOf(min + random.nextInt(max - min));
    }
}
