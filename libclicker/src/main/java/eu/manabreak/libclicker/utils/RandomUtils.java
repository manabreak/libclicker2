package eu.manabreak.libclicker.utils;

import java.util.Random;

public class RandomUtils {

    private static final Random random = new Random();

    /**
     * Generates a new random integer
     *
     * @return random integer
     */
    public static int getRandomInt() {
        return random.nextInt();
    }

    /**
     * Generates a new random integer between 0 (inclusive)
     * and the given maximum value (exclusive)
     *
     * @param max value (exclusive)
     * @return random integer
     */
    public static int getRandomInt(int max) {
        return random.nextInt(max);
    }

    /**
     * Generates a new random integer between the minimum
     * value (inclusive) and maximum value (exclusive)
     *
     * @param min minimum value (inclusive)
     * @param max maximum value (exclusive)
     * @return random integer
     */
    public static int getRandomInt(int min, int max) {
        return random.nextInt(max - min) + min;
    }

    /**
     * Generates a new random double between 0.0 and 1.0,
     * both inclusive.
     *
     * @return random double between 0.0 and 1.0
     */
    public static double nextDouble() {
        return random.nextDouble();
    }
}
