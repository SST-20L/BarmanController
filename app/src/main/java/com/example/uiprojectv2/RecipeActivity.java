package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;

import com.example.objectclasses.Recipe;
import com.example.objectclasses.RecipeItem;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;
import java.util.List;

public class RecipeActivity extends ParentActivity {

    private ArrayList<Recipe> recipies = new ArrayList<>();;
    private ArrayAdapter<Recipe> adapter;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_recipies, R.id.parent);
        recipies.addAll(MenuActivity.getBarman().getRecipies());
        recipies.add(new Recipe("",new ArrayList<RecipeItem>()));

        ListView lv = findViewById(R.id.recipies_recipe_list);

        adapter = new StableArrayAdapter(this, 0, recipies);
        lv.setAdapter(adapter);
    }

    @Override
    protected void onResume(){
        recipies.clear();
        recipies.addAll(MenuActivity.getBarman().getRecipies());
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
            View view = inflater.inflate(R.layout.double_textview_layout, null);

            TextView recipeName = view.findViewById(R.id.name);
            recipeName.setSelected(true);
            TextView status = view.findViewById(R.id.option);



            //set price and rental attributes
            recipeName.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            recipeName.setText(recipe.getName());
            if (position != recipies.size()-1) {
                status.setText("-");
                status.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.75f));
                recipeName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.25f));

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                recipeName.setBackgroundResource(outValue.resourceId);

                recipeName.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(context, RecipeDetailsActivity.class);
                        intent.putExtra("selectedRecipe", recipe);
                        startActivity(intent);
                    }
                });

            }
            else {
                status.setText("+");
                status.setTextColor(Color.parseColor("#007F00"));
                status.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0f));
                recipeName.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,1f));
            }

            status.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (position != recipies.size()-1){
                        //usuń
                        new AlertDialog.Builder(context)
                                .setTitle("Skasuj przepis")
                                .setMessage("Czy jesteś pewien, że chcesz skasować " + recipies.get(position).getName().toLowerCase()  + "?")
                                .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                                {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        sendMessageToBarman("DELETE_RECIPE " + recipe.getName()+"\r\n");
                                        MenuActivity.getBarman().removeRecipe(recipe.getName(),getBaseContext());
                                        recipies.clear();
                                        recipies.addAll(MenuActivity.getBarman().getRecipies());
                                        recipies.add(new Recipe("",new ArrayList<RecipeItem>()));
                                        notifyDataSetChanged();
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
                    else{//dodaj
                        Intent intent = new Intent(context, NewRecipeActivity.class);
                        startActivity(intent);
                    }
                }
            });
            return view;
        }

    }

}
