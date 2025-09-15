package com.example.faceattendanceapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Bundle;
import android.os.Handler;
import android.util.Base64;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageAnalysis;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.LifecycleOwner;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.face.Face;
import com.google.mlkit.vision.face.FaceDetection;
import com.google.mlkit.vision.face.FaceDetector;
import com.google.mlkit.vision.face.FaceDetectorOptions;

import java.io.ByteArrayOutputStream;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import android.location.Location;


public class AdminFaceAttendanceActivity extends AppCompatActivity {

    private static final String TAG = "AdminFaceAttendance";
    private static final int CAMERA_PERMISSION_CODE = 1001;
    private static final int LOCATION_PERMISSION_CODE = 1002;


    private FrameLayout cameraPreview;
    private TextView statusText;
    private PreviewView previewView;

    private ExecutorService cameraExecutor;
    private ImageCapture imageCapture;
    private FaceDetector detector;

    private boolean isFaceCaptured = false;
    private ApiService apiService;
    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private FusedLocationProviderClient fusedLocationClient;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_face_attendance);

        previewView = findViewById(R.id.preview_view);
        statusText = findViewById(R.id.status_text);

        cameraExecutor = Executors.newSingleThreadExecutor();

        // Configure Face Detector
        FaceDetectorOptions options = new FaceDetectorOptions.Builder()
                .setPerformanceMode(FaceDetectorOptions.PERFORMANCE_MODE_FAST)
                .setLandmarkMode(FaceDetectorOptions.LANDMARK_MODE_NONE)
                .setClassificationMode(FaceDetectorOptions.CLASSIFICATION_MODE_ALL)
                .enableTracking()
                .build();
        detector = FaceDetection.getClient(options);

        apiService = ApiClient.getClient(this).create(ApiService.class);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        getLastLocation();


        // Ask camera permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        } else {
            startFaceDetection();
        }
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            // Ask for location permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    1002);
            return;
        }

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        currentLatitude = location.getLatitude();
                        currentLongitude = location.getLongitude();
                        Log.d(TAG, "Location: " + currentLatitude + ", " + currentLongitude);
                    } else {
                        Log.d(TAG, "Location is null");
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Failed to get location", e));
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Camera granted — start detection
                startFaceDetection();
            } else {
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // ✅ Location granted — try again
                getLastLocation();
            } else {
                Toast.makeText(this, "Location permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }


    private void startFaceDetection() {
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);
        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                Preview preview = new Preview.Builder().build();
                preview.setSurfaceProvider(previewView.getSurfaceProvider());

                imageCapture = new ImageCapture.Builder()
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation())
                        .build();


                ImageAnalysis imageAnalysis = new ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build();
                imageAnalysis.setAnalyzer(cameraExecutor, this::processFaceDetection);

                CameraSelector cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA;

                cameraProvider.unbindAll();
                cameraProvider.bindToLifecycle((LifecycleOwner) this, cameraSelector, preview, imageAnalysis, imageCapture);

                runOnUiThread(() -> statusText.setText("Looking for face..."));

            } catch (Exception e) {
                Log.e(TAG, "Camera init failed", e);
            }
        }, ContextCompat.getMainExecutor(this));
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private void processFaceDetection(ImageProxy imageProxy) {
        Image mediaImage = imageProxy.getImage();
        if (mediaImage == null) {
            imageProxy.close();
            return;
        }

        InputImage image = InputImage.fromMediaImage(mediaImage, imageProxy.getImageInfo().getRotationDegrees());

        detector.process(image)
                .addOnSuccessListener(faces -> {
                    if (!faces.isEmpty()) {
                        for (Face face : faces) {
                            Float left = face.getLeftEyeOpenProbability();
                            Float right = face.getRightEyeOpenProbability();

                            if (!isFaceCaptured && left != null && right != null && left < 0.6f && right < 0.6f) {
                                isFaceCaptured = true;
                                runOnUiThread(() -> statusText.setText("Eye blink detected"));

                                imageProxy.close();
                                new Handler().postDelayed(this::captureImageAndSend, 500);
                                return;
                            }
                        }
                    } else {
                        runOnUiThread(() -> statusText.setText("No face detected"));
                    }
                })
                .addOnFailureListener(e -> Log.e(TAG, "Face detection failed", e))
                .addOnCompleteListener(task -> imageProxy.close());
    }

    private void captureImageAndSend() {
        if (imageCapture == null) return;

        imageCapture.takePicture(cameraExecutor, new ImageCapture.OnImageCapturedCallback() {
            @OptIn(markerClass = ExperimentalGetImage.class)
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy imageProxy) {
                Bitmap bitmap = imageProxyToBitmap(imageProxy);
                imageProxy.close();

                if (bitmap != null) {
                    ByteArrayOutputStream stream = new ByteArrayOutputStream();
                    bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);
                    String base64 = Base64.encodeToString(stream.toByteArray(), Base64.NO_WRAP);
                    // ✅ Build photo string with prefix
                    String photo = "data:image/jpeg;base64," + base64;

                    // ✅ Get userId from SessionManager
                    SessionManager sessionManager = new SessionManager(AdminFaceAttendanceActivity.this);
                    String userId = sessionManager.getEmployeeId();

                    // ✅ Get location (replace with your location util)
                    String latitude = String.valueOf(currentLatitude);
                    String longitude = String.valueOf(currentLongitude);

                    // ✅ Create request body
                    Base64ImageRequest request = new Base64ImageRequest(userId, photo, latitude, longitude);

                    runOnUiThread(() -> statusText.setText("Submitting attendance..."));

                    apiService.markBase64FaceAttendance(request)
                            .enqueue(new Callback<ApiResponse>() {
                                @Override
                                public void onResponse(@NonNull Call<ApiResponse> call, @NonNull Response<ApiResponse> response) {
                                    isFaceCaptured = false;
                                    if (response.isSuccessful() && response.body() != null) {
                                        ApiResponse apiResponse = response.body();

                                        runOnUiThread(() -> {
                                            if (apiResponse.hasError()) {
                                                // Show error
                                                statusText.setText("❌ " + apiResponse.getError());
                                                Toast.makeText(AdminFaceAttendanceActivity.this, "Error: " + apiResponse.getError(), Toast.LENGTH_LONG).show();
                                            }
                                            else if (apiResponse.isCheckIn()) {
                                                statusText.setText("✅ Checked-in successfully!");
                                                Toast.makeText(AdminFaceAttendanceActivity.this, "Checked-in", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else if (apiResponse.isCheckOut()) {
                                                statusText.setText("✅ Checked-out successfully!");
                                                Toast.makeText(AdminFaceAttendanceActivity.this, "Checked-out", Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else if (apiResponse.isCompleted()) {
                                                statusText.setText("✅ Attendance Completed!");
                                                Toast.makeText(AdminFaceAttendanceActivity.this, apiResponse.getMessage(), Toast.LENGTH_SHORT).show();
                                                finish();
                                            }
                                            else if (apiResponse.isWait()) {
                                                // Wait response: show remaining seconds
                                                String waitMsg = "⏳ Please wait " + apiResponse.getRemainingSeconds() + "s before next attendance.";
                                                statusText.setText(waitMsg);
                                                Toast.makeText(AdminFaceAttendanceActivity.this, waitMsg, Toast.LENGTH_LONG).show();
                                            }
                                            else {
                                                // Fallback generic message
                                                statusText.setText(apiResponse.getMessage() != null ? apiResponse.getMessage() : "Unknown response");
                                            }
                                        });

                                    } else {
                                        runOnUiThread(() -> statusText.setText("Attendance failed"));
                                    }
                                }


                                @Override
                                public void onFailure(@NonNull Call<ApiResponse> call, @NonNull Throwable t) {
                                    isFaceCaptured = false;
                                    runOnUiThread(() -> statusText.setText("Network error"));
                                }
                            });
                } else {
                    isFaceCaptured = false;
                }
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e(TAG, "Capture failed", exception);
                isFaceCaptured = false;
            }
        });
    }

    @ExperimentalGetImage
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) return null;

        Bitmap bitmap = Bitmap.createBitmap(imageProxy.getWidth(), imageProxy.getHeight(), Bitmap.Config.ARGB_8888);

        YuvToRgbConverter converter = new YuvToRgbConverter(this);
        converter.yuvToRgb(imageProxy, bitmap);

        return bitmap;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (detector != null) detector.close();
        if (cameraExecutor != null) cameraExecutor.shutdown();
    }
}
