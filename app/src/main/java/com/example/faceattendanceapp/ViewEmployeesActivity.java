package com.example.faceattendanceapp;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Collections;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ViewEmployeesActivity extends AppCompatActivity {

    private RecyclerView recyclerViewEmployees;
    private FloatingActionButton fabAddEmployee;
    private EditText editTextSearchId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeManager.applySavedTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_employees);

        // Initialize views
        recyclerViewEmployees = findViewById(R.id.recyclerViewEmployees);
        recyclerViewEmployees.setLayoutManager(new LinearLayoutManager(this));

        fabAddEmployee = findViewById(R.id.fabAddEmployee);
        editTextSearchId = findViewById(R.id.editTextSearchId);

        fabAddEmployee.setOnClickListener(view -> {
            startActivity(new Intent(ViewEmployeesActivity.this, AddEmployeeActivity.class));
        });

        // Handle keyboard "Search" key
        editTextSearchId.setOnEditorActionListener((TextView v, int actionId, KeyEvent event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                String empIdStr = editTextSearchId.getText().toString().trim();
                if (empIdStr.isEmpty()) {
                    fetchEmployeesFromAPI(); // Load all if empty
                } else {
                    try {
                        int id = Integer.parseInt(empIdStr);
                        fetchEmployeeById(id);
                    } catch (NumberFormatException e) {
                        Toast.makeText(this, "Please enter a valid numeric ID", Toast.LENGTH_SHORT).show();
                    }
                }
                return true;
            }
            return false;
        });

        // Load all employees initially
        fetchEmployeesFromAPI();
    }

    private void fetchEmployeesFromAPI() {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<List<Employee>> call = apiService.getAllEmployees();

        call.enqueue(new Callback<List<Employee>>() {
            @Override
            public void onResponse(Call<List<Employee>> call, Response<List<Employee>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    List<Employee> employeeList = response.body();
                    recyclerViewEmployees.setAdapter(new EmployeeAdapter(ViewEmployeesActivity.this, employeeList));
                } else {
                    Toast.makeText(ViewEmployeesActivity.this, "❌ Failed to load employees", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Employee>> call, Throwable t) {
                Toast.makeText(ViewEmployeesActivity.this, "🚫 Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void fetchEmployeeById(int id) {
        ApiService apiService = ApiClient.getClient(this).create(ApiService.class);
        Call<Employee> call = apiService.getEmployeeById(id);

        call.enqueue(new Callback<Employee>() {
            @Override
            public void onResponse(Call<Employee> call, Response<Employee> response) {
                if (response.isSuccessful() && response.body() != null) {
                    Employee employee = response.body();
                    recyclerViewEmployees.setAdapter(new EmployeeAdapter(ViewEmployeesActivity.this, Collections.singletonList(employee)));
                } else {
                    Toast.makeText(ViewEmployeesActivity.this, "No employee found with ID: " + id, Toast.LENGTH_SHORT).show();
                    recyclerViewEmployees.setAdapter(null);
                }
            }

            @Override
            public void onFailure(Call<Employee> call, Throwable t) {
                Toast.makeText(ViewEmployeesActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
