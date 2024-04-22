import dao.ReceivedRate;
import dto.JsonMapper;

public class RunnerApplication {
    public static void main(String[] args)  {
        ReceivedRate receivedRate = new ReceivedRate("RUB", "EUR");
        System.out.println(receivedRate.translateBuild());
        JsonMapper jsonMapper = new JsonMapper();
        jsonMapper.method();
    }
}
