# Досьє якості v4 — розділ «Автоматизація»

Продукт: «СтейНау» (П4) · модуль staynow v1 (1.0.0) · Спринт 4 «Автоматизація: JUnit 5 і Selenium WebDriver»
Автор розділу: Бєлянцева А. (ТА, задача 10.3) · Рецензент: Вігура С. · Стан на 25.11.2026
Репозиторій: `qa-staynow` (посилання — у README команди) · Трекер: GitHub Issues, мітка `module`

## 1. Норматив (план / факт)

| Норматив (5 осіб) | План | Факт |
|---|---|---|
| Юніт-тестів JUnit 5 (параметризований метод = 1 тест) | ≥ 20 | 44 (AvailabilityTest 15, PriceCalculatorTotalTest 11, BookingValidatorTest 7, PriceCalculatorSeasonTest 6, BookingContactsTest 5) |
| Юніт-тестів на особу (за комітами) | ≥ 3 | Шкарупа 15, Чаплюк 11, Бєлянцева 7, Вігура 6, Гетьман 5 |
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
| BL-50 | вартість ночі, `HALF_UP`, підсумок, знижка від 7 ночей | `PriceCalculatorTotalTest` | Чаплюк Д. | BUG-M04 |
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
| Повний набір після заливки | `mvn clean test` | очікується `Tests run: 130, Failures: 17, Errors: 1`; фактичне — ___ (`docs/sprint4/surefire-summary.md`) |
| UI-набір | `mvn test -DexcludedGroups=none` | ___ (`docs/sprint4/ui-stability-runs.md`) |

`BUILD FAILURE` очікуваний: усі падіння — зареєстровані дефекти модуля v1, неочікуваних немає.

## 5. Дефекти модуля v1

| ID | Вимога | Тест, що фіксує | Severity | Issue |
|---|---|---|---|---|
| BUG-M01 | BL-10: рівно 30 ночей відхиляється | `BookingValidatorTest#nightsAreAllowedFromOneToThirty` | Major | #7 |
| BUG-M02 | BL-10: перевіряється дата виїзду замість дати заїзду | `BookingValidatorTest#checkInBeforeTodayIsRejected` | Major | #8 |
| BUG-M03 | BL-30: вихідні — сб/нд замість пт/сб | `PriceCalculatorSeasonTest` (4 методи) | Major | #4 |
| BUG-M04 | BL-50: округлення `DOWN` замість `HALF_UP` | `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` | Major | #6 |
| BUG-M05 | BL-70: виїзд/заїзд в один день вважається перетином | `AvailabilityTest` (3 виконання) | за звітом | #2 |
| BUG-M06 | BL-60: на 7-й день береться 50 % | `AvailabilityTest#cancellationFeeAtBoundaries` | за звітом | #3 |

## 6. Висновок про стан модуля v1

Модуль v1 до випуску не готовий: тести зафіксували всі 6 закладених дефектів, і кожен порушує явне правило BL. Найбільший вплив на гостя мають дефекти розрахунку вартості (BUG-M03 — неправильна надбавка в п'ятницю й неділю для будь-якого бронювання з цими днями; BUG-M04 — заниження сум на копійки) і валідації (BUG-M02 — приймається заїзд у минулому; BUG-M01 — відхиляється допустиме бронювання на 30 ночей). Кожен дефект виправляється локально, однією умовою чи режимом округлення. Червоні тести лишаються в наборі: у спринті 5 вони покажуть, які дефекти виправлено у v2 (closed), а які ні (reopened).

Обмеження перевірки: потік бронювання на demo недоступний (HTTP 500), тож UI-кейси бронювання не автоматизовано; BL-20 перевіряється лише юніт-тестами.
