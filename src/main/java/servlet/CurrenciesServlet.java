package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import repository.JdbcCurrencyRepository;
import util.Validator;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<Currency> list = jdbcCurrencyRepository.findAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), list);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = Validator.getRequiredParameter(req, "code");
        String name = Validator.getRequiredParameter(req, "name");
        String sign = Validator.getRequiredParameter(req, "sign");

        Currency currency = new Currency(code, name, sign);
        long id = jdbcCurrencyRepository.save(currency);
        currency.setId(id);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), currency);
    }
}
