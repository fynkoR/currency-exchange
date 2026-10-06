package repository;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void update(T entity);

    List<T> findAll();

    Long save (T entity);
}
