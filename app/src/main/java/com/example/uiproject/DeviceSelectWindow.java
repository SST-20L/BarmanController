package com.example.uiproject;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DeviceSelectWindow extends AppCompatActivity {

    String[] values = new String[] { "Barman_1", "Słuchawki", "Smartfon Ewelinki",
            "Barman_2", "Głośnik drechola", "Telewizor sąsiadki", "Drukarka brata", "Urządzenie butuf",
            "Zdalnie sterowana rakieta", "STM32_test", "Komputer mamy", "iPhone 20XS Plus Max Pro", "Urządzenie 1", "Urządzenie 2",
            "Urządzenie 3", "Ile jeszcze", "Potrzeba elementów", "Żeby trzeba", "Było całkiem", "Długo scrollować",
            "Wtedy lepiej", "Wszystko widać", "A nie tak pusto" };

    private String[] addDevice(String[] devices, String device){
        String[] newArray = new String[devices.length+1];
        for (int i=0; i<devices.length; i++){
            newArray[i]=devices[i];
        }
        newArray[devices.length]=device;
        return newArray;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_device_select);

        final ListView listview = (ListView) findViewById(R.id.device_list);

        values = addDevice(values, "Dodatkowo");

        final ArrayList<String> list = new ArrayList<String>();
        for (int i = 0; i < values.length; ++i) {
            list.add(values[i]);
        }

        final StableArrayAdapter adapter = new StableArrayAdapter(this,
                android.R.layout.simple_list_item_1, list);
        listview.setAdapter(adapter);

        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                Toast.makeText(getApplicationContext(),"Uruchomiono: " + values[position],Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MenuActivity.class);
                startActivity(intent);
            }

        });
    }


    private class StableArrayAdapter extends ArrayAdapter<String> {
        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
        }
    }

    public void sendExit(View view) {
        new AlertDialog.Builder(this)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setTitle("Zamykanie aplikacji")
                .setMessage("Czy jesteś pewien, że chcesz zamknąć aplikację?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        DeviceSelectWindow.super.onBackPressed();
                    }

                })
                .setNegativeButton("Nie", null)
                .show();
    }


    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();
    }


    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;
    private Toast exit;

    @Override
    public void onBackPressed()
    {
        if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis())
        {
            super.onBackPressed();
            return;
        }
        else {
            exit.makeText(this, "Wciśnij ponownie, aby wyjść", Toast.LENGTH_SHORT).show();
        }

        mBackPressed = System.currentTimeMillis();
    }

    @Override
    public void onDestroy(){
        try{
            exit.cancel();
        }
        catch (NullPointerException e){}
        super.onDestroy();
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
