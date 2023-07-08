package org.example;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templateresolver.FileTemplateResolver;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Map;
import java.util.TimeZone;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(value = "/time")

public class TimeServlet extends HttpServlet {
    private TemplateEngine engine;

    @Override
    public void init() {
        engine = new TemplateEngine();

        FileTemplateResolver resolver = new FileTemplateResolver();
        resolver.setPrefix(getServletContext().getRealPath("templates/"));
        resolver.setSuffix(".html");
        resolver.setTemplateMode("HTML5");
        resolver.setOrder(engine.getTemplateResolvers().size());
        resolver.setCacheable(false);
        engine.addTemplateResolver(resolver);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html; charset=utf-8");
        String timezone = request.getParameter("timezone");
        String normalTimezone = "";
        if (timezone != null & request.getCookies() == null) {
            normalTimezone = timezone.replace(' ', '+');
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(normalTimezone));

            String result = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Cookie responceCookie = new Cookie("lastTimeZone", normalTimezone);
            response.addCookie(responceCookie);
            Context context = new Context(
                    request.getLocale(),
                    Map.of("time", result,
                            "lastTimeZone", normalTimezone)
            );
            engine.process("time", context, response.getWriter());
            response.getWriter().close();
        } else if (timezone != null & request.getCookies() != null) {
            normalTimezone = timezone.replace(' ', '+');
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of(normalTimezone));

            String result = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Cookie responceCookie = new Cookie("lastTimeZone", normalTimezone);
            response.addCookie(responceCookie);
            Context context = new Context(
                    request.getLocale(),
                    Map.of("time", result,
                            "lastTimeZone", normalTimezone)
            );
            engine.process("time", context, response.getWriter());
            response.getWriter().close();
        }

        if (timezone == null & request.getCookies() != null) {
            Cookie[] cookies = request.getCookies();
            normalTimezone = cookies[cookies.length - 1].getValue();
            LocalDateTime currentTime = LocalDateTime.now(ZoneId.of("Europe/Kyiv"));
            String result = currentTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            Context context = new Context(
                    request.getLocale(),
                    Map.of("time", result,
                            "lastTimeZone", normalTimezone)
            );
            engine.process("time", context, response.getWriter());
            response.getWriter().close();
        }
        if (timezone == null & request.getCookies() == null) {

            Calendar calendar = Calendar.getInstance();
            DateFormat formatterUTC = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
            formatterUTC.setTimeZone(TimeZone.getTimeZone("UTC")); // UTC timezone
            String format = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            String[] split = calendar.getTimeZone().toZoneId().getRules().toString().split("=");
            String replace = split[1].replace(']', ' ').replace('+', ' ').trim();
            timezone = format + " " + "UTC " + replace;

            Cookie responceCookie = new Cookie("lastTimeZone", replace);
            response.addCookie(responceCookie);
            Context context = new Context(
                    request.getLocale(),
                    Map.of("time", timezone,
                            "lastTimeZone", normalTimezone)
            );
            engine.process("time", context, response.getWriter());
            response.getWriter().close();
        }
    }
}

