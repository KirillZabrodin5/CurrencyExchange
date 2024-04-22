import dao.ReceivedRate;

public class RunnerApplication {
    public static void main(String[] args)  {
        ReceivedRate receivedRate = new ReceivedRate("RUB", "EUR");
        System.out.println(receivedRate.translateBuild());
    }
}
