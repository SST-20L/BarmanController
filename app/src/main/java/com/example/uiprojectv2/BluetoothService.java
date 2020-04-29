package com.example.uiprojectv2;

import android.app.ProgressDialog;
import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class BluetoothService extends Service {
    private static final String TAG = "MY-DEB BlueServ";
    //"00001101-0000-1000-8000-00805f9b34fb
    private UUID deviceUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB");
    private StringBuilder recDataString = new StringBuilder();
    private BluetoothDevice device;
    private String deviceAddress;
    private BluetoothAdapter mBluetoothAdapter;
    private BluetoothSocket btSocket;
    private ConnectedThread mConnectedThread;
    private Handler bluetoothHandler;
    private IBinder myBinder = new LocalBinder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    public class LocalBinder extends Binder {
        public BluetoothService getService() {
            return BluetoothService.this;
        }
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "Destroy service");
        super.onDestroy();
        unregisterReceiver(broadCastReceiverBluetooth);
        disconnect();
    }

    public void write(String input) {
        if (mConnectedThread != null) {
            mConnectedThread.write(input);
        }
    }

    public void exit(){
        sendMessageToActivities("unbound");
        disconnect();
        stopSelf();
    }

    public void disconnect(){
        Log.d(TAG, "Disconnect service");
        if(mConnectedThread != null) {
            mConnectedThread.closeStream();
        }
        try {
            if(btSocket != null) {
                btSocket.close();
            }
        }catch(IOException e){

        }
        btSocket = null;
    }

    private void sendMessageToActivities(String input) {
        Intent intent = new Intent("barman-msg");
        intent.putExtra("message", input);
        sendBroadcast(intent);
    }

    private final BroadcastReceiver broadCastReceiverBluetooth = new BroadcastReceiver() {
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(mBluetoothAdapter.ACTION_STATE_CHANGED)) {
                final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, mBluetoothAdapter.ERROR);
                if (state == BluetoothAdapter.STATE_OFF) {
                    Log.d(TAG, "Sth happen 2");
                    sendMessageToActivities("goToStartActivity");
                    disconnect();
                }
            } else {
                sendMessageToActivities("goToStartActivity");
                disconnect();
            }
        }
    };

    @Override
    public void onCreate() {
        Log.d(TAG, "Create service");
        super.onCreate();

        IntentFilter filter = new IntentFilter();
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECTED);
        filter.addAction(BluetoothDevice.ACTION_ACL_DISCONNECT_REQUESTED);
        registerReceiver(broadCastReceiverBluetooth, filter);

        bluetoothHandler = new Handler(Looper.getMainLooper()) {
            @Override
            public void handleMessage(@NonNull Message msg) {
                super.handleMessage(msg);
                if (msg.what == 0) {
                    String readMessage = (String) msg.obj;
                    recDataString.append(readMessage);
                    while(true){
                        int endOfLineIndex = recDataString.indexOf("~");
                        if (endOfLineIndex > 0) {
                            String dataInPrint = recDataString.substring(1, endOfLineIndex);
                            if (recDataString.charAt(0) == '#') {
                                sendMessageToActivities(dataInPrint);
                            }
                            recDataString.delete(0, endOfLineIndex+1);
                        }else{
                            break;
                        }
                    }
                }
            }
        };
        Log.d(TAG, "End create service");
    }


    public void connectToDevice(String addressMAC) {
        Log.d(TAG, "Connect to device " + addressMAC);

        if (btSocket != null) {
            Log.d(TAG, "We must disconnect first");
            disconnect();
        }

        if (btSocket == null) {
            deviceAddress = addressMAC;
            new AsyncTaskConnection().execute();
        }
    }

    public void failedConnect(){
        Log.d(TAG, "Connect to device failed");
        sendMessageToActivities("enableDeviceList");
        sendMessageToActivities("goToMenu");
        sendMessageToActivities("barman test");
    }

    public void successConnect(){
        mConnectedThread = new ConnectedThread();

        if(mConnectedThread != null) {
            Log.d(TAG, "Connect to socket");
            mConnectedThread.start();
            if(mConnectedThread.openStream()){
                sendMessageToActivities("goToMenu");
            }
            Log.d(TAG, "Connect to device success");
        } else {
            Log.d(TAG, "Connect to device failed");
        }
        sendMessageToActivities("enableDeviceList");
    }


    private class AsyncTaskConnection extends AsyncTask<String, String, String> {

        private boolean connectionSuccess;

        @Override
        protected String doInBackground(String... strings) {
            connectionSuccess = false;
            mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            if(mBluetoothAdapter != null){
                Log.d(TAG, "Get Bluetooth Adapter");
                device = mBluetoothAdapter.getRemoteDevice(deviceAddress);
            }

            try {
                if(device != null){
                    Log.d(TAG, "Get Device Adapter");
                    btSocket = device.createRfcommSocketToServiceRecord(deviceUUID);
                }
            } catch(IOException e){
                btSocket = null;
                device = null;
            }

            try {
                if (btSocket != null) {
                    Log.d(TAG, "Get Socket");
                    btSocket.connect();
                    Log.d(TAG, "Connection success");
                    connectionSuccess = true;
                }
            } catch(IOException e){
                Log.d(TAG, "Cannot connect");
                btSocket = null;
                device = null;
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            if(connectionSuccess){
                successConnect();
            } else {
                failedConnect();
        }
    }
}

    public class ConnectedThread extends Thread {
        private InputStream mmInStream;
        private OutputStream mmOutStream;
        private boolean stopRunning = false;

        public void write(String input) {
            try {
                if(mmOutStream != null){
                    mmOutStream.write(input.getBytes());
                    mmOutStream.flush();
                }
            } catch (IOException e) {

            }
        }

        @Override
        public void run() {
            super.run();
            byte[] buffer = new byte[256];
            while (true) {
                if(stopRunning){
                    break;
                }
                if(mmInStream != null) {
                    try {
                        int bytes = mmInStream.read(buffer);
                        String readMessage =  new String(buffer, 0, bytes);
                        bluetoothHandler.obtainMessage(0, bytes, -1, readMessage).sendToTarget();
                    } catch (IOException e) {
                        break;
                    }
                }
            }

        }

        void closeStream(){
            try {
                if(mmInStream != null) {
                    mmInStream.close();
                }
                mmInStream = null;
            } catch (Exception r) {

            }
            try {
                if(mmOutStream != null) {
                    mmOutStream.close();
                }
                mmOutStream = null;
            } catch (Exception e) {

            }
            stopRunning = true;
        }

        boolean openStream(){
            InputStream tmpIn = null;
            OutputStream tmpOut = null;
            try {
                tmpIn = btSocket.getInputStream();
                tmpOut = btSocket.getOutputStream();
            } catch (IOException e) {

            }
            if(tmpIn != null && tmpOut != null){
                mmInStream = tmpIn;
                mmOutStream = tmpOut;

            }
            stopRunning = false;

            return (mmInStream != null && mmOutStream != null);
        }
    }
}
