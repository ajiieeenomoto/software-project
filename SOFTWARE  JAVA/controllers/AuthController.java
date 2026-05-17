package controllers;

import database.AdminDAO;
import database.StudentDAO;
import models.Admin;
import models.Student;

/**
 * Authentication controller — delegates to the respective DAOs.
 */
public class AuthController {

    private final StudentDAO studentDAO = new StudentDAO();
    private final AdminDAO   adminDAO   = new AdminDAO();

    /**
     * Authenticates a student.
     * @return Student on success, null on failure.
     */
    public Student loginStudent(String studentNumber, String plainPassword) {
        return studentDAO.authenticate(studentNumber, plainPassword);
    }

    /**
     * Authenticates an admin.
     * @return Admin on success, null on failure.
     */
    public Admin loginAdmin(String username, String plainPassword) {
        return adminDAO.authenticate(username, plainPassword);
    }
}