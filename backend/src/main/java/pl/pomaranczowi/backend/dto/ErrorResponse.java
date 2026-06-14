package pl.pomaranczowi.backend.dto;

import java.time.LocalDateTime;

/**
 * DTO for structured error responses sent to clients.
 */
public class ErrorResponse {

    /** HTTP status code of the error. */
    private int status;
    /** Error message describing the problem. */
    private String message;
    /** Timestamp when the error occurred. */
    private LocalDateTime timestamp;

    /**
     * Creates a new instance of {@link ErrorResponse}.
     */
    public ErrorResponse() {
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Constructs an error response with the given status and message.
     * The timestamp is set to the current time.
     *
     * @param status the HTTP status code of the error
     * @param message the error message describing the problem
     */
    public ErrorResponse(int status, String message) {
        this.status = status;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    /**
     * Returns the {@link #status}.
     *
     * @return the {@link #status}
     */
    public int getStatus() { return status; }
    /**
     * Sets the {@link #status}.
     *
     * @param status the {@link #status} to set
     */
    public void setStatus(int status) { this.status = status; }

    /**
     * Returns the {@link #message}.
     *
     * @return the {@link #message}
     */
    public String getMessage() { return message; }
    /**
     * Sets the {@link #message}.
     *
     * @param message the {@link #message} to set
     */
    public void setMessage(String message) { this.message = message; }

    /**
     * Returns the {@link #timestamp}.
     *
     * @return the {@link #timestamp}
     */
    public LocalDateTime getTimestamp() { return timestamp; }
    /**
     * Sets the {@link #timestamp}.
     *
     * @param timestamp the {@link #timestamp} to set
     */
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }
}
