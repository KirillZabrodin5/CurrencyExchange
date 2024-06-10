package servlet.exchangeRate;

import exception.InvalidParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateDto;
import entity.Currency;
import entity.ExchangeRate;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ConverterUtil;
import utils.ValidationUtil;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<ExchangeRateDto> exchangeRates = exchangeRateDao
                .findAll()
                .stream()
                .map(CONVERTER_UTIL::exchangeRateToDto)
                .toList();

        mapper.writeValue(response.getWriter(), exchangeRates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);
        if (rate == null) {
            throw new InvalidParameterException("rate is not valid");
        }
        CurrencyDao currencyDao = new JdbcCurrencyDao();

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode).orElseThrow();

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(CONVERTER_UTIL.currencyToDto(baseCurrency),
                CONVERTER_UTIL.currencyToDto(targetCurrency), new BigDecimal(rate));

        ExchangeRate exchangeRate = exchangeRateDao
                .save(CONVERTER_UTIL.dtoToExchangeRate(exchangeRateDto))
                .orElseThrow();

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.exchangeRateToDto(exchangeRate));
    }
}
