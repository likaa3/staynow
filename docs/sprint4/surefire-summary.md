# Протокол запуску тестів — Спринт 4 («СтейНау», модуль v1)

Веде: Вігура С. (КТ), задача 10.2. Єдине достовірне джерело підсумкових цифр для README,
досьє v4 і демо — цей файл, а не хардкод у README.md.

## Як читати цей протокол

**Очікуваний результат `mvn clean test` — `BUILD FAILURE`, і це правильно**, доки дефекти не
виправлено у v2. У модулі v1 навмисно закладено 6 дефектів (задача 5); відповідні «червоні»
тести команда свідомо не вимикає й не підганяє.

**Failures проти Errors.** `AvailabilityTest.bookBackToBackRegistersSecondStay` падає не через
`assert`, а тому, що `book()` кидає `IllegalStateException` там, де мало пройти успішне
бронювання «спина до спини» — той самий дефект BUG-M05, але JUnit рахує це як `Error`, а не
`Failure`. Писати в звітах і README точно «Failures: N, Errors: 1», не підсумовувати в одне число.

## Команда запуску

```
mvn clean test                    # юніт-тести (тег ui виключено pom-ом)
mvn test -DexcludedGroups=none    # юніт + UI, два прогони поспіль — перевірка стабільності
```

## Фінальний прогін — ПІДТВЕРДЖЕНО

**Виконавець:** Гетьман Є. (аудитор-опонент) · **Дата/час:** 25.11.2026, 23:28–23:32 ·
**Умови:** чиста папка (клон `main` «з нуля»), відповідає протоколу аудиту задачі 9.1.

| Команда | Tests run | Failures | Errors | Результат |
|---|---|---|---|---|
| `mvn clean test` | **130** | **17** | **1** | `BUILD FAILURE` (очікувано) |
| `mvn test -DexcludedGroups=none`, прогін 1 | **141** | 17 | 1 | `BUILD FAILURE` (очікувано) |
| `mvn test -DexcludedGroups=none`, прогін 2 | **141** | 17 | 1 | `BUILD FAILURE` (очікувано) |

Два повні прогони (юніт + UI) дали ідентичний список падінь — **flaky-тестів немає**. Усі 11
UI-сценаріїв зелені в обох прогонах (141 − 130 = 11, збігається з нормативом ≥8 UI-сценаріїв).

Реальний підсумок **збігся з попередньою розрахунковою оцінкою** (130/17/1), зібраною з трьох
окремих прогонів до заливки всього дерева — тож розбір по дефектах нижче лишається чинним без
правок.

### Розбір Failures/Errors за дефектами (17 Failures + 1 Error)

| Дефект | Клас.метод(и) | Failures | Errors | Issue |
|---|---|---|---|---|
| BUG-M01 | `BookingValidatorTest.nightsAreAllowedFromOneToThirty[30,true]` | 1 | 0 | #7 |
| BUG-M02 | `BookingValidatorTest.checkInBeforeTodayIsRejected[-1,2]`, `[-1,0]` | 2 | 0 | #8 |
| BUG-M03 | `PriceCalculatorSeasonTest` — 9 параметризованих виконань (пт/нд у 4 методах) | 9 | 0 | #4 |
| BUG-M04 | `PriceCalculatorTotalTest.nightPriceRoundsHalfUp` | 1 | 0 | #6 |
| BUG-M05 | `AvailabilityTest.checkInOnPreviousGuestCheckOutDayIsAllowed`, `#checkOutOnNextGuestCheckInDayIsAllowed`, `#bookBackToBackRegistersSecondStay` (Error) | 2 | 1 | #2 |
| BUG-M06 | `AvailabilityTest.cancellationFeeAtBoundaries[7,0.00]`, `#refundAtBoundaries[...]` | 2 | 0 | #3 |
| **Разом** | | **17** | **1** | |

Жодного неочікуваного падіння — усі 18 проблемних виконань прив'язані до одного з 6
зареєстрованих дефектів модуля v1.

## Норматив — факт

- Юніт-тестів: 130 (≥20 — виконано з запасом).
- UI-сценаріїв: 11 (≥8 — виконано).
- Дефектів модуля, зафіксованих тестами: 6 із 6 (≥5 — виконано).
- Усі Issues створено: BUG-M01 #7, BUG-M02 #8, BUG-M03 #4, BUG-M04 #6, BUG-M05 #2, BUG-M06 #3.

## Вкладення

- `docs/sprint4/surefire-console-final.txt` — повний вивід `mvn clean test` (Гетьман Є., 25.11.2026).
- Звіти Surefire по кожному класу → релевантні копіюються в `docs/sprint4/bug-reports/attachments/`.
- UI-стабільність (два прогони `-DexcludedGroups=none`) детально — `docs/sprint4/ui-stability-runs.md` (Чаплюк Д.).
