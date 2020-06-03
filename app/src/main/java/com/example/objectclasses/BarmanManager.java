package com.example.objectclasses;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;

public class BarmanManager implements Serializable {
    private static final String TAG = "MY-DEB BarmanMan";

    private String barmanName;
    //Bottles
    private ArrayList<String> bottles = new ArrayList<>();
    //Recipies
    private ArrayList<Recipe> recipies = new ArrayList<>();

    private boolean isPouringFinished = true;
    private int progress = 0;

    //konstruktor
    public BarmanManager(String inName, Context ctx) throws IOException {
        initializeBarman(inName, ctx);
    }

    private void initializeBarman(String inName, Context ctx) throws IOException {
        barmanName = inName;
        BarmanDBHelper barmanDBHelper = new BarmanDBHelper(ctx);

        setRecipes(barmanDBHelper.getRecipies());
        setBottles(barmanDBHelper.getBottles());
        barmanDBHelper.close();

    }

    public ArrayList<RecipeItem> getRecipe(String recipeName){
        ArrayList<RecipeItem> recipeItems = new ArrayList<>();

        for(Recipe recipe : recipies){
            if (recipe.getName().equals(recipeName)){
                recipeItems = recipe.getItems();
                break;
            }
        }
        return recipeItems;
    }

    private boolean checkRecipeExist(String inName){
        for (Recipe recipe : recipies){
            if (recipe.getName().equals(inName)) return true;
        }
        return false;
    }

    public boolean addNewRecipe(String inName, ArrayList<RecipeItem> items, Context ctx){
        if(checkRecipeExist(inName)) return false;
        BarmanDBHelper barmanDBHelper = null;
        try {
            barmanDBHelper = new BarmanDBHelper(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        barmanDBHelper.addRecipe(new Recipe(inName,items));
        setRecipes(barmanDBHelper.getRecipies());
        barmanDBHelper.close();
        return true;
    }

    public void removeRecipe(String recipeName, Context ctx){
        BarmanDBHelper barmanDBHelper = null;
        try {
            barmanDBHelper = new BarmanDBHelper(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        barmanDBHelper.removeRecipe(recipeName);
        setRecipes(barmanDBHelper.getRecipies());
        barmanDBHelper.close();
    }
	
    public void changeBottles(ArrayList<String> bottles, Context ctx){
        BarmanDBHelper barmanDBHelper = null;
        try {
            barmanDBHelper = new BarmanDBHelper(ctx);
        } catch (IOException e) {
            e.printStackTrace();
        }
        barmanDBHelper.changeBottles(bottles);
        setBottles(barmanDBHelper.getBottles());
        barmanDBHelper.close();
    }

    //------------------------------------------------------
    public ArrayList<Recipe> getRecipes() {
        return recipies;
    }

    public ArrayList<String> getBottles() {
        return bottles;
    }

    public String getBarmanName() {
        return barmanName;
    }
    public int getProgress() {
        return progress;
    }

    public void setRecipes(ArrayList<Recipe> recipes) {
        this.recipies = recipes;
    }

    public void setBottles(ArrayList<String> bottles) {
        this.bottles.clear();
        this.bottles = (ArrayList<String>) bottles.clone();
    }

    public void setBarmanName(String barmanName) {
        Log.d(TAG, "Setting barman name" + barmanName);
        this.barmanName = barmanName;
    }

    public void setProgress(int inProgress){
        progress = inProgress;
    }

    public void setStartRecipe(){
        progress = 0;
        isPouringFinished = false;
    }

    public boolean checkEndRecipe(){
        return isPouringFinished;
    }

    public void parseMessage(String message) {
        Log.d(TAG, "Parse message " + message);
        String[] words = message.split(" ");
        String firstWord = words[0];
        switch(firstWord){
            case "SUCCESS":
                isPouringFinished = true;
                Log.d(TAG, "SUCCESS received");
                break;
            case "HELLO":
                Log.d(TAG, "Communication success");
                break;
            case "PROGRESS":
                String secondWord = words[1];
                Log.d(TAG, "Set progress to: "+secondWord);
                if (!isPouringFinished) progress = Integer.parseInt(secondWord);
                break;
            default:
                break;
        }
    }
}