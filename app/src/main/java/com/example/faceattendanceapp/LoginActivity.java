package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private EditText editEmployeeId, editPassword;
    private Button btnLogin, btnSignup;
    private TextView appTitle, forgotPasswordText;

    private final boolean USE_FAKE_API = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_SECURE);
        ThemeManager.applySavedTheme(this);
        setContentView(R.layout.activity_login);

        editEmployeeId = findViewById(R.id.editEmployeeId);
        editPassword = findViewById(R.id.editPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnSignup = findViewById(R.id.btnSignup);
        forgotPasswordText = findViewById(R.id.forgotPasswordText);
        appTitle = findViewById(R.id.appTitle);

        appTitle.setText(Html.fromHtml(getString(R.string.app_title_styled), Html.FROM_HTML_MODE_LEGACY));

        // ✅ Launch actual Forgot Password activity
        forgotPasswordText.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        btnLogin.setOnClickListener(v -> {
            String identifier = editEmployeeId.getText().toString().trim();
            String password = editPassword.getText().toString().trim();

            if (identifier.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter Email/Employee ID and Password", Toast.LENGTH_SHORT).show();
                return;
            }

            if (USE_FAKE_API) {
                if (identifier.equals("4") && password.equals("123456")) {
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.saveSession("FAKE_TOKEN", "employee");
                    sessionManager.saveEmployeeId("4");
                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                    finish();
                } else if (identifier.equals("admin@codevocado.in") && password.equals("admin123")) {
                    SessionManager sessionManager = new SessionManager(this);
                    sessionManager.saveSession("FAKE_TOKEN", "admin");
                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                    finish();
                } else {
                    Toast.makeText(this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                }
            } else {
                ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

                if (identifier.contains("@")) {
                    // Admin Login
                    String hashedPassword = password;

                    AdminLoginRequest request = new AdminLoginRequest(identifier, hashedPassword);
                    Call<AdminLoginResponse> call = apiService.loginAdmin(request);

                    call.enqueue(new Callback<AdminLoginResponse>() {
                        @Override
                        public void onResponse(Call<AdminLoginResponse> call, Response<AdminLoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                AdminLoginResponse adminResponse = response.body();
                                if (!adminResponse.getEmail().isEmpty()) {
                                    Toast.makeText(LoginActivity.this, "Welcome Admin", Toast.LENGTH_SHORT).show();

                                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                    sessionManager.saveSession("DUMMY_TOKEN", "admin"); // ✅ Fixed: no token used
                                    sessionManager.saveEmployeeId(String.valueOf(adminResponse.getUserId()));
                                    startActivity(new Intent(LoginActivity.this, AdminDashboardActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<AdminLoginResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });

                } else {
                    // Employee Login
                    String hashedPassword = password;

                    LoginRequest request = new LoginRequest(identifier, hashedPassword);
                    Call<LoginResponse> call = apiService.loginEmployee(request);

                    call.enqueue(new Callback<LoginResponse>() {
                        @Override
                        public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                            if (response.isSuccessful() && response.body() != null) {
                                LoginResponse loginResponse = response.body();
                                if (!loginResponse.getEmpId().isEmpty()) {
                                    Toast.makeText(LoginActivity.this, "Welcome, " + loginResponse.getName(), Toast.LENGTH_SHORT).show();

                                    SessionManager sessionManager = new SessionManager(LoginActivity.this);
                                    sessionManager.saveSession("DUMMY_TOKEN", "employee"); // ✅ use dummy token for employee
                                    sessionManager.saveEmployeeId(loginResponse.getEmpId());

                                    startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                    finish();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "Invalid credentials", Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<LoginResponse> call, Throwable t) {
                            Toast.makeText(LoginActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
        });

        btnSignup.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
