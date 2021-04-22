package com.example.pikachu.framentdialogs;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

import com.example.pikachu.activities.OptionActivity;
import com.example.pikachu.R;

import static com.example.pikachu.activities.MainActivity.musicPlay;
import static com.example.pikachu.activities.MainActivity.pauseFlag;
import static com.example.pikachu.activities.MainActivity.progressBarStatus;
import static com.example.pikachu.controllers.GraphicView.nhacKhiLose;


public class ExitFragmentDialog extends DialogFragment {

    Button btn_noExit, btn_yesExit;
    Dialog dl = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_exit_dialog, null);

        btn_noExit = view.findViewById(R.id.btn_noExit);
        btn_yesExit = view.findViewById(R.id.btn_yesExit);

        btn_yesExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressBarStatus = 0;
                pauseFlag = true;
                musicPlay.stop();

                if (musicPlay.isPlaying()) {
                    musicPlay.stop();
                }
                nhacKhiLose.stop();

                Intent intent = new Intent(getActivity(), OptionActivity.class);
                startActivity(intent);
                dismiss();
            }
        });

        btn_noExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*pauseFlag = false;*/
                PauseFragmentDialog pauseFragmentDialog = new PauseFragmentDialog();
                pauseFragmentDialog.show(getFragmentManager(), "");
                dismiss();
            }
        });


        builder.setView(view);
        dl = builder.create();
        dl.setCanceledOnTouchOutside(false);
        dl.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        dl.getWindow().setWindowAnimations(R.style.DialogAnimationExit);
        dl.getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);
        dl.show();

        //Set the dialog to immersive
        dl.getWindow().getDecorView().setSystemUiVisibility(getActivity().getWindow().getDecorView().getSystemUiVisibility());
        //Clear the not focusable flag from the window
        dl.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE);

        return dl;
    }

}