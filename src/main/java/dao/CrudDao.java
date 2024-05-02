package dao;

import java.util.List;
import java.util.Optional;

public interface CrudDao<T, C> {
    List<T> findAll();

    Optional<T> findByCode(C code);

    void save(T entity);

    //Optional<T> findById(int id);

    //void update(T entity);

    //void delete(T entity);
}
