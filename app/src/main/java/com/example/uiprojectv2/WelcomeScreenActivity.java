package com.example.uiprojectv2;

import com.example.parentclasses.ParentActivity;

import android.annotation.SuppressLint;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

public class WelcomeScreenActivity extends ParentActivity {
    private static final String TAG = "MY-DEB WelcomeA";
    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_welcome, R.id.parent);
        Log.d(TAG, "Create first screen");

        Intent intent =new Intent(getApplicationContext(), BluetoothService.class);
        startService(intent);

        //wyłączenie długiego kliknięcia
        ImageView img = findViewById(R.id.welcome_imgbackground);
        img.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                return true;
            }
        });
    }

    private final static int REQUEST_ENABLE_BT=1;
    BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

    public void sendMessage(View view) {
        if (mBluetoothAdapter == null) {
            Toast.makeText(this, "Brak wsparcia dla Bluetooth w twoim urządzeniu", Toast.LENGTH_SHORT).show();
        }
        else if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityIfNeeded(enableBtIntent, REQUEST_ENABLE_BT);
        }
        else{
            Intent intent = new Intent(this, DeviceSelectActivity.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(resultCode){
            case RESULT_OK:
                Intent intent = new Intent(this, DeviceSelectActivity.class);
                startActivity(intent);
                break;
            case RESULT_CANCELED:
                Toast.makeText(this, "Proszę włączyć bluetooth", Toast.LENGTH_SHORT).show();
            default:
                break;
        }
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed(true, "Wciśnij ponownie, aby wyjść");
    }

}
