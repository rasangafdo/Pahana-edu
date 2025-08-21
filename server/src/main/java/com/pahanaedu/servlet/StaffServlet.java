package com.pahanaedu.servlet;
 
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.pahanaedu.model.Staff;
import com.pahanaedu.service.StaffService;
import com.pahanaedu.util.JwtValidator;
import com.pahanaedu.util.Util;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/api/staff/*")
public class StaffServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
	private final StaffService staffService = new StaffService(); 
    private ObjectMapper objectMapper = Util.getObjectMapper();
 
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");
        try {
            // Get JWT token
            String authHeader = req.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
                return;
            }

            String token = authHeader.substring(7);
            String loggedInUsername = JwtValidator.getUsername(token);

            if (loggedInUsername == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                return;
            }

            // Fetch logged-in staff to check role
            Staff loggedInStaff = staffService.getStaffUser(loggedInUsername);

            String pathInfo = req.getPathInfo(); // /user or null
            if ("/user".equals(pathInfo)) {
                // Return logged-in user info
                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(loggedInStaff));
            } else if (pathInfo == null || "/".equals(pathInfo)) {
                // Only manager can access
                if (loggedInStaff.getRole() != null && !loggedInStaff.getRole().name().equals("MANAGER")) {
                    resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                    resp.getWriter().write("{\"error\":\"Access denied. Manager role required\"}");
                    return;
                }

                // Optional query param for username search
                String searchUsername = req.getParameter("username");

                List<Staff> staffList;
                if (searchUsername != null && !searchUsername.isEmpty()) {
                    Staff staff = staffService.getStaffUser(searchUsername);
                    staffList = staff != null ? List.of(staff) : List.of();
                } else {
                    staffList = staffService.getAllStaff();
                }

                // Remove passwords
                staffList.forEach(s -> s.setPassword(null));

                resp.setStatus(HttpServletResponse.SC_OK);
                resp.getWriter().write(objectMapper.writeValueAsString(staffList));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Invalid endpoint\"}");
            }

        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

    
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
       
        resp.setContentType("application/json");

        try {
        	 String authHeader = req.getHeader("Authorization");
             if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                 resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                 resp.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
                 return;
             }
            String token = authHeader.substring(7);
            String loggedInUsername = JwtValidator.getUsername(token);

            if (loggedInUsername == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                return;
            }

            // Fetch logged-in staff to check role
            Staff loggedInStaff = staffService.getStaffUser(loggedInUsername);
            
            if (loggedInStaff.getRole() != null && !loggedInStaff.getRole().name().equals("MANAGER")) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\":\"Access denied. Manager role required\"}");
                return;
            }

            
            Staff staff = Util.parseJsonBody(req, Staff.class);
            Map<String, Object> response = new HashMap<>(); 
                boolean isCreated = staffService.createStaff(staff);
                if(isCreated) {
        	        resp.setStatus(HttpServletResponse.SC_CREATED);
        	        resp.getWriter().write(objectMapper.writeValueAsString(staff));
        	        }else {
        	    		 resp.setStatus(HttpServletResponse.SC_BAD_REQUEST); 
        	             resp.getWriter().write("{\"error\":\"Staff creation failed\"}");
        	        }
        	        
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            resp.setStatus(500); 
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
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

               
                    // delete by id
                    Long id = Long.parseLong(identifier);
                    boolean deleted = staffService.delete(id);
                   
                if (deleted) {
                    resp.setStatus(HttpServletResponse.SC_NO_CONTENT);
                } else {
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    resp.getWriter().write("{\"error\":\"Staff user not found or delete failed\"}");
                }
            }
        } catch (IllegalStateException e) {
                resp.setStatus(HttpServletResponse.SC_CONFLICT);
                resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }  catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

    
    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setContentType("application/json");

        try {
            // Check auth
            String authHeader = req.getHeader("Authorization");
            if (authHeader == null || !authHeader.startsWith("Bearer ")) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
                return;
            }
            String token = authHeader.substring(7);
            String loggedInUsername = JwtValidator.getUsername(token);

            if (loggedInUsername == null) {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                resp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
                return;
            }

            // Logged in user
            Staff loggedInStaff = staffService.getStaffUser(loggedInUsername);

            // Only MANAGER can update staff (business rule)
            if (loggedInStaff.getRole() != null && !loggedInStaff.getRole().name().equals("MANAGER")) {
                resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
                resp.getWriter().write("{\"error\":\"Access denied. Manager role required\"}");
                return;
            }

            // Extract ID from path
            String pathInfo = req.getPathInfo();
            if (pathInfo == null || pathInfo.equals("/")) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Staff ID required for update\"}");
                return;
            }

            String[] splits = pathInfo.split("/");
            if (splits.length < 2) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                resp.getWriter().write("{\"error\":\"Invalid request path\"}");
                return;
            }

            Long staffId = Long.parseLong(splits[1]);

            // Parse incoming JSON body
            Staff staffToUpdate = Util.parseJsonBody(req, Staff.class);
            staffToUpdate.setId(staffId);  // ensure ID comes from path, not body

            // Call service
            boolean updated = staffService.updateStaff(staffToUpdate);

            if (updated) {
                resp.setStatus(HttpServletResponse.SC_OK);
                staffToUpdate.setPassword(null); // don’t leak password
                resp.getWriter().write(objectMapper.writeValueAsString(staffToUpdate));
            } else {
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                resp.getWriter().write("{\"error\":\"Staff user not found or update failed\"}");
            }

        } catch (NumberFormatException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            resp.getWriter().write("{\"error\":\"Invalid staff ID format\"}");
        } catch (Exception e) {
            e.printStackTrace();
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            resp.getWriter().write("{\"error\":\"Internal server error\"}");
        }
    }

}
 
        
