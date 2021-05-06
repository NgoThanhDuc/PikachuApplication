package com.example.pikachu.activities;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.example.pikachu.framentdialogs.ExitAppFragmentDialog;
import com.example.pikachu.framentdialogs.HelpFragmentDialog;
import com.example.pikachu.framentdialogs.InfoFragmentDialog;
import com.example.pikachu.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import static com.example.pikachu.R.drawable.ic_volume_off;

public class OptionActivity extends AppCompatActivity {

    private Button btn_play;
    private ImageButton ib_turnOff;
    private MediaPlayer musicBegins = null;
    private FloatingActionButton fab_setting, fab_info, fab_help, fab_volume;
    private ImageView imageBackground;

    boolean checkAnHien = false;

    private Animation rote_open, rote_close;
    private Animation move_trai, move_tren, move_cheo;
    private Animation back_trai, back_tren, back_cheo;

    private InfoFragmentDialog infoFragment = null;
    private ExitAppFragmentDialog exitAppFragmentDialog = null;
    private HelpFragmentDialog helpFragmentDialog = null;

    private ArrayList<Integer> arrBackGround;
    private int position = 0;
    private CountDownTimer countDownTimer = null;

    @SuppressLint("SourceLockedOrientationActivity")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_option);

        init();
        events();
    }

    private void init() {
        fab_setting = findViewById(R.id.fab_setting);
        fab_help = findViewById(R.id.fab_help);
        fab_info = findViewById(R.id.fab_info);
        fab_volume = findViewById(R.id.fab_volume);
        btn_play = findViewById(R.id.btn_play);
        btn_play.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.blink));
        ib_turnOff = findViewById(R.id.ib_turnOff);
        imageBackground = findViewById(R.id.imageBackground);

        infoFragment = new InfoFragmentDialog();
        exitAppFragmentDialog = new ExitAppFragmentDialog();
        helpFragmentDialog = new HelpFragmentDialog();

        arrBackGround = new ArrayList<>();
        arrBackGround.add(R.drawable.op1);
        arrBackGround.add(R.drawable.op2);
        arrBackGround.add(R.drawable.op3);

        if (musicBegins == null) {
            musicBegins = MediaPlayer.create(OptionActivity.this, R.raw.music_begins);
            musicBegins.start();
        }

        installAnimationFloatActionButton();
        countDownTimer();
    }

    private void installAnimationFloatActionButton(){
        rote_open = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_open_optionactivity_fab_setting);
        rote_close = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.rotate_close_optionactivity_fab_setting);
        move_trai = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_trai_optionactivity_fab_help);
        move_tren = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_tren_optionactivity_fab_info);
        move_cheo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.move_cheo_optionactivity_fab_volume);
        back_trai = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.back_trai_optionactivity_fab_help);
        back_tren = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.back_tren_optionactivity_fab_info);
        back_cheo = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.back_cheo_optionactivity_fab_volume);
    }

    private void countDownTimer(){
        countDownTimer = new CountDownTimer(30000000, 3000) {
            public void onTick(long millisUntilFinished) {
                //This is when you click on each tick it came here after 1000 millisecond
                if (position == arrBackGround.size())
                    position = 0;

                imageBackground.startAnimation(AnimationUtils.loadAnimation(getApplicationContext(), R.anim.fade_in));
                imageBackground.setImageResource(arrBackGround.get(position));
                position++;
            }

            public void onFinish() {
            }
        }.start();
    }

    private void events() {
        fab_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (checkAnHien == false) {
                    fab_setting.startAnimation(rote_open);
                    showFloatActionButton();
                    Move();
                    checkAnHien = true;
                } else {
                    fab_setting.startAnimation(rote_close);
                    hideFloatActionButton();
                    Back();
                    checkAnHien = false;
                }
            }
        });

        btn_play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent changeActivity = new Intent(OptionActivity.this, MainActivity.class);
                startActivity(changeActivity);

                if (musicBegins != null) {
                    musicBegins.stop();
                    musicBegins.release();
                    musicBegins = null;
                }

                if (countDownTimer != null) {
                    countDownTimer.cancel();
                }
            }
        });

        fab_info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                infoFragment.show(getSupportFragmentManager(), "");
            }
        });

        fab_help.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                helpFragmentDialog.show(getSupportFragmentManager(), "");
            }
        });

        fab_volume.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {

                if (musicBegins.isPlaying()) {
                    musicBegins.pause();
                    fab_volume.setForeground(ContextCompat.getDrawable(OptionActivity.this, ic_volume_off));
                } else {
                    fab_volume.setForeground(ContextCompat.getDrawable(OptionActivity.this, R.drawable.ic_volume_up));
                    musicBegins.start();
                }
            }
        });

        ib_turnOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                exitAppFragmentDialog.show(getSupportFragmentManager(), "");

            }
        });
    }

    private void showFloatActionButton() {
        fab_info.show();
        fab_volume.show();
        fab_help.show();
    }

    private void hideFloatActionButton() {
        fab_info.hide();
        fab_volume.hide();
        fab_help.hide();
    }

    private void Move() {
        FrameLayout.LayoutParams paramsTrai = (FrameLayout.LayoutParams) fab_help.getLayoutParams();
        paramsTrai.rightMargin = (int) (fab_help.getWidth() * 2.5);
        fab_help.setLayoutParams(paramsTrai);
        fab_help.startAnimation(move_trai);

        FrameLayout.LayoutParams paramsTren = (FrameLayout.LayoutParams) fab_info.getLayoutParams();
        paramsTren.bottomMargin = (int) (fab_info.getWidth() * 2.5);
        fab_info.setLayoutParams(paramsTren);
        fab_info.startAnimation(move_tren);

        FrameLayout.LayoutParams paramsCheo = (FrameLayout.LayoutParams) fab_volume.getLayoutParams();
        paramsCheo.bottomMargin = (int) (fab_volume.getWidth() * 2.0);
        paramsCheo.rightMargin = (int) (fab_volume.getWidth() * 2.0);
        fab_volume.setLayoutParams(paramsCheo);
        fab_volume.startAnimation(move_cheo);
    }

    private void Back() {
        FrameLayout.LayoutParams paramsTrai = (FrameLayout.LayoutParams) fab_help.getLayoutParams();
        paramsTrai.rightMargin -= (int) (fab_help.getWidth() * 2.5);
        fab_help.setLayoutParams(paramsTrai);
        fab_help.startAnimation(back_trai);

        FrameLayout.LayoutParams paramsTren = (FrameLayout.LayoutParams) fab_info.getLayoutParams();
        paramsTren.bottomMargin -= (int) (fab_info.getWidth() * 2.5);
        fab_info.setLayoutParams(paramsTren);
        fab_info.startAnimation(back_tren);

        FrameLayout.LayoutParams paramsCheo = (FrameLayout.LayoutParams) fab_volume.getLayoutParams();
        paramsCheo.bottomMargin -= (int) (fab_volume.getWidth() * 2.0);
        paramsCheo.rightMargin -= (int) (fab_volume.getWidth() * 2.0);
        fab_volume.setLayoutParams(paramsCheo);
        fab_volume.startAnimation(back_cheo);
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

    @Override
    public void onBackPressed() {
        exitAppFragmentDialog.show(getSupportFragmentManager(), "");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus) {
            hideSystemUI();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (musicBegins != null){
            musicBegins.pause();
            musicBegins.stop();
            musicBegins.release();
        }
    }
}
