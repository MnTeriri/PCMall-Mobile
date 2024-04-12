package com.example.pcmall.dialog;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.R;

public class FullScreenDialog extends DialogFragment {
    private final String TAG = "FullScreenDialog";
    private final FragmentActivity activity;
    private OnDialogClosedListener listener;

    public FullScreenDialog(FragmentActivity activity) {
        this.activity = activity;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "FullScreenDialog.onCreate()");
        setStyle(STYLE_NO_FRAME, R.style.MaterialAlertDialog_Material3);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "FullScreenDialog.onCreateView()");
        getDialog().getWindow().setWindowAnimations(R.style.MaterialAlertDialog_Material3_Animation);//设置打开关闭动画
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    public void show() {

    }

    public void show(String tag) {
        Log.d(TAG, "FullScreenDialog.show()");
        FragmentManager fragmentManager = activity.getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Fragment prev = fragmentManager.findFragmentByTag(tag);
        if (prev != null) {
            fragmentTransaction.remove(prev);
        }
        fragmentTransaction.addToBackStack(null);
        this.show(fragmentManager, tag);
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        super.onDismiss(dialog);
        if (listener != null) {
            listener.onDialogClosed(this);
        }
    }

    public void setOnDialogClosedListener(OnDialogClosedListener listener) {
        this.listener = listener;
    }

    public interface OnDialogClosedListener {
        void onDialogClosed(DialogFragment dialogFragment);
    }
}
