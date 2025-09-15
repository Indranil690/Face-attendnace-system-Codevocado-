package com.example.faceattendanceapp;

import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.qrcode.QRCodeWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EmployeeAdapter extends RecyclerView.Adapter<EmployeeAdapter.EmployeeViewHolder> {

    private Context context;
    private List<Employee> employeeList;

    public EmployeeAdapter(Context context, List<Employee> employeeList) {
        this.context = context;
        this.employeeList = employeeList;
    }

    @NonNull
    @Override
    public EmployeeViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.employee_item, parent, false);
        return new EmployeeViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmployeeViewHolder holder, int position) {
        Employee employee = employeeList.get(position);
        holder.tvName.setText(employee.getName());
        holder.tvEmail.setText(employee.getEmail());
        holder.tvDesignation.setText(employee.getDesignation());
        holder.tvEmpId.setText("ID: " + employee.getEmp_id());
        holder.tvDepartment.setText("Dept: " + employee.getDepartment());
        holder.tvPhone.setText("Phone: " + employee.getPhone());

        // ✅ Edit icon click
        holder.ivEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, EditEmployeeActivity.class);
            intent.putExtra("id", employee.getId());
            intent.putExtra("emp_id", employee.getEmp_id());
            intent.putExtra("name", employee.getName());
            intent.putExtra("email", employee.getEmail());
            intent.putExtra("designation", employee.getDesignation());
            intent.putExtra("department", employee.getDepartment());
            intent.putExtra("phone", employee.getPhone());
            context.startActivity(intent);
        });

        // ✅ Delete icon click
        holder.ivDelete.setOnClickListener(v -> {
            new AlertDialog.Builder(context)
                    .setTitle("Delete Employee")
                    .setMessage("Are you sure you want to delete " + employee.getName() + "?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        int employeeId = employee.getId();
                        ApiService apiService = ApiClient.getClient(context).create(ApiService.class);
                        Call<ApiResponse> call = apiService.deleteEmployee(employeeId);

                        call.enqueue(new Callback<ApiResponse>() {
                            @Override
                            public void onResponse(Call<ApiResponse> call, Response<ApiResponse> response) {
                                if (response.isSuccessful()) {
                                    int pos = holder.getAdapterPosition();
                                    employeeList.remove(pos);
                                    notifyItemRemoved(pos);
                                    Toast.makeText(context, "✅ Deleted " + employee.getName(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(context, "❌ Failed to delete employee", Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onFailure(Call<ApiResponse> call, Throwable t) {
                                Toast.makeText(context, "🚫 Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    })
                    .setNegativeButton("Cancel", null)
                    .show();
        });

        // ✅ Generate QR click
        holder.ivGenerateQR.setOnClickListener(v -> {
            String empId = employee.getEmp_id();  // use employee ID
            generateAndShowQR(empId);
        });
    }

    @Override
    public int getItemCount() {
        return employeeList.size();
    }

    public class EmployeeViewHolder extends RecyclerView.ViewHolder {
        TextView tvName, tvEmail, tvDesignation, tvEmpId, tvDepartment, tvPhone;
        ImageView ivEdit, ivDelete, ivGenerateQR;

        public EmployeeViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tvEmployeeName);
            tvEmail = itemView.findViewById(R.id.tvEmployeeEmail);
            tvDesignation = itemView.findViewById(R.id.tvEmployeeDesignation);
            tvEmpId = itemView.findViewById(R.id.tvEmployeeId);
            tvDepartment = itemView.findViewById(R.id.tvEmployeeDept);
            tvPhone = itemView.findViewById(R.id.tvEmployeePhone);
            ivEdit = itemView.findViewById(R.id.ivEdit);
            ivDelete = itemView.findViewById(R.id.ivDelete);
            ivGenerateQR = itemView.findViewById(R.id.ivGenerateQR);
        }
    }

    // 🔹 Generate QR Code and show in dialog
    private void generateAndShowQR(String empId) {
        QRCodeWriter writer = new QRCodeWriter();
        try {
            int size = 512;
            com.google.zxing.common.BitMatrix bitMatrix = writer.encode(empId, BarcodeFormat.QR_CODE, size, size);
            Bitmap bitmap = Bitmap.createBitmap(size, size, Bitmap.Config.RGB_565);

            for (int x = 0; x < size; x++) {
                for (int y = 0; y < size; y++) {
                    bitmap.setPixel(x, y, bitMatrix.get(x, y) ? Color.BLACK : Color.WHITE);
                }
            }

            // Show QR in dialog
            ImageView qrImage = new ImageView(context);
            qrImage.setImageBitmap(bitmap);

            new AlertDialog.Builder(context)
                    .setTitle("QR Code for Employee ID: " + empId)
                    .setView(qrImage)
                    .setPositiveButton("Download", (dialog, which) -> saveQR(bitmap, empId)) // ✅ now works
                    .setNegativeButton("Close", null)
                    .show();

        } catch (WriterException e) {
            e.printStackTrace();
            Toast.makeText(context, "Error generating QR", Toast.LENGTH_SHORT).show();
        }
    }

    // ✅ This now actually saves QR
    private void saveQR(Bitmap bitmap, String empId) {
        saveQrToDownloads(bitmap); // just call your existing method
    }

    // 🔹 Save QR to Downloads
    private void saveQrToDownloads(Bitmap bitmap) {
        OutputStream fos;
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, "QRCode_" + System.currentTimeMillis() + ".png");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/png");
                // Save in Pictures/QR Codes so it shows in Gallery
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/QR Codes");
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);

                if (imageUri != null) {
                    fos = resolver.openOutputStream(imageUri);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                    fos.close();
                    Toast.makeText(context, "QR saved to Gallery (Pictures/QR Codes)", Toast.LENGTH_SHORT).show();

                    // Refresh Gallery
                    context.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, imageUri));
                }
            } else {
                // For older Android versions
                String imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString() + "/QR Codes";
                File dir = new File(imagesDir);
                if (!dir.exists()) dir.mkdirs();

                File image = new File(dir, "QRCode_" + System.currentTimeMillis() + ".png");
                fos = new FileOutputStream(image);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();
                Toast.makeText(context, "QR saved to Gallery (Pictures/QR Codes)", Toast.LENGTH_SHORT).show();

                // Refresh gallery
                MediaScannerConnection.scanFile(context, new String[]{image.getAbsolutePath()}, null, null);
            }
        } catch (Exception e) {
            Toast.makeText(context, "Failed to save QR: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
