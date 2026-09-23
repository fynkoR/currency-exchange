package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import org.sqlite.SQLiteErrorCode;
import repositories.JdbcCurrencyRepository;
import utils.Validator;

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
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.setStatus(200);
            objectMapper.writeValue(resp.getWriter(), list);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded");
        resp.setCharacterEncoding("UTF-8");
        try {
            String code = Validator.getRequiredParameter(req, "code");
            String fullName = Validator.getRequiredParameter(req, "fullName");
            String sign = Validator.getRequiredParameter(req, "sign");

            Currency currency = new Currency(code, fullName, sign);
            long id = jdbcCurrencyRepository.save(currency);
            currency.setId(id);
            resp.setStatus(HttpServletResponse.SC_CREATED);
            objectMapper.writeValue(resp.getWriter(), currency);

        } catch (SQLException e) {
            if (e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code) {
                resp.sendError(HttpServletResponse.SC_CONFLICT, "A currency with this code already exists");
            } else {
                resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable");
            }
        } catch (ValidationException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
    }
}
