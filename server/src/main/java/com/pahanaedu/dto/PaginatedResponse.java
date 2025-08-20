package com.pahanaedu.dto;

import java.util.List;

public class PaginatedResponse<T> {
    private List<T> data;
    private int totalPages;
    private int totalCount;

    public PaginatedResponse(List<T> data, int totalPages, int totalCount) {
        this.data = data;
        this.totalPages = totalPages;
        this.totalCount = totalCount;
    }
    


    // Add getters for Jackson
    public List<T> getData() {
        return data;
    }

    public int getTotalPages() {
        return totalPages;
    }

    public int getTotalCount() {
        return totalCount;
    }

    // Optional: setters if needed
    public void setData(List<T> data) {
        this.data = data;
    }

    public void setTotalPages(int totalPages) {
        this.totalPages = totalPages;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }
 
}

