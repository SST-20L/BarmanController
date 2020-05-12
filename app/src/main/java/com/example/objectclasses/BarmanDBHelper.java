package com.example.objectclasses;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class BarmanDBHelper extends SQLiteOpenHelper {

    public String DB_PATH = "data/data/com.example/databases/";

    public static final String DB_NAME = "Barman.db";

    public static final String RECIPIES_TABLE_NAME = "recipies";
    public static final String RECIPIES_COLUMN_RECIPE_NAME = "recipe_name";

    public static final String INGREDIENTS_TABLE_NAME = "ingredients";
    public static final String INGREDIENTS_COLUMN_ID = "ingredient_id";
    public static final String INGREDIENTS_COLUMN_RECIPE_NAME = "recipe_name";
    public static final String INGREDIENTS_COLUMN_INGREDIENT_NAME = "ingredient_name";
    public static final String INGREDIENTS_COLUMN_AMOUNT = "amount";

    public static final String BOTTLES_TABLE_NAME = "bottles";
    public static final String BOTTLES_COLUMN_ID = "bottle_id";
    public static final String BOTTLES_COLUMN_BOTTLE_NAME = "bottle_name";

    private HashMap hp;
    private SQLiteDatabase db;

    private Context ctx;
    public int GetCursor;

    public BarmanDBHelper(Context context) throws IOException {
        super(context, DB_NAME , null, 1);
    }


    public void addRecipe(Recipe recipe){
        db = getWritableDatabase();
        db.execSQL(
                "insert into " + RECIPIES_TABLE_NAME + " (" + RECIPIES_COLUMN_RECIPE_NAME + ") values ('" +recipe.getName()+ "');"
        );
        for (RecipeItem item : recipe.getItems()){
            if (!item.getName().equals("+")){
                db.execSQL(
                        "insert into " + INGREDIENTS_TABLE_NAME + " ("
                                + INGREDIENTS_COLUMN_RECIPE_NAME
                                + "," + INGREDIENTS_COLUMN_INGREDIENT_NAME
                                + "," + INGREDIENTS_COLUMN_AMOUNT
                                + ") values ("
                                + "'" +recipe.getName()+ "'"
                                + ",'" +item.getName()+ "'"
                                + "," +item.getValue()+ ""
                                + ");"
                );
            }
        }
        db.close();
    }

    public void removeRecipe(String recipeName){
        db = getWritableDatabase();
        db.execSQL(
                "delete from " + RECIPIES_TABLE_NAME + " where " + RECIPIES_COLUMN_RECIPE_NAME + "='"+recipeName+"';"
        );
        db.execSQL(
                "delete from " + INGREDIENTS_TABLE_NAME + " where " + INGREDIENTS_COLUMN_RECIPE_NAME + "='"+recipeName+"';"
        );
        db.close();
    }

    public void changeBottles(ArrayList<String> bottles){
        db = getWritableDatabase();
        db.execSQL(
                "DELETE FROM "+ BOTTLES_TABLE_NAME +";"
        );
        for (String bottle : bottles){
            db.execSQL(
                    "insert into " + BOTTLES_TABLE_NAME + " ("
                            + BOTTLES_COLUMN_BOTTLE_NAME
                            + ") values ("
                            + "'" +bottle+ "'"
                            + ");"
            );
        }
        db.close();
    }

    public ArrayList<Recipe> getRecipies(){
        db = getWritableDatabase();
        ArrayList<Recipe> recipies = new ArrayList<>();

        Cursor c_name = db.rawQuery("SELECT "+RECIPIES_COLUMN_RECIPE_NAME+" FROM "+RECIPIES_TABLE_NAME+";", null);

        while (c_name.moveToNext()) {
            String recipeName = c_name.getString(0);
            ArrayList<RecipeItem> recipeItems = new ArrayList<>();
            Cursor c_item = db.rawQuery("SELECT "+INGREDIENTS_COLUMN_INGREDIENT_NAME+","+INGREDIENTS_COLUMN_AMOUNT+" FROM "+INGREDIENTS_TABLE_NAME+" WHERE "+INGREDIENTS_COLUMN_RECIPE_NAME+"='"+recipeName+"';", null);

            while (c_item.moveToNext()) {
                recipeItems.add(new RecipeItem(c_item.getString(0),c_item.getInt(1)));
            }
            c_item.close();

            recipies.add(new Recipe(recipeName,recipeItems));
        }
        c_name.close();
        db.close();
        return recipies;
    }


    public ArrayList<String> getBottles(){
        db = getWritableDatabase();
        ArrayList<String> bottles = new ArrayList<>();

        Cursor c = db.rawQuery("SELECT "+BOTTLES_COLUMN_BOTTLE_NAME+" FROM "+BOTTLES_TABLE_NAME+";", null);

        while (c.moveToNext()) {
            bottles.add(c.getString(0));
        }
        c.close();
        db.close();
        return bottles;
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "create table if not exists " + RECIPIES_TABLE_NAME + " (" +
                        RECIPIES_COLUMN_RECIPE_NAME + " text PRIMARY KEY NOT NULL" +
                        ");"
        );

        db.execSQL(
                "create table if not exists " + INGREDIENTS_TABLE_NAME + " (" +
                        INGREDIENTS_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", "+
                        INGREDIENTS_COLUMN_RECIPE_NAME + " text" +", "+
                        INGREDIENTS_COLUMN_INGREDIENT_NAME + " text" +", "+
                        INGREDIENTS_COLUMN_AMOUNT + " integer" +
                        ");"
        );

        db.execSQL(
                "create table if not exists " + BOTTLES_TABLE_NAME + " (" +
                        BOTTLES_COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL" + ", "+
                        BOTTLES_COLUMN_BOTTLE_NAME + " text" +
                        ");"
        );
        //Create 3 default bottles
        for (int i=0;i<3;i++) {
            db.execSQL(
                    "insert into " + BOTTLES_TABLE_NAME + " (" + BOTTLES_COLUMN_BOTTLE_NAME + ") values ('');"
            );
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

}
