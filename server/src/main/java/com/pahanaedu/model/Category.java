package com.pahanaedu.model;
 
import java.time.LocalDateTime;
import java.util.List;
 
public class Category {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    private String name;
    private LocalDateTime lastUpdatedAt;
    
    public Category() {}
    
    public Category(String name) {
    	this.name = name;
    }

//    @OneToMany(mappedBy = "category")
    private List<Item> items;

	public Long getCategoryId() {
		return categoryId;
	}

	public void setCategoryId(Long categoryId) {
		this.categoryId = categoryId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public LocalDateTime getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public void setLastUpdatedAt(LocalDateTime lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
	}

	public List<Item> getItems() {
		return items;
	}

	public void setItems(List<Item> items) {
		this.items = items;
	}
     
    
    
    
}