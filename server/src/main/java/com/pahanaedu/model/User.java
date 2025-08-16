package com.pahanaedu.model;
 
import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
 


 
public abstract class User { 
    protected Long id;
 
    protected String name; 
    
    protected String telephone;
 
    protected String address;
    
    protected Boolean isActive;
    
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    protected LocalDateTime lastUpdated;
    
    

//    @OneToMany(mappedBy = "user")
    protected List<Sale> orders;
     
    
    // Getters and Setters

    public void setId(Long id) {
    	this.id = id;
    }
    
    public Long getId() {
    	return id;
    }
    
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name; 
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone; 
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address; 
    }

    public Boolean getIsActive() {
        return isActive;
    }

    public void setIsActive(Boolean isActive) {
        this.isActive = isActive; 
    }

    public void setLastUpdated(LocalDateTime lastUpdated) {
        this.lastUpdated =  lastUpdated;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }
    
    public void setOrders(List<Sale> orders) {
    	this.orders =  orders;
    }
    
    public List<Sale> getOrders(){
    	return orders;
    }
}