package servlet;

import Exceptions.DatabaseUnavailableException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ObjectNode;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import utils.ValidatorCode;
import java.io.BufferedReader;

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
        try{
            List<Currency> currencies = currencyDao.findAll();
            String answer = mapper.writeValueAsString(currencies);
            response.getWriter().write(answer);
        } catch (Exception e){
            String message = e.getMessage();
            ObjectNode json = mapper.createObjectNode();
            if (e instanceof DatabaseUnavailableException) {
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }
            json.put("message", message);
            response.getWriter().write(json.toString());
        }
    }

    //TODO научиться обрабатывать post запрос, тут фигня какая-то
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String[] paths = path.split("/");
        String codeCurrency = paths[paths.length - 1];
        if (!ValidatorCode.isValid(codeCurrency)) {
            ObjectNode json = mapper.createObjectNode();
            String message = "Currency code missing at address";
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        BufferedReader reader = req.getReader();
        String line;
        StringBuilder result = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        resp.getWriter().write(result.toString());
    }
}
