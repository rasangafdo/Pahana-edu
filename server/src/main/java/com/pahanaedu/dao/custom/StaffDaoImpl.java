package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.List;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.dto.PaginatedResponse; 
import com.pahanaedu.model.Staff;
import com.pahanaedu.model.role.StaffRole;
import com.pahanaedu.util.PasswordHash; 

public class StaffDaoImpl implements StaffDao {

    @Override
    public boolean create(Staff staff) throws Exception {
        String hashedPassword = PasswordHash.hashPassword(staff.getPassword());

        return CrudUtil.executeUpdate(
            "INSERT INTO staff (name, telephone, address, username, password, email, role, isActive, lastUpdated) VALUES (?,?,?,?,?,?,?,?,?)",
            staff.getName(),
            staff.getTelephone(),
            staff.getAddress(),
            staff.getUsername(),
            hashedPassword,
            staff.getEmail(),
            staff.getRole().name(),
            true,
            Timestamp.valueOf(staff.getLastUpdated())
        );
    }

    @Override
    public boolean update(Staff staff) throws Exception {
        return CrudUtil.executeUpdate(
            "UPDATE staff SET name=?, telephone=?, address=?, username=?, email=?, role=?, lastUpdated=? WHERE id=?",
            staff.getName(),
            staff.getTelephone(),
            staff.getAddress(),
            staff.getUsername(),
            staff.getEmail(),
            staff.getRole().name(),
            Timestamp.valueOf(staff.getLastUpdated()),
            staff.getId()
        );
    }

    @Override
    public boolean delete(Long id) throws Exception {
        return CrudUtil.executeUpdate("DELETE FROM staff WHERE id=?", id) ;
    }

    @Override
    public Staff get(Long id) throws Exception {
        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM staff WHERE id=?", id);
        if (rs.next()) {
            return mapResultSetToStaff(rs);
        }
        return null;
    }

    @Override
    public PaginatedResponse<Staff> getAll(int pageNumber) throws Exception {
        // TODO: implement paging (LIMIT ?,20)
        return null;
    }

 
    public List<Staff> getAllStaff() throws Exception {
    	List<Staff> staffList = new java.util.ArrayList<>();

        // Select all staff including password, because mapResultSetToStaff expects it
        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM staff");

        while (rs.next()) {
            Staff staff = mapResultSetToStaff(rs);
            staff.setPassword(null); // remove password for safety
            staffList.add(staff);
        }

        return staffList;
    }

    

    @Override
    public Staff getStaffByUserName(String username) throws Exception {

        ResultSet rs = CrudUtil.executeQuery("SELECT * FROM staff WHERE username=?", username);
        if (rs.next()) {
            return mapResultSetToStaff(rs);
        }
        return null;
    }
 

    private Staff mapResultSetToStaff(ResultSet rs) throws SQLException {
        Staff staff = new Staff(
            rs.getString("name"),
            rs.getString("telephone"),
            rs.getString("address"),
            rs.getString("username"),
            rs.getString("password"),
            rs.getString("email"),
            StaffRole.valueOf(rs.getString("role"))
        );

        staff.setId(rs.getLong("id"));
        staff.setIsActive(rs.getBoolean("isActive"));

        Timestamp ts = rs.getTimestamp("lastUpdated");
        if (ts != null) {
            staff.setLastUpdated(ts.toLocalDateTime());
        }
        return staff;
    }
}
