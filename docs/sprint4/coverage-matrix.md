# Матриця покриття — Спринт 4 («СтейНау», модуль staynow v1)

Автор: Бєлянцева А. (ТА, задача 3.1) · Рецензент матриці: Вігура С. · Фінальні позначки рецензентів тестів — Шкарупа М. (задача 8.5).
Джерело очікуваних значень — розд. 5 SRS_04 «СтейНау» (BL-10…BL-70), продубльований у `README.md` модуля. Стан на 25.11.2026.

Для кожного правила — нормальний випадок, обидва боки кожної межі і заборонений вхід (методичка, §5.1, п. 3).
Позначка «червоні на v1» — кількість виконань, що падають через закладений дефект; такі тести не вимикаються і не «підганяються» (§4.9).

## 1. Юніт-тести: BL-вимога → правило → тести

| BL | Правило | Межі / класи значень | Тест-клас#метод | Виконань (червоних на v1) | Дефект | Автор | Рецензент | Позначка рецензента |
|---|---|---|---|---|---|---|---|---|
| BL-10 | Допустиме бронювання не має помилок | 2 ночі, 2 гості з 2, заїзд за тиждень | `BookingValidatorTest#validBookingHasNoErrors` | 1 (0) | — | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Виїзд пізніший за заїзд | виїзд = заїзд; на 1 і 10 днів раніше | `BookingValidatorTest#checkOutNotAfterCheckInIsRejected` | 3 (0) | — | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Ночей 1..30 | 1, 2 · 29, **30**, 31 · 45 | `BookingValidatorTest#nightsAreAllowedFromOneToThirty` | 6 (1) | BUG-M01 | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Заїзд не раніше today | today, today+1 | `BookingValidatorTest#checkInTodayOrLaterIsAccepted` | 2 (0) | — | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Заїзд не раніше today | today−1 із виїздом today+2 і today; обидві дати в минулому | `BookingValidatorTest#checkInBeforeTodayIsRejected` | 3 (2) | BUG-M02 | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Гостей 1..місткість | 0, 1, 2, 3 при місткості 2; 4, 5 при місткості 4 | `BookingValidatorTest#guestsAreAllowedFromOneToCapacity` | 6 (0) | — | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-10 | Усі порушення повертаються разом | заїзд у минулому + 0 гостей (`assertAll`) | `BookingValidatorTest#severalViolationsAreAllReported` | 1 (0) | — | Бєлянцева А. | Шкарупа М. | approved (задача 8.5) |
| BL-20 | Email «локальна_частина@домен.зона»; телефон «+380» і рівно 9 цифр; коди `INVALID_EMAIL`, `INVALID_PHONE` у `validate(…, email, phone)` | телефон 8 / 9 / 10 цифр після +380, без +380, код +381, з літерою; email без @, без локальної частини, без домену, без зони, з двома @, з пробілом, порожній; null | `BookingContactsTest#validEmailIsAccepted`, `#invalidEmailIsRejected`, `#phoneFormatAtBoundaries`, `#nullContactsAreRejected`, `#validateAddsContactErrors` | 18 (0) | — | Гетьман Є. | Шкарупа М. | approved 22.11 |
| BL-30 | Вихідні ночі — з пт на сб і з сб на нд | пн…нд тижня 14–20.09.2026 (межі чт/пт і сб/нд) | `PriceCalculatorSeasonTest#weekendNightsAreFridayAndSaturday` | 7 (2) | BUG-M03 | Вігура С. | Шкарупа М. | схвалено (задача 8.2) |
| BL-30 | Надбавка 15 % за вихідну ніч | чт 100.00, пт 115.00, сб 115.00, нд 100.00 | `PriceCalculatorSeasonTest#weekendSurchargeAppliedOnFridayAndSaturdayOnly` | 4 (2) | BUG-M03 | Вігура С. | Шкарупа М. | схвалено (задача 8.2) |
| BL-40 | Високий сезон ×1,30; низький ×0,80; інший ×1,00 | 31.05/01.06, 31.08/01.09, 19/20.12, 10/11.01, 31.01/01.02, 28.02, 29.02.2028, 31.03/01.04, 31.10/01.11, 30.11/01.12 | `PriceCalculatorSeasonTest#seasonFactorAtBoundaries` | 22 (0) | — | Вігура С. | Шкарупа М. | схвалено (задача 8.2) |
| BL-30 + BL-40 | Сезон і вихідна надбавка перемножуються | пт/сб/нд у високому та низькому сезоні | `PriceCalculatorSeasonTest#seasonAndWeekendCombine` | 6 (2) | BUG-M03 | Вігура С. | Шкарупа М. | схвалено (задача 8.2) |
| BL-40 + BL-50 | Підсумок через межу сезону | 31.08 + 01.09 (будні); 31.05 (нд) + 01.06 | `PriceCalculatorSeasonTest#totalAcrossSeasonBoundaryAugustToSeptember`, `#totalAcrossSeasonBoundaryMayToJune` | 2 (1) | BUG-M03 | Вігура С. | Шкарупа М. | схвалено (задача 8.2) |
| BL-50 | Вартість ночі, округлення `HALF_UP` до 2 знаків | 8.70 × 1,15 = 10.005 → 10.01 (сб, звичайний сезон) | `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` | 1 (1) | BUG-M04 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Вартість ночі = тариф × сезонний коефіцієнт, шкала 2 знаки | будень ×1,00 / ×1,30 / ×0,80; тариф 0.01 і 999999.99 | `PriceCalculatorTotalTest#nightPriceAppliesSeasonFactorWithTwoDecimals` | 5 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Вихідна ніч × 1,15, шкала 2 знаки | сб: 100 → 115.00 | `PriceCalculatorTotalTest#nightPriceWeekendSurchargeHasScaleTwo` | 1 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Знижка 10 % від 7 ночей | 6 ночей → 630.00 (без знижки) · 7 ночей → 657.00 | `PriceCalculatorTotalTest#totalAppliesLongStayDiscountFromSevenNights` | 2 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Знижка округлюється `HALF_UP` до 2 знаків | 219.105 → 219.11; 422.775 → 422.78 | `PriceCalculatorTotalTest#totalDiscountRoundsHalfUp` | 2 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Підсумок = сума ночей, шкала 2 знаки | 2 будні ночі → 200.00, `scale() == 2` | `PriceCalculatorTotalTest#totalAlwaysHasScaleTwo` | 1 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Заборонений вхід BL-50 (контракт `@throws IllegalArgumentException` у Javadoc `PriceCalculator`; у тексті BL-50 винятків немає): тариф ≤ 0 у `nightPrice` | −1, 0, 0.00 → `IllegalArgumentException` | `PriceCalculatorTotalTest#nightPriceRejectsNonPositiveBase` | 3 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Заборонений вхід BL-50 (контракт `@throws IllegalArgumentException` у Javadoc `PriceCalculator`; у тексті BL-50 винятків немає): тариф `null` у `nightPrice` | `null` → `IllegalArgumentException` | `PriceCalculatorTotalTest#nightPriceRejectsNullBase` | 1 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Заборонений вхід BL-50 (контракт `@throws IllegalArgumentException` у Javadoc `PriceCalculator`; у тексті BL-50 винятків немає): тариф ≤ 0 у `total` | −1, 0 → `IllegalArgumentException` | `PriceCalculatorTotalTest#totalRejectsNonPositiveBase` | 2 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-50 | Заборонений вхід BL-50 (контракт `@throws IllegalArgumentException` у Javadoc `PriceCalculator`; у тексті BL-50 винятків немає): виїзд не пізніший за заїзд у `total` | виїзд = заїзд (0 ночей); виїзд раніше заїзду (`assertAll`) | `PriceCalculatorTotalTest#totalRejectsCheckOutNotAfterCheckIn` | 1 (0) | — | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| BL-60 | Плата за скасування: ≥7 днів — 0; 3..6 — 50 %; <3 — 100 % | 30, 8, **7** · 6, 3 · 2, 0 (вартість 1000.00) | `AvailabilityTest#cancellationFeeAtBoundaries` | 7 (1) | BUG-M06 | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-60 | Повернення = вартість − плата | межі 7 / 3 днів | `AvailabilityTest#refundAtBoundaries`, `#freeCancellationRefundsEverything` | (1) | BUG-M06 | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Номер вільний, якщо інтервали не перетинаються; виїзд у день заїзду іншого гостя дозволено | 15–17.09 і 17–19.09 («спина до спини») з обох боків; перетин 16–18.09 | `AvailabilityTest#checkInOnPreviousGuestCheckOutDayIsAllowed`, `#checkOutOnNextGuestCheckInDayIsAllowed`, `#overlappingBookingIsRejected`, `#bookBackToBackRegistersSecondStay` | (2 Failure + 1 Error) | BUG-M05 | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Перетин інтервалів — номер зайнятий | часткове перекриття зліва 13–16.09 при існуючому 15–17.09 | `AvailabilityTest#partialOverlapFromLeftIsNotAvailable` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Перетин інтервалів — номер зайнятий | ідентичний інтервал 15–17.09 | `AvailabilityTest#identicalIntervalIsNotAvailable` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Перетин інтервалів — номер зайнятий | новий інтервал усередині існуючого (15–16.09 у 15–17.09) | `AvailabilityTest#intervalInsideExistingStayIsNotAvailable` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Перетин інтервалів — номер зайнятий | новий інтервал охоплює існуючий | `AvailabilityTest#intervalCoveringExistingStayIsNotAvailable` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | Зайнятість рахується для конкретного номера | ті самі дати, інший номер — вільний | `AvailabilityTest#otherRoomIsAvailable` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| BL-70 | `book` реєструє проживання | бронювання вільного номера з'являється в `stays()` | `AvailabilityTest#bookRegistersStay` | (0) | — | Шкарупа М. | Вігура С. | approved з зауваженнями (задача 8.4) |
| — (контракт методу) | Від'ємна вартість, виїзд не пізніший за заїзд → `IllegalArgumentException` | — | `AvailabilityTest#negativeTotalThrows`, `#invalidIntervalThrows` | (0) | — | Шкарупа М. | Вігура С. | позначити «за Javadoc, не BL» (зауваження 1 review 8.4) |

BL-10 не визначає винятків: заборонений вхід повертає коди `BookingError`, тож `BookingValidatorTest` перевіряє коди, а не `assertThrows`.

## 2. UI-тести: тест-метод → тест-кейс спринта 3 → вимога

Деталі (кроки, локатори, розбіжності) — `docs/sprint4/ui-scenarios.md`.

| Тест-метод | TC-ID | Вимога | Автор | Рецензент | Позначка рецензента |
|---|---|---|---|---|---|
| `StayNowHomePageTest#homePageShowsHotelNameBookNowAndMenu` | TC-001 | F-10 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#menuAnchorsAndAdminOpenLoginPage` | TC-002 | F-10 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#ourRoomsSectionShowsRoomCards` | TC-003 | F-20 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#checkAvailabilityUpdatesRoomLinks` | TC-004 | F-30 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#contactFormWithValidDataShowsThanks` | TC-020 | F-70, GUI-85 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#shortSubjectIsRejected` | TC-021 | F-70 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowHomePageTest#shortMessageIsRejected` | TC-022 | F-70 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowAdminAuthTest#adminLoginOpensDashboard` | TC-023 | F-80 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowAdminAuthTest#wrongPasswordShowsErrorAndKeepsLoginForm` | TC-024 | F-80, GUI-70 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowAdminAuthTest#logoutTerminatesSession` | TC-025 | F-85, S-10 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| `StayNowAdminAuthTest#adminLoginPageHeaderShowsFrontPageAndLogout` | TC-069 | GUI-70 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| Selenium IDE `staynow.side`: «Головна сторінка» | TC-001, TC-003 | F-10, F-20 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |
| Selenium IDE `staynow.side`: «Вхід адміністратора» | TC-023, TC-024 | F-80 | Чаплюк Д. | Шкарупа М. | approved (задача 8.5) |

## 3. Дефекти модуля → тести

| ID | Клас.метод модуля | BL | Тест, що фіксує | Червоних виконань на v1 | Звіт | Issue |
|---|---|---|---|---|---|---|
| BUG-M01 | `BookingValidator.validate` — рівно 30 ночей відхиляється | BL-10 | `BookingValidatorTest#nightsAreAllowedFromOneToThirty` [30] | 1 | `bug-reports/BUG-M01.md` | #7 |
| BUG-M02 | `BookingValidator.validate` — перевіряється checkOut замість checkIn | BL-10 | `BookingValidatorTest#checkInBeforeTodayIsRejected` [-1, 2], [-1, 0] | 2 | `bug-reports/BUG-M02.md` | #8 |
| BUG-M03 | `PriceCalculator.isWeekendNight` — сб/нд замість пт/сб | BL-30 | `PriceCalculatorSeasonTest` (4 методи) | 9 | `bug-reports/BUG-M03.md` | #4 |
| BUG-M04 | `PriceCalculator.nightPrice` — `DOWN` замість `HALF_UP` | BL-50 | `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` | 1 | `bug-reports/BUG-M04.md` | #6 |
| BUG-M05 | `Availability.isAvailable` — рівність дат вважається перетином | BL-70 | `AvailabilityTest` (3 виконання: 2 Failure + 1 Error) | 3 | `bug-reports/BUG-M05.md` | #2 |
| BUG-M06 | `Availability.cancellationFee` — `> 7` замість `>= 7` | BL-60 | `AvailabilityTest#cancellationFeeAtBoundaries` [7], `#refundAtBoundaries` [2] | 2 | `bug-reports/BUG-M06.md` | #3 |

Знайдено тестами **6 із 6** закладених дефектів (норматив для 5 осіб — ≥ 5).

## 4. Норматив (план / факт)

| Норматив (5 осіб) | План | Факт | Примітка |
|---|---|---|---|
| Юніт-тестів (параметризований метод = 1 тест) | ≥ 20 | 43: AvailabilityTest 15, PriceCalculatorTotalTest 10, BookingValidatorTest 7, PriceCalculatorSeasonTest 6, BookingContactsTest 5 | ✅ |
| Юніт-тестів на особу | ≥ 3 | Шкарупа 15, Чаплюк 10, Бєлянцева 7, Вігура 6, Гетьман 5 | ✅; перевіряється за комітами |
| UI-сценаріїв WebDriver | ≥ 8 | 11 | TC-001…004, TC-020…025, TC-069 |
| Дефектів модуля (звіт + тест) | ≥ 5 | 6 тестами; Issues: 6 з 6 (M01 #7, M02 #8, M03 #4, M04 #6, M05 #2, M06 #3) | ✅ |
| Сценаріїв Selenium IDE | 1–2 | 2 | `selenium-ide/staynow.side` |

**Підсумок повного прогону** (фінальний прогін аудитора 25.11.2026, 23:28–23:32, чиста папка): `mvn clean test` → **`Tests run: 130, Failures: 17, Errors: 1`**; `mvn test -DexcludedGroups=none` двічі → `Tests run: 141, Failures: 17, Errors: 1`, список падінь однаковий, 11 UI-тестів зелені. Збігається з розрахунком: проміжний прогін (67 / 5 / 1) + `PriceCalculatorSeasonTest` (41 / 9) + `BookingValidatorTest` (22 / 3); 18 падінь = 1 + 2 + 9 + 1 + 3 + 2 за BUG-M01…M06. Протокол — `docs/sprint4/surefire-summary.md`.

## 5. Позначки рецензента матриці

| Роль | Хто | Дата | Позначка |
|---|---|---|---|
| Автор (ТА) | Бєлянцева А. | 26.11.2026 | матриця звірена з фінальним прогоном аудитора і позначками 8.5 |
| Рецензент матриці (задача 3.1) | Вігура С. | | |
