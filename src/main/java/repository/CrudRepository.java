package repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void update(T entity);

    Optional<T> findById(long id);

    List<T> findAll();

    void delete(T entity);

    Long save (T entity);
}
