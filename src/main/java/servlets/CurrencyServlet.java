package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import models.ErrorResponse;
import repositories.JdbcCurrencyRepository;
import utils.Validator;

import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
            String code = Validator.getRequiredPathSegment(req);

            Currency currency = jdbcCurrencyRepository.findByCode(code)
                    .orElseThrow(NoSuchElementException::new);
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), currency);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable"));
        } catch (NoSuchElementException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_NOT_FOUND, "Currency not found"));
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_BAD_REQUEST, e.getMessage()));
        }
    }
}
