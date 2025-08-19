package com.pahanaedu.dao.custom;

import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.SaleItem;
import java.util.List;

public interface SaleItemDao extends CrudDao<SaleItem, Long> {
    List<SaleItem> getItemsBySale(Sale sale) throws Exception;
}
