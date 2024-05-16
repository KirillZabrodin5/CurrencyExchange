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
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");

        String path = req.getPathInfo();
        String[] paths = path.split("/");

        String codeCurrency = paths[paths.length - 1];
        if (codeCurrency.length() != 6) {
            ObjectNode json = mapper.createObjectNode();
            String message = "Currency code not found";
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

        try {
            CurrencyDto baseCurrencyDto = new CurrencyDto(baseCodeCurrency);
            CurrencyDto targetCurrencyDto = new CurrencyDto(targetCodeCurrency);
            Currency baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
            Currency targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();

            ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency);
            ExchangeRate exchangeRate = exchangeRateDao.findByCode(exchangeRateDto).orElseThrow();
            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(mapper.writeValueAsString(exchangeRate));
        } catch (Exception e) {
            ObjectNode json = mapper.createObjectNode();
            String message = e.getMessage();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            if (e instanceof NotFoundException) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (e instanceof DatabaseUnavailableException) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }
    }



    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String method = request.getMethod();
        if (method.equalsIgnoreCase("PATCH")) {
            doPatch(request, response);
        }
        else {
            super.service(request, response);
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
            String message = "Currency code not found";
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
            try {
                rate = Double.parseDouble(params[1]);
            } catch (NumberFormatException e) {
                ObjectNode json = mapper.createObjectNode();
                String message = "The required field in the form was not found";
                json.put("message", message);
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }
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
        } catch (Exception e) {
            ObjectNode json = mapper.createObjectNode();
            String message = e.getMessage();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            if (e instanceof NotFoundException) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (e instanceof DatabaseUnavailableException) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

    }
}
