# Протокол запуску тестів — Спринт 4 («СтейНау», модуль v1)

Веде: Вігура С. (КТ), задача 10.2. Єдине достовірне джерело підсумкових цифр для README,
досьє v4 і демо — цей файл, а не хардкод у README.md.

## Термінологія: виконання ≠ тестові методи

**«Tests run» у виводі Maven Surefire — це кількість ВИКОНАНЬ, не кількість тестових методів.**
Один параметризований метод (`@ParameterizedTest`) з 7 рядками `@CsvSource` дає 7 виконань, але
лишається одним тестовим методом. Норматив «≥20 юніт-тестів» (§5.2 методички) рахує тестові
методи команди — їх **43** (після видалення дубліката, розд. «Зміни 26.11.2026» нижче), а не 129/130.
Разом із зразком `ExampleTest` у прогоні всього **45 методів**.
Не плутати ці два числа в README, досьє v4 і презентаціях.

| Клас | Тестових методів | Виконань |
|---|---|---|
| `AvailabilityTest` (Шкарупа М.) | 15 | 27 |
| `PriceCalculatorSeasonTest` (Вігура С.) | 6 | 41 |
| `BookingValidatorTest` (Бєлянцева А.) | 7 | 22 |
| `PriceCalculatorTotalTest` (Чаплюк Д.) | **10** (було 11) | **19** (було 20) |
| `BookingContactsTest` (Гетьман Є.) | 5 | 18 |
| **Разом, юніт-тести команди** | **43** (було 44) | **127** (було 128) |
| `ExampleTest` (зразок модуля, у норматив не входить) | 2 | 2 |
| **Разом у прогоні** | **45** (було 46) | **129** (було 130) |

## Зміни 26.11.2026 — видалено дублікат тесту

`PriceCalculatorTotalTest#totalSumsNightPricesAcrossSeasonBoundary` (Чаплюк Д.) і
`PriceCalculatorSeasonTest#totalAcrossSeasonBoundaryAugustToSeptember` (Вігура С.) перевіряли
один і той самий сценарій (31.08 → 01.09, сума 230.00). Рішення КТ: залишено тест у
`PriceCalculatorSeasonTest` — межа сезону тематично належить до BL-30/BL-40, а
`PriceCalculatorTotalTest` присвячений BL-50 (округлення й знижки). Дубль у
`PriceCalculatorTotalTest` видалено. Видалений тест був зеленим (не впливає на Failures/Errors).

## Як читати цей протокол

**Очікуваний результат `mvn clean test` — `BUILD FAILURE`, і це правильно**, доки дефекти не
виправлено у v2. У модулі v1 навмисно закладено 6 дефектів (задача 5); відповідні «червоні»
тести команда свідомо не вимикає й не підганяє.

**Failures проти Errors.** `AvailabilityTest.bookBackToBackRegistersSecondStay` падає не через
`assert`, а тому, що `book()` кидає `IllegalStateException` там, де мало пройти успішне
бронювання «спина до спини» — той самий дефект BUG-M05, але JUnit рахує це як `Error`, а не
`Failure`.

## Команда запуску

```
mvn clean test                    # юніт-тести (тег ui виключено pom-ом)
mvn test -DexcludedGroups=none    # юніт + UI, два прогони поспіль — перевірка стабільності
```

## Фінальні цифри (після видалення дубліката)

Прогін аудитора (Гетьман Є.), 27.11.2026.

| Команда | Tests run | Failures | Errors | Результат |
|---|---|---|---|---|
| `mvn clean test` | **129** | **17** | **1** | `BUILD FAILURE` (очікувано) |
| `mvn test -DexcludedGroups=none` | **140** | **18** | **1** | `BUILD FAILURE` (очікувано) |

140 − 129 = 11 UI-виконань: **10 зелені, 1 червоний** (`TC-025`). Різниця між рядками — рівно цей один Failure.

### Розбір юніт-прогону: 17 Failures + 1 Error — дефекти модуля v1

| Дефект | Клас.метод(и) | Failures | Errors | Issue |
|---|---|---|---|---|
| BUG-M01 | `BookingValidatorTest.nightsAreAllowedFromOneToThirty[30,true]` | 1 | 0 | #7 |
| BUG-M02 | `BookingValidatorTest.checkInBeforeTodayIsRejected[-1,2]`, `[-1,0]` | 2 | 0 | #8 |
| BUG-M03 | `PriceCalculatorSeasonTest` — 9 виконань (2 у weekendNightsAreFridayAndSaturday, 2 у weekendSurchargeAppliedOnFridayAndSaturdayOnly, 4 у seasonAndWeekendCombine, 1 у totalAcrossSeasonBoundaryMayToJune) | 9 | 0 | #4 |
| BUG-M04 | `PriceCalculatorTotalTest.nightPriceRoundsHalfUp` | 1 | 0 | #6 |
| BUG-M05 | `AvailabilityTest.checkInOnPreviousGuestCheckOutDayIsAllowed`, `#checkOutOnNextGuestCheckInDayIsAllowed`, `#bookBackToBackRegistersSecondStay` (Error) | 2 | 1 | #2 |
| BUG-M06 | `AvailabilityTest.cancellationFeeAtBoundaries[7,0.00]`, `#refundAtBoundaries[...]` | 2 | 0 | #3 |
| **Разом** | | **17** | **1** | |

### 18-й Failure — лише в прогоні з UI

| Клас.метод | Failures | Errors | Issue |
|---|---|---|---|
| `StayNowAdminAuthTest.logoutReturnsToLoginPage` (TC-025) | 1 | 0 | #12 |

Це **не дефект модуля v1**: тест працює з публічним demo, а не з кодом `staynow`. Він
не входить у 6 закладених дефектів (BUG-M01…M06), тому підсумок «6 із 6» не змінюється.
Деталі падіння — в Issue #12; відомі розбіжності demo з кейсом TC-025 описано в розд. 3, п. 5
`docs/sprint4/ui-scenarios.md`. Прогін 25.11 (141 тест, 17 Failures, усі UI зелені) і
прогін 140/18/1 відрізняються саме цим тестом, тож файли `ui-stability-runs.md` і
`dossier-v4-automation.md`, де написано «11 UI зелені», слід оновити їхнім авторам.

## Норматив — факт

- Юніт-тестових методів: 43 (≥20 — виконано з запасом; 129 виконань).
- UI-сценаріїв: 11 (≥8 — виконано); у прогоні 140/18/1 зелені 10, червоний — TC-025 (Issue #12).
- Дефектів модуля, зафіксованих тестами: 6 із 6 (≥5 — виконано).
- Усі Issues створено: BUG-M01 #7, BUG-M02 #8, BUG-M03 #4, BUG-M04 #6, BUG-M05 #2, BUG-M06 #3; поза модулем — demo #10, TC-025 #12.

## Вкладення

- `docs/sprint4/surefire-console-final.txt` — повний вивід `mvn clean test`.
- Звіти Surefire по кожному класу → релевантні копіюються в `docs/sprint4/bug-reports/attachments/`.
- UI-стабільність — `docs/sprint4/ui-stability-runs.md` (Чаплюк Д.).
