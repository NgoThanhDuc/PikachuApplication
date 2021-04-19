package com.example.pikachu.FramentDialog;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.pikachu.R;

import static com.example.pikachu.Activity.MainActivity.graphicView;
import static com.example.pikachu.Activity.MainActivity.musicPlay;
import static com.example.pikachu.Controller.GraphicView.LevelGame;
import static com.example.pikachu.Controller.GraphicView.TongDiem;

public class LoseFragmentDialog extends DialogFragment {

    Dialog dl = null;

    public static TextView tv_levelLose, tv_marksLose;
    Button btn_retryLose, btn_mainMenuLose;

    LayoutInflater inflater;
    View view;
    Thread thread;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_lose_dialog, null);

        btn_retryLose = view.findViewById(R.id.btn_retryLose);
        btn_mainMenuLose = view.findViewById(R.id.btn_mainMenuLose);
        tv_levelLose = view.findViewById(R.id.tv_levelLose);
        tv_marksLose = view.findViewById(R.id.tv_marksLose);

        tv_levelLose.setText("Current Level: " + LevelGame);
        tv_marksLose.setText("Your Scores: " + TongDiem);

        btn_retryLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!musicPlay.isPlaying()) {
                    musicPlay.stop();
                    musicPlay = MediaPlayer.create(getActivity(), R.raw.music_play);
                    musicPlay.start();
                }

                graphicView.retryGame();
                dismiss();
            }
        });

        btn_mainMenuLose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExitFragmentDialog exitFragmentDialog = new ExitFragmentDialog();
                exitFragmentDialog.show(getFragmentManager(), "");

                dismiss();
            }
        });

        builder.setView(view);
        dl = builder.create();
        dl.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dl.show();
        dl.setCanceledOnTouchOutside(false);
        dl.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dl.getWindow().setWindowAnimations(R.style.DialogAnimationWinLosePause);

        //Set the dialog to immersive
        dl.getWindow().getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        dl.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        return dl;

    }
}