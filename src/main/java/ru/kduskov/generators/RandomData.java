package ru.kduskov.generators;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public final class RandomData {
    private static final Random RANDOM = new Random();

    public static String getUsername() {
        return RandomStringUtils.randomAlphabetic(10);
    }

    public static String getPassword() {
        return RandomStringUtils.randomAlphabetic(3).toLowerCase() +
                RandomStringUtils.randomAlphabetic(3).toUpperCase() +
                RandomStringUtils.randomNumeric(3) + "!";
    }

    public static String getValidName() {
        return String.format("%s %s", RandomStringUtils.randomAlphabetic(1, 20).toLowerCase(), RandomStringUtils.randomAlphabetic(1, 20).toLowerCase());
    }

    public static String getStringAndNumericString(int length) {
        var stringsNumber = length/2;
        var digitsNumber = length - stringsNumber;
        return String.format(
                "%s %s",
                getAlphabeticString(stringsNumber),
                getNumericString(digitsNumber)
        );
    }

    public static String getNumericString(int length) {
        return RandomStringUtils.randomNumeric(length);
    }

    public static String getAlphabeticString(int length) {
        return RandomStringUtils.randomAlphabetic(length);
    }

    public static <T extends Enum<?>> T randomEnum(Class<T> enumClass) {
        T[] values = enumClass.getEnumConstants();
        return values[RANDOM.nextInt(values.length)];
    }

    public static int getValidDepositAmount() {
        return new Random().nextInt(1,5000);
    }



    public static int getValidTransferAmount() {
        return new Random().nextInt(1,10000);
    }

    public static long getId() {
        return new Random().nextLong();
    }
}
