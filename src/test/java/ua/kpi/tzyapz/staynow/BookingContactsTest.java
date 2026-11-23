package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Юніт-тести контактних даних гостя (BL-20): isValidEmail, isValidPhone, validate(..., email, phone).
 * Автор: Гетьман Є. Очікувані значення взято з вимоги BL-20 SRS «СтейНау», розд. 5.
 */
@Tag("unit")
@DisplayName("BookingValidator — контактні дані гостя (BL-20)")
class BookingContactsTest {

    private BookingValidator validator;

    @BeforeEach
    void setUp() {
        validator = new BookingValidator();
    }

    @ParameterizedTest(name = "BL-20: email {0} коректний")
    @ValueSource(strings = {"guest@example.com", "ivan.petrenko@kpi.ua", "test@mail.co.uk"})
    @DisplayName("BL-20: email у форматі локальна_частина@домен.зона приймається")
    void validEmailIsAccepted(String email) {
        assertTrue(validator.isValidEmail(email), "Email має бути коректним: " + email);
    }

    @ParameterizedTest(name = "BL-20: email \"{0}\" некоректний")
    @ValueSource(strings = {"guestexample.com", "@example.com", "guest@", "guest@example",
            "guest@@example.com", "guest @example.com", ""})
    @DisplayName("BL-20: email без однієї з частин формату відхиляється")
    void invalidEmailIsRejected(String email) {
        assertFalse(validator.isValidEmail(email), "Email має бути некоректним: " + email);
    }

    @ParameterizedTest(name = "BL-20: телефон {0} → {1}")
    @CsvSource({
            "+38050123456, false",
            "+380501234567, true",
            "+3805012345678, false",
            "0501234567, false",
            "+381501234567, false",
            "+38050123456a, false"
    })
    @DisplayName("BL-20: телефон — +380 і рівно 9 цифр (межі 8/9/10 цифр)")
    void phoneFormatAtBoundaries(String phone, boolean expected) {
        assertEquals(expected, validator.isValidPhone(phone));
    }

    @Test
    @DisplayName("BL-20: null замість email або телефону — некоректне значення")
    void nullContactsAreRejected() {
        assertAll("null-значення",
                () -> assertFalse(validator.isValidEmail(null)),
                () -> assertFalse(validator.isValidPhone(null))
        );
    }

    @Test
    @DisplayName("BL-20: validate з некоректними email і телефоном додає INVALID_EMAIL та INVALID_PHONE")
    void validateAddsContactErrors() {
        List<BookingError> errors = validator.validate(LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17),
                2, 2, LocalDate.of(2026, 9, 8), "guestexample.com", "0501234567");

        assertAll("помилки контактів",
                () -> assertEquals(2, errors.size(), "Отримано: " + errors),
                () -> assertTrue(errors.contains(BookingError.INVALID_EMAIL)),
                () -> assertTrue(errors.contains(BookingError.INVALID_PHONE))
        );
    }
}
