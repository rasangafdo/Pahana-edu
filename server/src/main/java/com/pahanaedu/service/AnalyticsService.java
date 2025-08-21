package com.pahanaedu.service;

import java.util.Map;

import com.pahanaedu.dao.DaoFactory;
import com.pahanaedu.dao.custom.AnalyticsDaoImpl;

public class AnalyticsService {

    private final AnalyticsDaoImpl analyticsDao = 
        (AnalyticsDaoImpl) DaoFactory.getInstance().getDao(DaoFactory.DaoTypes.ANALYTICS);

    public Map<String, Object> getDashboardStats() throws Exception {
        return analyticsDao.getDashboardStats();
    }
    

    public Map<String, Object> getItemAnalytics() throws Exception {
        return analyticsDao.getItemAnalytics();
    }

}
