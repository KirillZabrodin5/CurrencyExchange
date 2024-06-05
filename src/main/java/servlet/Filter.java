package servlet;

import Exceptions.DatabaseUnavailableException;
import Exceptions.EntityExistsException;
import Exceptions.InvalidParameterException;
import Exceptions.NotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class Filter {
    public void workMethod(HttpServletRequest request, HttpServletResponse response) {
        //надо здесь как-то перенаправлять запрос в нужный сервлет
        //а потом выполнять необходимый метод в блоке try, а в catch ловить ошибки все и
        //создавать Json и меняя код ответа

        //примерно так должно выглядеть:
        try {
            doGet();
        } catch (InvalidParameterException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } catch (DatabaseUnavailableException e) {
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } catch (NotFoundException e) {
            response.setStatus(HttpServletResponse.SC_NOT_FOUND);
        } catch (EntityExistsException e) {
            response.setStatus(HttpServletResponse.SC_CONFLICT);
        }
    }
}
