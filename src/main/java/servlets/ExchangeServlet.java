package servlets;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.ExchangeRateDTO;
import exceptions.ExchangeRateNotFoundException;
import exceptions.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import models.Currency;
import repositories.JdbcCurrencyRepository;
import repositories.JdbcExchangeRateRepository;
import services.ExchangeService;
import utils.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcExchangeRateRepository jdbcExchangeRateRepository = new JdbcExchangeRateRepository();
    JdbcCurrencyRepository jdbcCurrencyRepository = new JdbcCurrencyRepository();
    ExchangeService exchangeService = new ExchangeService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        try {
            String from = Validator.getRequiredParameter(req, "from");
            String to = Validator.getRequiredParameter(req, "to");
            String amountParameter = Validator.getRequiredParameter(req, "amount");

            BigDecimal amount = new BigDecimal(amountParameter);

            Currency currencyFrom = jdbcCurrencyRepository.findByCode(from)
                    .orElseThrow(NoSuchElementException::new);

            Currency currencyTo = jdbcCurrencyRepository.findByCode(to)
                    .orElseThrow(NoSuchElementException::new);

            BigDecimal rate = exchangeService.getRate(from,to);

            BigDecimal convertedRate = exchangeService.exchange(rate,amount);

            ExchangeRateDTO exchangeRateDTO = new ExchangeRateDTO(currencyFrom, currencyTo, rate, amount, convertedRate);

            resp.setStatus(HttpServletResponse.SC_OK);
            objectMapper.writeValue(resp.getWriter(), exchangeRateDTO);

        } catch (ValidationException | NumberFormatException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (NoSuchElementException e){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Currency not found");
        } catch (SQLException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Error! Database unavailable");
        } catch (ExchangeRateNotFoundException e){
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, e.getMessage());
        }
    }
}
