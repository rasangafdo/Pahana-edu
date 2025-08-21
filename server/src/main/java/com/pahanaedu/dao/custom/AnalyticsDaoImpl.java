package com.pahanaedu.dao.custom;

import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;

import com.pahanaedu.dao.CrudUtil;
import com.pahanaedu.dto.PaginatedResponse;

public class AnalyticsDaoImpl implements AnalyticsDao {

    @Override
    public Map<String, Object> getDashboardStats() throws Exception {
        Map<String, Object> stats = new HashMap<>();

        String sql = """
            SELECT 
                -- Customers
                COALESCE(c.total_customers, 0) AS total_customers,
                COALESCE(c.total_customers - c.last_month_customers, 0) AS customer_change,

                -- Items
                COALESCE(i.items_in_stock, 0) AS items_in_stock,
                COALESCE(i.items_in_stock - i.last_month_stock, 0) AS stock_change,

                -- Sales
                COALESCE(s.todays_sales, 0) AS todays_sales,
                COALESCE(s.todays_sales - s.last_month_sales, 0) AS sales_change
            FROM 
                (
                    SELECT COUNT(*) AS total_customers,
                        (SELECT COUNT(*) 
                         FROM customers 
                         WHERE isActive = 1
                           AND lastUpdated < DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
                        ) AS last_month_customers
                    FROM customers 
                    WHERE isActive = 1
                ) c
            CROSS JOIN 
                (
                    SELECT SUM(stock_available) AS items_in_stock,
                        (SELECT SUM(stock_available) 
                         FROM item
                         WHERE last_updated_at < DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
                        ) AS last_month_stock
                    FROM item
                ) i
            CROSS JOIN 
                (
                    SELECT SUM(total_amount) AS todays_sales,
                        (SELECT SUM(total_amount) 
                         FROM sales
                         WHERE sale_date = DATE_SUB(CURDATE(), INTERVAL 1 MONTH)
                        ) AS last_month_sales
                    FROM sales
                    WHERE sale_date = CURDATE()
                ) s;
            """;

        ResultSet rs = CrudUtil.executeQuery(sql);

        if (rs.next()) {
            stats.put("totalCustomers", rs.getInt("total_customers"));
            stats.put("customerChange", rs.getInt("customer_change"));

            stats.put("itemsInStock", rs.getInt("items_in_stock"));
            stats.put("stockChange", rs.getInt("stock_change"));

            stats.put("todaysSales", rs.getDouble("todays_sales"));
            stats.put("salesChange", rs.getDouble("sales_change"));
        }

        return stats;
    }

    
    @Override
    public Map<String, Object> getItemAnalytics() throws Exception {
        Map<String, Object> stats = new HashMap<>();

    String sql = """
        SELECT 
            COALESCE(COUNT(i.item_id), 0) AS total_items,
            COALESCE(SUM(i.stock_available), 0) AS in_stock,
            COALESCE((SELECT COUNT(*) FROM category), 0) AS total_categories,
            COALESCE(SUM(i.stock_available * i.unit_price), 0) AS avg_price_total
        FROM item i;
        """;

    ResultSet rs = CrudUtil.executeQuery(sql);

    if (rs.next()) {
        stats.put("totalItems", rs.getInt("total_items"));
        stats.put("inStock", rs.getInt("in_stock"));
        stats.put("totalCategories", rs.getInt("total_categories"));
        stats.put("avgPriceTotal", rs.getDouble("avg_price_total"));
    }

    return stats;
}
    
    
    
	@Override
	public boolean create(Object t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean update(Object t) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(Object id) throws Exception {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Object get(Object id) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public PaginatedResponse<Object> getAll(int pageNumber) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}
}
