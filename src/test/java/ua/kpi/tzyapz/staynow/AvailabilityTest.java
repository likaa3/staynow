package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести класу Availability: політика скасування (BL-60) і облік зайнятості номерів (BL-70).
 * Автор: Шкарупа М. Очікувані значення виведено з README модуля (BL-60, BL-70), а не з коду.
 * Відомі «червоні» тести на v1: BUG-M05 (межа дат BL-70), BUG-M06 (межа 7 днів BL-60).
 */
@Tag("unit")
@DisplayName("Availability — скасування (BL-60) і доступність номера (BL-70)")
class AvailabilityTest {

    private static final BigDecimal TOTAL = new BigDecimal("1000.00");
    private static final String ROOM = "101";

    private Availability availability;

    @BeforeEach
    void setUp() {
        availability = new Availability();
    }

    // ---------- BL-60: плата за скасування ----------

    // BUG-M06 (Issue #3): рядок "7,0.00" падає на v1 — код бере 50 % замість 0 рівно за 7 днів.
    @ParameterizedTest(name = "BL-60: {0} дн. до заїзду → плата {1}")
    @CsvSource({"30,0.00", "8,0.00", "7,0.00", "6,500.00", "3,500.00", "2,1000.00", "0,1000.00"})
    @DisplayName("BL-60: плата за скасування на межах 7 і 3 днів (вартість 1000.00)")
    void cancellationFeeAtBoundaries(int daysBeforeCheckIn, String expectedFee) {
        BigDecimal fee = availability.cancellationFee(TOTAL, daysBeforeCheckIn);
        assertEquals(new BigDecimal(expectedFee), fee);
    }

    @Test
    @DisplayName("BL-60: повернення = вартість − плата за скасування (5 днів → 50 %)")
    void refundIsTotalMinusFee() {
        assertEquals(new BigDecimal("500.00"), availability.refund(TOTAL, 5));
    }

    @Test
    @DisplayName("BL-60: безкоштовне скасування повертає всю суму (10 днів)")
    void freeCancellationRefundsEverything() {
        assertAll("10 днів до заїзду",
                () -> assertEquals(new BigDecimal("0.00"), availability.cancellationFee(TOTAL, 10)),
                () -> assertEquals(new BigDecimal("1000.00"), availability.refund(TOTAL, 10))
        );
    }

    @Test
    @DisplayName("BL-60: від'ємна вартість — IllegalArgumentException")
    void negativeTotalThrows() {
        assertThrows(IllegalArgumentException.class,
                () -> availability.cancellationFee(new BigDecimal("-1"), 5));
    }

    // ---------- BL-70: доступність номера ----------

    // BUG-M05 (Issue #2): падає на v1 — день виїзду = день заїзду іншого гостя помилково вважається перетином.
    @Test
    @DisplayName("BL-70: заїзд у день виїзду попереднього гостя дозволено")
    void checkInOnPreviousGuestCheckOutDayIsAllowed() {
        availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertTrue(availability.isAvailable(ROOM, LocalDate.of(2026, 9, 17), LocalDate.of(2026, 9, 19)),
                "Заїзд 17.09 має бути можливий: попередній гість виїздить 17.09");
    }

    // BUG-M05 (Issue #2): зворотний бік тієї ж межі.
    @Test
    @DisplayName("BL-70: виїзд у день заїзду наступного гостя дозволено")
    void checkOutOnNextGuestCheckInDayIsAllowed() {
        availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertTrue(availability.isAvailable(ROOM, LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 15)),
                "Виїзд 15.09 має бути можливий: наступний гість заїздить 15.09");
    }

    @Test
    @DisplayName("BL-70: перетин інтервалів — номер зайнятий, book кидає IllegalStateException")
    void overlappingBookingIsRejected() {
        availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertAll("перетин 16–18.09 з існуючим 15–17.09",
                () -> assertFalse(availability.isAvailable(ROOM, LocalDate.of(2026, 9, 16), LocalDate.of(2026, 9, 18))),
                () -> assertThrows(IllegalStateException.class,
                        () -> availability.book(ROOM, LocalDate.of(2026, 9, 16), LocalDate.of(2026, 9, 18)))
        );
    }

    @Test
    @DisplayName("BL-70: новий інтервал, що повністю накриває існуюче бронювання, — номер зайнятий")
    void intervalCoveringExistingStayIsNotAvailable() {
        availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertFalse(availability.isAvailable(ROOM, LocalDate.of(2026, 9, 10), LocalDate.of(2026, 9, 20)));
    }

    @Test
    @DisplayName("BL-70: інший номер на ті самі дати вільний")
    void otherRoomIsAvailable() {
        availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertTrue(availability.isAvailable("102", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17)));
    }

    @Test
    @DisplayName("BL-70: виїзд не пізніший за заїзд — IllegalArgumentException")
    void invalidIntervalThrows() {
        LocalDate day = LocalDate.of(2026, 9, 15);
        assertAll("некоректний інтервал",
                () -> assertThrows(IllegalArgumentException.class, () -> availability.isAvailable(ROOM, day, day)),
                () -> assertThrows(IllegalArgumentException.class, () -> availability.book(ROOM, day, day.minusDays(1)))
        );
    }

    @Test
    @DisplayName("BL-70: успішне бронювання потрапляє у перелік stays()")
    void bookRegistersStay() {
        Availability.Stay stay = availability.book(ROOM, LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));
        assertAll("зареєстроване бронювання",
                () -> assertEquals(1, availability.stays().size()),
                () -> assertEquals(stay, availability.stays().get(0))
        );
    }
}
