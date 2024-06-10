package entity;

import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Currency {
    private Long id = 0L;
    private String code;
    private String fullName;
    private String sign;

    public Currency(String code) {
        this.code = code;
    }
}
