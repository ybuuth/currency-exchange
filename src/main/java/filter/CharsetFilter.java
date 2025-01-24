package filter;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

@WebFilter("/*")
public class CharsetFilter implements Filter {
    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        servletRequest.setCharacterEncoding(StandardCharsets.UTF_8);
        servletResponse.setCharacterEncoding(StandardCharsets.UTF_8);
        servletResponse.setContentType("text/html");

        filterChain.doFilter(servletRequest, servletResponse);
    }
}
