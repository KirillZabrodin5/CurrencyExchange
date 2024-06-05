package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.NotFoundException;
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
import entities.Currency;
import utils.ValidatorCode;

import java.io.IOException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final ObjectMapper mapper = new ObjectMapper();
    private final CurrencyDao dao = new JdbcCurrencyDao();

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        mapper.enable(SerializationFeature.INDENT_OUTPUT);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String path = req.getPathInfo();
        String[] paths = path.split("/");
        String codeCurrency = null;
        if(paths.length != 0) {
            codeCurrency = paths[paths.length - 1];
        }

        if (!ValidatorCode.isValid(codeCurrency)) {
            String message = "Currency code missing at address";
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        resp.setCharacterEncoding("UTF-8");
        resp.setContentType("application/json");
        try{
            Currency currency = dao.findByCode(new CurrencyDto(codeCurrency)).orElseThrow();
            String answer = mapper.writeValueAsString(currency);
            resp.getWriter().write(answer);
        } catch (Exception e){
            String message = e.getMessage();
            ObjectNode json = mapper.createObjectNode();
            json.put("message", message);
            resp.getWriter().write(json.toString());
            if (e instanceof DatabaseUnavailableException) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                return;
            } else if (e instanceof NotFoundException) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                return;
            }


            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
    }
}
