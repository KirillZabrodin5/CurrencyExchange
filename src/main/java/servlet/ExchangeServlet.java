package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
import dto.CurrencyExchangeDto;
import entities.Currency;
import entities.CurrencyExchange;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.TransferRoute;
import utils.ValidationUtil;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");
        double amountDouble = Double.parseDouble(amountStr);
        CurrencyExchangeDto currencyExchangeDto =
                new CurrencyExchangeDto(baseCurrencyCode, targetCurrencyCode, amountDouble);
        ValidationUtil.validateCurrencyExchangeDto(currencyExchangeDto);

        CurrencyExchange currencyExchange = getCurrencyExchange(baseCurrencyCode, targetCurrencyCode, amountDouble);
        mapper.writeValue(resp.getWriter(), currencyExchange);
        resp.setStatus(HttpServletResponse.SC_OK);
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
