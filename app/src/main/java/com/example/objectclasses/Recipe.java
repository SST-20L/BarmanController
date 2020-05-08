package com.example.objectclasses;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable {
    public String name;
    public ArrayList<RecipeItem> items = new ArrayList<>();

    //utwórz przepis na podstawie listy wprowadzonej w UI (lista obiektów RecipeItem bez ostatniego elementu, który służy jako przycisk "+") oraz nazwy przepisu
    public Recipe(String inName, ArrayList<RecipeItem> list){
        name = inName;
        for (int i = 0; i < list.size()-1; i++){
            if (!list.get(i).getName().equals("")) {
                items.add(list.get(i));
            }
        }
    }

    public Recipe(String inName){
        name = inName;
    }

    public String getName() {
        return name;
    }

    public ArrayList<RecipeItem> getItems() {
        return items;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setItems(ArrayList<RecipeItem> items) {
        this.items = items;
    }

    public void addItem(RecipeItem recipeItem) {
        this.items.add(recipeItem);
    }
}
