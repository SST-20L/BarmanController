package com.example.uiprojectv2;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.objectclasses.RecipeItem;
import com.example.parentclasses.ParentActivity;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

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

    private final Handler customHandler = new Handler();

    protected void afterServiceConnected() {
        ArrayList<String> bottles = new ArrayList<>();
        bottles.addAll(MenuActivity.getBarman().getBottles());
        for (int i = 0; i < bottles.size(); ++i) {
            sendMessageToBarman("SET-BOTTLE+" + i + "+" + bottles.get(i) + "\0");
        }
        Log.d(TAG, "Start pouring " + selectedRecipe);
        ArrayList<RecipeItem> RecipeItems = MenuActivity.getBarman().getRecipe(selectedRecipe);
        sendMessageToBarman("START_RECIPE\0");
        sendMessageToBarman(RecipeItems.size() + "\0");
        for (int i = 0; i < RecipeItems.size(); ++i) {
            sendMessageToBarman(RecipeItems.get(i).name + "+" + RecipeItems.get(i).value + "\0");
        }
        MenuActivity.getBarman().setStartRecipe();
    }

    protected void updateActivity(String msg) {
        MenuActivity.getBarman().parseMessage(msg);
        if (MenuActivity.getBarman().checkEndRecipe()) {
            Log.d(TAG, "End recipe " + selectedRecipe);
            realProgress = recipeMaxProgress;

            stoptimertask();

            finished = true;
            TextView progressClose = findViewById(R.id.progress_close);
            progressClose.setTextColor(Color.parseColor("#3D2B3D"));
            progressClose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ProgressActivity.super.onBackPressed();
                }
            });
        } else {
            realProgress = MenuActivity.getBarman().getProgress();
            Log.d(TAG, "real progress : " + realProgress);

            if (vProgress == nProgress) prevProgress = nProgress;


            double realPercentage = realProgress / recipeMaxProgress;
            nProgress = Math.floor(realPercentage * progressCeil * progressMax);
            Log.d(TAG, "n progress : " + nProgress);
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

        startTimer();

    }


    @Override
    public void onBackPressed() {
        if (finished) super.onBackPressed();
        else super.onBackPressed(true, "Wciśnij ponownie, aby cofnąć");
    }

    private void setProgress() {
        TextView progressText = findViewById(R.id.progress_perc);
        int viewableProgress = (int) Math.floor(realProgress / recipeMaxProgress * 100);

        String progressStr = viewableProgress + "%";
        progressText.setText(progressStr);

        ProgressBar pb = findViewById(R.id.vertical_progressbar);
        pb.setProgress((int) vProgress);
    }


    Timer timer;
    TimerTask timerTask;

    public void startTimer() {
        //set a new Timer
        timer = new Timer();

        //initialize the TimerTask's job
        initializeTimerTask();

        //schedule the timer, after the first 5000ms the TimerTask will run every 10000ms
        timer.schedule(timerTask, 0, 50); //
    }

    public void stoptimertask() {
        //stop the timer, if it's not already null
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    public void initializeTimerTask() {

        timerTask = new TimerTask() {
            public void run() {

                //use a handler to run a toast that shows the current timestamp
                customHandler.post(new Runnable() {
                    public void run() {
                        //get the current timeStamp
                        if (vProgress <= nProgress) {
                            vProgress = vProgress + (double) (1 / 100) * (nProgress - prevProgress);
                            if (vProgress>nProgress) vProgress = nProgress;
                        }
                        Log.d(TAG, "UpdateTimer v progress : " + vProgress);
                        setProgress();
                    }
                });
            }
        };
    }

}
