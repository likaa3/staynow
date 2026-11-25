package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести вимоги BL-10 SRS_04 «СтейНау», розд. 5 (метод {@link BookingValidator#validate}):
 * <ul>
 *   <li>дата виїзду пізніша за дату заїзду;</li>
 *   <li>ночей 1..30 (межі 1, 30 і 31);</li>
 *   <li>дата заїзду не раніше today;</li>
 *   <li>гостей від 1 до місткості номера.</li>
 * </ul>
 * Очікувані значення виведено з BL-10 (README модуля, розд. «Реалізовані правила»), а не з коду.
 * «Сьогодні» зафіксовано константою, тож тест не залежить від дати запуску.
 * BL-10 не визначає винятків: заборонений вхід повертає коди {@link BookingError}, їх і перевіряємо.
 * Автор: Бєлянцева А. (ТА). Рецензент: Шкарупа М.
 * Відомі «червоні» виконання на v1 — 3: BUG-M01 (рядок «30 ночей») і BUG-M02 (два рядки із заїздом учора).
 */
@Tag("unit")
@DisplayName("BookingValidator — дати, ночі, гості (BL-10)")
class BookingValidatorTest {

    /** Умовне «сьогодні»: вівторок 08.09.2026 (вересень — звичайний сезон). */
    private static final LocalDate TODAY = LocalDate.of(2026, 9, 8);
    /** Заїзд через тиждень — свідомо допустима дата для перевірок інших правил. */
    private static final LocalDate CHECK_IN = TODAY.plusDays(7);
    private static final int CAPACITY = 2;

    private BookingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
    }

    // нормальний випадок

    @Test
    @DisplayName("BL-10: 2 ночі, 2 гості з 2 місць, заїзд у майбутньому → помилок немає")
    void validBookingHasNoErrors() {
        List<BookingError> errors = validator.validate(CHECK_IN, CHECK_IN.plusDays(2), 2, CAPACITY, TODAY);

        assertTrue(errors.isEmpty(), "Очікувався порожній список помилок, отримано: " + errors);
    }

    // дата виїзду пізніша за дату заїзду

    @ParameterizedTest(name = "BL-10: виїзд на {0} дн. від заїзду → CHECK_OUT_NOT_AFTER_CHECK_IN")
    @CsvSource({"0", "-1", "-10"})
    @DisplayName("BL-10: виїзд у день заїзду або раніше відхиляється")
    void checkOutNotAfterCheckInIsRejected(int checkOutOffset) {
        List<BookingError> errors =
                validator.validate(CHECK_IN, CHECK_IN.plusDays(checkOutOffset), 1, CAPACITY, TODAY);

        assertTrue(errors.contains(BookingError.CHECK_OUT_NOT_AFTER_CHECK_IN),
                "Очікувалась помилка CHECK_OUT_NOT_AFTER_CHECK_IN, отримано: " + errors);
    }

    // ночей 1..30

    // BUG-M01 (Issue #7): рядок «30, true» падає на v1 — код відхиляє рівно 30 ночей (умова nights >= 30).
    @ParameterizedTest(name = "BL-10: {0} ноч. → допустимо: {1}")
    @CsvSource({
            "1, true",    // нижня межа
            "2, true",    // поруч із нижньою межею
            "29, true",   // поруч із верхньою межею
            "30, true",   // верхня межа — BUG-M01
            "31, false",  // за верхньою межею
            "45, false"   // далеко за межею
    })
    @DisplayName("BL-10: кількість ночей — від 1 до 30 включно (межі 1, 30, 31)")
    void nightsAreAllowedFromOneToThirty(int nights, boolean allowed) {
        List<BookingError> errors =
                validator.validate(CHECK_IN, CHECK_IN.plusDays(nights), 1, CAPACITY, TODAY);

        assertEquals(!allowed, errors.contains(BookingError.NIGHTS_OUT_OF_RANGE),
                "Ночей: " + nights + ", помилки: " + errors);
    }

    // дата заїзду не раніше today

    @ParameterizedTest(name = "BL-10: заїзд today + {0} дн. → дозволено")
    @CsvSource({"0", "1"})
    @DisplayName("BL-10: заїзд сьогодні або пізніше не дає CHECK_IN_IN_PAST")
    void checkInTodayOrLaterIsAccepted(int checkInOffset) {
        LocalDate checkIn = TODAY.plusDays(checkInOffset);

        List<BookingError> errors = validator.validate(checkIn, checkIn.plusDays(2), 1, CAPACITY, TODAY);

        assertFalse(errors.contains(BookingError.CHECK_IN_IN_PAST),
                "Заїзд не раніше сьогодні має бути дозволено, отримано: " + errors);
    }

    // BUG-M02 (Issue #__): рядки «-1, 2» і «-1, 0» падають на v1 — код порівнює з today дату виїзду
    // замість дати заїзду, тому заїзд учора з виїздом сьогодні або пізніше не відхиляється.
    @ParameterizedTest(name = "BL-10: заїзд today + ({0}) дн., виїзд today + ({1}) дн. → CHECK_IN_IN_PAST")
    @CsvSource({
            "-1, 2",    // заїзд учора, виїзд у майбутньому — BUG-M02
            "-1, 0",    // заїзд учора, виїзд сьогодні — BUG-M02
            "-5, -3"    // обидві дати в минулому
    })
    @DisplayName("BL-10: заїзд раніше сьогоднішньої дати відхиляється незалежно від дати виїзду")
    void checkInBeforeTodayIsRejected(int checkInOffset, int checkOutOffset) {
        LocalDate checkIn = TODAY.plusDays(checkInOffset);
        LocalDate checkOut = TODAY.plusDays(checkOutOffset);

        List<BookingError> errors = validator.validate(checkIn, checkOut, 1, CAPACITY, TODAY);

        assertTrue(errors.contains(BookingError.CHECK_IN_IN_PAST),
                "Очікувалась помилка CHECK_IN_IN_PAST, отримано: " + errors);
    }

    // гостей 1..місткість

    @ParameterizedTest(name = "BL-10: {0} гост. при місткості {1} → допустимо: {2}")
    @CsvSource({
            "0, 2, false",  // за нижньою межею
            "1, 2, true",   // нижня межа
            "2, 2, true",   // верхня межа = місткість
            "3, 2, false",  // за верхньою межею
            "4, 4, true",   // інша місткість: верхня межа
            "5, 4, false"   // інша місткість: за межею
    })
    @DisplayName("BL-10: кількість гостей — від 1 до місткості номера")
    void guestsAreAllowedFromOneToCapacity(int guests, int capacity, boolean allowed) {
        List<BookingError> errors =
                validator.validate(CHECK_IN, CHECK_IN.plusDays(2), guests, capacity, TODAY);

        assertEquals(!allowed, errors.contains(BookingError.GUESTS_OUT_OF_RANGE),
                "Гостей: " + guests + ", місткість: " + capacity + ", помилки: " + errors);
    }

    // кілька порушень одночасно

    @Test
    @DisplayName("BL-10: заїзд у минулому і 0 гостей → обидві помилки в списку, зайвих немає")
    void severalViolationsAreAllReported() {
        LocalDate checkIn = TODAY.minusDays(3);

        List<BookingError> errors = validator.validate(checkIn, checkIn.plusDays(2), 0, CAPACITY, TODAY);

        assertAll("помилки валідації",
                () -> assertTrue(errors.contains(BookingError.CHECK_IN_IN_PAST), "немає CHECK_IN_IN_PAST: " + errors),
                () -> assertTrue(errors.contains(BookingError.GUESTS_OUT_OF_RANGE), "немає GUESTS_OUT_OF_RANGE: " + errors),
                () -> assertEquals(2, errors.size(), "зайві помилки: " + errors));
    }
}
