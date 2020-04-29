package com.example.objectclasses;

import android.util.Log;

import java.io.Serializable;
import java.util.ArrayList;

public class BarmanManager implements Serializable {
    private static final String TAG = "MY-DEB BarmanMan";

    public String barmanName;
    //Bottles
    public ArrayList<String> bottles = new ArrayList<>();
    //Recipies
    public ArrayList<Recipe> recipies = new ArrayList<>();

    //testowy konstruktor
    public BarmanManager(String inName){
        barmanName = inName;

        //po testach wywalić co niżej
        bottles.add("WÓDKA");
        bottles.add("WHISKY");
        bottles.add("WODA");

        ArrayList<RecipeItem> temp_recipe = new ArrayList<RecipeItem>();

        temp_recipe.add(new RecipeItem("WODA",100));
        temp_recipe.add(new RecipeItem("WHISKY",200));
        temp_recipe.add(new RecipeItem("SOK POMARANCZOWY",150));
        temp_recipe.add(new RecipeItem("+",0));
        recipies.add(new Recipe("POMARANCZOWA BOMBA ROBIACA WRAZENIE",temp_recipe));
        temp_recipe.clear();

        temp_recipe.add(new RecipeItem("WHISKY",200));
        temp_recipe.add(new RecipeItem("SOK POMARANCZOWY",150));
        temp_recipe.add(new RecipeItem("+",0));
        recipies.add(new Recipe("BARMAN SPECIAL",temp_recipe));
        temp_recipe.clear();

        temp_recipe.add(new RecipeItem("ALKO",100));
        temp_recipe.add(new RecipeItem("+",0));
        recipies.add(new Recipe("MOLOTOV",temp_recipe));
    }

    public ArrayList<Recipe> getRecipiesForUI(){
        return recipies;
    }

    public boolean addNewRecipe(String inName, ArrayList<RecipeItem> items){
        for (Recipe recipe : recipies){
            if (recipe.getName().equals(inName)) return false;
        }
        recipies.add(new Recipe(inName,items));
        return true;
    }

    public void removeRecipe(String recipeName){
        for (int i = 0; i < recipies.size(); i++){
            if (recipeName==recipies.get(i).getName()) recipies.remove(i);
        }
    }

    //------------------------------------------------------
    public ArrayList<Recipe> getRecipies() {
        return recipies;
    }

    public ArrayList<String> getBottles() {
        return bottles;
    }

    public String getBarmanName() {
        return barmanName;
    }

    public void setBarmanName(String barmanName) {
        Log.d(TAG, "Setting barman name" + barmanName);
        this.barmanName = barmanName;
    }

    public void setBottles(ArrayList<String> bottles) {
        this.bottles.clear();
        this.bottles = (ArrayList<String>) bottles.clone();
    }

    public void setRecipies(ArrayList<Recipe> recipies) {
        this.recipies = recipies;
    }

    public void parseMessage(String message) {
        Log.d(TAG, "Parse message " + message);
    }
}
