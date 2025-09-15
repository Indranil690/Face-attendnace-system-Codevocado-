package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText editOldPass, editNewPass, editConfirmPass;
    private Button btnChangePassword;

    // Whether this is Admin or Employee
    private boolean isAdmin = false;
    // Either userId (Admin) or empId (Employee)
    private String idValue = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        ThemeManager.applySavedTheme(this);
        setContentView(R.layout.activity_change_password);

        // Get Intent extras
        Intent intent = getIntent();
        isAdmin = intent.getBooleanExtra("IS_ADMIN", false);
        idValue = intent.getStringExtra("ID_VALUE");

        if (idValue == null) {
            idValue = "";
        }

        // Log what we got
        Log.d("ChangePasswordDebug", "isAdmin=" + isAdmin + " idValue=" + idValue);

        // Bind views
        editOldPass = findViewById(R.id.editOldPassword);
        editNewPass = findViewById(R.id.editNewPassword);
        editConfirmPass = findViewById(R.id.editConfirmPassword);
        btnChangePassword = findViewById(R.id.btnChangePassword);

        btnChangePassword.setOnClickListener(v -> {
            String oldPass = editOldPass.getText().toString().trim();
            String newPass = editNewPass.getText().toString().trim();
            String confirmPass = editConfirmPass.getText().toString().trim();

            if (oldPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!newPass.equals(confirmPass)) {
                Toast.makeText(this, "❌ New passwords do not match", Toast.LENGTH_SHORT).show();
                return;
            }

            // ⚠️ DO NOT HASH HERE - server expects plain text
            String plainOldPass = oldPass;
            String plainNewPass = newPass;

            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            btnChangePassword.setEnabled(false);

            if (isAdmin) {
                ChangePasswordRequest request = new ChangePasswordRequest(idValue, plainOldPass, plainNewPass);
                Call<ApiResponse> call = apiService.changeAdminPassword(request);
                call.enqueue(getCallback());
            } else {
                EmployeeChangePasswordRequest request = new EmployeeChangePasswordRequest(idValue, plainOldPass, plainNewPass);
                Call<ApiResponse> call = apiService.changeEmployeePassword(request);
                call.enqueue(getCallback());
            }
        });
    }

    private Callback<ApiResponse> getCallback() {
        return new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                btnChangePassword.setEnabled(true);
                if (response.isSuccessful() && response.body() != null) {
                    ApiResponse apiResponse = response.body();
                    String msg = (apiResponse.getMessage() == null || apiResponse.getMessage().isEmpty())
                            ? "✅ Password changed successfully"
                            : apiResponse.getMessage();

                    Toast.makeText(ChangePasswordActivity.this, msg, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(ChangePasswordActivity.this, "Failed to change password", Toast.LENGTH_SHORT).show();
                    Log.e("ChangePasswordDebug", "Response error: " + response.code());
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                btnChangePassword.setEnabled(true);
                Toast.makeText(ChangePasswordActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                Log.e("ChangePasswordDebug", "Network error", t);
            }
        };
    }
}
