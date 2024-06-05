package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.CurrencyExchange;
import service.TransferRoute;
import utils.ValidatorCode;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");
        double amountDouble = Double.parseDouble(amountStr);
        if (!ValidatorCode.isValid(baseCurrencyCode)
                || !ValidatorCode.isValid(targetCurrencyCode)
                || amountDouble <= 0) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "The required form field is present");
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        mapper.enable(SerializationFeature.INDENT_OUTPUT);
        try {
            CurrencyExchange helper = getCurrencyExchange(baseCurrencyCode, targetCurrencyCode, amountDouble);
            String answer = mapper.writeValueAsString(helper);
            resp.getWriter().write(answer);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            String message = e.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }

    private CurrencyExchange getCurrencyExchange(String baseCode, String targetCode, double amount) {
        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCode);
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCode);

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

        TransferRoute transferRoute = new TransferRoute(baseCode, targetCode);
        double rate = transferRoute.getRate();
        return new CurrencyExchange(baseCurrency, targetCurrency, rate, amount);
    }
}
