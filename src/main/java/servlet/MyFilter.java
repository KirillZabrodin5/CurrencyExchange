package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.InvalidParameterException;
import Exceptions.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class MyFilter extends HttpFilter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        //надо здесь как-то перенаправлять запрос в нужный сервлет
        //а потом выполнять необходимый метод в блоке try, а в catch ловить ошибки все и
        //создавать Json и меняя код ответа

        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        //примерно так должно выглядеть:
        try {
            super.doFilter(request, response, chain);
        } catch (InvalidParameterException e) {
            writeExceptionToResponse(response, HttpServletResponse.SC_BAD_REQUEST, e);
        } catch (DatabaseUnavailableException e) {
            writeExceptionToResponse(response, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e);
        } catch (NotFoundException e) {
            writeExceptionToResponse(response, HttpServletResponse.SC_NOT_FOUND, e);
        } catch (EntityExistsException e) {
            writeExceptionToResponse(response, HttpServletResponse.SC_CONFLICT, e);
        }
    }

    private void writeExceptionToResponse(HttpServletResponse response, int codeException, Exception exception) throws IOException {
        response.setStatus(codeException);
        mapper.writeValue(response.getWriter(), exception);
    }
}
