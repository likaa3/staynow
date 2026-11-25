# qa-staynow — репозиторій автотестів «СтейНау» (Спринт 4)

> Об'єднаний README команди (задача 10.1, Вігура С. КТ; база структури — Чаплюк Д., ІТ/SDET).
> Продукт: «СтейНау» (staynow) — бронювання готелів, Варіант А.
> Demo продукту: https://automationintesting.online

## 1. Вимоги до робочого місця

| Що | Версія | Як перевірити |
|---|---|---|
| JDK (Temurin) | 17 | `java -version` → `openjdk version "17.…"`, `mvn -v` → `Java version: 17.…` |
| Apache Maven | 3.9 (працює і 3.8) | `mvn -v` |
| Google Chrome | актуальна | драйвер — через Selenium Manager, встановлювати не потрібно |

Інтернет потрібен для першого збирання (`~/.m2`) і для UI-тестів (публічний demo). Клонувати
репозиторій у шлях **без кирилиці та пробілів** — деякі інструменти некоректно працюють з
не-ASCII шляхами. Якщо `mvn -v` показує іншу Java — задати `JAVA_HOME` на JDK 17 (розд. 7).

## 2. Структура репозиторію

```
qa-staynow/
├── pom.xml                                  # JDK 17, JUnit Jupiter 5.10, Selenium 4.21, Surefire (тег ui виключено)
├── README.md                                # цей файл
├── .gitignore
├── docs/
│   ├── sprint4/
│   │   ├── surefire-summary.md              # командний протокол (Вігура С., задача 10.2)
│   │   ├── ui-stability-runs.md             # стабільність UI-набору, два прогони поспіль (Чаплюк Д.)
│   │   ├── ui-scenarios.md                  # TC-xx спринта 3 → тест-метод, локатори (Чаплюк Д. / Бєлянцева А.)
│   │   ├── coverage-matrix.md               # BL → тести → рецензент (Бєлянцева А.)
│   │   ├── audit-protocol.md                # протокол аудиту (Гетьман Є.)
│   │   └── bug-reports/
│   │       ├── BUG-M01.md, BUG-M02.md       # BookingValidator, BL-10 (Бєлянцева А.)
│   │       ├── BUG-M03.md                   # PriceCalculator.isWeekendNight, BL-30 (Вігура С.)
│   │       ├── BUG-M04.md                   # PriceCalculator.nightPrice округлення, BL-50 (Чаплюк Д.)
│   │       ├── BUG-M05.md, BUG-M06.md       # Availability, BL-60/BL-70 (Шкарупа М.)
│   │       └── attachments/
│   ├── sprint1…sprint3/
│   └── team/charter.md
├── selenium-ide/
│   ├── staynow.side
│   └── export/
└── src/
    ├── main/java/ua/kpi/tzyapz/staynow/     # PriceCalculator, BookingValidator, BookingError, Availability
    └── test/java/ua/kpi/tzyapz/staynow/
        ├── ExampleTest.java
        ├── AvailabilityTest.java            # BL-60/BL-70 (Шкарупа М.) — червоні BUG-M05/M06
        ├── BookingContactsTest.java         # BL-20 (Гетьман Є.)
        ├── BookingValidatorTest.java        # BL-10 (Бєлянцева А.) — червоні BUG-M01/M02
        ├── PriceCalculatorSeasonTest.java   # BL-30/BL-40 (Вігура С.) — червоний BUG-M03
        ├── PriceCalculatorTotalTest.java    # BL-50 (Чаплюк Д.) — червоний BUG-M04
        └── ui/
            ├── UiTestBase.java
            ├── StayNowHomePage.java
            ├── StayNowAdminLoginPage.java
            ├── StayNowHomePageTest.java     # TC-001…TC-004, TC-020…TC-022
            └── StayNowAdminAuthTest.java    # TC-023, TC-024, TC-025, TC-069
```

## 3. Запуск тестів

```bash
# юніт-тести (UI з тегом "ui" виключені за замовчуванням)
mvn test

# юніт- + UI-тести (потрібен Chrome і доступ до demo)
mvn test -DexcludedGroups=none

# UI-тести з видимим браузером (для демонстрації)
mvn test -DexcludedGroups=none -Dheadless=false

# один клас або метод
mvn test -Dtest=PriceCalculatorSeasonTest
mvn test -DexcludedGroups=none -Dtest=StayNowAdminAuthTest

# чисте збирання «з нуля» (так робить рецензент і аудитор)
mvn clean test
```

> Команда `mvn test -Dgroups=ui` **не працює** — pom.xml виключає тег `ui` саме через
> `excludedGroups`, а не `groups`. Для UI потрібно `-DexcludedGroups=none`.

### Очікуваний результат

Станом на 25.11.2026 (проміжний прогін, без `BookingValidatorTest` і `PriceCalculatorSeasonTest`):

```
[INFO] Tests run: 67, Failures: 5, Errors: 1, Skipped: 0
```

`BUILD FAILURE` — **очікувана** поведінка: усі падіння — зареєстровані дефекти модуля, жодного
неочікуваного. Один випадок — саме `Error`, а не `Failure`:
`AvailabilityTest.bookBackToBackRegistersSecondStay` кидає `IllegalStateException` через той самий
дефект BUG-M05 (метод `book()` помилково відхиляє друге бронювання «спина до спини»); виправляти
тест не обов'язково.

| Тест, що падає | Тип | Дефект | Issue |
|---|---|---|---|
| `PriceCalculatorTotalTest.nightPriceRoundsHalfUp` | Failure | BUG-M04 | #4 |
| `AvailabilityTest.checkInOnPreviousGuestCheckOutDayIsAllowed` | Failure | BUG-M05 | #2 |
| `AvailabilityTest.checkOutOnNextGuestCheckInDayIsAllowed` | Failure | BUG-M05 | #2 |
| `AvailabilityTest.cancellationFeeAtBoundaries[7,0.00]` | Failure | BUG-M06 | #3 |
| `AvailabilityTest.bookBackToBackRegistersSecondStay` | **Error** | BUG-M05 | #2 |
| `AvailabilityTest.refundAtBoundaries[2]` | Failure | BUG-M06 | #3 |

Після заливки `PriceCalculatorSeasonTest` (Вігура С.) і `BookingValidatorTest` (Бєлянцева А.)
числа зміняться — додасться 9 червоних виконань BUG-M03 і падіння BUG-M01/M02. **Фінальні цифри
внести в `docs/sprint4/surefire-summary.md` після прогону на повному наборі**, їх і вважати
авторитетними для звітів і демо, а не числа в цьому README.

UI-прогони та їх стабільність (два прогони поспіль, без flaky) — `docs/sprint4/ui-stability-runs.md`.

## 4. Selenium IDE

- `selenium-ide/staynow.side` — 2 сценарії на demo продукту (TC-001/TC-003 — головна сторінка;
  TC-023/TC-024 — вхід адміністратора), жорсткі `assert` і м'які `verify`, базовий URL — demo
  продукту (переносимий, без локальних шляхів).
- `selenium-ide/export/` — чернетка експорту Java JUnit 4 (не збирається Maven) + таблиця замін
  для адаптації до JUnit 5.

## 5. Дефекти (норматив ≥5 із 6)

| ID | Клас.метод | BL | Issue | Статус |
|---|---|---|---|---|
| BUG-M01 | `BookingValidator.validate` (ночі=30) | BL-10 | створити | очікується |
| BUG-M02 | `BookingValidator.validate` (checkOut замість checkIn) | BL-10 | створити | очікується |
| BUG-M03 | `PriceCalculator.isWeekendNight` | BL-30 | #4 | звіт готовий |
| BUG-M04 | `PriceCalculator.nightPrice` округлення | BL-50 | #4 | підтверджено |
| BUG-M05 | `Availability.isAvailable` | BL-70 | #2 | підтверджено |
| BUG-M06 | `Availability.cancellationFee` | BL-60 | #3 | підтверджено |

Дефект demo (не модуля): `GET /api/room/{id}` → HTTP 500, сторінка `/reservation/{id}` не
рендериться; кейси TC-007…TC-019 відкладено до відновлення demo. Issue з міткою `demo` — створити.

## 6. Хто що написав (Спринт 4)

| Виконавець | Внесок |
|---|---|
| Чаплюк Д. (ІТ / SDET) | інфраструктура (pom, .gitignore, структура), модуль staynow v1, `PriceCalculatorTotalTest` (BL-50, BUG-M04), Selenium IDE, WebDriver-тести (Page Object + 11 сценаріїв), `ui-stability-runs.md` |
| Бєлянцева А. (ТА) | `BookingValidatorTest` (BL-10, BUG-M01/M02), `coverage-matrix.md`, перелік UI-сценаріїв |
| Гетьман Є. (АО) | `BookingContactsTest` (BL-20), `audit-protocol.md` |
| Вігура С. (КТ) | `PriceCalculatorSeasonTest` (BL-30/BL-40, BUG-M03), цей README, `surefire-summary.md`, демо |
| Шкарупа М. (СМ) | `AvailabilityTest` (BL-60/BL-70, BUG-M05/M06), code review, інтеграція |

## 7. Типові помилки

**`mvn` збирає старим JDK.** `mvn -v` → `Java version: 1.8…`/`11…` означає `JAVA_HOME` не на 17:
```powershell
$env:JAVA_HOME = "<шлях до JDK 17>"
mvn -v
```

**`No tests were executed`.** Клас не в `src/test/java`, метод без `@Test`, або ім'я класу не
закінчується на `Test`/`Tests`/`TestCase` (Surefire include-патерни).

**UI-тест не запускається.** Тег `ui` виключено за замовчуванням — `mvn test -DexcludedGroups=none`
(не `-Dgroups=ui`, pom працює саме через `excludedGroups`).

**`SessionNotCreatedException`.** Selenium Manager не завантажив драйвер — перевірте інтернет і
Chrome; за проксі — `HTTPS_PROXY`. Кеш — `~/.cache/selenium`.

**`NoSuchElementException`/`TimeoutException`.** Елемент ще не з'явився або локатор неправильний —
перевірте у DevTools, додайте `WebDriverWait`.

**Тест «іноді падає».** SPA перемальовується — чекайте на конкретний стан (текст, кількість
елементів, URL), а не «щось з'явилося».

**Кирилиця → `????`.** У `pom.xml` вже `UTF-8`; для консолі Windows — `chcp 65001`.

**`BigDecimal` — `expected: <120> but was: <120.00>`.** `assertEquals` порівнює зі шкалою — задайте
очікуване з тією ж шкалою (`new BigDecimal("120.00")`) або порівнюйте через `compareTo`.
