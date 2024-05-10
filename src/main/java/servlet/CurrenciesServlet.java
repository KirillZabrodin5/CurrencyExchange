package servlet;

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

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private CurrencyDao currencyDao = null;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        currencyDao = new JdbcCurrencyDao();
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
            json.put("message", message);
            response.getWriter().write(json.toString());
        }
    }
}
