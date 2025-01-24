package service;

import exception.DatabaseException;
import DTO.ConvertedRate;
import model.Currency;
import model.ExchangeRate;
import util.DatabaseConnector;

import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ExchangeRateDao implements Dao<ExchangeRate, Integer> {

    private final DatabaseConnector connector;

    private final String QUERY_FIND_ALL = """
            Select er.id, c.id base_id,c.code base_code, c.full_name as base_name, c.sign base_sign, c1.id target_id, c1.code code_id, c1.full_name target_name,\s
                                        c1.sign target_sign, er.rate from exchange_rates er join currencies as c on er.base_currency_id = c.id join currencies as c1 on er.target_currency_id=c1.id
            """;
    private final String QUERY_FIND_BY_PAIR = """
            Select er.id, c.*, c1.*, er.rate from exchange_rates er join currencies as c on er.base_currency_id = c.id join currencies as c1 on er.target_currency_id=c1.id
            where\s
            base_currency_id in (select id from currencies where code=?1)
            and target_currency_id in (select id from currencies where code=?2)
            """;
    private final String QUERY_SAVE_EXCHANGE_RATE = """
            insert into exchange_rates (base_currency_id, target_currency_id, rate)
            values (?, ?, ?)
            """;
    private final String QUERY_FIND_BY_ID = """
            Select er.id, c.*, c1.*, er.rate from exchange_rates er join currencies as c on er.base_currency_id = c.id join currencies as c1 on er.target_currency_id=c1.id
            where\s
            er.id=?
            """;
    private final String QUERY_UPDATE_BY_ID = """
            UPDATE exchange_rates 
            set rate = ?1
            where id=?2
            """;
    private final String QUERY_FIND_EXCHANGE_RATE_BY_PAIR_CODE = """
            select c.*,c1.*, er.rate, 1 as priority from exchange_rates er\s
            join currencies c on er.base_currency_id=c.id
            join currencies c1 on er.target_currency_id=c1.id
            where c.code = ?1 and c1.code=?2
            union all
            select c.*,c1.*,Round(1/er.rate, 6), 2 as priority from exchange_rates er
            join currencies c on er.base_currency_id=c.id
            join currencies c1 on er.target_currency_id=c1.id
            where c.code = ?2 and c1.code = ?1
            union all
            select c.*,c1.*, Round(er2.rate / er1.rate, 6) as rate , 3 as priority  from exchange_rates er1 join exchange_rates er2 on er1.base_currency_id = er2.base_currency_id
            join currencies c on er1.target_currency_id=c.id
            join currencies c1 on er2.target_currency_id=c1.id
            where c.code = ?1 and c1.code=?2
            limit 1
            """;

    public ExchangeRateDao(DatabaseConnector connector) {
        this.connector = connector;
    }

    @Override
    public List<ExchangeRate> findAll() {

        List<ExchangeRate> rates = new ArrayList<>();

        try (Connection connection = connector.getConnection()){

            PreparedStatement statement = connection.prepareStatement(QUERY_FIND_ALL);
            ResultSet set = statement.executeQuery();

            while (set.next()) {
                rates.add(getExchangeRate(set));
            }
            return rates;
        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }

    }

    public ExchangeRate findByCurrencyPairCode(String code1, String code2) {

        try (Connection connection = connector.getConnection()) {

            PreparedStatement statement = connection.prepareStatement(QUERY_FIND_BY_PAIR);
            statement.setString(1, code1);
            statement.setString(2, code2);

            ResultSet set = statement.executeQuery();

            if (set.next()) {
                return getExchangeRate(set);
            }
            return null;
        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    public ExchangeRate saveExchangeRate(Integer baseCurrencyId, Integer targetCurrencyId, BigDecimal rate) {

        try (PreparedStatement statement = connector.getConnection().prepareStatement(
                QUERY_SAVE_EXCHANGE_RATE,
                Statement.RETURN_GENERATED_KEYS);
             PreparedStatement  statement2 = connector.getConnection().prepareStatement(QUERY_FIND_BY_ID)) {

            statement.setInt(1, baseCurrencyId);
            statement.setInt(2, targetCurrencyId);
            statement.setBigDecimal(3, rate);

            statement.executeUpdate();
            ResultSet set = statement.getGeneratedKeys();

            if (set.next()) {
                statement2.setInt(1, set.getInt(1));
            }

            set = statement2.executeQuery();

            if (set.next()) {
                return getExchangeRate(set);
            }
            return null;

        } catch (SQLException | ClassNotFoundException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public ExchangeRate findById(Integer Id) {
        return null;
    }

    @Override
    public ExchangeRate findByCode(String code) {
        return null;
    }
    public ExchangeRate update(int id, BigDecimal rate)  {

        try (PreparedStatement statement = connector.getConnection().prepareStatement(
                QUERY_UPDATE_BY_ID);
             PreparedStatement statement2 = connector.getConnection().prepareStatement(QUERY_FIND_BY_ID)) {

            statement.setBigDecimal(1, rate);
            statement.setInt(2, id);

            statement.executeUpdate();

            statement2.setInt(1, id);
            ResultSet set1 = statement2.executeQuery();

            return getExchangeRate(set1);

        } catch (Exception e) {
            throw new DatabaseException(e);
        }
    }

    public ConvertedRate findConvertedRateByPairCode(String baseCode, String targetCode, BigDecimal amount) throws SQLException, ClassNotFoundException {

        PreparedStatement statement = connector.getConnection().prepareStatement(QUERY_FIND_EXCHANGE_RATE_BY_PAIR_CODE);
        statement.setString(1, baseCode);
        statement.setString(2, targetCode);

        ResultSet set = statement.executeQuery();
        if (set.next()) {
            return new ConvertedRate(new Currency(set.getInt(1),
                    set.getString(2),
                    set.getString(3),
                    set.getString(4)),
                    new Currency(set.getInt(5),
                            set.getString(6),
                            set.getString(7),
                            set.getString(8)),
                    set.getBigDecimal(9),
                    amount);
        }
        return null;
    }

    private ExchangeRate getExchangeRate(ResultSet set) {
        try {
            return new ExchangeRate(set.getInt(1),

                    new Currency(set.getInt(2), set.getString(3), set.getString(4), set.getString(5)),
                    new Currency(set.getInt(6), set.getString(7), set.getString(8), set.getString(9)),
                    set.getBigDecimal(10));
        } catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

}
