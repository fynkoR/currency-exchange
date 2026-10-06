package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDto;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import repository.JdbcCurrencyRepository;
import repository.JdbcExchangeRateRepository;
import service.ExchangeService;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcExchangeRateRepository jdbcExchangeRateRepository = new JdbcExchangeRateRepository();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();
    ExchangeService exchangeService = new ExchangeService(jdbcExchangeRateRepository);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String from = Validator.getRequiredParameter(req, "from");
        String to = Validator.getRequiredParameter(req, "to");
        String amountParameter = Validator.getRequiredParameter(req, "amount");

        BigDecimal amount = Validator.getPositiveDecimal(amountParameter, "amount");

        Currency currencyFrom = jdbcCurrencyRepository.findByCode(from)
                .orElseThrow(() -> new NoSuchElementException("Currency not found"));

        Currency currencyTo = jdbcCurrencyRepository.findByCode(to)
                .orElseThrow(() -> new NoSuchElementException("Currency not found"));

        BigDecimal rate = exchangeService.getRate(from,to);

        BigDecimal convertedRate = exchangeService.exchange(rate,amount);

        ExchangeRateDto exchangeRateDTO = new ExchangeRateDto(currencyFrom, currencyTo, rate, amount, convertedRate);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRateDTO);
    }
}
