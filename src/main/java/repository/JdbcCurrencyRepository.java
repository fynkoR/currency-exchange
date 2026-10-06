package repository;

import exception.CurrencyAlreadyExistsException;
import exception.DatabaseException;
import model.Currency;
import org.sqlite.SQLiteErrorCode;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcCurrencyRepository implements CurrencyRepository {

    @Override
    public List<Currency> findAll(){
        String query = "SELECT * FROM Currencies";

        List<Currency> list = new ArrayList<>();

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                Currency currency = toEntity(resultSet);
                list.add(currency);
            }
        } catch (SQLException e){
            throw new DatabaseException("Failed to find all currencies", e);
        }


        return list;
    }

    @Override
    public Optional<Currency> findByCode(String code){
        String query = "SELECT id, code, name, sign" +
                " FROM Currencies" +
                " WHERE code = ?";

        Currency currency = new Currency();

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,code);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                return Optional.empty();
            }

            currency = toEntity(resultSet);
        } catch (SQLException e){
            throw new DatabaseException("Failed to find by code currency !", e);
        }
        return Optional.of(currency);
    }

    @Override
    public Long save(Currency entity){
        String query = "INSERT INTO Currencies (code,name,sign) VALUES (?,?,?)";

        long id = 0L;

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setString(1, entity.getCode());
            preparedStatement.setString(2, entity.getName());
            preparedStatement.setString(3, entity.getSign());

            int affectedRow = preparedStatement.executeUpdate();

            if(affectedRow == 0){
                throw new RuntimeException("No affected row !");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                id = resultSet.getLong(1);
            }
        } catch (SQLException e){
            if(e.getErrorCode() == SQLiteErrorCode.SQLITE_CONSTRAINT.code){
                throw new CurrencyAlreadyExistsException(entity.getCode());
            }
            else{
                throw new DatabaseException("Failed to save currency", e);
            }
        }
        return id;
    }

    @Override
    public Optional<Currency> findById(long id){
        String query = "SELECT id,code,name,sign" +
                " FROM Currencies" +
                " WHERE id = ?";

        Currency currency = new Currency();
        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setLong(1, id);
            ResultSet resultSet = preparedStatement.executeQuery();

            if(!resultSet.next()){
                return Optional.empty();
            }

            currency = toEntity(resultSet);

        } catch (SQLException e){
            throw new DatabaseException("Failed find by id currency", e);
        }
        return Optional.of(currency);
    }

    @Override
    public void update(Currency entity){

    }


    @Override
    public void delete(Currency entity){

    }

    public static Currency toEntity(ResultSet resultSet) throws SQLException {
        Currency currency = new Currency();
        long id = resultSet.getLong(1);
        String code = resultSet.getString(2);
        String name = resultSet.getString(3);
        String sign = resultSet.getString(4);

        currency.setId(id);
        currency.setCode(code);
        currency.setName(name);
        currency.setSign(sign);

        return currency;
    }
}
