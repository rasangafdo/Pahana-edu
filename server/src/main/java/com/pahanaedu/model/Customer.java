package com.pahanaedu.model;

import java.time.LocalDateTime;

import com.pahanaedu.model.role.CustomerRole;
 
public class Customer extends User {
 
    private CustomerRole role = CustomerRole.CUSTOMER; // Always CUSTOMER

    // ✅ No-argument constructor for Jackson
    public Customer() {
        this.role = CustomerRole.CUSTOMER;
    }
    
    // Constructor with fields
    public Customer(String name, String telephone, String address) {
        this.name = name;
        this.telephone = telephone;
        this.address = address; 
        this.role = CustomerRole.CUSTOMER;
        this.lastUpdated = LocalDateTime.now();
    }

    // Getters and setters
  

    public CustomerRole getRole() {
        return role;
    }


}
