package com.example.pizzaapp.dao;

import java.util.List;

// Interface générique avec type paramétré T
public interface IDao<T> {
    boolean create(T o);
    boolean update(T o);
    boolean delete(T o);
    List<T> findAll();
    T findById(int id);
}