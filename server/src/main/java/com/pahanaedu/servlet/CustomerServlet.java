package com.pahanaedu.servlet;
 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dao.CustomerDAO;
import com.pahanaedu.model.Customer;
import com.pahanaedu.util.JsonUtil;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    

    private CustomerDAO customerDAO = new CustomerDAO();


    private ObjectMapper objectMapper = JsonUtil.getObjectMapper(); // For JSON

   
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo(); // e.g., /123 or /search or /top
        resp.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) {
            // List all customers with pagination (e.g. /api/customers)
            List<Customer> customers = customerDAO.getAllCustomers();
            resp.getWriter().write(objectMapper.writeValueAsString(customers));
            return;
        }

        String[] splits = pathInfo.split("/");
        if (splits.length >= 2) {
            String action = splits[1];

            switch (action) {
                case "search": {
                    // /api/customers/search?name=John
                    String name = req.getParameter("name");
                   
                    List<Customer> customers = customerDAO.getCustomersByName(name);
                    resp.getWriter().write(objectMapper.writeValueAsString(customers));
                    break;
                }
                case "telephone": {
                    // /api/customers/telephone?number=123456789
                    String number = req.getParameter("number");
                    Customer customer = customerDAO.getCustomerByTelephone(number);
                    if (customer != null) {
                        resp.getWriter().write(objectMapper.writeValueAsString(customer));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Customer not found\"}");
                    }
                    break;
                } 
                default: {
                    // Assume it's an id /api/customers/123
                    try {
                        Long id = Long.parseLong(action);
                        Customer customer = customerDAO.getCustomerById(id);
                        if (customer != null) {
                            resp.getWriter().write(objectMapper.writeValueAsString(customer));
                        } else {
                            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                            resp.getWriter().write("{\"error\":\"Customer not found\"}");
                        }
                    } catch (NumberFormatException e) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                        resp.getWriter().write("{\"error\":\"Invalid customer id\"}");
                    }
                    break;
                }
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Add new customer
        Customer customer = objectMapper.readValue(req.getReader(), Customer.class);
 
        // Set default isActive if not set
        if (customer.getIsActive() == null) {
            customer.setIsActive(true);
        }

        customerDAO.createCustomer(customer);
        resp.setStatus(HttpServletResponse.SC_CREATED);
        resp.getWriter().write(objectMapper.writeValueAsString(customer));
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Update existing customer - expects full Customer JSON with valid ID
        Customer customer = objectMapper.readValue(req.getReader(), Customer.class);
        if (customer.getId() == null) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer id is required for update\"}");
            return;
        }
        Customer existing = customerDAO.getCustomerById(customer.getId());
        if (existing == null) {
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            resp.getWriter().write("{\"error\":\"Customer not found\"}");
            return;
        }

        

        customerDAO.updateCustomer(customer);
        resp.getWriter().write(objectMapper.writeValueAsString(customer));
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // Delete by id: /api/customers/{id}
        String pathInfo = req.getPathInfo();
        if (pathInfo == null || pathInfo.equals("/")) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer id required\"}");
            return;
        }
        String[] splits = pathInfo.split("/");
        if (splits.length < 2) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Customer id required\"}");
            return;
        }
        try {
            Long id = Long.parseLong(splits[1]);
            customerDAO.deleteCustomer(id);
            resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid customer id\"}");
        }
    }
}
