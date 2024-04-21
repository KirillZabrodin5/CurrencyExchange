import dao.DirectTranslation;
import dao.RequestToDbUtil;

public class RunnerApplication {
    public static void main(String[] args)  {
        DirectTranslation directTranslation = new DirectTranslation("RUB", "USD");
        System.out.println(directTranslation.translate());
        RequestToDbUtil.answerFromDB("USD");
    }
}
