package com.example.pcmall.adapter.listener;

import android.view.View;

public interface AdapterInterface {
    public interface OnItemButtonClickListener<T> {
        void onClick(View v, T data);
    }

    public interface OnItemCheckBoxClickListener<T> {
        void onCheckedChanged(View v, T data);
    }

    public interface OnClickListener<T> {
        void onClick(View v, T data);
    }

    public interface OnLongClickListener<T> {
        boolean onLongClick(View v, T data);
    }
}
