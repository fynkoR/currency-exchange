package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ErrorResponse;
import org.sqlite.SQLiteErrorCode;
import repository.JdbcCurrencyRepository;
import util.Validator;

import java.io.IOException;
import java.sql.*;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            List<Currency> list = jdbcCurrencyRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), list);

        } catch (SQLException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable"));
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String code = Validator.getRequiredParameter(req, "code");
            String name = Validator.getRequiredParameter(req, "name");
            String sign = Validator.getRequiredParameter(req, "sign");

            Currency currency = new Currency(code, name, sign);
            long id = jdbcCurrencyRepository.save(currency);
            currency.setId(id);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), currency);

        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                        (HttpServletResponse.SC_CONFLICT, "A currency with this code already exists"));
            } else {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                        (HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable"));
            }
        } catch (ValidationException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            objectMapper.writeValue(resp.getWriter(), new ErrorResponse
                    (HttpServletResponse.SC_BAD_REQUEST, e.getMessage()));
        }
    }
}
