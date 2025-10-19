package ru.kduskov.generators.common;

import com.github.curiousoddman.rgxgen.RgxGen;
import net.datafaker.Faker;
import ru.kduskov.annotations.GeneratingRule;
import ru.kduskov.enums.GenerationsRules;
import ru.kduskov.models.body.request.BaseRequest;

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.time.LocalDateTime;
import java.util.*;

public final class RequestDataGenerator {
    private static final Faker faker = new Faker();
    private static final Random random = new Random();
    private static final DecimalFormat df = new DecimalFormat("#.##");

    public static <T extends BaseRequest> T generateFilledObject(Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();
            fillFields(instance);
            return instance;
        } catch (Exception e) {
            throw new RuntimeException("Failed to create instance of " + clazz.getName(), e);
        }
    }

    private static Object generateFromRegex(String regex) {
        return RgxGen.parse(regex).generate();
    }

    private static Object generateFromValueKey(GenerationsRules rule, int minLength, int maxLength) {
        return switch (rule) {
            case DEPOSIT_BALANCE -> Double.parseDouble(df.format(new Random().nextDouble(0.01, 5_001)));
            case TRANSFER_AMOUNT -> Double.parseDouble(df.format(new Random().nextDouble(0.01, 10_001)));
            case PASSWORD -> generateSecurePassword(minLength, maxLength);
            default -> null;
        };
    }

    private static String generateSecurePassword(int minLength, int maxLength) {
        var upper = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        var lower = "abcdefghijklmnopqrstuvwxyz";
        var digits = "0123456789";
        var special = "@$!%-+?&";

        var random = new Random();
        var password = new StringBuilder();

        password.append(lower.charAt(random.nextInt(lower.length())));
        password.append(upper.charAt(random.nextInt(upper.length())));
        password.append(digits.charAt(random.nextInt(digits.length())));
        password.append(special.charAt(random.nextInt(special.length())));

        var allChars = upper + lower + digits + special;
        minLength = minLength < 0 ? 0 : maxLength;
        maxLength = maxLength < 0 ? Integer.MAX_VALUE : maxLength;
        var length = minLength + random.nextInt(maxLength - 1);
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Перемешиваем символы
        var chars = password.toString().toCharArray();
        for (var i = chars.length - 1; i > 0; i--) {
            var j = random.nextInt(i + 1);
            var temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }

    private static Object generateByType(Class<?> type) {
        if (type == String.class) {
            return faker.lorem().word();
        } else if (type == int.class || type == Integer.class) {
            return faker.number().numberBetween(1, 99999999);
        } else if (type == long.class || type == Long.class) {
            return faker.number().randomNumber();
        } else if (type == double.class || type == Double.class) {
            return faker.number().randomDouble(2, 1, 999999999);
        } else if (type == boolean.class || type == Boolean.class) {
            return faker.bool().bool();
        } else if (type == LocalDateTime.class) {
            return LocalDateTime.now();
        } else if (type.isEnum()) {
            return generateEnumValue(type);
        } else if (type == List.class) {
            return new ArrayList<>();
        } else if (type == Map.class) {
            return new HashMap<>();
        }

        // Для кастомных классов - рекурсивное заполнение
        if (type.getPackage() != null &&
                type.getPackage().getName().startsWith("ru.kduskov")) {
            try {
                Object nestedInstance = type.getDeclaredConstructor().newInstance();
                if (nestedInstance instanceof BaseRequest) {
                    fillFields(nestedInstance);
                }
                return nestedInstance;
            } catch (Exception e) {
                return null;
            }
        }

        return null;
    }

    // Остальные методы остаются без изменений
    private static Object generateEnumValue(Class<?> enumClass) {
        var enumConstants = enumClass.getEnumConstants();
        return enumConstants.length > 0 ?
                enumConstants[random.nextInt(enumConstants.length)] : null;
    }

    private static void fillFields(Object obj) throws IllegalAccessException {
        var clazz = obj.getClass();
        while (clazz != null && clazz != Object.class) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                if (!java.lang.reflect.Modifier.isStatic(field.getModifiers()) &&
                        field.get(obj) == null) {
                    Object value = generateValueForField(field);
                    field.set(obj, value);
                }
            }
            clazz = clazz.getSuperclass();
            if (clazz == BaseRequest.class) break;
        }
    }

    private static Object generateValueForField(Field field) {
        GeneratingRule rule = field.getAnnotation(GeneratingRule.class);
        if (rule != null) {
            if (!rule.regex().isEmpty())
                return generateFromRegex(rule.regex());
            else if (rule.valueKey() != GenerationsRules.DEFAULT)
                return generateFromValueKey(rule.valueKey(), rule.minLength(), rule.maxLength());
        }

        return generateByType(field.getType());
    }
}