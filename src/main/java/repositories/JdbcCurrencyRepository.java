package repositories;

import models.Currency;
import utils.DatabaseManager;

import java.io.PrintWriter;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {

    @Override
    public List<Currency> findAll() throws SQLException {
        String query = "SELECT * FROM Currencies";

        List<Currency> list = new ArrayList<>();

        try(Connection connection = DatabaseManager.getConnection()){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            long id = 1L;

            while(resultSet.next()){
                String code = resultSet.getString(2);
                String fullName = resultSet.getString(3);
                String sign = resultSet.getString(4);
                Currency currency = new Currency(code, fullName, sign);
                currency.setId(id++);
                list.add(currency);
            }
        }
        return list;
    }


    @Override
    public Currency findByCode(String code) throws SQLException {
        return null;
    }

    @Override
    public void create(Currency entity) throws SQLException {

    }

    @Override
    public Optional<Currency> findById(int id) throws SQLException {
        return Optional.empty();
    }


    @Override
    public void delete(Currency entity) throws SQLException {

    }

    @Override
    public Integer save(Currency entity) throws SQLException {
        return 0;
    }
}
