package com.pahanaedu.dao.custom;

import java.util.List;
import com.pahanaedu.dao.CrudDao;
import com.pahanaedu.model.SaleItem;
import com.pahanaedu.model.Sale;

public interface SaleItemDao extends CrudDao<SaleItem, Long> {
    List<SaleItem> getItemsBySale(Sale sale) throws Exception;
}
