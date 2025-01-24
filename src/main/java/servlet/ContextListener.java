package servlet;

import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;
import service.CurrencyDao;
import service.ExchangeRateDao;
import util.DatabaseConnector;

import java.sql.SQLException;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        ServletContext context = sce.getServletContext();

        DatabaseConnector databaseConnector = new DatabaseConnector();
        CurrencyDao currencyDao = new CurrencyDao(databaseConnector);
        ExchangeRateDao exchangeRateDao = new ExchangeRateDao(databaseConnector);

        try {
            databaseConnector.enableForeignKeys();
        } catch (SQLException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }

        context.setAttribute("currencyDao", currencyDao);
        context.setAttribute("databaseConnector", databaseConnector);
        context.setAttribute("exchangeRateDao", exchangeRateDao);
    }

}
