package dao;

import entity.Currency;

import java.util.Optional;

public interface CurrencyDao extends CrudDao<Currency, Long> {
    Optional<Currency> findByCode(String entity);
}
