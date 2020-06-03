package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.SystemClock;
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

    private final static double progressCeil = 0.85; //(mnożnik progresu - żeby szklanka nie była pełna, tylko się kończyła animacja na końcówce)
    private final static double progressMax = 1000; //maks progresu - jakby było 100 to animacja nie jest płynna

    private double realProgress = 0; //progres rzeczywisty (w gramach)
    private double recipeMaxProgress = 0; //maksymalny progres rzeczywisty (w gramach)

    private double nProgress = 0; //progres docelowy do wyświetlenia (z 1000)
    private double vProgress = 0; //progres do wyświetlenia
    private double prevProgress = 0; //ostatni stan progresu

    boolean finished = false;

    private Handler customHandler = new Handler();

    protected void afterServiceConnected(){
        ArrayList<String> bottles = new ArrayList<>();
        bottles.addAll(MenuActivity.getBarman().getBottles());
        for(int i = 0; i < bottles.size(); ++i){
            sendMessageToBarman("SET-BOTTLE+"+ i + "+" + bottles.get(i) + "\0");
        }
        Log.d(TAG, "Start pouring " + selectedRecipe);
        ArrayList<RecipeItem> RecipeItems = MenuActivity.getBarman().getRecipe(selectedRecipe);
        sendMessageToBarman("START_RECIPE\0");
        sendMessageToBarman(RecipeItems.size() + "\0");
        for(int i = 0 ; i < RecipeItems.size(); ++i){
            sendMessageToBarman(RecipeItems.get(i).name + "+" + RecipeItems.get(i).value +"\0");
        }
        MenuActivity.getBarman().setStartRecipe();
    }

    protected void updateActivity(String msg){
        MenuActivity.getBarman().parseMessage(msg);
        if(MenuActivity.getBarman().checkEndRecipe()){
            Log.d(TAG, "End recipe "+ selectedRecipe);
            realProgress = recipeMaxProgress;

            customHandler.removeCallbacks(updateTimerThread);

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
        else{
            realProgress = MenuActivity.getBarman().getProgress();

            if (vProgress==nProgress) prevProgress=nProgress;

            double realPercentage = realProgress/recipeMaxProgress;
            nProgress = Math.floor(realPercentage * progressCeil * progressMax);
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


        ArrayList<RecipeItem> RecipeItems = MenuActivity.getBarman().getRecipe(selectedRecipe);
        for (RecipeItem item : RecipeItems) {
            recipeMaxProgress += item.getValue();
        }

        setProgress();

        //do zastąpienia przez listenera na bt - odbieranie dopóki realprogress<recipeMaxProgress

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

    }


    @Override
    public void onBackPressed(){
        if (finished) super.onBackPressed();
        else super.onBackPressed(true, "Wciśnij ponownie, aby cofnąć");
    }

    private void setProgress(){
        TextView progressText = findViewById(R.id.progress_perc);
        int viewableProgress = (int)Math.floor(realProgress/recipeMaxProgress*100);

        String progressStr = viewableProgress + "%";
        progressText.setText(progressStr);

        ProgressBar pb = findViewById(R.id.vertical_progressbar);
        pb.setProgress((int)vProgress);
    }

    private long startTime = 0L;
    private long pMillis = 0L;

    private Runnable updateTimerThread = new Runnable() {


        public void run() {

            long cMillis = SystemClock.uptimeMillis() - startTime;

            if (pMillis!= cMillis){
                int milliseconds = (int) (cMillis % 1000);
                if (milliseconds%10==0){

                    if (vProgress!=nProgress){
                        vProgress=vProgress+(double)(1/100)*(nProgress-prevProgress);
                    }
                    setProgress();
                    pMillis= cMillis;
                    customHandler.postDelayed(this, 0);
                }
            }
        }

    };
}
