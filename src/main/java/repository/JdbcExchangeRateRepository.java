package repository;

import model.Currency;
import model.ExchangeRate;
import util.DatabaseManager;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class JdbcExchangeRateRepository implements ExchangeRateRepository{
    private static final String QUERY_FIND_ALL = "SELECT er.id AS id, " +
            "bc.id AS base_id, " +
            "bc.name AS base_name, " +
            "bc.code AS base_code, " +
            "bc.sign AS base_sign, " +
            "tc.id AS target_id, " +
            "tc.name AS target_name, " +
            "tc.code AS target_code, " +
            "tc.sign AS target_sign, " +
            "er.Rate AS rate " +
            "FROM ExchangeRates er " +
            "JOIN Currencies bc ON er.baseCurrencyId = bc.id " +
            "JOIN Currencies tc ON er.targetCurrencyId = tc.id ";

    private static final String QUERY_TWO_CODES = " WHERE bc.code = ? AND tc.code = ? ";

    @Override
    public Optional<ExchangeRate> findByTwoCodes (String codeBase, String codeTarget) throws SQLException {
        String query = QUERY_FIND_ALL + QUERY_TWO_CODES;

        ExchangeRate exchangeRate = new ExchangeRate();
        try (Connection connection = DatabaseManager.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1,codeBase);
            preparedStatement.setString(2,codeTarget);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return Optional.empty();
            }

            exchangeRate = toEntity(resultSet);
        }

        return Optional.of(exchangeRate);
    }

    @Override
    public void update(ExchangeRate entity) throws SQLException {
        String query = "UPDATE ExchangeRates SET Rate = ? WHERE id = ?";

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query);
            preparedStatement.setBigDecimal(1, entity.getRate());
            preparedStatement.setLong(2, entity.getId());

            int affectedRow = preparedStatement.executeUpdate();

            if(affectedRow == 0){
                throw new RuntimeException("No affected row !");
            }

        }
    }

    @Override
    public Optional<ExchangeRate> findById(long id) throws SQLException {
        return Optional.empty();
    }

    @Override
    public List<ExchangeRate> findAll() throws SQLException {
        List<ExchangeRate> list = new ArrayList<>();

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(QUERY_FIND_ALL);
            ResultSet resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                ExchangeRate exchangeRate = toEntity(resultSet);
                list.add(exchangeRate);
            }
        }
        return list;
    }


    @Override
    public void delete(ExchangeRate entity) throws SQLException {

    }

    @Override
    public Long save(ExchangeRate entity) throws SQLException {
        String query = "INSERT INTO ExchangeRates (baseCurrencyId, targetCurrencyId, Rate) VALUES (?,?,?)";

        long id = 0L;

        try(Connection connection = DatabaseManager.getConnection()){
            PreparedStatement preparedStatement = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);

            preparedStatement.setLong(1,entity.getBaseCurrency().getId());
            preparedStatement.setLong(2, entity.getTargetCurrency().getId());
            preparedStatement.setBigDecimal(3,entity.getRate());

            int affectedRow = preparedStatement.executeUpdate();

            if(affectedRow == 0){
                throw new RuntimeException("No affected row !");
            }

            ResultSet resultSet = preparedStatement.getGeneratedKeys();
            if(resultSet.next()){
                id = resultSet.getLong(1);
            }
        }

        return id;
    }
    public static ExchangeRate toEntity (ResultSet resultSet) throws SQLException {
        ExchangeRate exchangeRate = new ExchangeRate();

        exchangeRate.setId(resultSet.getLong("id"));

        Currency base = new Currency(resultSet.getString("base_code"),
                resultSet.getString("base_name"),
                resultSet.getString("base_sign"));
        base.setId(resultSet.getLong("base_id"));

        exchangeRate.setBaseCurrency(base);

        Currency target = new Currency(resultSet.getString("target_code"),
                resultSet.getString("target_name"),
                resultSet.getString("target_sign"));
        target.setId(resultSet.getLong("target_id"));

        exchangeRate.setTargetCurrency(target);

        exchangeRate.setRate(resultSet.getBigDecimal("rate"));

        return exchangeRate;
    }
}
