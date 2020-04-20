package com.example.uiproject;

import android.content.Intent;
import android.os.Build;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

public class MenuActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }

    public void handleOnClick(View view)
    {
        Intent intent;
        switch(view.getId())
        {
            case R.id.zaz_przep:
                System.out.println("Zarządzaj przepisami");
                intent = new Intent(this, RecipeActivity.class);
                startActivity(intent);
                break;
            case R.id.roz_pol:
                System.out.println("Polewaj");
                intent = new Intent(this, StartActivity.class);
                startActivity(intent);
                break;
            case R.id.dozowniki:
                System.out.println("Dozowniki");
                intent = new Intent(this, BottleActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    public void sendExit(View view) {
        this.onBackPressed();
    }

    @Override
    protected void onResume() {
        super.onResume();
        FullScreencall();

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
