package service;

import exception.DatabaseException;
import lombok.Data;
import model.Currency;
import util.DatabaseConnector;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Data
public class CurrencyDao implements Dao<Currency, Integer> {

    private final DatabaseConnector connector;

    private final String QUERY_FIND_ALL = """
            Select * from currencies
            """;
    private final String QUERY_FIND_BY_CODE = """
            Select * from currencies where code = ?1
            """;
    private final String QUERY_SAVE_CURRENCY = """
            insert into currencies (code, full_name, sign)
            values (?, ?, ?)
            """;
    private final String QUERY_GET_PAIR_BY_CODE = """
            select c1.id, c1.code from currencies c1 where c1.code=?1
            union all
            select c1.id, c1.code from currencies c1 where c1.code=?2
            """;

    public CurrencyDao(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public List<Currency> findAll() {

        List<Currency> currencies = new ArrayList<>();

        try (Connection connection = connector.getConnection()){

            PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL);
            ResultSet resultSet = statement.executeQuery();

            while (resultSet.next()) {

                Currency currency = getCurrencyFromResultSet(resultSet);
                currencies.add(currency);

            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }

        return currencies;
    }

    @Override
    public Currency findById(Integer Id) {
        return null;
    }

    @Override
    public Currency findByCode(String code) {

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(QUERY_FIND_BY_CODE);
            statement.setString(1, code);
            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return getCurrencyFromResultSet(set);
            }

        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
        return null;
    }

    public Map<String, Integer> findIdByCode(String code1, String code2) {

        Map<String, Integer> ids = new HashMap<>();

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(QUERY_GET_PAIR_BY_CODE);
            statement.setString(1, code1);
            statement.setString(2, code2);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                ids.put(set.getString(2), set.getInt(1));
            }
            return ids;
        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public Currency save(Currency currency) {
        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(QUERY_SAVE_CURRENCY);
            statement.setString(1, currency.getCode());
            statement.setString(2, currency.getName());
            statement.setString(3, String.valueOf(currency.getSign()));

            int num = statement.executeUpdate();
            if (num >0) {
                return findByCode(currency.getCode());
            }

        } catch (RuntimeException | SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        return null;
    }

    public Currency getCurrencyFromResultSet(ResultSet resultSet) throws SQLException {
        return new Currency(resultSet.getInt(1),
                resultSet.getString(2),
                resultSet.getString(3),
                resultSet.getString(4));
    }
}
