package campusconnect.backend.entity;
    public enum EventStatus {
        PENDING,    // College made an event request
        PLANNED,    // Admin given the event flow
        CONFIRMED,  // College confirmed
        REJECTED,   // College/Admin rejected
        RESCHEDULED, // College wants to change date
        BOOKED, // Student booked the event
        COMPLETED
    }
