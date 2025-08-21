package com.pahanaedu.dao.custom;

import java.util.Map;

import com.pahanaedu.dao.CrudDao; 

public interface AnalyticsDao extends CrudDao<Object, Object>  {
    Map<String, Object> getDashboardStats() throws Exception;
    Map<String, Object> getItemAnalytics() throws Exception;
}
