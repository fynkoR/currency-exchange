package services;

import exceptions.ExchangeRateNotFoundException;
import models.ExchangeRate;
import repositories.JdbcExchangeRateRepository;

import javax.swing.text.html.Option;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.SQLException;
import java.util.Optional;

public class ExchangeService {
    private static final int SCALE = 2;
    private final JdbcExchangeRateRepository jdbcExchangeRateRepository;

    public ExchangeService() {
        jdbcExchangeRateRepository = new JdbcExchangeRateRepository();
    }

    public BigDecimal getRate(String from, String to) throws SQLException {
        Optional<BigDecimal> rate = convertRate(from, to);
        if (!rate.isPresent()) {
            rate = inverseConvertRate(from, to);
        }
        if (!rate.isPresent()) {
            rate = crossRate(from, to);
        }

        return rate.orElseThrow(() -> new ExchangeRateNotFoundException(from, to));
    }

    public BigDecimal exchange(BigDecimal rate, BigDecimal amount){
        return rate.multiply(amount);
    }

    public Optional<BigDecimal> convertRate(String from, String to) throws SQLException {
        Optional<ExchangeRate> exchangeRateFromTo = jdbcExchangeRateRepository.findByTwoCodes(from, to);
        if (exchangeRateFromTo.isPresent()) {
            BigDecimal rate = exchangeRateFromTo.get().getRate();
            return Optional.of(rate);
        }

        return Optional.empty();
    }

    public Optional<BigDecimal> inverseConvertRate(String from, String to) throws SQLException {
        Optional<ExchangeRate> exchangeRateToFrom = jdbcExchangeRateRepository.findByTwoCodes(to, from);
        if (exchangeRateToFrom.isPresent()) {
            BigDecimal rate = exchangeRateToFrom.get().getRate();
            return Optional.of(BigDecimal.ONE.divide(rate, SCALE, RoundingMode.HALF_UP));
        }

        return Optional.empty();
    }

    public Optional<BigDecimal> crossRate(String from, String to) throws SQLException {
        Optional<ExchangeRate> exchangeRateUsdFrom = jdbcExchangeRateRepository.findByTwoCodes("USD", from);
        Optional<ExchangeRate> exchangeRateUsdTo = jdbcExchangeRateRepository.findByTwoCodes("USD", to);
        if (exchangeRateUsdFrom.isPresent() && exchangeRateUsdTo.isPresent()) {
            BigDecimal rateUsdFrom = exchangeRateUsdFrom.get().getRate();
            BigDecimal rateUsdTo = exchangeRateUsdTo.get().getRate();

            return Optional.of(rateUsdTo.divide(rateUsdFrom, SCALE, RoundingMode.HALF_UP));
        }
        return Optional.empty();
    }
}
