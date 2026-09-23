package repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void update(T entity) throws SQLException;

    Optional<T> findById(long id) throws SQLException;

    List<T> findAll() throws SQLException;

    void delete(T entity) throws SQLException;

    Long save (T entity) throws SQLException;
}
