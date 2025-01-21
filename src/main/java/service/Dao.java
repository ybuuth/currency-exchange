package service;

import java.util.List;

public interface Dao<T, TypeId> {

    List<T> findAll();
    T findById(TypeId Id);
    T findByCode(String code);
}
