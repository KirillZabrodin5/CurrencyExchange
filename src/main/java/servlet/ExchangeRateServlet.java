package servlet;

import Exceptions.InvalidParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import entities.Currency;
import entities.ExchangeRate;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ValidationUtil;

import java.io.IOException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final CurrencyDao currencyDao = new JdbcCurrencyDao();
    public static final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

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

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCodeCurrency);
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCodeCurrency);
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency);
        ExchangeRate exchangeRate = exchangeRateDao.findByCode(exchangeRateDto).orElseThrow();
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), exchangeRate);
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
        double rate;
        if (params.length == 2 && params[0].equals("rate")) {
            rate = Double.parseDouble(params[1]);
        } else {
            throw new InvalidParameterException("Rate is not valid");
        }

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCodeCurrency);
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCodeCurrency);
        Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency, rate);
        ExchangeRate exchangeRate = exchangeRateDao.update(exchangeRateDto).orElseThrow();
        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), exchangeRate);
    }
}
