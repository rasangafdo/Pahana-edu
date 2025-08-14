package com.pahanaedu.dao;

import java.util.List;

public interface CrudDao<T, ID> extends SuperDao {
    boolean create(T t) throws Exception;

    boolean update(T t) throws Exception;

    boolean delete(ID id) throws Exception;

    T get(ID id) throws Exception;

    List<T> getAll(int pageNumber) throws Exception;
}
