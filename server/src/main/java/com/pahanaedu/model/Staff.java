package com.pahanaedu.model;

import java.time.LocalDateTime;

import com.pahanaedu.model.role.StaffRole;
 
public class Staff extends User {
    private String username;
    private String password;
    private String email;
 
    private StaffRole role; // MANAGER or CASHIER only

    // Constructor including User fields and Staff fields
    public Staff(String name, String telephone, String address, 
                 String username, String password, String email, StaffRole role) {
    	this.name = name;
    	this.telephone = telephone;
    	this.address =  address;
        this.username = username;
        this.password = password;
        this.email = email;
        this.role = role;
        this.lastUpdated = LocalDateTime.now();
    }

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
        this.lastUpdated = LocalDateTime.now();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
        this.lastUpdated = LocalDateTime.now();
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
        this.lastUpdated = LocalDateTime.now();
	}

	public StaffRole getRole() {
		return role;
	}

	public void setRole(StaffRole role) {
		this.role = role;
        this.lastUpdated = LocalDateTime.now();
	}
    
    
    
    
}