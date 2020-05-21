package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.objectclasses.BarmanManager;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;

public class MenuActivity extends ParentActivity {
    private static final String TAG = "MY-DEB MenuA";
    private static BarmanManager barman;


    protected void afterServiceConnected(){
        ArrayList<String> bottles = new ArrayList<>();
        bottles.addAll(MenuActivity.getBarman().getBottles());
        for(int i = 0; i < bottles.size(); ++i){
            sendMessageToBarman("SET-BOTTLE+"+ i + "+" + bottles.get(i) + "\r\n");
        }
    }

    protected void updateActivity(String msg){
        barman.parseMessage(msg);
        Log.d(TAG, "Getting message "+ msg);
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState, R.layout.activity_menu, R.id.parent);
        setBarman((BarmanManager) getIntent().getSerializableExtra("Barman"));
    }

    public void handleOnClick(View view)
    {
        Intent intent = new Intent();
        switch(view.getId())
        {
            case R.id.menu_recipies:
                intent.setClass(this,RecipeActivity.class);
                break;
            case R.id.menu_pour:
                intent.setClass(this,PourActivity.class);
                break;
            case R.id.menu_bottles:
                intent.setClass(this,BottlesActivity.class);
                break;
            default:
                break;
        }
        intent.putExtra("Barman", getBarman());
        startActivity(intent);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed(true, "Wciśnij ponownie, aby wyjść");
    }

    public static BarmanManager getBarman() {
        return MenuActivity.barman;
    }

    public static void setBarman(BarmanManager barman) {
        MenuActivity.barman = barman;
    }
}
