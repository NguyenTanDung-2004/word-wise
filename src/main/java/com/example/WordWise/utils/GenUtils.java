package com.example.WordWise.utils;

import java.util.Random;

public class GenUtils {
    public static String generateCodeResetPassword() {
        StringBuilder code = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 6; i++) {
            int digit = random.nextInt(10);
            code.append(digit);
        }
        return code.toString();
    }
}
