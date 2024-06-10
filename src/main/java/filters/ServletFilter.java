package filters;

import exception.DatabaseUnavailableException;
import exception.EntityExistsException;
import exception.InvalidParameterException;
import exception.NotFoundException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ErrorResponseDto;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter("/*")
public class ServletFilter extends HttpFilter {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public void doFilter(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json");

        try {
            response.setStatus(HttpServletResponse.SC_OK);
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
        ErrorResponseDto errorResponseDto = new ErrorResponseDto(exception.getMessage());
        mapper.writeValue(response.getWriter(), errorResponseDto);
    }
}
