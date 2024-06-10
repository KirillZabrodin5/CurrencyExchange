package servlet;

import exception.InvalidParameterException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import dao.CurrencyDao;
import dao.JdbcCurrencyDao;

import dto.CurrencyExchangeDto;
import dto.CurrencyExchangeRequestDto;
import entity.Currency;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import service.CurrencyExchangeService;
import utils.ConverterUtil;
import utils.ValidationUtil;

import java.io.IOException;
import java.math.BigDecimal;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao currencyDao = new JdbcCurrencyDao();
    private final CurrencyExchangeService currencyExchangeService = new CurrencyExchangeService();
    private static final ConverterUtil CONVERTER_UTIL = new ConverterUtil();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String baseCurrencyCode = req.getParameter("from");
        String targetCurrencyCode = req.getParameter("to");
        String amountStr = req.getParameter("amount");

        ValidationUtil.validateCurrencyCode(baseCurrencyCode);
        ValidationUtil.validateCurrencyCode(targetCurrencyCode);

        if (amountStr == null) {
            throw new InvalidParameterException("Amount is empty");
        }

        BigDecimal amountBigDecimal = new BigDecimal(amountStr);

        Currency baseCurrency = currencyDao.findByCode(baseCurrencyCode).orElseThrow();
        Currency targetCurrency = currencyDao.findByCode(targetCurrencyCode).orElseThrow();

        CurrencyExchangeRequestDto currencyExchangeRequestDto = new CurrencyExchangeRequestDto(
                CONVERTER_UTIL.currencyToDto(baseCurrency),
                CONVERTER_UTIL.currencyToDto(targetCurrency),
                amountBigDecimal);

        CurrencyExchangeDto currencyExchangeResponseDto = currencyExchangeService
                .getCurrencyExchange(currencyExchangeRequestDto);

        mapper.writeValue(resp.getWriter(), currencyExchangeResponseDto);
    }
}
