package servlet;


import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyDao;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {

    private CurrencyDao currencyDao;

    @Override
    public void init(ServletConfig config) throws ServletException {
        currencyDao = (CurrencyDao) config.getServletContext().getAttribute("currencyDao");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        List<Currency> currencies = null;

        try {
            currencies = currencyDao.findAll();
            PrintWriter writer = resp.getWriter();

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(writer, currencies);

            writer.println(currencies);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        Currency currency = new Currency();

        for (Map.Entry<String, String[]> entry : req.getParameterMap().entrySet()) {
            String k = entry.getKey();
            String[] v = entry.getValue();
            if (k.equals("name")) {
                currency.setName(Arrays.stream(v).findFirst().get());
            } else if (k.equals("code")) {
                currency.setCode(Arrays.stream(v).findFirst().get());
            } else if (k.equals("sign")) {
                currency.setSign(Arrays.stream(v).findFirst().get());
            }
        }

        if (currency.getCode()==null || currency.getSign()==null || currency.getName()==null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "all parameters are required: code, sign, name");
            return;
        }

        if (currencyDao.findByCode(currency.getCode()) != null) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Currency already exists");
            return;
        }

        try {
            currency = currencyDao.save(currency);

            ObjectMapper mapper = new ObjectMapper();
            mapper.writeValue(resp.getWriter(), currency);

            resp.getWriter().println(currency);

        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }

    }
}
