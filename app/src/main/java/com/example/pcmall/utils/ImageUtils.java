package com.example.pcmall.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
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

    public static Bitmap getImageFromUri(Context context, String imageUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(Uri.parse(imageUri));
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static Bitmap getImageFromFile(File file) {
        try {
            InputStream inputStream = new FileInputStream(file);
            return BitmapFactory.decodeStream(inputStream);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}
