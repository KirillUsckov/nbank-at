package ru.kduskov.generators;

import org.apache.commons.lang3.EnumUtils;
import org.apache.commons.lang3.RandomStringUtils;

import java.util.Random;

public final class RandomData {
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
