import entities.Currency;

public class Main {
    public static void main(String[] args) {
        Currency currency = new Currency(1L, "USD", "Dollar", "$");
        System.out.println(currency.toString());
    }
}
