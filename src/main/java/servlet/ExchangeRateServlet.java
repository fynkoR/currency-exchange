package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.ValidationException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.ExchangeRate;
import repository.JdbcExchangeRateRepository;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.NoSuchElementException;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    ObjectMapper objectMapper = new ObjectMapper();
    JdbcExchangeRateRepository jdbcExchangeRateRepository = new JdbcExchangeRateRepository();

    @Override
    protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        if("PATCH".equalsIgnoreCase(req.getMethod())){
            processPatch(req,resp);
        }
        else{
            super.service(req, resp);
        }
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        String[] codes = Validator.getRequiredPathTwoSegment(req);
        ExchangeRate exchangeRate = jdbcExchangeRateRepository.findByTwoCodes(codes[0], codes[1])
                .orElseThrow(() -> new NoSuchElementException("Exchange rate for the pair not found"));
        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRate);
    }

    protected void processPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String[] codes = Validator.getRequiredPathTwoSegment(req);
        ExchangeRate exchangeRate = jdbcExchangeRateRepository.findByTwoCodes(codes[0], codes[1])
                .orElseThrow(() -> new NoSuchElementException("Exchange rate for the pair not found"));

        String rateString = Validator.getRequiredParameterForPatch(req, "rate");

        BigDecimal rate = Validator.getPositiveDecimal(rateString, "rate");

        exchangeRate.setRate(rate);

        jdbcExchangeRateRepository.update(exchangeRate);

        resp.setStatus(HttpServletResponse.SC_OK);
        objectMapper.writeValue(resp.getWriter(), exchangeRate);
    }
}
