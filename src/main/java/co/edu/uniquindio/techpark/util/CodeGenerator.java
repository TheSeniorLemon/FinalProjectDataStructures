package co.edu.uniquindio.techpark.util;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

public class CodeGenerator {
    public CodeGenerator() {}

    private static final String HEXADECIMAL = "0123456789ABCDEF";
    private static final String DECIMAL = "0123456789";
    private static final Random random = new Random();
    private static final Set<String> usedCodes = new HashSet<>();

    public void validatePositiveLength(int length) throws IllegalArgumentException {
        if (length <= 0) {
            throw new IllegalArgumentException("Length must be greater than 0");
        }
    }

    public long convertBuilderToLong(StringBuilder code, int base) throws RuntimeException {
        try {
            return Long.parseLong(code.toString(), base);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Error converting generated code to number", e);
        }
    }

    public long generateHexadecimalCode(int length) {
        validatePositiveLength(length);
        StringBuilder code = generateUniqueCode(length, HEXADECIMAL);
        return convertBuilderToLong(code, 16);
    }

    public long generateDecimalCode(int length) {
        validatePositiveLength(length);
        StringBuilder code = generateUniqueCode(length, DECIMAL);
        return convertBuilderToLong(code, 10);
    }

    public String generateHexadecimalCodeStr(int length) {
        validatePositiveLength(length);
        return generateUniqueCode(length, HEXADECIMAL).toString();
    }

    public String generateDecimalCodeStr(int length) {
        validatePositiveLength(length);
        return generateUniqueCode(length, DECIMAL).toString();
    }

    private StringBuilder generateCode(int length, String characters) {
        StringBuilder code = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(characters.length());
            code.append(characters.charAt(index));
        }
        return code;
    }

    private StringBuilder generateUniqueCode(int length, String characters) {
        StringBuilder code;
        int attempts = 0;
        do {
            if (++attempts > 1000) {
                throw new RuntimeException("Could not generate a unique code after many attempts");
            }
            code = generateCode(length, characters);
        } while (!usedCodes.add(code.toString()));
        return code;
    }
}