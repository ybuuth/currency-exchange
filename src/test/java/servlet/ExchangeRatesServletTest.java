package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import service.ExchangeRateDao;

import static org.junit.jupiter.api.Assertions.*;

class ExchangeRatesServletTest {

    @Test
    void doGet() {

        ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
        var answer = exchangeRateDao.findAll();
        System.out.println(answer);
    }
}