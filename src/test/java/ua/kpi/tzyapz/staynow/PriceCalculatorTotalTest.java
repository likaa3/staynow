package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Юніт-тести вимоги BL-50 (SRS_04 «СтейНау»):
 * вартість ночі = базовий тариф × сезонний коефіцієнт × (1,15 для вихідної ночі),
 * округлення HALF_UP до 2 знаків; підсумок = сума ночей; за 7 і більше ночей —
 * знижка 10 % від підсумку (округлення HALF_UP до 2 знаків). Усі суми — BigDecimal.
 *
 * Структура кожного тесту: Arrange – Act – Assert; один тест — одна поведінка.
 * Автор: Чаплюк Д. (ІТ / SDET). Простежуваність: TC-059…TC-062 (спринт 3) → BL-50.
 */
@DisplayName("BL-50: PriceCalculator — вартість ночі, підсумок і знижка за тривале перебування")
class PriceCalculatorTotalTest {

    private PriceCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new PriceCalculator();
    }

    // BUG-M04, Issue #1 (мітка module; звіт: docs/bug-reports/BUG-M04.md)
    @Test
    @DisplayName("BL-50: nightPrice — граничне округлення HALF_UP: 8.70 × 1.15 = 10.005 → 10.01 (зафіксував BUG-M04)")
    void nightPriceRoundsHalfUp() {
        // arrange
        BigDecimal base = new BigDecimal("8.70");
        LocalDate saturdayInRegularSeason = LocalDate.of(2026, 4, 11); // субота, звичайний сезон (×1.15)

        // act
        BigDecimal price = calc.nightPrice(base, saturdayInRegularSeason);

        // assert
        assertEquals(new BigDecimal("10.01"), price,
                "BL-50 вимагає округлення HALF_UP: 8.70 × 1.15 = 10.005 → 10.01; "
                        + "реалізація застосовує DOWN і повертає 10.00 (див. docs/bug-reports/BUG-M04.md)");
    }

    @ParameterizedTest(name = "BL-50: nightPrice({0} грн, {1}) = {2} грн")
    @CsvSource({
            "100,        2026-04-15, 100.00",    // будень, звичайний сезон ×1.00
            "100,        2026-07-15, 130.00",    // будень, високий сезон ×1.30
            "100,        2026-02-17, 80.00",     // будень, низький сезон ×0.80
            "0.01,       2026-04-15, 0.01",      // гранично мала сума
            "999999.99,  2026-04-15, 999999.99"  // гранично велика сума
    })
    @DisplayName("BL-50: вартість ночі = тариф × сезонний коефіцієнт; сума завжди зі шкалою 2 знаки")
    void nightPriceAppliesSeasonFactorWithTwoDecimals(BigDecimal base, String night, String expected) {
        // arrange
        LocalDate nightDate = LocalDate.parse(night);

        // act
        BigDecimal price = calc.nightPrice(base, nightDate);

        // assert
        assertEquals(new BigDecimal(expected), price);
    }

    @Test
    @DisplayName("BL-50: вихідна ніч (субота) додає надбавку +15 %: 100 × 1.15 = 115.00")
    void nightPriceWeekendSurchargeHasScaleTwo() {
        // arrange
        BigDecimal base = new BigDecimal("100");
        LocalDate saturday = LocalDate.of(2026, 4, 11); // субота

        // act
        BigDecimal price = calc.nightPrice(base, saturday);

        // assert
        assertEquals(new BigDecimal("115.00"), price); // саме «115.00», а не «115» — шкала 2 знаки
    }

    @Test
    @DisplayName("BL-50: підсумок = сума вартостей ночей; перетин межі сезону 31.08 → 01.09: 130.00 + 100.00 = 230.00")
    void totalSumsNightPricesAcrossSeasonBoundary() {
        // arrange
        BigDecimal base = new BigDecimal("100");

        // act
        BigDecimal total = calc.total(base, LocalDate.of(2026, 8, 31), LocalDate.of(2026, 9, 2));

        // assert
        assertEquals(new BigDecimal("230.00"), total);
    }

    @ParameterizedTest(name = "BL-50: {0} ночей ({1} … {2}) → {3} грн")
    @CsvSource({
            "6, 2026-09-15, 2026-09-21, 630.00",  // межа знизу: 6 ночей — знижки немає
            "7, 2026-09-14, 2026-09-21, 657.00"   // межа знижки: 7 ночей — знижка 10 %
    })
    @DisplayName("BL-50: знижка 10 % від підсумку застосовується від 7 ночей; за 6 ночей — ні")
    void totalAppliesLongStayDiscountFromSevenNights(int nights, String checkIn, String checkOut, String expected) {
        // arrange
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);

        // act
        BigDecimal total = calc.total(new BigDecimal("100"), checkInDate, checkOutDate);

        // assert: пересторога — параметр nights збігається з фактичною кількістю ночей інтервалу
        assertAll(
                () -> assertEquals(nights, ChronoUnit.DAYS.between(checkInDate, checkOutDate),
                        "CSV-рядок відповідає довжині інтервалу [checkIn, checkOut)"),
                () -> assertEquals(new BigDecimal(expected), total)
        );
    }

    @ParameterizedTest(name = "BL-50: знижка 10 % округлюється HALF_UP: 7 ночей × {0} грн → {3} грн")
    @CsvSource({
            // третій знак після множення на 0.90 дорівнює 5 → HALF_UP округлює вгору, DOWN — «з'їв» би копійку
            // 33.35: 5×33.35 + 2×38.35 = 243.45 → ×0.90 = 219.105 → 219.11
            "33.35, 2026-09-14, 2026-09-21, 219.11",
            // 64.35: 5×64.35 + 2×74.00 = 469.75 → ×0.90 = 422.775 → 422.78
            "64.35, 2027-05-03, 2027-05-10, 422.78"
    })
    @DisplayName("BL-50: знижка 10 % округлюється HALF_UP до 2 знаків (захист від регресії у v2)")
    void totalDiscountRoundsHalfUp(String base, String checkIn, String checkOut, String expected) {
        // arrange
        BigDecimal baseRate = new BigDecimal(base);
        LocalDate checkInDate = LocalDate.parse(checkIn);
        LocalDate checkOutDate = LocalDate.parse(checkOut);

        // act
        BigDecimal total = calc.total(baseRate, checkInDate, checkOutDate);

        // assert
        assertEquals(new BigDecimal(expected), total,
                "BL-50: знижка 10 % округлюється HALF_UP; усічення дало б "
                        + expected.substring(0, expected.length() - 1) + "0");
    }

    @ParameterizedTest(name = "BL-50: nightPrice({0} грн, …) → IllegalArgumentException")
    @ValueSource(strings = {"-1", "0", "0.00"})
    @DisplayName("BL-50: непозитивний базовий тариф ночі заборонено (нуль і від'ємні значення)")
    void nightPriceRejectsNonPositiveBase(String base) {
        // arrange
        BigDecimal invalidBase = new BigDecimal(base);

        // act + assert
        assertThrows(IllegalArgumentException.class,
                () -> calc.nightPrice(invalidBase, LocalDate.of(2026, 4, 15)));
    }

    @Test
    @DisplayName("BL-50: nightPrice(null, …) → IllegalArgumentException")
    void nightPriceRejectsNullBase() {
        // arrange + act + assert
        assertThrows(IllegalArgumentException.class,
                () -> calc.nightPrice(null, LocalDate.of(2026, 4, 15)));
    }

    @ParameterizedTest(name = "BL-50: total({0} грн, …) → IllegalArgumentException")
    @ValueSource(strings = {"-1", "0"})
    @DisplayName("BL-50: total не приймає непозитивний базовий тариф")
    void totalRejectsNonPositiveBase(String base) {
        // arrange
        BigDecimal invalidBase = new BigDecimal(base);

        // act + assert
        assertThrows(IllegalArgumentException.class,
                () -> calc.total(invalidBase, LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 21)));
    }

    @Test
    @DisplayName("BL-50: total кидає виняток, якщо виїзд не пізніший за заїзд (рівні дати та виїзд раніше заїзду)")
    void totalRejectsCheckOutNotAfterCheckIn() {
        // arrange
        BigDecimal base = new BigDecimal("100");

        // act + assert
        assertAll(
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.total(base, LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 14)),
                        "виїзд тим самим днем, що й заїзд (0 ночей)"),
                () -> assertThrows(IllegalArgumentException.class,
                        () -> calc.total(base, LocalDate.of(2026, 9, 14), LocalDate.of(2026, 9, 13)),
                        "виїзд раніше заїзду")
        );
    }

    @Test
    @DisplayName("BL-50: підсумок завжди зі шкалою 2 знаки — жодного «цілого» значення (120 ≠ 120.00)")
    void totalAlwaysHasScaleTwo() {
        // arrange + act
        BigDecimal total = calc.total(new BigDecimal("100"),
                LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));

        // assert
        assertAll(
                () -> assertEquals(new BigDecimal("200.00"), total),
                () -> assertTrue(total.scale() == 2, "шкала результату має бути 2, а не " + total.scale())
        );
    }
}
