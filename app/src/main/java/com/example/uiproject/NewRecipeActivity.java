package com.example.uiproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputType;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AbsListView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class NewRecipeActivity extends AppCompatActivity {

    private ArrayList<RecipeSubject> recipeSubjects = new ArrayList<>();
    private ListView lv;
    private ArrayAdapter<RecipeSubject> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_recipe);
        setupUI(findViewById(R.id.parent));


        recipeSubjects.add(new RecipeSubject("Test",1000,true));
        recipeSubjects.add(new RecipeSubject("+",0,false));

        lv = (ListView)findViewById(R.id.skladniki);

        adapter = new NewRecipeActivity.StableArrayAdapter(this, 0, recipeSubjects);
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


    private class StableArrayAdapter extends ArrayAdapter<RecipeSubject> {

        private Context context;
        private List<RecipeSubject> recipeSubjects;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<RecipeSubject> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.recipeSubjects = objects;
        }

        //called when rendering the list
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
        public View getView(final int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            final RecipeSubject recipe = recipeSubjects.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.recipe_subject_layout, null);

            EditText subject = (EditText) view.findViewById(R.id.skladnik);
            EditText value = (EditText) view.findViewById(R.id.ilosc);


            //set price and rental attributes
            if (recipe.getStatus()){
                subject.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0.4f
                ));
                value.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0.6f
                ));
                subject.setText(recipe.getName());
                subject.setInputType(InputType.TYPE_CLASS_TEXT);
                subject.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_START);
                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, false);
                subject.setBackgroundResource(outValue.resourceId);
                subject.setClickable(false);
                value.setText(Integer.toString(recipe.getValue()));
            }
            else {
                subject.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        0.0f
                ));
                value.setLayoutParams(new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        1.0f
                ));
                subject.setText(recipe.getName());
                subject.setInputType(InputType.TYPE_NULL);
                subject.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                TypedValue outValue = new TypedValue();
                getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                subject.setBackgroundResource(outValue.resourceId);
                subject.setFocusableInTouchMode(false);

                value.setText(Integer.toString(0));

                System.out.println(position + " : " + recipeSubjects.size());
                subject.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        recipeSubjects.add(position,new RecipeSubject("",0,true));
                        notifyDataSetChanged();
                    }
                });
            }

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

    public static void hideSoftKeyboard(Activity activity) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) activity.getSystemService(
                        Activity.INPUT_METHOD_SERVICE);
        if(activity.getCurrentFocus() != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    activity.getCurrentFocus().getWindowToken(), 0);
        }

    }

    public void setupUI(View view) {

        // Set up touch listener for non-text box views to hide keyboard.
        if (!(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                public boolean onTouch(View v, MotionEvent event) {
                    hideSoftKeyboard(NewRecipeActivity.this);
                    return false;
                }
            });
        }

        //If a layout container, iterate over children and seed recursion.
        if (view instanceof ViewGroup) {
            for (int i = 0; i < ((ViewGroup) view).getChildCount(); i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                setupUI(innerView);
            }
        }
    }
}
