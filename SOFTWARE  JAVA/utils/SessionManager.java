package utils;

import models.Admin;
import models.Student;

/**
 * Simple session manager — holds the currently logged-in user.
 * Use SessionManager.getInstance() anywhere in the application.
 */
public class SessionManager {

    private static SessionManager instance;

    private Student currentStudent;
    private Admin   currentAdmin;
    private boolean isAdminSession;

    private SessionManager() {}

    public static synchronized SessionManager getInstance() {
        if (instance == null) instance = new SessionManager();
        return instance;
    }

    public void loginStudent(Student student) {
        this.currentStudent = student;
        this.currentAdmin   = null;
        this.isAdminSession = false;
    }

    public void loginAdmin(Admin admin) {
        this.currentAdmin   = admin;
        this.currentStudent = null;
        this.isAdminSession = true;
    }

    public void logout() {
        currentStudent  = null;
        currentAdmin    = null;
        isAdminSession  = false;
    }

    public boolean isLoggedIn()     { return currentStudent != null || currentAdmin != null; }
    public boolean isAdminSession() { return isAdminSession; }

    public Student getCurrentStudent() { return currentStudent; }
    public Admin   getCurrentAdmin()   { return currentAdmin; }
}