package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.objectclasses.Recipe;
import com.example.objectclasses.RecipeItem;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;
import java.util.List;

public class PourActivity extends ParentActivity {

    private ArrayList<Recipe> recipies = new ArrayList<>();;
    private ArrayAdapter<Recipe> adapter;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_pour, R.id.parent);
        recipies.addAll(MenuActivity.getBarman().getRecipiesForUI());

        ListView lv = findViewById(R.id.recipies_recipe_list);

        adapter = new StableArrayAdapter(this, 0, recipies);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        recipies.clear();
        recipies.addAll(MenuActivity.getBarman().getRecipiesForUI());
        recipies.add(new Recipe("",new ArrayList<RecipeItem>()));
        adapter.notifyDataSetChanged();
        super.onResume();
    }


    private class StableArrayAdapter extends ArrayAdapter<Recipe> {

        private Context context;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<Recipe> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        //called when rendering the list
        public View getView(final int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            final Recipe recipe = recipies.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.single_textview_layout, null);

            TextView recipeName = view.findViewById(R.id.single_txtview);
            recipeName.setSelected(true);



            //set price and rental attributes
            recipeName.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            recipeName.setText(recipe.getName());

            recipeName.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(context)
                            .setTitle("Rozpocznij")
                            .setMessage("Czy jesteś pewien, że chcesz polać " + recipe.getName().toLowerCase()  + "?")
                            .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent intent = new Intent(context, ProgressActivity.class);
                                    intent.putExtra("selectedRecipe", recipe.getName());
                                    startActivity(intent);
                                }

                            })
                            .setNegativeButton("Nie", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    FullScreencall();
                                }
                            })
                            .show();
                }
            });
            return view;
        }

    }
}
