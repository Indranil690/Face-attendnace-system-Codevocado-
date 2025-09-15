package com.example.faceattendanceapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeName, etEmployeeEmail, etEmployeeDesignation, etEmployeePhone, etEmployeeDepartment;
    private Button btnSaveChanges;

    private String userId; // this is the actual backend ID (emp_id)

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_employee);

        // Initialize views
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        etEmployeeDesignation = findViewById(R.id.etEmployeeDesignation);
        etEmployeePhone = findViewById(R.id.etEmployeePhone);
        etEmployeeDepartment = findViewById(R.id.etEmployeeDepartment);
        btnSaveChanges = findViewById(R.id.btnSaveChanges);

        // Get passed data from adapter
        userId = getIntent().getStringExtra("emp_id");
        String name = getIntent().getStringExtra("name");
        String email = getIntent().getStringExtra("email");
        String phone = getIntent().getStringExtra("phone");
        String department = getIntent().getStringExtra("department");
        String designation = getIntent().getStringExtra("designation");

        // Pre-fill fields
        etEmployeeName.setText(name);
        etEmployeeEmail.setText(email);
        etEmployeeDesignation.setText(designation);
        etEmployeePhone.setText(phone);
        etEmployeeDepartment.setText(department);

        btnSaveChanges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String updatedName = etEmployeeName.getText().toString().trim();
                String updatedEmail = etEmployeeEmail.getText().toString().trim();
                String updatedDesignation = etEmployeeDesignation.getText().toString().trim();
                String updatedPhone = etEmployeePhone.getText().toString().trim();
                String updatedDepartment = etEmployeeDepartment.getText().toString().trim();

                if (TextUtils.isEmpty(updatedName) || TextUtils.isEmpty(updatedEmail) ||
                        TextUtils.isEmpty(updatedDesignation) || TextUtils.isEmpty(updatedPhone) ||
                        TextUtils.isEmpty(updatedDepartment)) {
                    Toast.makeText(EditEmployeeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                    return;
                }

                updateEmployeeOnServer(updatedName, updatedEmail, updatedPhone, updatedDepartment, updatedDesignation);
            }
        });
    }

    private void updateEmployeeOnServer(String name, String email, String phone, String department, String designation) {
        if (userId == null) {
            Toast.makeText(this, "Invalid user ID", Toast.LENGTH_SHORT).show();
            return;
        }

        int realBackendId;
        try {
            realBackendId = Integer.parseInt(userId);  // Use emp_id as backend ID
        } catch (NumberFormatException e) {
            Toast.makeText(this, "Invalid emp_id format", Toast.LENGTH_SHORT).show();
            return;
        }

        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);

        Map<String, String> updateData = new HashMap<>();
        updateData.put("name", name);
        updateData.put("email", email);
        updateData.put("phone", phone);
        updateData.put("department", department);
        updateData.put("designation", designation);
        updateData.put("userId", userId);  // still pass userId in body if API expects it

        Call<ApiResponse> call = apiService.updateEmployee(realBackendId, updateData);
        call.enqueue(new Callback<ApiResponse>() {
            @Override
            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(EditEmployeeActivity.this, "✅ Employee updated successfully", Toast.LENGTH_SHORT).show();
                    finish(); // Close activity
                } else {
                    Toast.makeText(EditEmployeeActivity.this, "❌ Update failed", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ApiResponse> call, Throwable t) {
                Toast.makeText(EditEmployeeActivity.this, "🚫 Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
