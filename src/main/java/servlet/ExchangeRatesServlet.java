package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import service.CurrencyDao;
import service.ExchangeRateDao;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.fasterxml.jackson.databind.type.LogicalType.Map;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final CurrencyDao currencyDao = CurrencyDao.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");

        List<ExchangeRate>rates = exchangeRateDao.findAll();
         objectMapper.writeValue(resp.getWriter(), rates);

         resp.getWriter().println(rates);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");
        Map<String, String[]> parameters = req.getParameterMap();

        String baseCurrencyCode = null;
        String targetCurrencyCode = null;
        BigDecimal rate = null;

        try {
            baseCurrencyCode = parameters.get("baseCurrencyCode")[0].toUpperCase();
            targetCurrencyCode = parameters.get("targetCurrencyCode")[0].toUpperCase();
            rate = BigDecimal.valueOf(Double.parseDouble(parameters.get("rate")[0]));
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Expected fields: baseCurrencyCode, targetCurrencyCode, rate");
            return;
        }

        if (baseCurrencyCode.equals(targetCurrencyCode)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "baseCurrencyCode, targetCurrencyCode should be different");
            return;
        }

        Map<String, Integer> ids = new HashMap<>();
        try {
             ids =currencyDao.findIdByCode(baseCurrencyCode, targetCurrencyCode);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        if (!ids.containsKey(baseCurrencyCode)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, baseCurrencyCode +" not exists");
            return;
        }

        if (!ids.containsKey(targetCurrencyCode)) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, targetCurrencyCode + " not exists");
            return;
        }

        if (exchangeRateDao.findByCurrencyPairCode(baseCurrencyCode, targetCurrencyCode) != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, " Currency pair already exists");
            return;
        }

        try {
            ExchangeRate rate1 = exchangeRateDao.saveExchangeRate(ids.get(baseCurrencyCode), ids.get(targetCurrencyCode), rate);

            objectMapper.writeValue(resp.getWriter(), rate1);

            resp.getWriter().println(rate1);

            resp.setStatus(HttpServletResponse.SC_CREATED);

        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
