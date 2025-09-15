package com.example.faceattendanceapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegisterActivity extends AppCompatActivity {

    private EditText editEmpId, editName, editEmail, editPhone,
            editDepartment, editDesignation, editPassword, editUserId;
    private Button btnRegister;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Link views
        editEmpId = findViewById(R.id.editEmpId);
        editName = findViewById(R.id.editName);
        editEmail = findViewById(R.id.editEmail);
        editPhone = findViewById(R.id.editPhone);
        editDepartment = findViewById(R.id.editDepartment);
        editDesignation = findViewById(R.id.editDesignation);
        editPassword = findViewById(R.id.editPassword);
        editUserId = findViewById(R.id.editUserId);
        btnRegister = findViewById(R.id.btnRegister);
        backToLogin = findViewById(R.id.backToLogin);

        backToLogin.setOnClickListener(v -> {
            finish(); // Go back to LoginActivity
        });

        btnRegister.setOnClickListener(v -> {
            // Read inputs
            String empId = editEmpId.getText().toString().trim();
            String name = editName.getText().toString().trim();
            String email = editEmail.getText().toString().trim();
            String phone = editPhone.getText().toString().trim();
            String department = editDepartment.getText().toString().trim();
            String designation = editDesignation.getText().toString().trim();
            String password = editPassword.getText().toString().trim();
            String userId = editUserId.getText().toString().trim();

            // Validate inputs
            if (TextUtils.isEmpty(empId) || TextUtils.isEmpty(name) || TextUtils.isEmpty(email)
                    || TextUtils.isEmpty(phone) || TextUtils.isEmpty(department)
                    || TextUtils.isEmpty(designation) || TextUtils.isEmpty(password)
                    || TextUtils.isEmpty(userId)) {
                Toast.makeText(this, "Please fill all fields.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!email.contains("@")) {
                Toast.makeText(this, "Please enter a valid email address.", Toast.LENGTH_SHORT).show();
                return;
            }

            // Create request object
            RegisterRequest request = new RegisterRequest(
                    empId, name, email, phone,
                    department, designation, password, userId
            );

            // Call API
            ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
            Call<GenericResponse> call = apiService.registerUser(request);

            btnRegister.setEnabled(false); // disable button to prevent duplicate taps

            call.enqueue(new Callback<GenericResponse>() {
                @Override
                public void onResponse(Call<GenericResponse> call, Response<GenericResponse> response) {
                    btnRegister.setEnabled(true);
                    if (response.isSuccessful() && response.body() != null) {
                        Toast.makeText(RegisterActivity.this,
                                response.body().getMessage(),
                                Toast.LENGTH_LONG).show();
                        // Go back to login screen
                        finish();
                    } else {
                        try {
                            String errorBody = response.errorBody() != null
                                    ? response.errorBody().string()
                                    : "Unknown error";
                            Log.e("RegisterError", "Error body: " + errorBody);
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed: " + errorBody,
                                    Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                            Toast.makeText(RegisterActivity.this,
                                    "Registration failed. Please try again.",
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                }

                @Override
                public void onFailure(Call<GenericResponse> call, Throwable t) {
                    btnRegister.setEnabled(true);
                    Toast.makeText(RegisterActivity.this,
                            "Error: " + t.getMessage(),
                            Toast.LENGTH_LONG).show();
                }
            });
        });
    }
}
