package pl.pomaranczowi.backend.entity;

/**
 * Enumeration of possible appointment states.
 */
public enum AppointmentStatus {
    /** Appointment has been confirmed and is awaiting the scheduled time. */
    BOOKED,
    /** Appointment was cancelled and will not proceed. */
    CANCELLED,
    /** Appointment has been finished and the service was provided. */
    COMPLETED
}
