package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.CurrencyDao;
import dao.ExchangeRateDao;
import dao.JdbcCurrencyDao;
import dao.JdbcExchangeRateDao;
import dto.CurrencyDto;
import dto.ExchangeRateDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import entities.Currency;
import entities.ExchangeRate;
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
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");
        try {
            List<ExchangeRate> exchangeRates = exchangeRateDao.findAll();
            String answer = mapper.writeValueAsString(exchangeRates);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(answer);
        } catch (Exception e) {
            String message = e.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            if (e instanceof DatabaseUnavailableException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            response.getWriter().write(json.toString());
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("baseCurrencyCode");
        String targetCurrencyCode = req.getParameter("targetCurrencyCode");
        String rate = req.getParameter("rate");
        if (!ValidationUtil.validateCurrencyCode(baseCurrencyCode)
                || !ValidationUtil.validateCurrencyCode(targetCurrencyCode)
                || rate == null) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "The required form field is present");
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        CurrencyDao currencyDao = new JdbcCurrencyDao();

        CurrencyDto baseCurrencyDto = new CurrencyDto(baseCurrencyCode);
        CurrencyDto targetCurrencyDto = new CurrencyDto(targetCurrencyCode);
        Currency baseCurrency = null;
        Currency targetCurrency = null;
        try {
            baseCurrency = currencyDao.findByCode(baseCurrencyDto).orElseThrow();
            targetCurrency = currencyDao.findByCode(targetCurrencyDto).orElseThrow();
        } catch (NotFoundException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", e.getMessage());
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            return;
        } catch (DatabaseUnavailableException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", e.getMessage());
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        }

        ExchangeRateDto exchangeRateDto = new ExchangeRateDto(baseCurrency, targetCurrency, Double.parseDouble(rate));
        ExchangeRate exchangeRate = null;
        try {
            exchangeRate = exchangeRateDao.save(exchangeRateDto).orElseThrow();
        } catch (DatabaseUnavailableException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", e.getMessage());
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return;
        } catch (EntityExistsException e) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", e.getMessage());
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            return;
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String answer = mapper.writeValueAsString(exchangeRate);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(answer);
    }
}
