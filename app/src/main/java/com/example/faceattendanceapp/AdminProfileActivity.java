package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class AdminProfileActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_profile);

        // Views
        TextView textViewName = findViewById(R.id.textViewName);
        TextView textViewEmail = findViewById(R.id.textViewEmail);
        TextView textViewPhone = findViewById(R.id.textViewPhone);
        TextView textViewDesignation = findViewById(R.id.textViewRole);

        ImageButton buttonEdit = findViewById(R.id.buttonEdit);

        // Load dummy data (replace with real data later)
        textViewName.setText("Name: Admin Name");
        textViewEmail.setText("Email: admin@example.com");
        textViewPhone.setText("Phone: +1234567890");
        textViewDesignation.setText("Designation: Administrator");

        // Edit Button Click - Navigate to EditAdminProfileActivity
        buttonEdit.setOnClickListener(v -> {
            Intent intent = new Intent(AdminProfileActivity.this, EditAdminProfileActivity.class);
            startActivity(intent);
        });
    }
}
