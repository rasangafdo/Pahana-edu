package com.pahanaedu.dao;

import com.pahanaedu.model.Customer;

import java.lang.reflect.Field;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class CustomerDAO {

    // This will act as our "database"
    private static final List<Customer> customers = new ArrayList<>();
    private static long idCounter = 1; // simulate auto-increment IDs
 
    public List<Customer> getAllCustomers() {
        
        return customers;
    }

    // Find by telephone
    public Customer getCustomerByTelephone(String telephone) {
        return customers.stream()
                .filter(c -> c.getTelephone().equals(telephone))
                .findFirst()
                .orElse(null);
    }

    // Search by name
    public List<Customer> getCustomersByName(String name) {
        List<Customer> filtered = customers.stream()
                .filter(c -> c.getName() != null && c.getName().toLowerCase().contains(name.toLowerCase()))
                .sorted(Comparator.comparing(Customer::getLastUpdated).reversed())
                .collect(Collectors.toList());

         return filtered;
    }

    // Create new customer
    public void createCustomer(Customer customer) {
        try{
        Field idField = Customer.class.getSuperclass().getDeclaredField("id");
        idField.setAccessible(true); // allow private/protected modification
        idField.set(customer, idCounter++); 
        customers.add(customer); 
        } catch (Exception e) {
            throw new RuntimeException("Unable to set customer ID", e);
        }
    }

    // Get by ID
    public Customer getCustomerById(Long id) {
        return customers.stream()
                .filter(c -> c.getId().equals(id))
                .findFirst()
                .orElse(null);
    }

    // Update existing customer
    public void updateCustomer(Customer customer) {
        for (int i = 0; i < customers.size(); i++) {
            if (customers.get(i).getId().equals(customer.getId())) {
                customers.set(i, customer);
                return;
            }
        }
    }

    // Delete by ID
    public void deleteCustomer(Long id) {
        customers.removeIf(c -> c.getId().equals(id));
    }
 
}
