package com.example.pcmall.utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.ByteArrayOutputStream;
import java.util.Base64;

public class ImageUtils {

    public static Bitmap decodeImageString(String imageString) {
        byte[] bytes = Base64.getMimeDecoder().decode(imageString);
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
    }

    public static String encodeImageBitmap(Bitmap bitmap) {
        String imageString = "";
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        imageString = Base64.getEncoder().encodeToString(out.toByteArray());
        return imageString;
    }

}
