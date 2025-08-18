package com.pahanaedu.model;
 
import java.time.LocalDateTime; 
 
public class Item { 
    private Long itemId;
    private String name;
    private Double unitPrice;        
    private Integer stockAvailable;  
    private Double discount;         
    private Integer qtyToAllowDiscount;  
    private LocalDateTime lastUpdatedAt;

//    @ManyToOne
    private Long categoryId;
    
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

	public Double getUnitPrice() {
		return unitPrice;
	}

	public void setUnitPrice(Double unitPrice) {
		this.unitPrice = unitPrice;
	}

	public Integer getStockAvailable() {
		return stockAvailable;
	}

	public void setStockAvailable(Integer stockAvailable) {
		this.stockAvailable = stockAvailable;
	}

	public Double getDiscount() {
		return discount;
	}

	public void setDiscount(Double discount) {
		this.discount = discount;
	}

	public Integer getQtyToAllowDiscount() {
		return qtyToAllowDiscount;
	}

	public void setQtyToAllowDiscount(Integer qtyToAllowDiscount) {
		this.qtyToAllowDiscount = qtyToAllowDiscount;
	}

	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}
 
    
    
}