package servlet.exchangeRate;

import exception.InvalidParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.ExchangeRateDto;
import entity.Currency;
import entity.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ConverterUtil;
import utils.ValidationUtil;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final CurrencyDao currencyDao = new JdbcCurrencyDao();
    public static final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.equals("PATCH")) {
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo().replaceFirst("/", "");
        if (path.length() != 6) {
            throw new InvalidParameterException("Currency codes are missing in the URL");
        }

        String baseCodeCurrency = path.substring(0, 3);
        String targetCodeCurrency = path.substring(3, 6);

        ValidationUtil.validateCurrencyCode(baseCodeCurrency);
        ValidationUtil.validateCurrencyCode(targetCodeCurrency);

        ExchangeRate exchangeRate = exchangeRateDao.findByCodes(baseCodeCurrency, targetCodeCurrency).orElseThrow();

        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.exchangeRateToDto(exchangeRate));
    }

    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo().replaceFirst("/", "");
        if (path.length() != 6) {
            throw new InvalidParameterException("Currency codes are missing in the URL");
        }

        String baseCodeCurrency = path.substring(0, 3);
        String targetCodeCurrency = path.substring(3, 6);

        ValidationUtil.validateCurrencyCode(baseCodeCurrency);
        ValidationUtil.validateCurrencyCode(targetCodeCurrency);

        StringBuilder body = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            body.append(line);
        }
        String postBody = body.toString();
        String[] params = postBody.split("=");
        BigDecimal rate;
        if (params.length == 2 && params[0].equals("rate")) {
            rate = BigDecimal.valueOf(Double.parseDouble(params[1]));
        } else {
            throw new InvalidParameterException("Rate is not valid");
        }

        Currency baseCurrency = currencyDao.findByCode(baseCodeCurrency).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCodeCurrency).orElseThrow();

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(
                CONVERTER_UTIL.currencyToDto(baseCurrency), CONVERTER_UTIL.currencyToDto(targetCurrency), rate);

        ExchangeRate exchangeRate = exchangeRateDao
                .update(CONVERTER_UTIL.dtoToExchangeRate(exchangeRateDto))
                .orElseThrow();

        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.exchangeRateToDto(exchangeRate));
    }
}
