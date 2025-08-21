package com.pahanaedu.servlet;

import java.io.IOException;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.model.Staff;
import com.pahanaedu.service.AnalyticsService;
import com.pahanaedu.util.AuthUtil;
import com.pahanaedu.util.Util;

@WebServlet("/api/analytics/*")
public class AnalyticsServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final AnalyticsService analyticsService = new AnalyticsService();
    private final ObjectMapper objectMapper = Util.getObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
//            Staff loggedInStaff = AuthUtil.authenticate(req, resp);
//            if (loggedInStaff == null) return;
 

            String pathInfo = req.getPathInfo(); // e.g. /dashboard or /items
            Map<String, Object> stats;

            if ("/dashboard".equals(pathInfo)) {
                stats = analyticsService.getDashboardStats();
            } else if ("/items".equals(pathInfo)) {
                stats = analyticsService.getItemAnalytics();
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Invalid analytics endpoint\"}");
                return;
            }

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.getWriter().write(objectMapper.writeValueAsString(stats));

        } catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
