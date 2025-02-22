package ru.sber.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class RandomValues {

    private static Random random = new Random();

    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    public static BigDecimal getBigDecimalAmound(float bound) {
        return BigDecimal.valueOf(ThreadLocalRandom.current().nextFloat() * bound)
                .setScale(2, RoundingMode.HALF_UP);
    }
}
