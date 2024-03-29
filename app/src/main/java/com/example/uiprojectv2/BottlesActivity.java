package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.parentclasses.ParentActivity;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class BottlesActivity extends ParentActivity {
    private static final String TAG = "MY-DEB BottleAc";
    private ArrayList<String> bottles = new ArrayList<>();
    private ArrayAdapter<String> adapter;
    private ListView lv;

    public boolean savedChange = true;

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_bottles, R.id.parent);
        bottles.addAll(MenuActivity.getBarman().getBottles());

        lv = findViewById(R.id.bottles_list);

        adapter = new StableArrayAdapter(this, 0, bottles);
        lv.setAdapter(adapter);

    }


    public void accept() {

        int itemNo = lv.getChildCount();

        for (int i = 0; i < itemNo; i++){
            EditText bottleField = (EditText)lv.getChildAt(i).findViewById(R.id.single_edittext);
            String name = bottleField.getText().toString();
            if(!bottles.get(i).equals(name)){
                Log.d(TAG, "butelki" + bottles.get(i) + " " + name );
                bottles.set(i,name);
                sendMessageToBarman("SET-BOTTLE+"+ i + "+" + name + "\0");
            }
        }
        MenuActivity.getBarman().changeBottles(bottles,getBaseContext());

        savedChange = true;
        adapter.notifyDataSetChanged();

        Toast.makeText(this,"Zapisano zmiany",Toast.LENGTH_SHORT).show();
        TextView tv = findViewById(R.id.bottles_accept);
        tv.setTextColor(Color.parseColor("#CDABAD"));
        tv.setClickable(false);
        TypedValue outValue = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, false);
        tv.setBackgroundResource(outValue.resourceId);
    }

    @Override
    public void onBackPressed(){
        if (savedChange) super.onBackPressed();
        else super.onBackPressed(true, "Wciśnij ponownie, aby cofnąć i anulować zmiany");
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        private Context context;

        public StableArrayAdapter(Context context, int textViewResourceId,
                                  List<String> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
        }

        //called when rendering the list
        public View getView(final int position, View convertView, ViewGroup parent) {

            //get the property we are displaying
            final String bottle = bottles.get(position);

            //get the inflater and inflate the XML layout for each item
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.single_edittext_layout, null);

            EditText bottleField = view.findViewById(R.id.single_edittext);



            //set price and rental attributes
            bottleField.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            bottleField.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
            bottleField.setText(bottle);

            bottleField.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    savedChange = false;
                    TextView tv = findViewById(R.id.bottles_accept);
                    tv.setTextColor(Color.parseColor("#3D2B3D"));
                    tv.setClickable(true);
                    TypedValue outValue = new TypedValue();
                    getContext().getTheme().resolveAttribute(android.R.attr.selectableItemBackground, outValue, true);
                    tv.setBackgroundResource(outValue.resourceId);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            accept();
                        }
                    });
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
            return view;
        }

    }

}
