package repository;

import model.ExchangeRate;

import java.sql.SQLException;
import java.util.Optional;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate> {
    Optional<ExchangeRate> findByTwoCodes(String codeBase, String codeTarget) throws SQLException;
}
