package servlet;

import dto.CurrencyDto;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
//GET /currency/EUR
@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        String path = req.getPathInfo();
        if (path == null) {
            path = path.substring(9);
        }

        if (path.startsWith("/") && path.length() < 5) {
            String currencyCode = path.substring(1, path.length());

            CurrencyDto currencyDTO  = new CurrencyDto();
            //currencyDTO.getJsonCurrency(currencyCode);

            resp.setCharacterEncoding("UTF-8");
            var stream = CurrenciesServlet.class.getResourceAsStream("jsonCurrency.json");

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    resp.setContentType("application/json");
                    resp.getWriter().write(line);
                    resp.setStatus(200);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
