# Face-attendnace-system (Codevocado)

A **Face-based Smart Attendance System** built with Android Studio, designed for organizations to simplify and automate attendance management.  
The app leverages **Google ML Kit** for live face detection, GPS for location verification, and REST APIs for secure backend integration.  
---

##  Features

### 👨‍💼 Employee
- Mark Attendance  
  - **Face Attendance** (Live face detection + API verification)  
  - **Manual Attendance** (via Employee ID & API)  
- Apply for Leave  
- View Personal Attendance History  
- Update Face Image  

### 🛠️ Admin
- Mark Attendance  
  - **Face Attendance**  
  - **QR Code Attendance**  
- View Attendance Overview (Daily / Weekly / Monthly)  
- Approve / Reject Leave Requests  
- View & Manage Employee List  
- Add New Employee  

###  App-wide Features
- **Navigation Drawer** with:  
  - Dark Mode Toggle  
  - Change Password  
  - Logout  
- **Custom Splash Screen**  
- **Dark/Light Theme Support**  
- **User-friendly Dashboard**  
---

## 🏗️ Technologies Used
- **Platform:** Android Studio  
- **Language:** Java (Android SDK)  
- **UI Layouts:** XML  
- **Face Detection:** Google ML Kit  
- **Backend Communication:** REST APIs (provided by Codevocado)  
- **Database:** MySQL (via backend APIs)  
- **Location Services:** Android Location API  
- **Version Control:** GitHub  
- **API Testing:** Postman  
---

## 🔄 Workflow
1. **Login & Registration** – Users can log in as Admin or Employee, with registration for new users.  
2. **Face Detection** – Google ML Kit ensures a live face is detected before submission.  
3. **Location Fetching** – The app captures the employee’s GPS location.  
4. **Attendance Marking** – The captured image + location are verified through server-side APIs.  
5. **Data Management** – Attendance logs, leave requests, and employee data are stored in a backend MySQL database via APIs.  
---

##  Results
- Fully functional **Face Attendance App** with Admin & Employee panels.  
- Automated attendance process with improved **speed, accuracy, and reliability**.  
- Enhanced **user experience** with dark mode, navigation drawer, and smooth UI.  
- Real-time **AI-powered face detection** integrated with server-side recognition APIs.  

---
