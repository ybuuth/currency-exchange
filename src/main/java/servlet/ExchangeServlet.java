package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ConvertedRate;
import service.ExchangeRateDao;

import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {

    private final ExchangeRateDao exchangeRateDao = ExchangeRateDao.getInstance();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        resp.setContentType("text/html;charset=UTF-8");

        try {
            for (Map.Entry<String, String[]> entry: req.getParameterMap().entrySet()) {
                if (Arrays.asList(entry.getValue()).contains("")) {
                    throw new ServletException(String.format("Parameter %s required", entry.getKey()));
                }
            }
        } catch (ServletException e) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        String baseCode = req.getParameterMap().get("from")[0].toUpperCase();
        String targetCode = req.getParameterMap().get("to")[0].toUpperCase();
        Double amount = Double.parseDouble(req.getParameterMap().get("amount")[0]);

        try {
            ConvertedRate convertedRate = exchangeRateDao.findConvertedRateByPairCode(baseCode, targetCode, amount);
            objectMapper.writeValue(resp.getWriter(), convertedRate);

            resp.getWriter().println(convertedRate);
        } catch (Exception e) {
            resp.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }
}
