package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");

        var stream = CurrenciesServlet.class.getResourceAsStream("/jsonCur.json");

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                response.setContentType("application/json");
                response.getWriter().write(line);
                response.setStatus(400);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    @Override
    public void destroy() {
        super.destroy();
    }
}
