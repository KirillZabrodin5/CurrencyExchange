package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import utils.ValidatorCode;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();

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
            List<Currency> currencies = currencyDao.findAll();
            String answer = mapper.writeValueAsString(currencies);
            response.setStatus(HttpServletResponse.SC_OK);
            response.getWriter().write(answer);
        } catch (Exception e) {
            String message = e.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            response.getWriter().write(json.toString());
            if (e instanceof DatabaseUnavailableException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        if (!ValidatorCode.isValid(code) || name == null || sign == null) {
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "The required form field is present");
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        CurrencyDto currencyDto = new CurrencyDto(code, name, sign);
        Currency currency = null;
        try {
            currency = currencyDao.save(currencyDto).orElseThrow();
        } catch (Exception e) {
            if (e instanceof DatabaseUnavailableException) {
                ObjectNode json = mapper.createObjectNode();
                json.put("message", e.getMessage());
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } else if (e instanceof EntityExistsException) {
                ObjectNode json = mapper.createObjectNode();
                json.put("message", e.getMessage());
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String answer = mapper.writeValueAsString(currency);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(answer);
    }

    //передается код в теле запроса
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        if (!ValidatorCode.isValid(code)) { // эта проверка в нужном слое находится
            ObjectNode json = mapper.createObjectNode();
            json.put("message", "The required form field is present");
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }
        CurrencyDto currencyDto = new CurrencyDto(code);
        Currency currency = null;
        try {
            currency = currencyDao.delete(currencyDto).orElseThrow();
        } catch (Exception e) {
            if (e instanceof DatabaseUnavailableException) {
                ObjectNode json = mapper.createObjectNode();
                json.put("message", e.getMessage());
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } else if (e instanceof EntityExistsException) {
                ObjectNode json = mapper.createObjectNode();
                json.put("message", e.getMessage());
                resp.getWriter().write(json.toString());
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                return;
            }
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        String answer = mapper.writeValueAsString(currency);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(answer);
    }
}
