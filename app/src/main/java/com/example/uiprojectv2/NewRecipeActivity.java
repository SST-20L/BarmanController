package com.example.uiprojectv2;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.objectclasses.RecipeItem;
import com.example.parentclasses.ParentActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.database.DataSetObserver;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class NewRecipeActivity extends ParentActivity {

    public ArrayList<RecipeItem> NewRecipeItems = new ArrayList<>();
    private ListView lv;
    private ArrayAdapter<RecipeItem> adapter;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_new_recipe, R.id.parent);

        NewRecipeItems.add(new RecipeItem("",0));
        NewRecipeItems.add(new RecipeItem("+",0));

        lv = findViewById(R.id.new_rec_recipe_item_list);

        EditText name = findViewById(R.id.new_rec_name);
        name.setFilters(new InputFilter[] {new InputFilter.AllCaps()});

        //Ustaw adapter oraz parametr, że zawsze ma scrollować na sam dół
        adapter = new StableArrayAdapter(this, 0, NewRecipeItems);
        lv.setTranscriptMode(AbsListView.TRANSCRIPT_MODE_ALWAYS_SCROLL);
        lv.setAdapter(adapter);
        adapter.registerDataSetObserver(new DataSetObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                lv.setSelection(adapter.getCount() - 1);
            }
        });
    }

    private void updateList(){
        //przejdź po polach edycji i uzupełnij ArrayListę
        int itemNo = lv.getChildCount();
        for (int i = 0; i < itemNo-1; i++){
            EditText nameField = (EditText)lv.getChildAt(i).findViewById(R.id.name);
            EditText valueField = (EditText)lv.getChildAt(i).findViewById(R.id.volume);
            String name = nameField.getText().toString();
            int value;
            if (valueField.getText().toString().equals("")) value = 0;
            else value = Integer.parseInt(valueField.getText().toString());
            NewRecipeItems.set(i,new RecipeItem(name,value));
        }
    }

    public void accept(View view){
        updateList();

        EditText name = findViewById(R.id.new_rec_name);

        //Czy są podane składniki i czy ich nazwy się powtarzają?
        int size = 0;
        boolean repeated = false;
        boolean valueZero = false;
        for (int i = 0; i < NewRecipeItems.size(); i++){
            if ((i != NewRecipeItems.size()-1) && !NewRecipeItems.get(i).getName().equals("")) {
                size++;
                if (NewRecipeItems.get(i).getValue()==0) valueZero = true;
                for (int j = 0; j < NewRecipeItems.size(); j++){
                    if (i != j && NewRecipeItems.get(i).getName().equals(NewRecipeItems.get(j).getName())) {
                        repeated = true;
                        j = NewRecipeItems.size();
                    }
                }
                if (valueZero||repeated) i = NewRecipeItems.size();
            }
        }

        if (name.getText().toString().equals("")) Toast.makeText(this,"Wymagana nazwa przepisu",Toast.LENGTH_SHORT).show();
        else if (repeated) Toast.makeText(this,"Nazwa skladnika sie powtarza",Toast.LENGTH_SHORT).show();
        else if (valueZero) Toast.makeText(this,"Skladnik ma podana wielkosc 0",Toast.LENGTH_SHORT).show();
        else if (size==0){
            Toast.makeText(this,"Podaj skladniki",Toast.LENGTH_SHORT).show();
        }
        else {
            //Dodaj przepis, jeżeli nazwa nie jest już zajęta
            boolean result = MenuActivity.getBarman().addNewRecipe(name.getText().toString(), NewRecipeItems);
            if (result) super.onBackPressed();
            else Toast.makeText(this,"Zajeta nazwa",Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public void onBackPressed(){
        super.onBackPressed(true, "Wciśnij ponownie, aby cofnąć");
    }

    private class StableArrayAdapter extends ArrayAdapter<RecipeItem> {

        private Context context;

        public StableArrayAdapter(Context context, int textViewResourceId, ArrayList<RecipeItem> object) {
            super(context, textViewResourceId, object);
            this.context = context;
        }

        //renderowanie listy
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public View getView(final int position, final View convertView, final ViewGroup parent) {

            //weź aktualny element listy
            final RecipeItem recipe = NewRecipeItems.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            final View view = inflater.inflate(R.layout.double_edittext_layout, null);

            final EditText subject = (EditText) view.findViewById(R.id.name);
            final EditText value = (EditText) view.findViewById(R.id.volume);
            subject.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            value.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));

            //ustaw odpowiednie atrybuty layoutu na podstawie statusu (false = ostatni element listy, czyli przycisk "dodaj")
            if (position != NewRecipeItems.size()-1){
                subject.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,0.4f));
                value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,0.6f));

                subject.setInputType(InputType.TYPE_CLASS_TEXT|InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS | InputType.TYPE_TEXT_FLAG_CAP_CHARACTERS);
                subject.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                subject.setClickable(false);

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, false);
                subject.setBackgroundResource(outValue.resourceId);

                subject.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
                subject.setTextColor(Color.parseColor("#635D5C"));

                value.setTextColor(Color.parseColor("#635D5C"));

                subject.setText(recipe.getName());
                value.setText(Integer.toString(recipe.getValue()));

            }
            else {
                subject.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,0.0f));
                value.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,ViewGroup.LayoutParams.MATCH_PARENT,1.0f));

                subject.setInputType(InputType.TYPE_NULL);
                subject.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                subject.setFocusableInTouchMode(false);

                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                subject.setBackgroundResource(outValue.resourceId);

                subject.setTextColor(Color.parseColor("#007F00"));

                subject.setText("+");
                value.setText("0");

                subject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        updateList();
                        //nowy pusty element do wypełnienia
                        NewRecipeItems.add(position,new RecipeItem("",0));
                        notifyDataSetChanged();
                    }
                });
            }
            return view;
        }

    }


}
