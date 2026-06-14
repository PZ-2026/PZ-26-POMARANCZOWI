package pl.pomaranczowi.backend.entity;

/**
 * Enumeration of user roles in the system.
 */
public enum UserRole {
    /** System administrator with full access. */
    ADMIN,
    /** Employee (barber) who can manage appointments and availability. */
    EMPLOYEE,
    /** Regular client who can book appointments. */
    CLIENT
}
