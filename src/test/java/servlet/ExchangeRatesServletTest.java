package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import service.ExchangeRateDao;
import util.DatabaseConnector;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRatesServletTest {

    @Test
    void doGet() {

        DatabaseConnector databaseConnector = new DatabaseConnector();
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao(databaseConnector);
        var answer = exchangeRateDao.findAll();
        System.out.println(answer);
    }
}