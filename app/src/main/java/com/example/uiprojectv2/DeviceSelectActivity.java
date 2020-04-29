package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;

import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;
import java.util.List;

public class DeviceSelectActivity extends ParentActivity {
    private static final String TAG = "MY-DEB DeviceSelA";
    public ArrayList<String> devicesName = new ArrayList<>();
    public ArrayList<String> devicesAddress = new ArrayList<>();
    private BluetoothAdapter mBluetoothAdapter;
    private ArrayAdapter<String> adapter;
    private ListView lv;

    private final BroadcastReceiver broadcastBluetoothState = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                if(state == BluetoothAdapter.STATE_ON){
                    findAndDisplayDevices();
                }
            }
        }
    };

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastBluetoothState);
    }

    @Override
    protected void onResume() {
        super.onResume();

        registerReceiver(broadcastBluetoothState,new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));

        findAndDisplayDevices();
    }

    protected void enableDeviceList(){
        Log.d(TAG, "Enable listView");
        lv.setClickable(true);
        lv.setEnabled(true);
        lv.setFocusable(true);
    }

    private void findAndDisplayDevices() {
        lv = findViewById(R.id.device_device_list);

        mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        Log.d(TAG, "There are " + ((Integer)mBluetoothAdapter.getBondedDevices().size()).toString() + " devices");

        devicesAddress.clear();
        devicesName.clear();

        for (BluetoothDevice device : mBluetoothAdapter.getBondedDevices()) {
            if(device.getName() != null && device.getAddress() != null){
                devicesName.add(device.getName());
                devicesAddress.add(device.getAddress());
            }
        }

        adapter = new StableArrayAdapter(this, 0, devicesName);
        lv.setAdapter(adapter);

        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String text = (String)(lv.getItemAtPosition(position));
                int pos = devicesName.indexOf(text);
                String address = devicesAddress.get(pos);
                if(myService != null) {
                    Log.d(TAG, "Trying to connect to device " + address);
                    myService.connectToDevice(address);
                    lv.setClickable(false);
                    lv.setFocusable(false);
                    lv.setEnabled(false);
                }
            }
        });
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_device_select, R.id.parent);
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

            final String item = devicesName.get(position);

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
