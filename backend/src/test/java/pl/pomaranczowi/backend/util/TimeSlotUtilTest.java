package pl.pomaranczowi.backend.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TimeSlotUtilTest {

    @Test
    void generateTimeSlots_StandardInterval() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(9, 0), LocalTime.of(10, 0), 15);

        assertAll("standard slots",
            () -> assertEquals(4, slots.size()),
            () -> assertEquals("09:00", slots.get(0)),
            () -> assertEquals("09:15", slots.get(1)),
            () -> assertEquals("09:30", slots.get(2)),
            () -> assertEquals("09:45", slots.get(3))
        );
    }

    @ParameterizedTest
    @CsvSource({
        "9, 0, 17, 0, 60, 8",
        "8, 0, 12, 0, 30, 8",
        "10, 0, 10, 30, 15, 2"
    })
    void generateTimeSlots_VariousIntervals(int startH, int startM, int endH, int endM, int interval, int expectedSize) {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(startH, startM), LocalTime.of(endH, endM), interval);

        assertEquals(expectedSize, slots.size());
    }

    @Test
    void generateTimeSlots_NullStart_ReturnsEmpty() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(null, LocalTime.of(10, 0), 15);
        assertTrue(slots.isEmpty());
    }

    @Test
    void generateTimeSlots_NullEnd_ReturnsEmpty() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(LocalTime.of(9, 0), null, 15);
        assertTrue(slots.isEmpty());
    }

    @Test
    void generateTimeSlots_StartAfterEnd_ReturnsEmpty() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(10, 0), LocalTime.of(9, 0), 15);
        assertTrue(slots.isEmpty());
    }

    @Test
    void generateTimeSlots_StartEqualsEnd_ReturnsEmpty() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(9, 0), LocalTime.of(9, 0), 15);
        assertTrue(slots.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"0", "-1", "-30"})
    void generateTimeSlots_InvalidInterval_ReturnsEmpty(int interval) {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(9, 0), LocalTime.of(10, 0), interval);
        assertTrue(slots.isEmpty());
    }

    @Test
    void generateTimeSlots_ExactBoundary_ExcludesEnd() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(9, 0), LocalTime.of(9, 30), 30);

        assertEquals(1, slots.size());
        assertEquals("09:00", slots.get(0));
    }

    @Test
    void generateTimeSlots_LargeRange() {
        List<String> slots = TimeSlotUtil.generateTimeSlots(
            LocalTime.of(6, 0), LocalTime.of(22, 0), 60);

        assertAll("daily slots",
            () -> assertEquals(16, slots.size()),
            () -> assertEquals("06:00", slots.get(0)),
            () -> assertEquals("21:00", slots.get(15))
        );
    }
}
