package com.example.pikachu.FramentDialog;

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

import com.example.pikachu.R;


public class ExitAppFragmentDialog extends DialogFragment {

    private Button btn_noExitApp, btn_yesExitApp;
    private Dialog dl = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_exit_app_dialog, null);

        btn_noExitApp = view.findViewById(R.id.btn_noExitApp);
        btn_yesExitApp = view.findViewById(R.id.btn_yesExitApp);

        btn_yesExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startMain = new Intent(Intent.ACTION_MAIN);
                startMain.addCategory(Intent.CATEGORY_HOME);
                startActivity(startMain);
                getActivity().finish();
                dismiss();
            }
        });
        btn_noExitApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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