package com.sopan.food.recipe.model;

/**
 * Created by Sopan on 2017-03-16.
 */

public class CategoryItem {
    private String category;
    private byte[] mainImg;

    public String get_category(){return category;}
    public byte[] get_mainImg(){return mainImg;}

    public CategoryItem(String category, byte[] mainImg) {
        this.category = category;
        this.mainImg = mainImg;
    }
}
