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

        double rate;

        //todo сделать анализ код ниже, почему rate не видит при паф запросе
        try {
            rate = Double.parseDouble(req.getParameter("rate"));
        } catch (Exception e) {
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
        } catch (Exception e) {
            if (e instanceof NotFoundException) {
                ObjectNode json = mapper.createObjectNode();
                String message = e.getMessage();
                json.put("message", message);
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (e instanceof DatabaseUnavailableException) {
                ObjectNode json = mapper.createObjectNode();
                String message = e.getMessage();
                json.put("message", message);
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
        }

    }
}
