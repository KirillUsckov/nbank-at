package ru.kduskov.api.generators.common;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomData {
    public static String getValidName() {
        return String.format(
                "%s %s",
                RandomStringUtils.randomAlphabetic(1, 20).toLowerCase(),
                RandomStringUtils.randomAlphabetic(1, 20).toLowerCase()
        );
    }

    public static String getStringAndNumericString(int length) {
        var stringsNumber = length / 2;
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
}
