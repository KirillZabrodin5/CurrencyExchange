package dao;

import entity.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Long> {
    Optional<ExchangeRate> update(ExchangeRate exchangeRate);
    Optional<ExchangeRate> findByCodes(String baseCode, String targetCode);
}
