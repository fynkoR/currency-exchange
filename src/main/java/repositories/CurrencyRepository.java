package repositories;

import models.Currency;

import java.sql.SQLException;

public interface CurrencyRepository extends CrudRepository<Currency> {
    Currency findByCode(String code) throws SQLException;
}
