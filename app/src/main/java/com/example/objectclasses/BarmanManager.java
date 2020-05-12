package com.example.objectclasses;

import android.content.Context;
import android.util.Log;

import java.io.IOException;
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

    //konstruktor
    public BarmanManager(String inName, Context ctx) throws IOException {
        initializeBarman(inName, ctx);
    }

    public void initializeBarman(String inName, Context ctx) throws IOException {
        barmanName = inName;
        BarmanDBHelper barmanDBHelper = new BarmanDBHelper(ctx);

        setRecipies(barmanDBHelper.getRecipies());
        setBottles(barmanDBHelper.getBottles());
        barmanDBHelper.close();

    }

    public boolean checkRecipeExist(String inName){
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
        setRecipies(barmanDBHelper.getRecipies());
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
        setRecipies(barmanDBHelper.getRecipies());
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
    public ArrayList<Recipe> getRecipies() {
        return recipies;
    }

    public ArrayList<String> getBottles() {
        return bottles;
    }

    public String getBarmanName() {
        return barmanName;
    }

    public void setRecipies(ArrayList<Recipe> recipies) {
        this.recipies = recipies;
    }

    public void setBottles(ArrayList<String> bottles) {
        this.bottles.clear();
        this.bottles = (ArrayList<String>) bottles.clone();
    }

    public void setBarmanName(String barmanName) {
        Log.d(TAG, "Setting barman name" + barmanName);
        this.barmanName = barmanName;
    }


    public void setStartRecipe(){
        isPouringFinished = false;
    }

    public boolean checkEndRecipe(){
        return isPouringFinished;
    }


    //TODO: DEPRECATE
    public boolean addNewRecipe(String inName){
        if(checkRecipeExist(inName)) return false;
        recipies.add(new Recipe(inName));
        return true;
    }
    //TODO: DEPRECATE
    public void addRecipeIng(String recName, String ingName, int ingAmount){
        for (Recipe recipe : recipies){
            if (recipe.getName().equals(recName)){
                recipe.addItem(new RecipeItem(ingName,ingAmount));
            }
        }
    }
    //TODO:DEPRECATE
    public void addRecipeName(String message){
        String[] words = message.split(" ");
        if(words.length ==  2 ){
            addNewRecipe(words[1]);
        }
    }
    //TODO: DEPRECATE
    public void setBottle(String bottleName, int bottleIndex) {
        this.bottles.set(bottleIndex, bottleName);
    }
    //TODO:DEPRECATE
    public void changeBottle(String message){
        String[] words = message.split(" ");
        if(words.length  == 3 ){
            setBottle(words[1], Integer.parseInt(words[2]));
        }
    }
    //TODO:DEPRECATE
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
                //changeBottle(message);
                break;
            case "RECIPE_ING":
                //addRecipeIng(message);
                break;
            case "RECIPE_NAME":
                //addRecipeName(message);
                break;
        }
    }
}