package ru.kduskov.common.utils;

import lombok.Getter;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexLengthExtractor {
    private RegexLengthExtractor(){}

    // Извлечение минимальной и максимальной длины для всей строки
    public static LengthInfo extractTotalLengthConstraint(String regex) {
        LengthInfo info = new LengthInfo();

        // Ищем ограничение длины в конце строки: {6,100}$
        Pattern totalLength = Pattern.compile("\\{(\\d+),(\\d+)}");
        Matcher matcher = totalLength.matcher(regex);

        if (matcher.find()) {
            info.minLength += Integer.parseInt(matcher.group(1));
            info.maxLength += Integer.parseInt(matcher.group(2));
        }

        return info;
    }

    @Getter
    public static class LengthInfo {
        int minLength = 0;
        int maxLength = Integer.MAX_VALUE;
    }
}
