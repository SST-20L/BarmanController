package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.example.objectclasses.BarmanManager;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceSelectActivity extends ParentActivity {

    public ArrayList<String> devicesArray;
    private ArrayAdapter<String> adapter;

    String[] devices = new String[] { "Barman_1", "Słuchawki", "Smartfon Ewelinki lezacy na stoliku",
            "Barman_2", "Głośnik drechola", "Telewizor sąsiadki" };

    public DeviceSelectActivity(){
        super();
        devices = addDevice(devices, "Dodatkowo");

        devicesArray = new ArrayList<>();
        for (int i = 0; i < devices.length; ++i) {
            devicesArray.add(devices[i]);
        }
    }

    private String[] addDevice(String[] devices, String device){
        String[] newArray = new String[devices.length+1];
        for (int i=0; i<devices.length; i++){
            newArray[i]=devices[i];
        }
        newArray[devices.length]=device;
        return newArray;
    }



    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_device_select, R.id.parent);

        System.out.println(devicesArray.size());
        ListView lv = findViewById(R.id.device_device_list);

        adapter = new StableArrayAdapter(this, 0, devicesArray);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),"Połączono z: " + devices[position],Toast.LENGTH_SHORT).show();
                BarmanManager barman = new BarmanManager("Testowy");
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                intent.putExtra("Barman", barman);
                startActivity(intent);
            }

        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {

        private Context context;
        private List<String> recipeList;

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            this.context = context;
            this.recipeList = objects;
        }

        public View getView(final int position, View convertView, ViewGroup parent) {

            final String item = devicesArray.get(position);

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            View view = inflater.inflate(R.layout.single_textview_layout, null);

            TextView tv = view.findViewById(R.id.single_txtview);
            tv.setTypeface(ResourcesCompat.getFont(context, R.font.rubik_mono_one));
            tv.setText(item);
            tv.setSelected(true);

            return view;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed(true, "Wciśnij ponownie, aby wyjść");
    }

}
