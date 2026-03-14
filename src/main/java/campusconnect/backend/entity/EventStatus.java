package campusconnect.backend.entity;
    public enum EventStatus {
        PENDING,    // College has not responded
        CONFIRMED,  // College confirmed
        REJECTED,   // College rejected
        RESCHEDULED // College wants to change date
    }
