package models;

import java.sql.Timestamp;

/**
 * Represents a row in the `registrations` table.
 */
public class Registration {

    public enum Status { pending, approved, rejected, cancelled }

    private int       registrationId;
    private int       eventId;
    private int       studentId;
    private Status    status;
    private String    qrCodeData;
    private Timestamp registeredAt;
    private Timestamp cancelledAt;

    // Joined display fields
    private String    studentName;
    private String    studentNumber;
    private String    eventTitle;

    public Registration() {}

    public boolean isCancellable() {
        return status == Status.approved || status == Status.pending;
    }

    // ── Getters & Setters ──────────────────────────────────────

    public int       getRegistrationId()     { return registrationId; }
    public void      setRegistrationId(int v){ this.registrationId = v; }

    public int       getEventId()            { return eventId; }
    public void      setEventId(int v)       { this.eventId = v; }

    public int       getStudentId()          { return studentId; }
    public void      setStudentId(int v)     { this.studentId = v; }

    public Status    getStatus()             { return status; }
    public void      setStatus(Status v)     { this.status = v; }

    public String    getQrCodeData()         { return qrCodeData; }
    public void      setQrCodeData(String v) { this.qrCodeData = v; }

    public Timestamp getRegisteredAt()       { return registeredAt; }
    public void      setRegisteredAt(Timestamp v){ this.registeredAt = v; }

    public Timestamp getCancelledAt()        { return cancelledAt; }
    public void      setCancelledAt(Timestamp v){ this.cancelledAt = v; }

    public String    getStudentName()        { return studentName; }
    public void      setStudentName(String v){ this.studentName = v; }

    public String    getStudentNumber()      { return studentNumber; }
    public void      setStudentNumber(String v){ this.studentNumber = v; }

    public String    getEventTitle()         { return eventTitle; }
    public void      setEventTitle(String v) { this.eventTitle = v; }
}