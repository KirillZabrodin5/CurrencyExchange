package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import entities.Currency;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import utils.ConverterUtil;
import utils.ValidationUtil;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codeCurrency = req.getPathInfo().replaceFirst("/", "");

        ValidationUtil.validateCurrencyCode(codeCurrency);

        Currency currency = currencyDao.findByCode(codeCurrency).orElseThrow();

        resp.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.currencyToDto(currency));
    }
}
