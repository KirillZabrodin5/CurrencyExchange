package dao;

import model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, String> {
    Optional<ExchangeRate> findByCode(String baseCode, String targetCode);

    Optional<ExchangeRate> update(ExchangeRate exchangeRate);
}
