package com.example.pcmallcompose.utils

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.util.Base64

object ImageUtils {
    @JvmStatic
    fun decodeImageString(imageString: String): ImageBitmap {
        val bytes = Base64.getMimeDecoder().decode(imageString)
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
    }
}