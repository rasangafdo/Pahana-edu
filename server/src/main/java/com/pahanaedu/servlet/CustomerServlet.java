package com.pahanaedu.servlet;
 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pahanaedu.dto.PaginatedResponse;
import com.pahanaedu.model.Customer;
import com.pahanaedu.service.CustomerService;
import com.pahanaedu.util.Util;

import java.io.IOException;
import java.util.List;

@WebServlet("/api/customers/*")
public class CustomerServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    

    private CustomerService customerService = new CustomerService();
    private ObjectMapper objectMapper = Util.getObjectMapper(); // For JSON

   
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

        try {
        	 
        String pathInfo = req.getPathInfo(); // e.g., /123 or /search or /telephone
        resp.setContentType("application/json");

        if (pathInfo == null || pathInfo.equals("/")) { 
        	
            // List all customers with pagination (e.g. /api/customers?page=1)
            int page = 1;
            String pageParam = req.getParameter("page");
            if (pageParam != null) page = Integer.parseInt(pageParam);
            
            PaginatedResponse<Customer> customers = customerService.getAll(page);
            resp.getWriter().write(objectMapper.writeValueAsString(customers));
            
            return;
        }

        String[] splits = pathInfo.split("/");

        if (splits.length >= 2) {
            String action = splits[1];

            switch (action) {
            case "search": {  // /api/customers/search?name=John&page=1
            	
                String name = req.getParameter("name");
                if(name == null || name.isBlank()) {
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                    resp.getWriter().write("{\"error\":\"Name parameter not found\"}");
                    return; 
                }
                
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);
               
                List<Customer> customers = customerService.getCustomersByName(name,page); 
                resp.getWriter().write(objectMapper.writeValueAsString(customers));
                
                break;
            }
            case "active": {  // /api/customers/active 
                
                int page = 1;
                String pageParam = req.getParameter("page");
                if (pageParam != null) page = Integer.parseInt(pageParam);
               
                List<Customer> customers = customerService.getActiveCustomers(page); 
                resp.getWriter().write(objectMapper.writeValueAsString(customers));
                
                break;
            }
                case "telephone": { // /api/customers/telephone?number=123456789
                	
                    String number = req.getParameter("number");
                    if(number == null || number.isBlank()) {
                        resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
                        resp.getWriter().write("{\"error\":\"Number parameter not found\"}");
                        return; 
                    }
                    
                    Customer customer = customerService.getCustomerByTelephone(number);
                    if (customer != null) {
                        resp.getWriter().write(objectMapper.writeValueAsString(customer));
                    } else {
                        resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                        resp.getWriter().write("{\"error\":\"Customer not found\"}");
                    }
                    
                    break;
                } 
                default: { // it's an id /api/customers/123
                    try {
                    	
                        Long id = Long.parseLong(action);
                        Customer customer = customerService.get(id);
                        
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
        } catch(Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
            
        }
        
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	try {

            Customer customer = Util.parseJsonBody(req, Customer.class);
	    	if(customer == null) {
	    		 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
	             resp.getWriter().write("{\"error\":\"Customer object validation failed\"}");
	             return; 
	    	}
	    	
	    	if(Util.anyNullOrEmpty(customer.getName(), customer.getTelephone(), customer.getAddress())) {
	    		 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
	             resp.getWriter().write("{\"error\":\"Missing required field\"}");
	             return; 
	    	} 
		
	    	 
	        Boolean isCreated = customerService.create(customer);
	        if(isCreated) {
	        resp.setStatus(HttpServletResponse.SC_CREATED);
	        resp.getWriter().write(objectMapper.writeValueAsString(customer));
	        }else {
	    		 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
	             resp.getWriter().write("{\"error\":\"Customer creation failed\"}");
	        }
	        

        } catch (IllegalStateException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            
      
	    } catch(Exception e) {
	        e.printStackTrace();
	        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        resp.getWriter().write("{\"error\":\"Internal server error\"}");
	        
	    }
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try{
        	
	        // Update existing customer - expects full Customer JSON with valid ID
	        Customer customer = Util.parseJsonBody(req, Customer.class);
	    	if(customer == null) {
	    		 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
	             resp.getWriter().write("{\"error\":\"Customer object validation failed\"}");
	             return; 
	    	}
	    	
	        if (customer.getId() == null) {
	            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
	            resp.getWriter().write("{\"error\":\"Customer id is required for update\"}");
	            return;
	        } 	
	        
	
	        customerService.update(customer);
	        resp.getWriter().write(objectMapper.writeValueAsString(customer));

        } catch (IllegalStateException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
            
      
	    } catch(Exception e) {
	        e.printStackTrace();
	        resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
	        resp.getWriter().write("{\"error\":\"Internal server error\"}");
	        
	    }
    }
 
}
