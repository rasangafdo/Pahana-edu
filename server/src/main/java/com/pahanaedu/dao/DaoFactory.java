package com.pahanaedu.dao;

import com.pahanaedu.dao.custom.*;

public class DaoFactory {

    private static DaoFactory daoFactory;

    private DaoFactory() {
    }

    public static DaoFactory getInstance() {
        if (daoFactory == null) {
            daoFactory = new DaoFactory();
        }
        return daoFactory;
    }

    public SuperDao getDao(DaoTypes type) {
        switch (type) {
        case CATEGORY:
            return new CategoryDaoImpl(); 
        case CUSTOMER:
            return new CustomerDaoImpl(); 
        case ITEM:
            return new ItemDaoImpl(); 
        case SALE:
            return new SaleDaoImpl();
        case SALEITEM:
            return new SaleItemDaoImpl(); 
        case STAFF:
            return new StaffDaoImpl(); 
        case ANALYTICS:
            return new AnalyticsDaoImpl(); 
            default:
                return null;
        }
    }

    public enum DaoTypes {
    	 CATEGORY,CUSTOMER, ITEM,SALE,SALEITEM, STAFF, ANALYTICS;
    }
}
