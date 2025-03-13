package com.example.pizzaapp.service;

import com.example.pizzaapp.classes.Produit;

import java.util.ArrayList;
import java.util.List;
import com.example.pizzaapp.dao.IDao;

public class ProduitService implements IDao<Produit> {
    private List<Produit> produits;
    private static ProduitService instance;

    private ProduitService() {
        this.produits = new ArrayList<>();
    }

    public static ProduitService getInstance() {
        if(instance == null)
            instance = new ProduitService();
        return instance;
    }

    @Override
    public boolean create(Produit o) {
        return produits.add(o);
    }

    @Override
    public boolean update(Produit o) {
        for(int i = 0; i < produits.size(); i++){
            if(produits.get(i).getId() == o.getId()){
                produits.set(i, o);
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean delete(Produit o) {
        return produits.remove(o);
    }

    @Override
    public List<Produit> findAll() {
        return produits;
    }

    @Override
    public Produit findById(int id) {
        for(Produit p : produits){
            if(p.getId() == id)
                return p;
        }
        return null;
    }
}