package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import models.ExchangeRate;
import org.sqlite.SQLiteErrorCode;
import repositories.JdbcCurrencyRepository;
import repositories.JdbcExchangeRateRepository;
import utils.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcExchangeRateRepository jdbcExchangeRateRepository = new JdbcExchangeRateRepository();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try{
            List<ExchangeRate> list = jdbcExchangeRateRepository.findAll();
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(),list);

        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/x-www-form-urlencoded");
        resp.setCharacterEncoding("UTF-8");

        try{
            String baseCurrencyCode = Validator.getRequiredParameter(req, "baseCurrencyCode");
            String targetCurrencyCode = Validator.getRequiredParameter(req, "targetCurrencyCode");
            String rateString = Validator.getRequiredParameter(req, "rate");

            BigDecimal rate = new BigDecimal(rateString);

            Currency baseCurrency = jdbcCurrencyRepository.findByCode(baseCurrencyCode)
                    .orElseThrow(NoSuchElementException::new);

            Currency targetCurrency = jdbcCurrencyRepository.findByCode(targetCurrencyCode)
                    .orElseThrow(NoSuchElementException::new);

            ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);
            long id = jdbcExchangeRateRepository.save(exchangeRate);
            exchangeRate.setId(id);
            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRate);

        } catch (ValidationException | NumberFormatException e){
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "One (or both) of the currencies in the currency pair does not exist.");
        } catch (SQLException e) {
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                resp.sendError(HttpServletResponse.SC_CONFLICT, "A currency pair with this code already exists.");
            }
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable: " + e.getErrorCode());
        }


    }
}
