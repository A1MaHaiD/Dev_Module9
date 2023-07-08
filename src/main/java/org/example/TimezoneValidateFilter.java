package org.example;

import java.io.IOException;
import java.time.*;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebFilter(value = "/time")
public class TimezoneValidateFilter extends HttpFilter {

    @Override
    protected void doFilter(HttpServletRequest request,
                            HttpServletResponse response,
                            FilterChain chain) throws IOException, ServletException {

        String timezone = request.getParameter("timezone");

        try {
            if (timezone != null) {
                String normalTimezone = timezone.replace(' ', '+');
                ZoneId zoneId = ZoneId.of(normalTimezone);
                chain.doFilter(request, response);
            } else {
                chain.doFilter(request, response);
            }
        } catch (DateTimeException exception) {
            response.setStatus(404);
            response.setContentType("text/html; charset=utf-8");
            response.getWriter().println("<h1>Invalid timezone</h1>");
            response.getWriter().close();
        }
    }
}
