package dao;

import dto.CurrencyDto;
import model.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, CurrencyDto> {
    Optional<Currency> findById(Currency currency);
}
