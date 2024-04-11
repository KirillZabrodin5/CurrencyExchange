import TipoRestApi.GetCurrency;

public class ClassForRunApplication {
    public static void main(String[] args) {
        System.out.println(GetCurrency.GETID(2));
        System.out.println(GetCurrency.GETCODE("USD"));
        System.out.println(GetCurrency.GETFULLNAME("Russian Ruble"));
        System.out.println(GetCurrency.GETSIGN("₸"));
    }
}
