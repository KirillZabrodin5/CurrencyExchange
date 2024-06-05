package utils;

public class ValidatorCode {
    public static boolean isValid(String code) {
        if (code == null) {
            return false;
        }
        String mask = "[A-Z]{3}";
        return code.matches(mask);
    }
}
