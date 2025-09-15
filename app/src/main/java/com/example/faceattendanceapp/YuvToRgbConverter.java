package com.example.faceattendanceapp;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ImageFormat;
import android.graphics.Rect;
import android.graphics.YuvImage;
import android.util.Log;

import androidx.camera.core.ImageProxy;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;

public class YuvToRgbConverter {
    public YuvToRgbConverter(Context context) {
        // No initialization required here
    }

    public void yuvToRgb(ImageProxy imageProxy, Bitmap output) {
        try {
            ImageProxy.PlaneProxy[] planes = imageProxy.getPlanes();
            if (planes.length < 3) {
                Log.e("YUV_CONVERT", "Unexpected image format: less than 3 planes");
                return;
            }

            ByteBuffer yBuffer = planes[0].getBuffer();
            ByteBuffer uBuffer = planes[1].getBuffer();
            ByteBuffer vBuffer = planes[2].getBuffer();

            int ySize = yBuffer.remaining();
            int uSize = uBuffer.remaining();
            int vSize = vBuffer.remaining();

            byte[] nv21 = new byte[ySize + uSize + vSize];
            yBuffer.get(nv21, 0, ySize);
            vBuffer.get(nv21, ySize, vSize); // V first
            uBuffer.get(nv21, ySize + vSize, uSize); // then U

            YuvImage yuvImage = new YuvImage(
                    nv21,
                    ImageFormat.NV21,
                    imageProxy.getWidth(),
                    imageProxy.getHeight(),
                    null
            );

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            boolean success = yuvImage.compressToJpeg(
                    new Rect(0, 0, imageProxy.getWidth(), imageProxy.getHeight()),
                    100,
                    out
            );

            if (!success) {
                Log.e("YUV_CONVERT", "Failed to compress YUV image");
                return;
            }

            byte[] imageBytes = out.toByteArray();
            Bitmap bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

            if (bitmap == null) {
                Log.e("YUV_CONVERT", "Decoded bitmap is null");
                return;
            }

            if (output != null) {
                Canvas canvas = new Canvas(output);
                canvas.drawBitmap(bitmap, 0f, 0f, null);
            } else {
                Log.e("YUV_CONVERT", "Output bitmap is null");
            }
        } catch (Exception e) {
            Log.e("YUV_CONVERT", "Exception in yuvToRgb: " + e.getMessage());
        }
    }
}