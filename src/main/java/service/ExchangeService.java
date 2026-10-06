package service;

import exception.ExchangeRateNotFoundException;
import model.ExchangeRate;
import repository.JdbcExchangeRateRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

public class ExchangeService {
    private static final int SCALE_DIVIDE = 6;
    private static final int SCALE_MULTIPLY = 2;

    private final JdbcExchangeRateRepository jdbcExchangeRateRepository;

    public ExchangeService(JdbcExchangeRateRepository jdbcExchangeRateRepository) {
        this.jdbcExchangeRateRepository = jdbcExchangeRateRepository;
    }

    public BigDecimal getRate(String from, String to){



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
        return rate.multiply(amount).setScale(SCALE_MULTIPLY, RoundingMode.HALF_UP);
    }

    public Optional<BigDecimal> convertRate(String from, String to){
        Optional<ExchangeRate> exchangeRateFromTo = jdbcExchangeRateRepository.findByTwoCodes(from, to);
        if (exchangeRateFromTo.isPresent()) {
            BigDecimal rate = exchangeRateFromTo.get().getRate();
            return Optional.of(rate);
        }

        return Optional.empty();
    }

    public Optional<BigDecimal> inverseConvertRate(String from, String to) {
        Optional<ExchangeRate> exchangeRateToFrom = jdbcExchangeRateRepository.findByTwoCodes(to, from);
        if (exchangeRateToFrom.isPresent()) {
            BigDecimal rate = exchangeRateToFrom.get().getRate();
            return Optional.of(BigDecimal.ONE.divide(rate, SCALE_DIVIDE, RoundingMode.HALF_UP));
        }

        return Optional.empty();
    }

    public Optional<BigDecimal> crossRate(String from, String to) {
        Optional<ExchangeRate> exchangeRateUsdFrom = jdbcExchangeRateRepository.findByTwoCodes("USD", from);
        Optional<ExchangeRate> exchangeRateUsdTo = jdbcExchangeRateRepository.findByTwoCodes("USD", to);
        if (exchangeRateUsdFrom.isPresent() && exchangeRateUsdTo.isPresent()) {
            BigDecimal rateUsdFrom = exchangeRateUsdFrom.get().getRate();
            BigDecimal rateUsdTo = exchangeRateUsdTo.get().getRate();

            return Optional.of(rateUsdTo.divide(rateUsdFrom, SCALE_DIVIDE, RoundingMode.HALF_UP));
        }
        return Optional.empty();
    }
}
