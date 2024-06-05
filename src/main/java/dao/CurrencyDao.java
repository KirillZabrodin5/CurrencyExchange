package dao;

import dto.CurrencyDto;
import entities.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, CurrencyDto> {
    Optional<Currency> findById(Currency currency);
}
