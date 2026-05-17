package models;

import java.sql.Date;
import java.sql.Time;
import java.sql.Timestamp;

/**
 * Represents an event record from the `events` table.
 */
public class Event {

    private int       eventId;
    private int       categoryId;
    private String    categoryName;   // joined from event_categories
    private String    title;
    private String    description;
    private String    venue;
    private Date      eventDate;
    private Time      eventTime;
    private String    organizer;
    private int       maxParticipants;
    private int       registeredCount;
    private boolean   registrationOpen;
    private Timestamp registrationDeadline;
    private boolean   active;
    private int       createdBy;
    private Timestamp createdAt;

    public Event() {}

    /** Convenience: remaining slots */
    public int getRemainingSlots() {
        return Math.max(0, maxParticipants - registeredCount);
    }

    /** True when there are still open slots and registration is open */
    public boolean isAvailableForRegistration() {
        return registrationOpen && getRemainingSlots() > 0 && active;
    }

    // ── Getters & Setters ──────────────────────────────────────

    public int       getEventId()           { return eventId; }
    public void      setEventId(int v)      { this.eventId = v; }

    public int       getCategoryId()        { return categoryId; }
    public void      setCategoryId(int v)   { this.categoryId = v; }

    public String    getCategoryName()      { return categoryName; }
    public void      setCategoryName(String v){ this.categoryName = v; }

    public String    getTitle()             { return title; }
    public void      setTitle(String v)     { this.title = v; }

    public String    getDescription()       { return description; }
    public void      setDescription(String v){ this.description = v; }

    public String    getVenue()             { return venue; }
    public void      setVenue(String v)     { this.venue = v; }

    public Date      getEventDate()         { return eventDate; }
    public void      setEventDate(Date v)   { this.eventDate = v; }

    public Time      getEventTime()         { return eventTime; }
    public void      setEventTime(Time v)   { this.eventTime = v; }

    public String    getOrganizer()         { return organizer; }
    public void      setOrganizer(String v) { this.organizer = v; }

    public int       getMaxParticipants()   { return maxParticipants; }
    public void      setMaxParticipants(int v){ this.maxParticipants = v; }

    public int       getRegisteredCount()   { return registeredCount; }
    public void      setRegisteredCount(int v){ this.registeredCount = v; }

    public boolean   isRegistrationOpen()   { return registrationOpen; }
    public void      setRegistrationOpen(boolean v){ this.registrationOpen = v; }

    public Timestamp getRegistrationDeadline(){ return registrationDeadline; }
    public void      setRegistrationDeadline(Timestamp v){ this.registrationDeadline = v; }

    public boolean   isActive()             { return active; }
    public void      setActive(boolean v)   { this.active = v; }

    public int       getCreatedBy()         { return createdBy; }
    public void      setCreatedBy(int v)    { this.createdBy = v; }

    public Timestamp getCreatedAt()         { return createdAt; }
    public void      setCreatedAt(Timestamp v){ this.createdAt = v; }

    @Override
    public String toString() { return title + " @ " + venue + " on " + eventDate; }
}