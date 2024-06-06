package dao;

import dto.ExchangeRateDto;
import entities.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, Long> {
    Optional<ExchangeRate> update(ExchangeRate exchangeRate);
    Optional<ExchangeRate> findByCode(String baseCode, String targetCode);
}
