package dao;

import dto.ExchangeRateDto;
import model.ExchangeRate;

import java.util.Optional;

public interface ExchangeRateDao extends CrudDao<ExchangeRate, ExchangeRateDto> {
    Optional<ExchangeRate> update(ExchangeRateDto exchangeRateDto);
}
