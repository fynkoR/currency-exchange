package repositories;

import models.ExchangeRate;

public interface ExchangeRateRepository extends CrudRepository<ExchangeRate> {
    ExchangeRate findByTwoCodes(String codeBase, String codeTarget);
}
