package com.example.pcmall.listener;

import android.view.View;

import androidx.fragment.app.DialogFragment;

public interface ListenerInterface {
    public interface OnClickListener<T> {
        void onClick(View v, T data);
    }

    public interface OnLongClickListener<T> {
        boolean onLongClick(View v, T data);
    }

    public interface OnDialogClosedListener {
        void onDialogClosed(DialogFragment dialogFragment);
    }

    public interface OnDialogClosedReturnDataListener<T> {
        void onDialogClosed(DialogFragment dialogFragment, T data);
    }
}
