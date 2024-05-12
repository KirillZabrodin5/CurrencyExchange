package utils;

public class ValidatorCode {
    public static boolean isValid(String code) {
        String mask = "[A-Z]{3}";
        return code.matches(mask);
    }
}
