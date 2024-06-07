package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
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
import java.math.BigDecimal;

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

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), currencyExchange);
    }

    private CurrencyExchange getCurrencyExchange(String baseCode, String targetCode, double amount) {
        Currency baseCurrency = currencyDao.findByCode(baseCode).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCode).orElseThrow();

        TransferRoute transferRoute = new TransferRoute(baseCode, targetCode);
        BigDecimal rate = transferRoute.getRate();
        return new CurrencyExchange(baseCurrency, targetCurrency, rate, amount);
    }
}
