package dao;

import model.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, String> {
    Optional<Currency> findById(Long id);

    Optional<Currency> findByCode(String code);
}
