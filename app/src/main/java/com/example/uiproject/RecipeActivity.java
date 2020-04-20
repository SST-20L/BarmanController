package com.example.uiproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.widget.ContentLoadingProgressBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class RecipeActivity extends AppCompatActivity {

    private ArrayList<Recipe> recipies = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);


        ListView lv = (ListView)findViewById(R.id.recipe_list);
        recipies.add(new Recipe("Nowy",false));
        recipies.add(new Recipe("Android",true));
        recipies.add(new Recipe("Android2",true));
        recipies.add(new Recipe("Android3",true));
        recipies.add(new Recipe("Android4",true));
        recipies.add(new Recipe("Android5",true));
        recipies.add(new Recipe("Android6",true));
        recipies.add(new Recipe("Android7",true));
        recipies.add(new Recipe("Android8",true));
        recipies.add(new Recipe("Android9",true));
        recipies.add(new Recipe("Android10",true));
        recipies.add(new Recipe("Android11",true));
        recipies.add(new Recipe("Android12",true));

        ArrayAdapter<Recipe> adapter = new StableArrayAdapter(this, 0, recipies);
        lv.setAdapter(adapter);
    }


    private class StableArrayAdapter extends ArrayAdapter<Recipe> {

        private Context context;
        private List<Recipe> recipeList;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<Recipe> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.recipeList = objects;
        }

        //called when rendering the list
        public View getView(final int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            final Recipe recipe = recipeList.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.recipe_item_layout, null);

            TextView recipeName = (TextView) view.findViewById(R.id.secondLine);
            TextView status = (TextView) view.findViewById(R.id.firstLine);



            //set price and rental attributes
            recipeName.setText(recipe.getName());
            if (recipe.getStatus()) status.setText("-");
            else {
                status.setText("+");
                status.setTextColor(Color.parseColor("#00DF00"));
            }

            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (recipe.getStatus()){
                        //usuń
                        recipeList.remove(position);
                        notifyDataSetChanged();
                    }
                    else{//dodaj
                        Intent intent = new Intent(context, NewRecipeActivity.class);
                        startActivity(intent);
                    }
                }
            });

            return view;
        }

    }


    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();
    }

    public void sendBack(View view) {
        this.onBackPressed();
    }

    public void FullScreencall() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) { // lower api
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            //for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
}
