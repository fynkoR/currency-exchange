package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import model.ExchangeRate;
import repository.JdbcCurrencyRepository;
import repository.JdbcExchangeRateRepository;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.List;
import java.util.NoSuchElementException;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcExchangeRateRepository jdbcExchangeRateRepository = new JdbcExchangeRateRepository();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        List<ExchangeRate> list = jdbcExchangeRateRepository.findAll();
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), list);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String baseCurrencyCode = Validator.getRequiredParameter(req, "baseCurrencyCode");
        String targetCurrencyCode = Validator.getRequiredParameter(req, "targetCurrencyCode");
        String rateString = Validator.getRequiredParameter(req, "rate");

        Validator.validateCurrenciesNotEqual(baseCurrencyCode, targetCurrencyCode);

        BigDecimal rate = Validator.getPositiveDecimal(rateString, "rate");

        Currency baseCurrency = jdbcCurrencyRepository.findByCode(baseCurrencyCode)
                .orElseThrow(() -> new NoSuchElementException("Currency not found"));

        Currency targetCurrency = jdbcCurrencyRepository.findByCode(targetCurrencyCode)
                .orElseThrow(() -> new NoSuchElementException("Currency not found"));

        ExchangeRate exchangeRate = new ExchangeRate(baseCurrency, targetCurrency, rate);
        long id = jdbcExchangeRateRepository.save(exchangeRate);
        exchangeRate.setId(id);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        objectMapper.writeValue(resp.getWriter(), exchangeRate);
    }
}
