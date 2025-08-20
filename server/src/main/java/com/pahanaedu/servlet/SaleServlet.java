package com.pahanaedu.servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.SaleDaoImpl;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.dto.SaleRequestDTO;
import com.pahanaedu.model.Customer;
import com.pahanaedu.model.Sale;
import com.pahanaedu.service.SaleService;
import com.pahanaedu.util.Util;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/api/sales/*")
public class SaleServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final SaleDaoImpl saleDAO = (SaleDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.SALE);
    private final ObjectMapper objectMapper = Util.getObjectMapper();
    private final SaleService saleService = new SaleService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String pathInfo = req.getPathInfo();
            resp.setContentType("application/json");

            if (pathInfo == null || pathInfo.equals("/")) {
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);

                PaginatedResponse<Sale> sales = saleDAO.getAll(page);
                resp.getWriter().write(objectMapper.writeValueAsString(sales));
                return;
            }

            String[] splits = pathInfo.split("/");

            if (splits.length >= 2) {
                String action = splits[1];

                switch (action) {
                    case "search": {
                        String customerTelephone = req.getParameter("customerTele");
                        if (Util.anyNullOrEmpty(customerTelephone)) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"customer parameter required\"}");
                            return;
                        }
                        int page = 1;
                        String pageParam = req.getParameter("page");
                        if (pageParam != null) page = Integer.parseInt(pageParam);

                        Customer customer = new Customer("",customerTelephone,"");
                        
                        List<Sale> sales = saleDAO.getSalesByCustomer(customer, page);
                        resp.getWriter().write(objectMapper.writeValueAsString(sales));
                        break;
                    }
                    default: { // treat as id /api/sales/123
                        try {
                            Long id = Long.parseLong(action);
                            Sale sale = saleDAO.get(id);
                            if (sale != null) {
                                resp.getWriter().write(objectMapper.writeValueAsString(sale));
                            } else {
                                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                                resp.getWriter().write("{\"error\":\"Sale not found\"}");
                            }
                        } catch (NumberFormatException e) {
                            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                            resp.getWriter().write("{\"error\":\"Invalid sale id\"}");
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
            // 🔹 Parse request JSON into SaleRequest DTO
            SaleRequestDTO saleRequest = Util.parseJsonBody(req, SaleRequestDTO.class);
            if (saleRequest == null || Util.anyNullOrEmpty(saleRequest.getCustomer(),saleRequest.getSaleItems())) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid sale request\"}");
                return;
            }

            // 🔹 Call service layer (handles customer creation, discounts, stock, totals, commit/rollback)
            Sale createdSale = saleService.createSale(
                saleRequest.getCustomer(),
                saleRequest.getSaleItems()
            );

            resp.setStatus(HttpServletResponse.SC_CREATED);
            resp.getWriter().write(objectMapper.writeValueAsString(createdSale));

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            String idParam = req.getParameter("id");
            String paidParam = req.getParameter("paid");
            String balanceParam = req.getParameter("balance");

            if (Util.anyNullOrEmpty(idParam == null , paidParam == null , balanceParam == null)) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"id, paid, and balance parameters required\"}");
                return;
            }

            Long saleId = Long.parseLong(idParam);
            double paid = Double.parseDouble(paidParam);
            double balance = Double.parseDouble(balanceParam);

            boolean updated = saleDAO.updatePayment(saleId, paid, balance);

            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write("{\"message\":\"Payment updated successfully\"}");
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Sale not found or update failed\"}");
            }

        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }
 
}
