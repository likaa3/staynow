# Досьє якості v4 — розділ «Автоматизація»

Продукт: «СтейНау» (П4) · модуль staynow v1 (1.0.0) · Спринт 4 «Автоматизація: JUnit 5 і Selenium WebDriver»
Автор розділу: Бєлянцева А. (ТА, задача 10.3) · Рецензент: Вігура С. · Стан на 25.11.2026
Репозиторій: https://github.com/likaa3/staynow · Трекер: GitHub Issues репозиторію, мітка `module` (BUG-M01…M06 — #7, #8, #4, #6, #2, #3)

## 1. Норматив (план / факт)

| Норматив (5 осіб) | План | Факт |
|---|---|---|
| Юніт-тестів JUnit 5 (параметризований метод = 1 тест) | ≥ 20 | 43 (AvailabilityTest 15, PriceCalculatorTotalTest 10, BookingValidatorTest 7, PriceCalculatorSeasonTest 6, BookingContactsTest 5) |
| Юніт-тестів на особу (за комітами) | ≥ 3 | Шкарупа 15, Чаплюк 10, Бєлянцева 7, Вігура 6, Гетьман 5 |
| UI-сценаріїв WebDriver | ≥ 8 | 11 |
| Дефектів модуля (звіт + тест) | ≥ 5 | 6 із 6 зафіксовано тестами; звітів 6; Issues 6 з 6 |
| Сценаріїв Selenium IDE | 1–2 | 2 |

## 2. Матриця покриття BL (стисло)

Повна матриця «BL → правило → межі → тест → рецензент» — `docs/sprint4/coverage-matrix.md`.

| BL | Що перевіряється | Тест-клас | Автор | Дефект |
|---|---|---|---|---|
| BL-10 | виїзд > заїзд; ночей 1..30 (межі 1/30/31); заїзд ≥ today; гостей 1..місткість | `BookingValidatorTest` | Бєлянцева А. | BUG-M01, BUG-M02 |
| BL-20 | email, телефон +380 і 9 цифр | `BookingContactsTest` | Гетьман Є. | — |
| BL-30 | вихідні ночі пт/сб, надбавка 15 % | `PriceCalculatorSeasonTest` | Вігура С. | BUG-M03 |
| BL-40 | сезонні коефіцієнти, межі сезонів | `PriceCalculatorSeasonTest` | Вігура С. | — |
| BL-50 | вартість ночі, `HALF_UP`, підсумок, знижка від 7 ночей; заборонений вхід (тариф ≤ 0, null, виїзд не пізніший за заїзд) | `PriceCalculatorTotalTest` | Чаплюк Д. | BUG-M04 |
| BL-60 | плата за скасування (межі 7/3 дні), повернення | `AvailabilityTest` | Шкарупа М. | BUG-M06 |
| BL-70 | зайнятість, «спина до спини» дозволено | `AvailabilityTest` | Шкарупа М. | BUG-M05 |

## 3. UI-сценарії

11 WebDriver-тестів за кейсами спринта 3: TC-001, TC-002, TC-003, TC-004 (головна сторінка, «Our Rooms», доступність), TC-020, TC-021, TC-022 (контактна форма), TC-023, TC-024, TC-025, TC-069 (вхід і вихід адміністратора). Кроки, локатори, очікування і тест-методи — `docs/sprint4/ui-scenarios.md`.

Відкладено до відновлення demo: TC-005, TC-007…TC-019, TC-035…TC-037, TC-040 — сторінка `/reservation/{id}` не завантажується (`GET /api/room/{id}` → HTTP 500; той самий дефект, що BUG-001 спринта 3).

## 4. Підсумок Surefire

| Прогін | Команда | Результат |
|---|---|---|
| Проміжний, 25.11.2026 (без SeasonTest і BookingValidatorTest) | `mvn clean test` | `Tests run: 67, Failures: 5, Errors: 1, Skipped: 0` — `BUILD FAILURE` (очікувано) |
| `PriceCalculatorSeasonTest` окремо, 25.11.2026 | `mvn test -Dtest=PriceCalculatorSeasonTest` | `Tests run: 41, Failures: 9, Errors: 0` (BUG-M03) |
| Фінальний прогін аудитора, 25.11.2026, 23:28–23:32, чиста папка | `mvn clean test` | `Tests run: 130, Failures: 17, Errors: 1` — `BUILD FAILURE` (очікувано; `docs/sprint4/surefire-summary.md`) |
| Юніт- + UI-набір, два прогони поспіль, 25.11.2026 | `mvn test -DexcludedGroups=none` | обидва рази `Tests run: 141, Failures: 17, Errors: 1`; список падінь однаковий; 11 UI-тестів зелені, flaky немає (`docs/sprint4/ui-stability-runs.md`) |

`BUILD FAILURE` очікуваний: усі 18 падінь (17 Failures + 1 Error) — зареєстровані дефекти модуля v1 BUG-M01…M06, неочікуваних немає. Розклад: BUG-M01 — 1, BUG-M02 — 2, BUG-M03 — 9, BUG-M04 — 1, BUG-M05 — 2 Failures + 1 Error, BUG-M06 — 2. Error — `AvailabilityTest.bookBackToBackRegistersSecondStay` (`IllegalStateException`, побічний ефект BUG-M05).

## 5. Дефекти модуля v1

| ID | Вимога | Тест, що фіксує | Severity | Issue |
|---|---|---|---|---|
| BUG-M01 | BL-10: рівно 30 ночей відхиляється | `BookingValidatorTest#nightsAreAllowedFromOneToThirty` | Major | #7 |
| BUG-M02 | BL-10: перевіряється дата виїзду замість дати заїзду | `BookingValidatorTest#checkInBeforeTodayIsRejected` | Major | #8 |
| BUG-M03 | BL-30: вихідні — сб/нд замість пт/сб | `PriceCalculatorSeasonTest` (4 методи) | Major | #4 |
| BUG-M04 | BL-50: округлення `DOWN` замість `HALF_UP` | `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` | Major | #6 |
| BUG-M05 | BL-70: виїзд/заїзд в один день вважається перетином | `AvailabilityTest` (3 виконання) | Major | #2 |
| BUG-M06 | BL-60: на 7-й день береться 50 % | `AvailabilityTest#cancellationFeeAtBoundaries` | Major | #3 |

## 6. Висновок про стан модуля v1

Модуль v1 до випуску не готовий: тести зафіксували всі 6 закладених дефектів, і кожен порушує явне правило BL. Найбільший вплив на гостя мають дефекти розрахунку вартості (BUG-M03 — неправильна надбавка в п'ятницю й неділю для будь-якого бронювання з цими днями; BUG-M04 — заниження сум на копійки) і валідації (BUG-M02 — приймається заїзд у минулому; BUG-M01 — відхиляється допустиме бронювання на 30 ночей). Кожен дефект виправляється локально, однією умовою чи режимом округлення. Червоні тести лишаються в наборі: у спринті 5 вони покажуть, які дефекти виправлено у v2 (closed), а які ні (reopened).

Обмеження перевірки: потік бронювання на demo недоступний (HTTP 500), тож UI-кейси бронювання не автоматизовано; BL-20 перевіряється лише юніт-тестами.
