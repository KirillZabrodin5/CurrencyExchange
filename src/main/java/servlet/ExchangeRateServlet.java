package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ExchangeRate;
import utils.ValidatorCode;

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

    //TODO метод GET
    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        String path = req.getPathInfo();
        String[] paths = path.split("/");

        String codeCurrency = null;
        String baseCodeCurrency = null;
        String targetCodeCurrency = null;
        if (paths.length != 0) {
            codeCurrency = paths[paths.length - 1];
            if (codeCurrency.length() == 6) {
                baseCodeCurrency = codeCurrency.substring(0, 3);
                targetCodeCurrency = codeCurrency.substring(3, 6);
            }
        }

        if (!ValidatorCode.isValid(baseCodeCurrency) || !ValidatorCode.isValid(targetCodeCurrency)) {
            ObjectNode json = mapper.createObjectNode();
            String message = "The currency code is missing";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }


        try {
            CurrencyDto baseCurrencyDto = new CurrencyDto(baseCodeCurrency);
            CurrencyDto targetCurrencyDto = new CurrencyDto(targetCodeCurrency);
            Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
            Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency);
            ExchangeRate exchangeRate = exchangeRateDao.findByCode(exchangeRateDto).orElseThrow();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (Exception ex) {
            ObjectNode json = mapper.createObjectNode();
            String message = ex.getMessage();
            if (ex instanceof NotFoundException) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                message = "Exchange rate not found";
            }
            json.put("message", message);
            resp.getWriter().write(json.toString());
        }

    }

    public void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        String path = req.getPathInfo();
        String[] paths = path.split("/");
        String codeCurrency = paths[paths.length - 1];
        if (codeCurrency.length() != 6) {
            ObjectNode json = mapper.createObjectNode();
            String message = "The required form field is present";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        String baseCodeCurrency = codeCurrency.substring(0, 3);
        String targetCodeCurrency = codeCurrency.substring(3, 6);

        if (!ValidatorCode.isValid(baseCodeCurrency) || !ValidatorCode.isValid(targetCodeCurrency)) {
            ObjectNode json = mapper.createObjectNode();
            String message = "Currency code not found";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        StringBuilder body = new StringBuilder();
        String line;
        while ((line = req.getReader().readLine()) != null) {
            body.append(line);
        }
        String postBody = body.toString();
        String[] params = postBody.split("=");
        double rate = 0.0;
        if (params.length == 2 && params[0].equals("rate")) {
            rate = Double.parseDouble(params[1]);
        } else {
            ObjectNode json = mapper.createObjectNode();
            String message = "The required field in the form was not found";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        try {
            CurrencyDto baseCurrencyDto = new CurrencyDto(baseCodeCurrency);
            CurrencyDto targetCurrencyDto = new CurrencyDto(targetCodeCurrency);
            Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
            Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency, rate);
            ExchangeRate exchangeRate = exchangeRateDao.update(exchangeRateDto).orElseThrow();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (NotFoundException e) {

            ObjectNode json = mapper.createObjectNode();
            String message = "Exchange rate not found";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);

        } catch (DatabaseUnavailableException e) {

            ObjectNode json = mapper.createObjectNode();
            String message = e.getMessage();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);

        }
    }
}
