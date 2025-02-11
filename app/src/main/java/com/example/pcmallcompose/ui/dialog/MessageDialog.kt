package com.example.pcmallcompose.ui.dialog

import android.content.Context
import android.content.DialogInterface
import android.view.View
import cn.pedant.SweetAlert.SweetAlertDialog
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MessageDialog(
    context: Context,
    alertType: Int,
    title: String,
    dismissListener: ((DialogInterface) -> Unit)? = null
) : SweetAlertDialog(context, alertType) {

    init {
        super.setTitleText(title)
        super.setOnDismissListener(dismissListener)
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun show() {
        super.show()
        super.getButton(BUTTON_CONFIRM).visibility = View.GONE
        GlobalScope.launch {
            delay(1500)
            super.dismissWithAnimation()
        }
    }
}