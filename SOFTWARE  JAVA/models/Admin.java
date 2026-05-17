package models;

import java.sql.Timestamp;

/**
 * Represents an admin record from the `admins` table.
 */
public class Admin {

    private int       adminId;
    private String    username;
    private String    passwordHash;
    private String    fullName;
    private String    email;
    private boolean   active;
    private Timestamp createdAt;

    public Admin() {}

    public int       getAdminId()          { return adminId; }
    public void      setAdminId(int v)     { this.adminId = v; }

    public String    getUsername()         { return username; }
    public void      setUsername(String v) { this.username = v; }

    public String    getPasswordHash()          { return passwordHash; }
    public void      setPasswordHash(String v)  { this.passwordHash = v; }

    public String    getFullName()         { return fullName; }
    public void      setFullName(String v) { this.fullName = v; }

    public String    getEmail()            { return email; }
    public void      setEmail(String v)    { this.email = v; }

    public boolean   isActive()            { return active; }
    public void      setActive(boolean v)  { this.active = v; }

    public Timestamp getCreatedAt()        { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    @Override public String toString() { return fullName + " (" + username + ")"; }
}