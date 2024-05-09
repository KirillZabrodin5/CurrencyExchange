package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, C> {
    List<T> findAll();

    Optional<T> findByCode(C dto);

    Optional<T> save(C entity);

    Optional<T> delete(C entity);
}
