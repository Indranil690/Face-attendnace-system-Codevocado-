package com.example.faceattendanceapp;


import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UpdateFaceImageActivity extends AppCompatActivity {

    private ImageView imageViewFace;
    private Button btnUpdateFace;
    private String base64Image = "";

    // Gallery launcher
    private final ActivityResultLauncher<Intent> galleryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Uri selectedImageUri = result.getData().getData();
                    imageViewFace.setImageURI(selectedImageUri);
                    base64Image = ImageUtils.uriToBase64(this, selectedImageUri);
                }
            }
    );

    // Camera launcher
    private final ActivityResultLauncher<Intent> cameraLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                    Bitmap photo = (Bitmap) result.getData().getExtras().get("data");
                    imageViewFace.setImageBitmap(photo);
                    base64Image = ImageUtils.bitmapToBase64(photo);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_face);

        imageViewFace = findViewById(R.id.imageViewFace);
        btnUpdateFace = findViewById(R.id.btnUpdateFace);
        Button btnCaptureFace = findViewById(R.id.btnCaptureFace);
        Button btnLoadFromGallery = findViewById(R.id.btnLoadFromGallery);

        btnCaptureFace.setOnClickListener(v -> openCamera());
        btnLoadFromGallery.setOnClickListener(v -> openGallery());

        btnUpdateFace.setOnClickListener(v -> {
            if (base64Image == null || base64Image.isEmpty()) {
                Toast.makeText(this, "Please select or capture a photo first.", Toast.LENGTH_SHORT).show();
                return;
            }

// Fetch emp_id from SessionManager
            SessionManager sessionManager = new SessionManager(this);
            String empId = sessionManager.getEmployeeId();


            if (empId == null) {
                Toast.makeText(this, "User ID not found. Please login again.", Toast.LENGTH_SHORT).show();
                return;
            }

            UpdateEmployeeFaceRequest request = new UpdateEmployeeFaceRequest(empId, base64Image);
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

            Call<ApiResponse> call = apiService.updateEmployeeFace(request);

            btnUpdateFace.setEnabled(false);

            call.enqueue(new Callback<ApiResponse>() {
                @Override
                public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                    btnUpdateFace.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(UpdateFaceImageActivity.this, response.body().getMessage(), Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(UpdateFaceImageActivity.this, "Update failed. Please check your data.", Toast.LENGTH_LONG).show();
                    }
                }
                @Override
                public void onFailure(Call<ApiResponse> call, Throwable t) {
                    btnUpdateFace.setEnabled(true);
                    Toast.makeText(UpdateFaceImageActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                }
            });
        });
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryLauncher.launch(intent);
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraLauncher.launch(intent);
    }
}
