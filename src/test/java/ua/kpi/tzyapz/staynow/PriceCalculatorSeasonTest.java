package ua.kpi.tzyapz.staynow;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Юніт-тести вимог BL-30 (вихідні ночі) і BL-40 (сезонні коефіцієнти) SRS_04 «СтейНау», розд. 5:
 * <ul>
 *   <li>BL-30: ночі з п'ятниці на суботу та з суботи на неділю — надбавка 15 %
 *       (тобто «вихідні» ночі — ті, що починаються в п'ятницю і суботу);</li>
 *   <li>BL-40: високий сезон (1 червня – 31 серпня, 20 грудня – 10 січня) ×1,30;
 *       низький (лютий, березень, листопад) ×0,80; інший період ×1,00.</li>
 * </ul>
 * Очікувані значення виведено з вимог (README модуля, розд. «Реалізовані правила»), а не з коду.
 * Базовий тариф у всіх сумових перевірках — 100: добутки точні, тож округлення (BUG-M04) не впливає.
 * Автор: Вігура С. (КТ). Рецензент: Шкарупа М.
 * Відомий «червоний» тест на v1: BUG-M03 (Issue #4, GitHub Issues репозиторію).
 * На v1 набір дає 9 червоних виконань: по два в методах weekendNightsAreFridayAndSaturday і
 * weekendSurchargeAppliedOnFridayAndSaturdayOnly, чотири в seasonAndWeekendCombine (пт і нд —
 * у високому і в низькому сезоні), один у totalAcrossSeasonBoundaryMayToJune. Значення схвалено
 * рецензентом Шкарупою М. (code review, задача 8.2) — код тесту не змінювати, лише номер Issue
 * в коментарях, якщо він коли-небудь зміниться.
 * Тиждень, на якому побудовано перевірки BL-30: пн 2026-09-14 … нд 2026-09-20 (вересень — звичайний сезон).
 */
@Tag("unit")
@DisplayName("PriceCalculator — вихідні ночі (BL-30) і сезони (BL-40)")
class PriceCalculatorSeasonTest {

    private static final BigDecimal BASE = new BigDecimal("100");

    private PriceCalculator calc;

    @BeforeEach
    void setUp() {
        calc = new PriceCalculator();
    }

    // ---------- BL-30: вихідні ночі ----------

    // BUG-M03 (Issue #4): рядки з пт (2026-09-18) і нд (2026-09-20) падають на v1 —
    // код вважає вихідними суботу й неділю замість п'ятниці й суботи.
    @ParameterizedTest(name = "BL-30: ніч {0} ({1}) → вихідна: {2}")
    @CsvSource({
            "2026-09-14, пн, false",
            "2026-09-15, вт, false",
            "2026-09-16, ср, false",
            "2026-09-17, чт, false",
            "2026-09-18, пт, true",
            "2026-09-19, сб, true",
            "2026-09-20, нд, false"
    })
    @DisplayName("BL-30: isWeekendNight — лише ночі з пт на сб і з сб на нд (усі дні тижня)")
    void weekendNightsAreFridayAndSaturday(LocalDate night, String dayName, boolean expected) {
        assertEquals(expected, calc.isWeekendNight(night));
    }

    // BUG-M03 (Issue #4): рядки "пт 115.00" і "нд 100.00" падають на v1.
    @ParameterizedTest(name = "BL-30: тариф 100, ніч {0} ({1}) → {2}")
    @CsvSource({
            "2026-09-17, чт, 100.00",
            "2026-09-18, пт, 115.00",
            "2026-09-19, сб, 115.00",
            "2026-09-20, нд, 100.00"
    })
    @DisplayName("BL-30: nightPrice — надбавка 15 % у пт і сб, без надбавки в чт і нд (звичайний сезон)")
    void weekendSurchargeAppliedOnFridayAndSaturdayOnly(LocalDate night, String dayName, String expected) {
        assertEquals(new BigDecimal(expected), calc.nightPrice(BASE, night));
    }

    // ---------- BL-40: сезонний коефіцієнт на межах ----------

    @ParameterizedTest(name = "BL-40: {0} → коефіцієнт {1}")
    @CsvSource({
            // літній високий сезон: 1.06 – 31.08
            "2026-05-31, 1.00", "2026-06-01, 1.30", "2026-07-15, 1.30", "2026-08-31, 1.30", "2026-09-01, 1.00",
            // зимовий високий сезон: 20.12 – 10.01 (перехід через новий рік)
            "2026-12-19, 1.00", "2026-12-20, 1.30", "2026-12-31, 1.30", "2027-01-01, 1.30",
            "2027-01-10, 1.30", "2027-01-11, 1.00",
            // низький сезон: лютий, березень, листопад
            "2027-01-31, 1.00", "2027-02-01, 0.80", "2027-02-28, 0.80", "2028-02-29, 0.80",
            "2027-03-01, 0.80", "2027-03-31, 0.80", "2027-04-01, 1.00",
            "2026-10-31, 1.00", "2026-11-01, 0.80", "2026-11-30, 0.80", "2026-12-01, 1.00"
    })
    @DisplayName("BL-40: seasonFactor — межі високого (×1,30), низького (×0,80) і звичайного (×1,00) сезонів")
    void seasonFactorAtBoundaries(LocalDate night, String expected) {
        assertEquals(new BigDecimal(expected), calc.seasonFactor(night));
    }

    // ---------- BL-30 + BL-40: поєднання ----------

    // BUG-M03 (Issue #4): 4 червоні рядки — пт і нд у кожному з двох сезонів (пт без надбавки,
    // нд з надбавкою, бо v1 вважає вихідними сб/нд замість пт/сб).
    @ParameterizedTest(name = "BL-30+BL-40: тариф 100, ніч {0} ({1}) → {2}")
    @CsvSource({
            "2026-07-17, пт, 149.50",   // високий сезон + пт: 100 × 1,30 × 1,15
            "2026-07-18, сб, 149.50",   // високий сезон + сб
            "2026-07-19, нд, 130.00",   // високий сезон, неділя без надбавки
            "2026-11-13, пт, 92.00",    // низький сезон + пт: 100 × 0,80 × 1,15
            "2026-11-14, сб, 92.00",    // низький сезон + сб
            "2026-11-15, нд, 80.00"     // низький сезон, неділя без надбавки
    })
    @DisplayName("BL-30+BL-40: nightPrice — сезонний коефіцієнт і вихідна надбавка перемножуються")
    void seasonAndWeekendCombine(LocalDate night, String dayName, String expected) {
        assertEquals(new BigDecimal(expected), calc.nightPrice(BASE, night));
    }

    // ---------- BL-40: підсумок через межу сезону (лише будні ночі, щоб не зачіпати BL-30) ----------

    @Test
    @DisplayName("BL-40: total за ночі 31.08 (×1,30) і 01.09 (×1,00) = 130.00 + 100.00")
    void totalAcrossSeasonBoundaryAugustToSeptember() {
        // пн 2026-08-31 і вт 2026-09-01 — обидві ночі буденні
        assertEquals(new BigDecimal("230.00"),
                calc.total(BASE, LocalDate.of(2026, 8, 31), LocalDate.of(2026, 9, 2)));
    }

    @Test
    @DisplayName("BL-40: total за ночі 31.05 (×1,00) і 01.06 (×1,30) — перша ніч нд без надбавки")
    void totalAcrossSeasonBoundaryMayToJune() {
        // нд 2026-05-31 (звичайний сезон, не вихідна ніч) = 100.00; пн 2026-06-01 (високий) = 130.00
        // BUG-M03 (Issue #4): на v1 неділя отримує надбавку → 245.00 замість 230.00
        assertEquals(new BigDecimal("230.00"),
                calc.total(BASE, LocalDate.of(2026, 5, 31), LocalDate.of(2026, 6, 2)));
    }
}
