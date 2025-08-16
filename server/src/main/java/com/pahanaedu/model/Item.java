package com.pahanaedu.model;
 
import java.time.LocalDateTime; 
 
public class Item { 
    private Long itemId;
    private String name;
    private double unitPrice;
    private int stockAvailable;
    private double discount;
    private int qtyToAllowDiscount;
    private LocalDateTime lastUpdatedAt;

//    @ManyToOne
    private Category category;
    
    public Long getItemId() {
    	return itemId;
    };
    
    public void setItemId(Long itemId) {
    	this.itemId = itemId;
    }

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public int getStockAvailable() {
		return stockAvailable;
	}

	public void setStockAvailable(int stockAvailable) {
		this.stockAvailable = stockAvailable;
	}

	public double getDiscount() {
		return discount;
	}

	public void setDiscount(double discount) {
		this.discount = discount;
	}

	public int getQtyToAllowDiscount() {
		return qtyToAllowDiscount;
	}

	public void setQtyToAllowDiscount(int qtyToAllowDiscount) {
		this.qtyToAllowDiscount = qtyToAllowDiscount;
	}

	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public Category getCategory() {
		return category;
	}

	public void setCategory(Category category) {
		this.category = category;
	}
 
    
    
}