package dto;

public class CurrencyDto {
    private String code = null;
    private String name = null;
    private String sign = null;

    public CurrencyDto(String code) {
        this.code = code;
    }

    public CurrencyDto(String code, String name, String sign) {
        this.code = code;
        this.name = name;
        this.sign = sign;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
