import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
import entities.Currency;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<String> list = List.of("Hello", "World", "!");
        System.out.println(list.indexOf("mama"));
    }
}
