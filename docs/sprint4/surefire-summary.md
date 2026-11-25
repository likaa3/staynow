# Протокол прогонів Surefire — Спринт 4, «СтейНау» (Варіант А)

Виконавець: Чаплюк Д. (ІТ / SDET). Дата: 23.11.2026.
Середовище: Windows 11 · Temurin JDK 17.0.21.1 · Apache Maven 3.9.11 · Surefire 3.2.5 ·
JUnit Jupiter 5.10.2 · Selenium Java 4.21.0 · Google Chrome 157 (headless=new, 1280×900) ·
demo https://automationintesting.online.

Після виправлень за результатами review (review-IT-sprint4.md, п. 1–11): видалено
`SmokeUiTest` (saucedemo) і стартовий `Calculator`/`CalculatorTest`, UI-сценарії переписано на
demo зі спринта 3 за реальними TC-ID, дати — від `LocalDate.now()`, `.side` — переносимий.

## 1. Юніт-прогін: `mvn test` (тег `ui` виключено за замовчуванням)

| Тест-клас | Tests run | Failures | Errors | Skipped | Примітка |
|---|---|---|---|---|---|
| `ua.kpi.tzyapz.staynow.ExampleTest` | 2 | 0 | 0 | 0 | зразок модуля staynow v1 |
| `ua.kpi.tzyapz.staynow.PriceCalculatorTotalTest` | 20 | **1** | 0 | 0 | BL-50; падіння = `BUG-M04` |
| **Підсумок** | **22** | **1** | **0** | **0** | `BUILD FAILURE` — очікувано |

Єдине падіння:

```
PriceCalculatorTotalTest.nightPriceRoundsHalfUp:50
BL-50 вимагає округлення HALF_UP: 8.70 × 1.15 = 10.005 → 10.01;
реалізація застосовує DOWN і повертає 10.00 (див. docs/bug-reports/BUG-M04.md)
==> expected: <10.01> but was: <10.00>
```

Це червоний тест закладеного дефекту `BUG-M04` (GitHub Issue #1; над методом — коментар
`// BUG-M04, Issue #1`). Вимоги спринту забороняють вимикати (`@Disabled`) чи підганяти
очікування під дефект. UI-тести (`@Tag("ui")`) у цьому прогоні не виконуються.

*Примітка: у репозиторії команди на `main` також лежить `AvailabilityTest` (юніт-тести BL-60/BL-70,
автор Шкарупа М.) — у локальному протоколі не враховано.*

## 2. Повні прогони: `mvn test -DexcludedGroups=none` (юніт + UI), два поспіль

### Прогін 1 (23.11.2026)

| Тест-клас | Tests run | Failures | Errors | Skipped | Час | Примітка |
|---|---|---|---|---|---|---|
| `ua.kpi.tzyapz.staynow.ExampleTest` | 2 | 0 | 0 | 0 | 0.079 s | |
| `ua.kpi.tzyapz.staynow.PriceCalculatorTotalTest` | 20 | **1** | 0 | 0 | 0.310 s | `BUG-M04` |
| `ua.kpi.tzyapz.staynow.ui.StayNowAdminAuthTest` | 4 | 0 | 0 | 0 | 17.19 s | TC-023, TC-024, TC-025, TC-069 |
| `ua.kpi.tzyapz.staynow.ui.StayNowHomePageTest` | 7 | 0 | 0 | 0 | 39.51 s | TC-001…TC-004, TC-020…TC-022 |
| **Підсумок** | **33** | **1** | **0** | **0** | ~60 s | |

### Прогін 2 (стабільність, 23.11.2026)

| Тест-клас | Tests run | Failures | Errors | Skipped | Час | Примітка |
|---|---|---|---|---|---|---|
| `ua.kpi.tzyapz.staynow.ExampleTest` | 2 | 0 | 0 | 0 | 0.079 s | |
| `ua.kpi.tzyapz.staynow.PriceCalculatorTotalTest` | 20 | **1** | 0 | 0 | 0.298 s | `BUG-M04` |
| `ua.kpi.tzyapz.staynow.ui.StayNowAdminAuthTest` | 4 | 0 | 0 | 0 | 18.19 s | |
| `ua.kpi.tzyapz.staynow.ui.StayNowHomePageTest` | 7 | 0 | 0 | 0 | 42.81 s | |
| **Підсумок** | **33** | **1** | **0** | **0** | ~61 s | |

**Висновок про стабільність:** два прогони поспіль дали ідентичний результат (33 / 1 / 0 / 0);
flaky-падінь не зафіксовано. Список failed ідентичний у обох прогонах і складається виключно
з червоного тесту дефекту `BUG-M04` — кожен «червоний» тест відповідає звіту про дефект.

## 3. Перелік тестів

### Юніт-тести (22 виконання; тести BL-50 → TC-059…TC-062 спринта 3)

`ExampleTest` (2): `twoWeekdayNightsRegularSeason` (BL-50), `validBookingHasNoErrors` (BL-10/BL-20).

`PriceCalculatorTotalTest` (20 виконань, 11 методів):

| Метод | Тип | Виконань | Статус |
|---|---|---|---|
| `nightPriceRoundsHalfUp` (`// BUG-M04, Issue #1`) | `@Test` | 1 | **RED — BUG-M04** |
| `nightPriceAppliesSeasonFactorWithTwoDecimals` | `@ParameterizedTest` `@CsvSource` | 5 | GREEN |
| `nightPriceWeekendSurchargeHasScaleTwo` | `@Test` | 1 | GREEN |
| `totalSumsNightPricesAcrossSeasonBoundary` | `@Test` | 1 | GREEN |
| `totalAppliesLongStayDiscountFromSevenNights` (межі 6/7 ночей, перевірка `ChronoUnit`) | `@ParameterizedTest` `@CsvSource` | 2 | GREEN |
| `totalDiscountRoundsHalfUp` (третій знак = 5 після знижки: 219.105→219.11; 422.775→422.78) | `@ParameterizedTest` `@CsvSource` | 2 | GREEN (захист від регресії у v2) |
| `nightPriceRejectsNonPositiveBase` | `@ParameterizedTest` `@ValueSource` | 3 | GREEN |
| `nightPriceRejectsNullBase` | `@Test` | 1 | GREEN |
| `totalRejectsNonPositiveBase` | `@ParameterizedTest` `@ValueSource` | 2 | GREEN |
| `totalRejectsCheckOutNotAfterCheckIn` | `@Test` (`assertAll`) | 1 | GREEN |
| `totalAlwaysHasScaleTwo` | `@Test` (`assertAll`) | 1 | GREEN |

### UI-тести (11 сценаріїв, тег `ui`; кожен — реальний TC-ID спринта 3)

| Сценарій | Клас / метод | Вимога | Що перевіряє |
|---|---|---|---|
| TC-001 | `StayNowHomePageTest#homePageShowsHotelNameBookNowAndMenu` | F-10 | назва готелю, «Book Now», меню з 6 пунктів у порядку |
| TC-002 | `StayNowHomePageTest#menuAnchorsAndAdminOpenLoginPage` | F-10 | якорі Rooms/Booking/Contact, Admin → /admin (Login) |
| TC-003 | `StayNowHomePageTest#ourRoomsSectionShowsRoomCards` | F-20 | картки Single/Double/Suite, ціна «£N per night», Book now |
| TC-004 | `StayNowHomePageTest#checkAvailabilityUpdatesRoomLinks` | F-30 | Check Availability від дат від `now()` оновлює посилання карток (checkin/checkout РРРР-ММ-ДД) |
| TC-020 | `StayNowHomePageTest#contactFormWithValidDataShowsThanks` | F-70, GUI-85 | подяка з іменем і темою |
| TC-021 | `StayNowHomePageTest#shortSubjectIsRejected` | F-70 | тема 4 символи — не надіслано, форма лишається |
| TC-022 | `StayNowHomePageTest#shortMessageIsRejected` | F-70 | повідомлення 19 символів — не надіслано |
| TC-023 | `StayNowAdminAuthTest#adminLoginOpensDashboard` | F-80 | вхід admin/password → дашборд (Rooms/Report/Branding/Messages) |
| TC-024 | `StayNowAdminAuthTest#wrongPasswordShowsErrorAndKeepsLoginForm` | F-80 | «Invalid credentials», форма лишається |
| TC-025 | `StayNowAdminAuthTest#logoutTerminatesSession` | F-85 | Logout завершує сесію, /admin знову вимагає вхід |
| TC-069 | `StayNowAdminAuthTest#adminLoginPageHeaderShowsFrontPageAndLogout` | GUI-70 | шапка входу: Front Page + Logout |

## 4. Відомі обмеження

- Потік бронювання (`/reservation/{id}` — кейси TC-007, TC-008, TC-009, TC-010, TC-014,
  TC-018, TC-019) станом на 23.11.2026 недоступний через помилку сервера demo:
  `GET https://automationintesting.online/api/room/{id}` → **HTTP 500** (Internal Server Error),
  сторінка номера не рендериться (вічно «Loading…»). Це дефект demo, а не тестів; зафіксовано
  у `docs/sprint4/ui-scenarios.md`, кейси автоматизуються після відновлення demo.
- Валидаційні помилки контактної форми у demo v2.2 не рендеряться (drift відносно спринта 3) —
  для TC-021/TC-022 перевіряється спек-поведінка «повідомлення не надіслано».

## 5. Звіти

- Сирі звіти останнього прогону: `target/surefire-reports/` (до репозиторію не комітяться;
  текстовий підсумок — цей файл).
- Норматив спринту (5 осіб): ≥20 юніт-тестів командою (внесок ІТ: 21 зелених + 1 червоний
  `BUG-M04`), ≥8 UI-сценаріїв (внесок ІТ: 11 сценаріїв за реальними TC-ID), ≥3 юніт-тести на
  особу (внесок ІТ: 11 методів BL-50 у `PriceCalculatorTotalTest`), ≥5 дефектів командою
  (внесок ІТ: BUG-M04 → Issue #1).
