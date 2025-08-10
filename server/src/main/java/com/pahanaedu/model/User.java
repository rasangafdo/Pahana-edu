package com.pahanaedu.model;
 
import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;


 
public abstract class User { 
    protected Long id;
 
    protected String name; 
    
    protected String telephone;
 
    protected String address;
    
    protected Boolean isActive  = true;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime lastUpdated;
     
    
    // Getters and Setters
    
    public Long getId() {
    	return id;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
        this.lastUpdated = LocalDateTime.now();
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
        this.lastUpdated = LocalDateTime.now();
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    
}