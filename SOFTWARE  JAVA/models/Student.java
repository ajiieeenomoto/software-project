package models;

import java.sql.Timestamp;

/**
 * Represents a student record from the `students` table.
 */
public class Student {

    private int       studentId;
    private String    studentNumber;
    private String    passwordHash;
    private String    firstName;
    private String    lastName;
    private String    email;
    private String    course;
    private int       yearLevel;
    private String    section;
    private String    contactNumber;
    private boolean   active;
    private Timestamp createdAt;
    private Timestamp updatedAt;

    public Student() {}

    public Student(int studentId, String studentNumber, String firstName,
                   String lastName, String email, String course,
                   int yearLevel, String section, String contactNumber) {
        this.studentId     = studentId;
        this.studentNumber = studentNumber;
        this.firstName     = firstName;
        this.lastName      = lastName;
        this.email         = email;
        this.course        = course;
        this.yearLevel     = yearLevel;
        this.section       = section;
        this.contactNumber = contactNumber;
    }

    /** Returns "First Last" */
    public String getFullName() {
        return firstName + " " + lastName;
    }

    // ── Getters & Setters ──────────────────────────────────────

    public int       getStudentId()     { return studentId; }
    public void      setStudentId(int v){ this.studentId = v; }

    public String    getStudentNumber()      { return studentNumber; }
    public void      setStudentNumber(String v){ this.studentNumber = v; }

    public String    getPasswordHash()       { return passwordHash; }
    public void      setPasswordHash(String v){ this.passwordHash = v; }

    public String    getFirstName()      { return firstName; }
    public void      setFirstName(String v){ this.firstName = v; }

    public String    getLastName()       { return lastName; }
    public void      setLastName(String v){ this.lastName = v; }

    public String    getEmail()          { return email; }
    public void      setEmail(String v)  { this.email = v; }

    public String    getCourse()         { return course; }
    public void      setCourse(String v) { this.course = v; }

    public int       getYearLevel()      { return yearLevel; }
    public void      setYearLevel(int v) { this.yearLevel = v; }

    public String    getSection()        { return section; }
    public void      setSection(String v){ this.section = v; }

    public String    getContactNumber()  { return contactNumber; }
    public void      setContactNumber(String v){ this.contactNumber = v; }

    public boolean   isActive()          { return active; }
    public void      setActive(boolean v){ this.active = v; }

    public Timestamp getCreatedAt()      { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    public Timestamp getUpdatedAt()      { return updatedAt; }
    public void      setUpdatedAt(Timestamp v){ this.updatedAt = v; }

    @Override
    public String toString() {
        return "[" + studentNumber + "] " + getFullName() + " — " + course;
    }
}