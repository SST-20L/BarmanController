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

    private boolean isPouringFinished = true;

    //testowy konstruktor
    public BarmanManager(String inName){
        barmanName = inName;

        //po testach wywalić co niżej
        bottles.add("?");
        bottles.add("?");
        bottles.add("?");

        /*
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
        */
    }

    public ArrayList<Recipe> getRecipiesForUI(){
        return recipies;
    }

    public boolean checkRecipeExist(String inName){
        for (Recipe recipe : recipies){
            if (recipe.getName().equals(inName)) return true;
        }
        return false;
    }

    public void addRecipeIng(String recName, String ingName, int ingAmount){
        for (Recipe recipe : recipies){
            if (recipe.getName().equals(recName)){
                recipe.addItem(new RecipeItem(ingName,ingAmount));
            }
        }
    }

    public boolean addNewRecipe(String inName, ArrayList<RecipeItem> items){
        if(checkRecipeExist(inName)) return false;
        recipies.add(new Recipe(inName,items));
        return true;
    }

    public boolean addNewRecipe(String inName){
        if(checkRecipeExist(inName)) return false;
        recipies.add(new Recipe(inName));
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

    public void setBottle(String bottleName, int bottleIndex) {
        this.bottles.set(bottleIndex, bottleName);
    }

    public void setRecipies(ArrayList<Recipe> recipies) {
        this.recipies = recipies;
    }

    public void setStartRecipe(){
        isPouringFinished = false;
    }

    public boolean checkEndRecipe(){
        return isPouringFinished;
    }

    public void addRecipeName(String message){
        String[] words = message.split(" ");
        if(words.length ==  2 ){
            addNewRecipe(words[1]);
        }
    }

    public void changeBottle(String message){
        String[] words = message.split(" ");
        if(words.length  == 3 ){
            setBottle(words[1], Integer.parseInt(words[2]));
        }
    }

    public void addRecipeIng(String message){
        String[] words = message.split(" ");
        if(words.length  == 4 ){
            if(!checkRecipeExist(words[1])) return;
            addRecipeIng(words[1], words[2], Integer.parseInt(words[3]));
        }
    }

    public void parseMessage(String message) {
        Log.d(TAG, "Parse message " + message);
        String[] words = message.split(" ");
        String firstWord = words[0];
        switch(firstWord){
            case "FINISH-RECIPE":
                isPouringFinished = true;
                break;
            case "BOTTLE":
                changeBottle(message);
                break;
            case "RECIPE_ING":
                addRecipeIng(message);
                break;
            case "RECIPE_NAME":
                addRecipeName(message);
                break;
        }
    }
}