package com.example.pikachu.FramentDialog;

import android.app.Dialog;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.pikachu.R;

import static com.example.pikachu.Activity.MainActivity.graphicView;
import static com.example.pikachu.Activity.MainActivity.musicPlay;
import static com.example.pikachu.Activity.MainActivity.musicCountdownTime;
import static com.example.pikachu.Activity.MainActivity.pauseFlag;

public class PauseFragmentDialog extends DialogFragment {

    AlertDialog dl = null;
    Button btn_resetPause, btn_mainMeuGame, btn_resumePause;
    public static ImageButton ib_musicPause;
    View view;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.fragment_pause_dialog, null);

        init();

        builder.setView(view);
        dl = builder.create();
        dl.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dl.getWindow().setWindowAnimations(R.style.DialogAnimationWinLosePause);
        dl.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dl.setCanceledOnTouchOutside(false);

        dl.show();

        //Set the dialog to immersive
        dl.getWindow().getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());

        //Clear the not focusable flag from the window
        dl.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        return dl;
    }

    private void init() {
        btn_resetPause = view.findViewById(R.id.btn_resetPause);
        btn_mainMeuGame = view.findViewById(R.id.btn_mainMeuGame);
        btn_resumePause = view.findViewById(R.id.btn_resumePause);
        ib_musicPause = view.findViewById(R.id.ib_musicPause);

        if (!musicPlay.isPlaying()) {
            ib_musicPause.setImageResource(R.drawable.ic_volume_off);
        }


        btn_resetPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlay.isPlaying()) {
                    musicPlay.stop();
                    musicPlay = MediaPlayer.create(getActivity(), R.raw.music_play);
                    musicPlay.start();
                } else {
                    musicCountdownTime.stop();
                    musicPlay = MediaPlayer.create(getActivity(), R.raw.music_play);
                    musicPlay.start();
                }

                graphicView.resetGame();

                dismiss();
            }
        });

        ib_musicPause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (musicPlay.isPlaying()) {
                    ib_musicPause.setImageResource(R.drawable.ic_volume_off);
                    musicPlay.pause();
                } else {
                    ib_musicPause.setImageResource(R.drawable.ic_volume_up);
                    musicPlay.start();
                }
                pauseFlag = false;
                dismiss();

            }
        });

        btn_resumePause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pauseFlag = false;
                dismiss();
            }
        });

        btn_mainMeuGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ExitFragmentDialog exitFragmentDialog = new ExitFragmentDialog();
                exitFragmentDialog.show(getFragmentManager(), "");
                dismiss();

            }
        });

    }

}