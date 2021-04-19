package com.example.pikachu.Activity;

import android.annotation.SuppressLint;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pikachu.Controller.GraphicView;
import com.example.pikachu.FramentDialog.ExitFragmentDialog;
import com.example.pikachu.FramentDialog.LoseFragmentDialog;
import com.example.pikachu.FramentDialog.PauseFragmentDialog;
import com.example.pikachu.R;

import java.util.concurrent.TimeUnit;

import static com.example.pikachu.Controller.GraphicView.LevelGame;
import static com.example.pikachu.Controller.GraphicView.TongDiem;
import static com.example.pikachu.Controller.GraphicView.countHelp;
import static com.example.pikachu.Controller.GraphicView.nhacKhiLose;

public class MainActivity extends AppCompatActivity {

    public static ImageView backgroundGame;
    public static ProgressBar progressBar;
    public static TextView tv_timeProgressBar, tv_marks, tv_level, tv_help;
    ImageButton ib_pause;

    public static GraphicView graphicView;

    public static Thread threadMain;
    public Handler handler = new Handler();
    public static int progressBarStatus = 374000;
    public static boolean pauseFlag = false;

    public static MediaPlayer  musicPlay;
    public static MediaPlayer musicCountdownTime;


    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.slide_in_right_two_activity, R.anim.slide_out_left_two_activity);

        init();
        events();
        runTimeProgressBar();
    }

    private void init() {
        backgroundGame = findViewById(R.id.backgroundGame);
        progressBar = findViewById(R.id.progressBar);
        tv_timeProgressBar = findViewById(R.id.tv_timeProgressBar);
        tv_marks = findViewById(R.id.tv_marks);
        tv_level = findViewById(R.id.tv_level);
        tv_help = findViewById(R.id.tv_help);
        ib_pause = findViewById(R.id.ib_pause);
        graphicView = findViewById(R.id.graphicView);

        musicPlay = MediaPlayer.create(MainActivity.this, R.raw.music_play);
        musicCountdownTime = MediaPlayer.create(MainActivity.this, R.raw.music_countdown_time);
        musicPlay.start();
    }

    public void events() {
        ib_pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PauseFragmentDialog pauseFragmentDialog = new PauseFragmentDialog();
                pauseFragmentDialog.show(getSupportFragmentManager(), "");
                pauseFlag = true;
            }
        });

        tv_help.setText("" + countHelp);

        tv_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                graphicView.suggestGame();
            }
        });

        tv_level.setText("Lv " + LevelGame);
        tv_marks.setText("" + TongDiem);
    }

    public void runTimeProgressBar() {
        pauseFlag = false;
        progressBarStatus = 374000;

        threadMain = new Thread() {
            @Override
            public void run() {
                while (progressBarStatus > 0) {
                    if (pauseFlag == false) {
                        progressBarStatus -= 1000;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }

                        if (progressBarStatus == 8000) {
                            if (musicPlay.isPlaying()) {
                                musicPlay.stop();
                            }

                            if (!musicCountdownTime.isPlaying()) {
                                musicCountdownTime.stop();
                                musicCountdownTime = MediaPlayer.create(MainActivity.this, R.raw.music_countdown_time);
                                musicCountdownTime.start();
                            } else {
                                musicCountdownTime.start();
                            }
                        }

                        if (progressBarStatus == 0) {
                            progressBarStatus = progressBarStatus + 1;
                            LoseFragmentDialog loseFragmentDialog = new LoseFragmentDialog();
                            loseFragmentDialog.show(getSupportFragmentManager(), "");
                            musicPlay.stop();
                            musicCountdownTime.stop();
                            nhacKhiLose.start();
                            pauseFlag = true;
                            progressBarStatus = 374000;
                        }
                    }

                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            if (pauseFlag == false) {
                                progressBar.setProgress(progressBarStatus);
                                tv_timeProgressBar.setText("" + String.format("%02d:%02d",
                                        TimeUnit.MILLISECONDS.toMinutes(progressBarStatus),
                                        TimeUnit.MILLISECONDS.toSeconds(progressBarStatus) - TimeUnit.MINUTES.toSeconds(
                                                TimeUnit.MILLISECONDS.toMinutes(progressBarStatus))));
                            }

                        }
                    });
                }
            }
        };
        threadMain.start();
    }

    @Override
    public void onBackPressed() {
        ExitFragmentDialog exitFragmentDialog = new ExitFragmentDialog();
        exitFragmentDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE
                        | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN);
    }

}

