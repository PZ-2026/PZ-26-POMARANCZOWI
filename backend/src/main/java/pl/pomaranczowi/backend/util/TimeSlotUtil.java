package pl.pomaranczowi.backend.util;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TimeSlotUtil {

    private static final DateTimeFormatter FORMAT = DateTimeFormatter.ofPattern("HH:mm");

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
