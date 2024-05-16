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
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.CurrencyExchange;
import model.ExchangeRate;
import service.Exchange;
import utils.ValidatorCode;

import java.io.IOException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private static final ObjectMapper mapper = new ObjectMapper();
    public static final CurrencyDao currencyDao = new JdbcCurrencyDao();
    public static final ExchangeRateDao exchangeRateDao = new JdbcExchangeRateDao();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String from = request.getParameter("from");
        String to = request.getParameter("to");
        String amount = request.getParameter("amount");

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        if (!ValidatorCode.isValid(from) || !ValidatorCode.isValid(to) || amount == null) {
            ObjectNode json = mapper.createObjectNode();
            String message = "Currency code not found";
            json.put("message", message);
            response.getWriter().write(json.toString());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        CurrencyDto baseCurrencyDto = new CurrencyDto(from);
        Currency baseCurrency;

        CurrencyDto targetCurrencyDto = new CurrencyDto(to);
        Currency targetCurrency;

        try {
            baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
            targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();
        } catch (Exception e) {
            ObjectNode json = mapper.createObjectNode();
            String message = e.getMessage();
            json.put("message", message);
            response.getWriter().write(json.toString());
            if (e instanceof NotFoundException) {
                response.setStatus(HttpServletResponse.SC_NOT_FOUND);
            } else if (e instanceof DatabaseUnavailableException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            } else {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            return;
        }
        Exchange exchange = new Exchange(baseCurrency.getId(), targetCurrency.getId());
        double rate = exchange.translate();
        CurrencyExchange currencyExchange = new CurrencyExchange(baseCurrency, targetCurrency,
                rate, Double.parseDouble(amount));
        String answer = mapper.writeValueAsString(currencyExchange);
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().write(answer);
    }
}
