package com.pahanaedu.servlet; 
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse; 

import com.fasterxml.jackson.databind.ObjectMapper; 
import com.pahanaedu.dto.LoginDto;
import com.pahanaedu.model.Staff; 
import com.pahanaedu.service.StaffService;
import com.pahanaedu.util.JwtValidator;
import com.pahanaedu.util.Util;

/**
 * Servlet implementation class LoginServlet
 */
@WebServlet("/api/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    private final ObjectMapper objectMapper = Util.getObjectMapper();
    private final StaffService staffService = new StaffService();
    

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        
        resp.setContentType("application/json");

        try { 
        	LoginDto loginDto = Util.parseJsonBody(req, LoginDto.class); 

            Map<String, Object> response = new HashMap<>(); 
            Staff staff = staffService.verifyPassword(loginDto);
            if(staff!=null) {
                String token = JwtValidator.generateToken(staff.getUsername(),staff.getRole().name());
                response.put("success", true);
                response.put("token", token);
                response.put("userId", staff.getId());
                response.put("username", staff.getUsername());
                response.put("role", staff.getRole());
            } else {
                resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.put("success", false);
                response.put("message", "Invalid username or password");
            }
            resp.getWriter().write(objectMapper.writeValueAsString(response));
        } catch (Exception e) {
            resp.setStatus(500);
            resp.getWriter().write("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }

}
