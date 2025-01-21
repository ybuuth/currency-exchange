package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Currency;
import service.CurrencyDao;

import java.io.IOException;
import java.io.PrintWriter;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {

    private final CurrencyDao currencyDao = CurrencyDao.getINSTANCE();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String currencyCode = req.getPathInfo().replaceAll("/", "");

        if (currencyCode.isBlank()) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "No parameter found");
            return;
        }
        Currency currency = null;

        try {
            currency = currencyDao.findByCode(currencyCode);
        } catch (RuntimeException e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
            return;
        }

        if (currency == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "No currency found by code "+currencyCode);
            return;
        }
        PrintWriter writer = resp.getWriter();

        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(writer, currency);

        writer.println(currency);
    }
}
