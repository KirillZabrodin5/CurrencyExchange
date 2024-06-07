package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;
import dto.CurrencyDto;
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
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        List<CurrencyDto> currencies = currencyDao.findAll()
                .stream()
                .map(CONVERTER_UTIL::currencyToDto)
                .toList();

        response.setStatus(HttpServletResponse.SC_OK);
        mapper.writeValue(response.getWriter(), currencies);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String name = req.getParameter("name");
        String code = req.getParameter("code");
        String sign = req.getParameter("sign");
        CurrencyDto currencyDto = new CurrencyDto(code, name, sign);
        ValidationUtil.validateCurrencyDto(currencyDto);

        Currency currency = currencyDao
                .save(CONVERTER_UTIL.dtoToCurrency(currencyDto))
                .orElseThrow();

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.currencyToDto(currency));
    }

    //TODO этот метод нерабочий
    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String code = req.getParameter("code");
        ValidationUtil.validateCurrencyCode(code);

        CurrencyDto currencyDto = new CurrencyDto(code);
        Currency currency = currencyDao
                .delete(CONVERTER_UTIL.dtoToCurrency(currencyDto))
                .orElseThrow();

        resp.setStatus(HttpServletResponse.SC_CREATED);
        mapper.writeValue(resp.getWriter(), CONVERTER_UTIL.currencyToDto(currency));
    }
}
