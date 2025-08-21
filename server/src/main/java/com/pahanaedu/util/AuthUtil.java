package com.pahanaedu.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.pahanaedu.model.Staff;
import com.pahanaedu.service.StaffService;

public class AuthUtil {

    private static final StaffService staffService = new StaffService();

    // Authenticate user with JWT
    public static Staff authenticate(HttpServletRequest req, HttpServletResponse resp) throws Exception {
    	 
        String authHeader = req.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Missing or invalid Authorization header\"}");
            return null;
        }

        String token = authHeader.substring(7);
        String username = JwtValidator.getUsername(token); 
        if (username == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"Invalid or expired token\"}");
            return null;
        }

        Staff staff = staffService.getStaffUser(username);
        if (staff == null) {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.getWriter().write("{\"error\":\"User not found\"}");
            return null;
        }

        return staff; 
    }

    // Authorize single role
    public static boolean authorizeRole(Staff staff, String requiredRole, HttpServletResponse resp) throws Exception {
        return authorizeRoles(staff, new String[]{requiredRole}, resp);
    }

    // Authorize multiple roles
    public static boolean authorizeRoles(Staff staff, String[] allowedRoles, HttpServletResponse resp) throws Exception {
        if (staff.getRole() == null) {
            resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
            resp.getWriter().write("{\"error\":\"Access denied. Role required\"}");
            return false;
        }

        for (String role : allowedRoles) {
            if (staff.getRole().name().equals(role)) {
                return true; // matched one role
            }
        }

        // If no roles matched
        resp.setStatus(HttpServletResponse.SC_FORBIDDEN);
        resp.getWriter().write("{\"error\":\"Access denied. Allowed roles: " + String.join(",", allowedRoles) + "\"}");
        return false;
    }
}
