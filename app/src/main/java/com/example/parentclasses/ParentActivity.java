package com.example.parentclasses;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import com.example.objectclasses.BarmanManager;
import com.example.uiprojectv2.BluetoothService;
import com.example.uiprojectv2.MenuActivity;

import java.io.IOException;

public class ParentActivity extends AppCompatActivity{
    private static final String TAG = "MY-DEB Parent";
    protected BluetoothService myService;
    Boolean isBound = false;
    private int counterBound = 0;
    private int counterUnbound = 0;
    private Context ctx;

    protected void sendMessageToBarman(String message) {
        Log.d(TAG, "Sending to microcontroller "+message);
        if(myService != null){
            myService.write(message);
        }
    }

    public void onCreate(Bundle savedInstanceState, int layout, int parent) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(layout);
        setupUI(findViewById(parent));
    }

    protected void afterServiceConnected(){

    }



    private ServiceConnection myConnection =new ServiceConnection(){



        public void onServiceDisconnected(    ComponentName name){
            isBound = false;
            myService = null;
        }
        public void onServiceConnected(    ComponentName name,    IBinder service){
            BluetoothService.LocalBinder binder = (BluetoothService.LocalBinder) service;
            myService = binder.getService();
            isBound = true;
            afterServiceConnected();
        }
    };


    protected void enableDeviceList(){

    }

    protected void updateActivity(String msg){

    }

    private final BroadcastReceiver broadcastBluetoothMessage= new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String message = intent.getStringExtra("message");

            switch(message) {
                case "goToMenu": {
                    BarmanManager barman = null;
                    try {
                        barman = new BarmanManager("Testowy", getBaseContext());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    Intent newIntent = new Intent(getApplicationContext(), MenuActivity.class);
                    newIntent.putExtra("Barman", barman);
                    startActivity(newIntent);
                    break;
                }
                case "enableDeviceList":
                    enableDeviceList();
                    break;
                case "unbound":
                    if(isBound){
                        unbindService(myConnection);
                        isBound = false;
                    }
                    break;
                default : {
                    Log.d(TAG, "default message for " + message);
                    updateActivity(message);
                }

            }
        }
    };

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

    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();

        registerReceiver(broadcastBluetoothMessage, new IntentFilter("barman-msg"));

        counterBound = counterBound + 1;
        Log.d(TAG, "Service bound " + counterBound);

        if(!isBound){
            Intent intent = new Intent(getApplicationContext(), BluetoothService.class);
            bindService(intent, myConnection, Context.BIND_AUTO_CREATE);
            isBound = true;
        }

    }

    @Override
    protected void onPause() {
        super.onPause();

        counterUnbound = counterUnbound + 1;
        Log.d(TAG, "Service unbound " + counterUnbound);

        if(isBound){
            unbindService(myConnection);
            isBound = false;
        }
        unregisterReceiver(broadcastBluetoothMessage);
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
                    hideSoftKeyboard(ParentActivity.this);
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

    private static final int TIME_INTERVAL = 2000; // # milliseconds, desired time passed between two back presses.
    private long mBackPressed;

    public void onBackPressed(boolean noBackActivity, String text) {
        if (noBackActivity) {
            if (mBackPressed + TIME_INTERVAL > System.currentTimeMillis()) {
                //jeżeli chcemy wyjść z aplikacji to musimy jeszcze sprawdzić "text", bo w ekranie progresu też tutaj wchodzi jeżeli trwa polewanie
                if (!text.equals("Wciśnij ponownie, aby cofnąć")) finishService();
                super.onBackPressed();
                return;
            } else {
                Toast.makeText(this, text, Toast.LENGTH_SHORT).show();
            }
            mBackPressed = System.currentTimeMillis();
        }
        else{
            super.onBackPressed();
        }
    }

    void finishService(){
        if(myService!= null){
            myService.exit();
        }
    }

    public void sendBack(View view) {
        this.onBackPressed();
    }

    public void sendBackMessageAlert(View view){
        new AlertDialog.Builder(this)
                .setTitle("Cofnij")
                .setMessage("Czy jesteś pewien, że chcesz cofnąć?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ParentActivity.super.onBackPressed();
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

    public void sendExitMessageAlert(View view){
        new AlertDialog.Builder(this)
                .setTitle("Zamykanie aplikacji")
                .setMessage("Czy jesteś pewien, że chcesz zamknąć aplikację?")
                .setPositiveButton("Tak", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishService();
                        ParentActivity.super.onBackPressed();
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

}
