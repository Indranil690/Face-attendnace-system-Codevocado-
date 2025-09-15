package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class EditAdminProfileActivity extends AppCompatActivity {

    private EditText editTextName, editTextEmail, editTextPhone, editTextDesignation;
    private Button buttonSave;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_admin_profile);

        // Initialize views
        editTextName = findViewById(R.id.editTextName);
        editTextEmail = findViewById(R.id.editTextEmail);
        editTextPhone = findViewById(R.id.editTextPhone);
        editTextDesignation = findViewById(R.id.editTextRole); // This is your "Designation" EditText
        buttonSave = findViewById(R.id.buttonSave);

        // TODO: Load real data here if you have it
        editTextName.setText("Admin Name");
        editTextEmail.setText("admin@example.com");
        editTextPhone.setText("1234567890");
        editTextDesignation.setText("Administrator");

        // Handle Save button
        buttonSave.setOnClickListener(v -> {
            String name = editTextName.getText().toString().trim();
            String email = editTextEmail.getText().toString().trim();
            String phone = editTextPhone.getText().toString().trim();
            String designation = editTextDesignation.getText().toString().trim();

            // You could validate fields here (optional)

            // Show a confirmation (you can customize)
            Toast.makeText(this, "Profile updated successfully!", Toast.LENGTH_SHORT).show();

            // Return to Admin Dashboard
            Intent intent = new Intent(EditAdminProfileActivity.this, AdminDashboardActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
            finish();
        });
    }
}
