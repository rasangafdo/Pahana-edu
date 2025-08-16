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
        case CUSTOMER:
            return new CustomerDaoImpl(); 
        case STAFF:
            return new StaffDaoImpl(); 
            default:
                return null;
        }
    }

    public enum DaoTypes {
        CUSTOMER, STAFF;
    }
}
