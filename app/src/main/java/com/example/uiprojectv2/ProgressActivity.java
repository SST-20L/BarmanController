package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.objectclasses.RecipeItem;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;

public class ProgressActivity extends ParentActivity {
    private static final String TAG = "MY-DEB ProgressA";
    private String selectedRecipe;

    private double progress;
    private final static double progressCeil = 0.85;
    private final static double progressMax = 1000;

    private double realProgress;
    private double recipeMaxProgress;

    boolean finished = false;

    protected void afterServiceConnected(){
        Log.d(TAG, "Start pouring " + selectedRecipe);
        ArrayList<RecipeItem> RecipeItems = MenuActivity.getBarman().getRecipe(selectedRecipe);
        sendMessageToBarman("START_RECIPE\r\n");
        sendMessageToBarman(RecipeItems.size() + "\r\n");
        for(int i = 0 ; i < RecipeItems.size(); ++i){
            sendMessageToBarman(RecipeItems.get(i).name + "+" + RecipeItems.get(i).value +"\r\n");
        }
        MenuActivity.getBarman().setStartRecipe();
    }

    protected void updateActivity(String msg){
        MenuActivity.getBarman().parseMessage(msg);
        if(MenuActivity.getBarman().checkEndRecipe()){
            Log.d(TAG, "End recipe "+ selectedRecipe);
        }
    }

    @SuppressLint("MissingSuperCall")
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState, R.layout.activity_progress, R.id.parent);
        selectedRecipe = (String) getIntent().getSerializableExtra("selectedRecipe");

        TextView recipeTV = (TextView) findViewById(R.id.progress_name);
        recipeTV.setSelected(true);
        recipeTV.setText(selectedRecipe);

        initRecipeProgress();

        calculateAndSetProgress();

        //do zastąpienia przez listenera na bt - odbieranie dopóki realprogress<recipeMaxProgress
        new CountDownTimer(10000,10) {

            public void onTick(long millisUntilFinished) {
                double diff = 10000 - millisUntilFinished;
                realProgress = (diff/(double)10000) * recipeMaxProgress;
                calculateAndSetProgress();
            }

            public void onFinish() {
                realProgress = recipeMaxProgress;
                calculateAndSetProgress();
            }

        }.start();

    }

    //dla testów
    private void initRecipeProgress(){
        realProgress = 0;
        recipeMaxProgress = 65;
    }

    @Override
    public void onBackPressed(){
        if (finished) super.onBackPressed();
        else super.onBackPressed(true, "Wciśnij ponownie, aby cofnąć");
    }

    private void setProgress(){
        TextView progressText = findViewById(R.id.progress_perc);
        String progressStr = (int)Math.floor(realProgress/recipeMaxProgress*100) + "%";
        progressText.setText(progressStr);

        ProgressBar pb = findViewById(R.id.vertical_progressbar);
        pb.setProgress((int)progress);
    }

    private void calculateAndSetProgress(){
        progress = Math.floor((realProgress/recipeMaxProgress) * progressCeil * progressMax);
        setProgress();
        if (realProgress>=recipeMaxProgress){
            finished = true;
            TextView progressClose = findViewById(R.id.progress_close);
            progressClose.setTextColor(Color.parseColor("#3D2B3D"));
            progressClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressActivity.super.onBackPressed();
                }
            });
        }
    }

}
