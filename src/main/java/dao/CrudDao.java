package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, C> {
    List<T> findAll();

    Optional<T> save(T entity);
}
