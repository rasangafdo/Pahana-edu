package com.pahanaedu.dao.custom;

import com.pahanaedu.model.Item;
import com.pahanaedu.model.Sale;
import com.pahanaedu.model.SaleItem;
import com.pahanaedu.util.DBConnection;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleItemDaoImpl implements SaleItemDao {

    @Override
    public boolean create(SaleItem t) throws Exception {
        String sql = "INSERT INTO sale_items (sale_id, item_id, qty, discount_amount, item_total, last_updated_at) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setLong(1, t.getSale().getSaleId());
            ps.setLong(2, t.getItem().getItemId());
            ps.setInt(3, t.getQty());
            ps.setDouble(4, t.getDiscountAmount());
            ps.setDouble(5, t.getItemTotal());
            ps.setTimestamp(6, Timestamp.valueOf(t.getLastUpdatedAt() != null ? t.getLastUpdatedAt() : LocalDateTime.now()));

            int affected = ps.executeUpdate();
            if (affected > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    t.setSaleItemId(rs.getLong(1));
                }
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean update(SaleItem t) throws Exception {
        String sql = "UPDATE sale_items SET sale_id=?, item_id=?, qty=?, discount_amount=?, item_total=?, last_updated_at=? " +
                     "WHERE sale_item_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, t.getSale().getSaleId());
            ps.setLong(2, t.getItem().getItemId());
            ps.setInt(3, t.getQty());
            ps.setDouble(4, t.getDiscountAmount());
            ps.setDouble(5, t.getItemTotal());
            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setLong(7, t.getSaleItemId());

            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public boolean delete(Long id) throws Exception {
        String sql = "DELETE FROM sale_items WHERE sale_item_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            return ps.executeUpdate() > 0;
        }
    }

    @Override
    public SaleItem get(Long id) throws Exception {
        String sql = "SELECT * FROM sale_items WHERE sale_item_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, id);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return mapRow(rs);
            }
        }
        return null;
    }

    @Override
    public List<SaleItem> getAll(int pageNumber) throws Exception {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT * FROM sale_items LIMIT 20 OFFSET ?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, (pageNumber - 1) * 20);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    @Override
    public List<SaleItem> getItemsBySale(Sale sale) throws Exception {
        List<SaleItem> list = new ArrayList<>();
        String sql = "SELECT * FROM sale_items WHERE sale_id=?";
        try (Connection con = DBConnection.getInstance().getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setLong(1, sale.getSaleId());
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                list.add(mapRow(rs));
            }
        }
        return list;
    }

    private SaleItem mapRow(ResultSet rs) throws Exception {
        SaleItem si = new SaleItem();
        si.setSaleItemId(rs.getLong("sale_item_id"));

        Item item = new Item();
        item.setItemId(rs.getLong("item_id"));
        si.setItem(item);

        Sale sale = new Sale();
        sale.setSaleId(rs.getLong("sale_id"));
        si.setSale(sale);

        si.setQty(rs.getInt("qty"));
        si.setDiscountAmount(rs.getDouble("discount_amount"));
        si.setItemTotal(rs.getDouble("item_total"));

        Timestamp ts = rs.getTimestamp("last_updated_at");
        si.setLastUpdatedAt(ts != null ? ts.toLocalDateTime() : null);

        return si;
    }
}
