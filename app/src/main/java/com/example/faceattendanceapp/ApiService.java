package com.example.faceattendanceapp;

import com.google.gson.JsonObject;

import java.util.List;
import java.util.Map;

import okhttp3.MultipartBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface ApiService {

    // 🔐 Login
    @POST("/emp-login")
    Call<LoginResponse> loginEmployee(@Body LoginRequest request);

    @POST("/login")
    Call<AdminLoginResponse> loginAdmin(@Body AdminLoginRequest request);

    // 🔒 Change Password
    @POST("/api/admin/change-password")
    Call<ApiResponse> changeAdminPassword(@Body ChangePasswordRequest request);

    @POST("/api/employee/change-password")
    Call<ApiResponse> changeEmployeePassword(@Body EmployeeChangePasswordRequest request);

    // 👤 Registration
    @POST("api/register")
    Call<GenericResponse> registerUser(@Body RegisterRequest request);

    // 🖼️ Face Upload
    @POST("/update-face")
    Call<ApiResponse> updateFace(@Body UpdateFaceRequest request);

    @POST("emp-update-face")
    Call<ApiResponse> updateEmployeeFace(@Body UpdateEmployeeFaceRequest request);

    // 📅 Monthly Attendance
    @GET("/api/employee/attendance/monthly")
    Call<List<AttendanceRecord>> getMonthlyAttendance(@Query("month") String month);

// ✅ Base64 Face Attendance
    @POST("/emp-attendance-img")
    Call<ApiResponse> markBase64FaceAttendance(@Body Base64ImageRequest request);


    // 📝 Leave Apply
    @POST("/api/leave/apply")
    Call<ApiResponse> applyLeave(@Body LeaveApplyRequest request);

    // 📄 My Leave Requests (for employees)
    @POST("/api/leave/my-requests")
    Call<LeaveStatusResponse> getLeaveStatus(@Body LeaveStatusRequest request);

    // ✅ All Leave Requests (for admin)
    @GET("/api/leave/requests")
    Call<LeaveStatusResponse> getAllLeaveRequests();

    // ✅ Approve/Reject Leave
    @POST("/api/leave/decide")
    Call<ApiResponse> decideLeave(@Body LeaveDecisionRequest request);

    // ✅ Update Leave Balance
    @POST("/api/leave/balance/update")
    Call<ApiResponse> updateLeaveBalance(@Body UpdateLeaveBalanceRequest request);

    // ✅ Get Leave Balance by emp_id using path param
    @GET("/api/leave/balance/{emp_id}")
    Call<LeaveBalanceResponse> getLeaveBalanceById(@Path("emp_id") String empId);

    // 👥 Get All Employees
    @GET("/api/employees")
    Call<List<Employee>> getAllEmployees();

    // ✅ Get employee by ID (for search)
    @GET("/api/employees/{id}")
    Call<Employee> getEmployeeById(@Path("id") int id);

    // ✏️ Update Employee
    @PUT("/api/employees/{id}")
    Call<ApiResponse> updateEmployee(@Path("id") int id, @Body Map<String, String> body);

    // ❌ Delete Employee
    @DELETE("/api/employees/{id}")
    Call<ApiResponse> deleteEmployee(@Path("id") int id);

    // 📊 Attendance Dashboard
    @GET("/api/dashboard/attendance")
    Call<AttendanceDashboardResponse> getAttendanceDashboard();

    // 📅 Attendance Report
    @GET("/attendance-report")
    Call<List<AttendanceRecordReport>> getAttendanceReport(@Query("date") String date);

    // ✅ On Leave Today(employee)
    @GET("/api/leave/on-leave-today")
    Call<TodayLeaveResponse> getTodayOnLeave();

    @POST("/get_attendance")
    Call<AttendanceResponse> getAttendanceHistory(@Body JsonObject empIdJson);

    @POST("/manual-emp-attendance")
    Call<ManualAttendanceResponse> markManualAttendance(@Body ManualAttendanceRequest request);

}
