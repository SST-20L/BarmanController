package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
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

import java.util.List;

public class RecipeDetailsActivity extends ParentActivity {

    public Recipe recipe;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_recipe_details, R.id.parent);
        recipe = (Recipe) getIntent().getSerializableExtra("selectedRecipe");

        ListView lv = findViewById(R.id.details_list);

        StableArrayAdapter adapter = new StableArrayAdapter(this, 0, recipe.getItems());
        lv.setAdapter(adapter);
    }


    private class StableArrayAdapter extends ArrayAdapter<RecipeItem> {

        private Context context;

        public StableArrayAdapter(Context context, int textViewResourceId, List<RecipeItem> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final RecipeItem item = recipe.getItems().get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.double_textview_layout, null);

            TextView nameTv = view.findViewById(R.id.name);
            TextView valueTv = view.findViewById(R.id.option);

            nameTv.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            nameTv.setText(item.getName());
            nameTv.setSelected(true);
            nameTv.setTextColor(Color.parseColor("#635D5C"));
            nameTv.setTextSize(20);

            valueTv.setText(Integer.toString(item.getValue()));
            valueTv.setTextColor(Color.parseColor("#635D5C"));
            valueTv.setTextSize(20);

            TypedValue outValue = new TypedValue();
            getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, false);
            valueTv.setBackgroundResource(outValue.resourceId);


            nameTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.4f));
            valueTv.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.WRAP_CONTENT,0.6f));

            return view;
        }
    }
}
