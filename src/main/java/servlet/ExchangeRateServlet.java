package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import exception.DatabaseException;
import jakarta.servlet.ServletConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ExchangeRate;
import service.CurrencyDao;
import service.ExchangeRateDao;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {

    private ExchangeRateDao exchangeRateDao;
    private final ObjectMapper objectMapper = new ObjectMapper();


    @Override
    public void init(ServletConfig config) {
        exchangeRateDao = (ExchangeRateDao) config.getServletContext().getAttribute("exchangeRateDao");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<String> codes = getParametersFromQuery(req, resp);

            ExchangeRate rate = exchangeRateDao.findByCurrencyPairCode(codes.get(0), codes.get(1));

            if (rate == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            objectMapper.writeValue(resp.getWriter(), rate);

            resp.getWriter().println(rate);
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (DatabaseException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws IOException {

        try {
            List<String> codes = getParametersFromQuery(req, resp);

            Map<String, String> parameters = new HashMap<>();
            try(BufferedReader reader = req.getReader()) {
                String line;
                while ((line = reader.readLine()) != null){
                    String[] splited = line.split("=");
                    parameters.put(splited[0], splited[1]);
                }
            }

            ExchangeRate exchangeRate = exchangeRateDao.findByCurrencyPairCode(codes.get(0), codes.get(1));

            if (exchangeRate == null) {
                resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No exchange rate found by received currency code");
                return;
            }

            if (!parameters.containsKey("rate")) {
                throw new ServletException("Parameter rate required");
            }

            exchangeRate =  exchangeRateDao.update(exchangeRate.getId(),
                    BigDecimal.valueOf(Double.parseDouble(parameters.get("rate"))));

            objectMapper.writeValue(resp.getWriter(), exchangeRate);
            resp.getWriter().println(exchangeRate);

        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    private List<String> getParametersFromQuery(HttpServletRequest req, HttpServletResponse resp) throws ServletException {

        List<String> params = new ArrayList<>();

        String codes = req.getPathInfo().replaceAll("/", "");

        if (codes.length() != 6) {
            throw new ServletException("The length of the \"codes\" string should be 6");
        }

        params.add(codes.substring(0, 3));
        params.add(codes.substring(3));

        return params;
    }
}
