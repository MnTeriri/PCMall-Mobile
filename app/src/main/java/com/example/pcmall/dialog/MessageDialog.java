package com.example.pcmall.dialog;

import android.content.Context;
import android.view.View;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class MessageDialog extends SweetAlertDialog {

    public MessageDialog(Context context, int alertType) {
        super(context, alertType);
    }

    public void show() {
        super.show();
        super.getButton(SweetAlertDialog.BUTTON_CONFIRM).setVisibility(View.GONE);
        new Thread(() -> {
            try {
                Thread.sleep(1500);
                super.dismissWithAnimation();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }
}
