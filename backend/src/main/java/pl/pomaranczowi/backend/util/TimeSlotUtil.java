package pl.pomaranczowi.backend.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Utility class for generating time slot strings at a specified interval.
 */
public class TimeSlotUtil {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

    /**
     * Generates a list of time slot strings between start and end times
     * at the given interval in minutes. Each slot is formatted as "HH:mm".
     *
     * @param start        the start time (inclusive)
     * @param end          the end time (exclusive - slots must fit fully within)
     * @param slotMinutes  the interval between slots in minutes
     * @return list of time slot strings, or an empty list if inputs are invalid
     */
    public static List<String> generateTimeSlots(LocalTime start, LocalTime end, int slotMinutes) {
        List<String> slots = new ArrayList<>();
        if (start == null || end == null || !start.isBefore(end) || slotMinutes <= 0) {
            return slots;
        }

        LocalTime cursor = start;
        while (!cursor.plusMinutes(slotMinutes).isAfter(end)) {
            slots.add(cursor.format(FORMAT));
            cursor = cursor.plusMinutes(slotMinutes);
        }
        return slots;
    }
}
