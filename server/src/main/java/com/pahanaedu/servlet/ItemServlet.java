package com.pahanaedu.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.ItemDaoImpl;
import com.pahanaedu.model.Item;
import com.pahanaedu.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/items/*")
public class ItemServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final ItemDaoImpl itemDAO = (ItemDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.ITEM);
    private final ObjectMapper objectMapper = Util.getObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo(); // e.g. /123 or /search
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                // list all items with pagination
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);

                List<Item> items = itemDAO.getAll(page);
                resp.getWriter().write(objectMapper.writeValueAsString(items));
                return;
            }

            String[] splits = pathInfo.split("/");

            if (splits.length >= 2) {
                String action = splits[1];

                switch (action) {
                    case "search": { // /api/items/search?name=abc&page=1
                        String name = req.getParameter("name");
                        if (name == null || name.isBlank()) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Name parameter required\"}");
                            return;
                        }
                        int page = 1;
                        String pageParam = req.getParameter("page");
                        if (pageParam != null) page = Integer.parseInt(pageParam);

                        List<Item> items = itemDAO.searchByName(name, page);
                        resp.getWriter().write(objectMapper.writeValueAsString(items));
                        break;
                    }
                    case "category": { // /api/items/category?categoryId=1&page=1
                        String categoryParam = req.getParameter("categoryId");
                        if (categoryParam == null) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"categoryId parameter required\"}");
                            return;
                        }
                        Long categoryId = Long.parseLong(categoryParam);
                        int page = 1;
                        String pageParam = req.getParameter("page");
                        if (pageParam != null) page = Integer.parseInt(pageParam);

                        List<Item> items = itemDAO.getItemsByCategoryId(categoryId, page);
                        resp.getWriter().write(objectMapper.writeValueAsString(items));
                        break;
                    }
                    case "lowStock": { // /api/items/lowStock?threshold=5
                        String thresholdParam = req.getParameter("threshold");
                        if (thresholdParam == null) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"threshold parameter required\"}");
                            return;
                        }
                        int threshold = Integer.parseInt(thresholdParam);
                        List<Item> items = itemDAO.getLowStockItems(threshold);
                        resp.getWriter().write(objectMapper.writeValueAsString(items));
                        break;
                    }
                    default: { // treat as id /api/items/123
                        try {
                            Long id = Long.parseLong(action);
                            Item item = itemDAO.get(id);
                            if (item != null) {
                                resp.getWriter().write(objectMapper.writeValueAsString(item));
                            } else {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                resp.getWriter().write("{\"error\":\"Item not found\"}");
                            }
                        } catch (NumberFormatException e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Invalid item id\"}");
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
            Item item = Util.parseJsonBody(req, Item.class); 
            
            if (item == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Item object required\"}");
                return;
            }

            // Now check all required fields using anyNullOrEmpty safely
            if (Util.anyNullOrEmpty(
                    item.getName(),
                    item.getUnitPrice(),
                    item.getStockAvailable(),
                    item.getDiscount(),
                    item.getQtyToAllowDiscount(),
                    item.getCategoryId()
            )) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"All item parameters are required\"}");
                return;
            }
            // check duplicate
            if (itemDAO.existsByName(item.getName())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Item already exists\"}");
                return;
            }
 
            boolean created = itemDAO.create(item);

            if (created) {
                resp.setStatus(HttpServletResponse.SC_CREATED);
                resp.getWriter().write(objectMapper.writeValueAsString(item));
            } else {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Failed to create item\"}");
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
            Item item = Util.parseJsonBody(req, Item.class);
            if (item == null || item.getItemId() == null) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Item id required for update\"}");
                return;
            }

            Item existing = itemDAO.get(item.getItemId());
            if (existing == null) {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Item not found\"}");
                return;
            }
 
            if (item.getName()!= null && itemDAO.existsByNameExcludingId(item.getName(), item.getItemId())) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"Another item with this name already exists\"}");
                return;
            } 
            itemDAO.update(item);
            resp.getWriter().write(objectMapper.writeValueAsString(item));

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
                resp.getWriter().write("{\"error\":\"Item id required for delete\"}");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length >= 2) {
                String idStr = splits[1];
                try {
                    Long id = Long.parseLong(idStr);
                    boolean deleted = itemDAO.delete(id);

                    if (deleted) {
                        resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Item not found or delete failed\"}");
                    }
                } catch (NumberFormatException e) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    resp.getWriter().write("{\"error\":\"Invalid item id\"}");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
}
