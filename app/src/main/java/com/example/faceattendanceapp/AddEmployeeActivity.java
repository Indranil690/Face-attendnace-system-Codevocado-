package com.example.faceattendanceapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class AddEmployeeActivity extends AppCompatActivity {

    private EditText etEmployeeName, etEmployeeEmail, etEmployeeDesignation;
    private Button btnSaveEmployee;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply dark mode theme
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_employee);

        // Initialize views
        etEmployeeName = findViewById(R.id.etEmployeeName);
        etEmployeeEmail = findViewById(R.id.etEmployeeEmail);
        etEmployeeDesignation = findViewById(R.id.etEmployeeDesignation);
        btnSaveEmployee = findViewById(R.id.btnSaveEmployee);

        // Save button click listener
        btnSaveEmployee.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etEmployeeName.getText().toString().trim();
                String email = etEmployeeEmail.getText().toString().trim();
                String designation = etEmployeeDesignation.getText().toString().trim();

                // Simple validation
                if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(designation)) {
                    Toast.makeText(AddEmployeeActivity.this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                } else {
                    // For now, just show a confirmation
                    Toast.makeText(AddEmployeeActivity.this, "Employee added: " + name, Toast.LENGTH_SHORT).show();

                    // Later, you can save to database or return to ViewEmployeesActivity
                    finish(); // Close the Add screen
                }
            }
        });
    }
}
