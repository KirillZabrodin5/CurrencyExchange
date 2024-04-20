package servlet;

import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.PrintWriter;


@WebServlet("/hello_world")
public class FirstServlet extends HttpServlet {
    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("text/html");
        PrintWriter out = resp.getWriter();
        out.println("<h1>Hello, World!</h1>");
        out.close();
//        resp.setContentType("text/plain");
//        resp.setCharacterEncoding("UTF-8");
//
//        var stream = FirstServlet.class.getResourceAsStream("/app.json");
//
//        try (
//                BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
//            String line;
//            while ((line = reader.readLine()) != null) {
//                resp.setContentType("text/html");
//                resp.getWriter().write(line);
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void destroy() {
        super.destroy();
    }
}
