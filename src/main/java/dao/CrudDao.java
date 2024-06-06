package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, ID> {
    List<T> findAll();

    Optional<T> findById(ID id);

    Optional<T> save(T entity);

    Optional<T> delete(T entity);
}
