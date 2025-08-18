package com.pahanaedu.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.CategoryDaoImpl;
import com.pahanaedu.model.Category;
import com.pahanaedu.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/categories/*")
public class CategoryServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final CategoryDaoImpl categoryDAO = (CategoryDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.CATEGORY);
    private final ObjectMapper objectMapper = Util.getObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo(); // e.g. /123 or /search
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                // list all categories with pagination
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);

                List<Category> categories = categoryDAO.getAll(page);
                resp.getWriter().write(objectMapper.writeValueAsString(categories));
                return;
            }

            String[] splits = pathInfo.split("/");

            if (splits.length >= 2) {
                String action = splits[1];

                switch (action) {
                    case "search": { // /api/categories/search?name=abc
                        String name = req.getParameter("name");
                        if (name == null || name.isBlank()) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Name parameter required\"}");
                            return;
                        }
                        List<Category> categories = categoryDAO.searchByName(name);
                        resp.getWriter().write(objectMapper.writeValueAsString(categories));
                        break;
                    }
                    case "recent": { // /api/categories/recent?limit=5
                        int limit = 5;
                        String limitParam = req.getParameter("limit");
                        if (limitParam != null) limit = Integer.parseInt(limitParam);

                        List<Category> categories = categoryDAO.getRecentlyUpdated(limit);
                        resp.getWriter().write(objectMapper.writeValueAsString(categories));
                        break;
                    }
                    case "withMinItems": { // /api/categories/withMinItems?min=2
                        String minParam = req.getParameter("min");
                        if (minParam == null) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"min parameter required\"}");
                            return;
                        }
                        int min = Integer.parseInt(minParam);
                        List<Category> categories = categoryDAO.getCategoriesWithMinItems(min);
                        resp.getWriter().write(objectMapper.writeValueAsString(categories));
                        break;
                    }
                    default: { // treat as id /api/categories/123
                        try {
                            Long id = Long.parseLong(action);
                            Category category = categoryDAO.get(id);
                            if (category != null) {
                                resp.getWriter().write(objectMapper.writeValueAsString(category));
                            } else {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                resp.getWriter().write("{\"error\":\"Category not found\"}");
                            }
                        } catch (NumberFormatException e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Invalid category id\"}");
                        }
                        break;
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Category category = Util.parseJsonBody(req, Category.class); 
            if (category == null || category.getName() == null || category.getName().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Category name required\"}");
                return;
            }

            // check duplicate
            if (categoryDAO.existsByName(category.getName())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Category already exists\"}");
                return;
            }
 
            boolean created = categoryDAO.create(category);

            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(category));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Failed to create category\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            Category category = Util.parseJsonBody(req, Category.class);
            if (category == null || category.getCategoryId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Category id required for update\"}");
                return;
            }

            Category existing = categoryDAO.get(category.getCategoryId());
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Category not found\"}");
                return;
            }
            if (category == null || category.getName() == null || category.getName().isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Category name required\"}");
                return;
            }
            if (categoryDAO.existsByNameExcludingId(category.getName(), category.getCategoryId())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Another category with this name already exists\"}");
                return;
            } 
            categoryDAO.update(category);
            resp.getWriter().write(objectMapper.writeValueAsString(category));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Category id or name required for delete\"}");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length >= 2) {
                String identifier = splits[1];

                boolean deleted = false;
                try {
                    // delete by id
                    Long id = Long.parseLong(identifier);
                    deleted = categoryDAO.delete(id);
                } catch (NumberFormatException e) {
                    // delete by name
                    deleted = categoryDAO.deleteByName(identifier);
                }

                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Category not found or delete failed\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
}
