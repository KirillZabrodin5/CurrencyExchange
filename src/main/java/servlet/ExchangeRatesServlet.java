package servlet;

import Exceptions.InvalidParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import entities.Currency;
import entities.ExchangeRate;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ValidationUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
        mapper.writeValue(response.getWriter(), exchangeRates);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);
        if (rate == null) {
            throw new InvalidParameterException("rate is null");
        }
        CurrencyDao currencyDao = new JdbcCurrencyDao();

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCurrencyCode);
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCurrencyCode);
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency, Double.parseDouble(rate));
        ExchangeRate exchangeRate = exchangeRateDao.save(exchangeRateDto).orElseThrow();

        mapper.writeValue(resp.getWriter(), exchangeRate);
        resp.setStatus(HttpServletResponse.SC_CREATED);

    }
}
