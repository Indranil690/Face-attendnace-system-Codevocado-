package com.example.faceattendanceapp;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.journeyapps.barcodescanner.BarcodeCallback;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.journeyapps.barcodescanner.DecoratedBarcodeView;
import com.journeyapps.barcodescanner.camera.CameraSettings;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.LuminanceSource;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;

import java.io.IOException;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class QRAttendanceActivity extends AppCompatActivity {

    private static final int CAMERA_PERMISSION_REQUEST = 200;
    private static final int PICK_IMAGE_REQUEST = 300;

    private DecoratedBarcodeView barcodeView;
    private ApiService apiService;
    private Button btnUploadQR;
    private ImageButton btnSwitchCamera;

    private int currentCameraId = 0; // 0 = back, 1 = front (usually)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_qr_attendance);

        barcodeView   = findViewById(R.id.barcode_scanner_view);
        btnUploadQR   = findViewById(R.id.btnUploadQR);
        btnSwitchCamera = findViewById(R.id.btnSwitchCamera);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        btnUploadQR.setOnClickListener(v -> openGalleryForQR());

        if (btnSwitchCamera != null) {
            btnSwitchCamera.setOnClickListener(v -> switchCamera());
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_REQUEST);
        } else {
            startScanner(currentCameraId);
        }
    }

    /** Start scanner with specific camera (0=back, 1=front). */
    private void startScanner(int cameraId) {
        CameraSettings settings = barcodeView.getBarcodeView().getCameraSettings();
        settings.setRequestedCameraId(cameraId);
        barcodeView.getBarcodeView().setCameraSettings(settings);

        barcodeView.decodeContinuous(callback);
        barcodeView.resume();
    }

    /** Switch between front and back cameras */
    private void switchCamera() {
        currentCameraId = (currentCameraId == 0) ? 1 : 0;
        barcodeView.pause();
        startScanner(currentCameraId);

        String name = (currentCameraId == 0) ? "Back" : "Front";
        Toast.makeText(this, "Switched to " + name + " Camera", Toast.LENGTH_SHORT).show();
    }

    private void openGalleryForQR() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            Uri imageUri = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), imageUri);
                String qrData = decodeQRCode(bitmap);
                if (qrData != null) {
                    Toast.makeText(this, "QR Scanned: " + qrData, Toast.LENGTH_SHORT).show();
                    markManualAttendance(qrData);
                } else {
                    Toast.makeText(this, "No QR code found in the image", Toast.LENGTH_SHORT).show();
                }
            } catch (IOException e) {
                e.printStackTrace();
                Toast.makeText(this, "Failed to read image", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String decodeQRCode(Bitmap bitmap) {
        int[] intArray = new int[bitmap.getWidth() * bitmap.getHeight()];
        bitmap.getPixels(intArray, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        LuminanceSource source = new RGBLuminanceSource(bitmap.getWidth(), bitmap.getHeight(), intArray);
        BinaryBitmap binaryBitmap = new BinaryBitmap(new HybridBinarizer(source));

        try {
            return new QRCodeReader().decode(binaryBitmap).getText();
        } catch (Exception e) {
            return null;
        }
    }

    private final BarcodeCallback callback = new BarcodeCallback() {
        @Override
        public void barcodeResult(BarcodeResult result) {
            if (result == null) return;
            barcodeView.pause();
            String qrCodeData = result.getText();
            Toast.makeText(QRAttendanceActivity.this, "QR Scanned: " + qrCodeData, Toast.LENGTH_SHORT).show();
            markManualAttendance(qrCodeData);
        }

        @Override
        public void possibleResultPoints(java.util.List<com.google.zxing.ResultPoint> resultPoints) {}
    };

    private void markManualAttendance(String qrData) {
        ManualAttendanceRequest request = new ManualAttendanceRequest(qrData, "28.6139", "77.2090");

        apiService.markManualAttendance(request).enqueue(new Callback<ManualAttendanceResponse>() {
            @Override
            public void onResponse(@NonNull Call<ManualAttendanceResponse> call, @NonNull Response<ManualAttendanceResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ManualAttendanceResponse attendanceResponse = response.body();
                    if (attendanceResponse.getError() != null && !attendanceResponse.getError().isEmpty()) {
                        Toast.makeText(QRAttendanceActivity.this, "Error: " + attendanceResponse.getError(), Toast.LENGTH_SHORT).show();
                    } else {
                        String msg = "Marked successfully\nNext allowed: " + attendanceResponse.getNextAllowedTime() +
                                "\nRemaining: " + attendanceResponse.getRemainingHours() + "h " +
                                attendanceResponse.getRemainingMinutes() + "m";
                        Toast.makeText(QRAttendanceActivity.this, msg, Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(QRAttendanceActivity.this, "Failed to mark manual attendance", Toast.LENGTH_SHORT).show();
                }
                barcodeView.resume();
            }

            @Override
            public void onFailure(@NonNull Call<ManualAttendanceResponse> call, @NonNull Throwable t) {
                Toast.makeText(QRAttendanceActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                barcodeView.resume();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            startScanner(currentCameraId);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        barcodeView.pause();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_PERMISSION_REQUEST) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startScanner(currentCameraId);
            } else {
                Toast.makeText(this, "Camera Permission Denied", Toast.LENGTH_SHORT).show();
                finish();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
}
