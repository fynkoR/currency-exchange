package repositories;

import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public interface CrudRepository<T> {
    void create(T entity) throws SQLException;

    Optional<T> findById(int id) throws SQLException;

    List<T> findAll() throws SQLException;

    void delete(T entity) throws SQLException;

    Integer save (T entity) throws SQLException;
}
