# qa-staynow — репозиторій автотестів «СтейНау» (Спринт 4)

> **Виконавець (Спринт 4):** Чаплюк Д. (ІТ / SDET)
> Продукт: «СтейНау» (staynow) — бронювання готелів, Варіант А (модуль стає робочим проєктом команди).
> Demo продукту: https://automationintesting.online (той самий, що у тест-кейсах спринта 3).

## 1. Вимоги до робочого місця

| Що | Версія | Як перевірити |
|---|---|---|
| JDK (Temurin) | 17 | `java -version` → `openjdk version "17.…"`, `mvn -v` → `Java version: 17.…` |
| Apache Maven | 3.9 (працює і 3.8) | `mvn -v` |
| Google Chrome | актуальна | драйвер встановлювати НЕ потрібно — його завантажує Selenium Manager |

Інтернет потрібен для першого збирання (Maven завантажує залежності у `~/.m2`) і для UI-тестів
(вони відкривають публічний demo-застосунок). Якщо `mvn -v` показує інший JDK — задайте
`JAVA_HOME` на JDK 17 (див. розділ 7).

## 2. Структура репозиторію

```
qa-staynow/
├── pom.xml                                  # JDK 17, JUnit Jupiter 5.10, Selenium 4.21, Surefire (тег ui виключено)
├── README.md                                # цей файл
├── .gitignore                               # target/, .idea/, *.iml, out/, .DS_Store
├── docs/
│   ├── sprint4/
│   │   ├── surefire-summary.md              # протокол запуску: дата, команда, «Tests run: …», failed → ID дефекту
│   │   └── ui-scenarios.md                  # TC-xx спринта 3 → тест-метод, локатори, очікування
│   └── bug-reports/
│       ├── BUG-M04.md                       # звіт про дефект (Issue #1, мітка module)
│       └── attachments/                     # вкладення до звітів (звіт Surefire тощо)
├── selenium-ide/
│   ├── staynow.side                         # проєкт Selenium IDE (2 сценарії на demo продукту)
│   └── export/                              # Java-чернетка експорту (JUnit 4, не збирається) + таблиця замін JUnit 4 → 5
└── src/
    ├── main/java/ua/kpi/tzyapz/staynow/     # модуль продукту v1: PriceCalculator, BookingValidator, BookingError, Availability
    └── test/java/ua/kpi/tzyapz/staynow/
        ├── ExampleTest.java                 # зразок тестів модуля
        ├── PriceCalculatorTotalTest.java    # юніт-тести BL-50 (включно з червоним тестом BUG-M04)
        └── ui/
            ├── UiTestBase.java              # життєвий цикл UI-тестів: headless, quit, WebDriverWait
            ├── StayNowHomePage.java         # Page Object головної сторінки demo
            ├── StayNowAdminLoginPage.java   # Page Object сторінки входу адміністратора
            ├── StayNowHomePageTest.java     # UI-тести TC-001…TC-004, TC-020…TC-022
            └── StayNowAdminAuthTest.java    # UI-тести TC-023, TC-024, TC-025, TC-069
```

## 3. Запуск тестів

```bash
# юніт-тести (UI-тести з тегом "ui" за замовчуванням виключені)
mvn test

# юніт- + UI-тести (потрібен Chrome і доступ до demo)
mvn test -DexcludedGroups=none

# UI-тести з видимим браузером (для демонстрації)
mvn test -DexcludedGroups=none -Dheadless=false

# лише один клас або один метод
mvn test -Dtest=PriceCalculatorTotalTest
mvn test -DexcludedGroups=none -Dtest=StayNowAdminAuthTest

# чисте збирання «з нуля» (так робить рецензент і аудитор)
mvn clean test
```

Очікуваний результат `mvn test` (юніт-тести):

```
[INFO] Tests run: 22, Failures: 1, Errors: 0, Skipped: 0
[ERROR] PriceCalculatorTotalTest.nightPriceRoundsHalfUp ==> expected: <10.01> but was: <10.00>
```

`BUILD FAILURE` — **очікувана** поведінка: єдине падіння — червоний тест дефекту **`BUG-M04`**
(округлення `DOWN` замість `HALF_UP`, див. `docs/bug-reports/BUG-M04.md`, GitHub Issue #1).
Вимоги методички забороняють вимикати чи «підганяти» червоні тести дефектів. Решта 21 юніт-тесту
зелені. UI-тести (`@Tag("ui")`) у цьому прогоні не виконуються.

Очікуваний результат `mvn test -DexcludedGroups=none` (юніт + UI):

```
[INFO] Tests run: 33, Failures: 1, Errors: 0, Skipped: 0
```

UI-тести стабільні: два прогони поспіль дають ідентичний результат (без flaky), див.
`docs/sprint4/surefire-summary.md`.

## 4. Selenium IDE

- `selenium-ide/staynow.side` — 2 сценарії на demo продукту:
  «TC-001/TC-003 — головна сторінка і картки номерів» та «TC-023/TC-024 — вхід адміністратора
  і помилка»: команди `open`, `click`, `type`, очікування `wait for element visible`,
  жорсткі `assert text` / `assert element present` і м'які `verify text` / `verify value` /
  `verify element present`. Базовий URL проєкту — demo продукту (переносимий, без локальних шляхів).
- `selenium-ide/export/` — чернетка експорту Java JUnit (формат JUnit 4; файл лежить поза
  `src/test/java` і Maven не збирає його) та таблиця замін для адаптації до JUnit 5.

## 5. Дефекти

- `BUG-M04` (GitHub Issue #1, мітка `module`) — `PriceCalculator.nightPrice` округлює `DOWN`
  замість `HALF_UP` за BL-50; звіт — `docs/bug-reports/BUG-M04.md`, тест —
  `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` (лишається червоним до виправлення у v2).
- Дефекти `BUG-M05` (Issue #2) і `BUG-M06` (Issue #3) — див. трекер команди
  (`AvailabilityTest`, автор Шкарупа М.).
- Стан demo на 23.11.2026: потік бронювання `/reservation/*` недоступний через помилку сервера
  (`GET /api/room/{id}` → HTTP 500) — кейси бронювання (TC-007…TC-019) автоматизуються після
  відновлення demo; зафіксовано у `docs/sprint4/ui-scenarios.md`.

## 6. Хто що написав (Спринт 4)

| Виконавець | Внесок |
|---|---|
| Чаплюк Д. (ІТ / SDET) | інфраструктура (pom, .gitignore, структура), підключення модуля staynow v1, юніт-тести BL-50 (`PriceCalculatorTotalTest`), дефект BUG-M04, Selenium IDE (.side + експорт), WebDriver-тести (Page Object + 11 сценаріїв), протокол прогонів |
| Бєлянцева А. (ТА) | `BookingValidatorTest` (BL-10), матриця покриття, перелік UI-сценаріїв |
| Гетьман Є. (АО) | `BookingContactsTest` (BL-20), протокол відтворення «з нуля» |
| Вігура С. (КТ) | `PriceCalculatorSeasonTest` (BL-30/BL-40), README команди, протокол запуску, демо |
| Шкарупа М. (СМ) | `AvailabilityTest` (BL-60/BL-70), code review, дефекти BUG-M05/BUG-M06 |

## 7. Типові помилки

**`mvn` збирає старим JDK (помилки на кшталт `invalid target release: 17`).** Перевірте, який JDK
бачить Maven:

```bash
mvn -v
# Java version: 1.8.0_… або 11.… → JAVA_HOME вказує не на 17
```

Windows (PowerShell, поточне вікно):
```powershell
$env:JAVA_HOME = "<шлях до JDK 17>"      # наприклад: C:\Program Files\Eclipse Adoptium\jdk-17.0.11.9-hotspot
mvn -v
```

**`No tests were executed` або `Tests run: 0`.** Клас тесту не в `src/test/java`, метод не
позначений `@Test`, або ім'я файлу/класу не збігається і не закінчується на `Test` (Surefire
включає лише `*Test`, `Test*`, `*Tests`, `*TestCase`). Шлях папок і `package` мають збігатися.

**UI-тест не запускається (`Skipped` або взагалі не в списку).** Тег `ui` виключено за
замовчуванням — запускайте `mvn test -DexcludedGroups=none`.

**`SessionNotCreatedException` / `Unable to obtain driver`.** Selenium Manager не зміг
завантажити драйвер: перевірте інтернет і чи встановлений Chrome; у корпоративній мережі із
проксі — задайте `HTTPS_PROXY`. Кеш драйверів — `~/.cache/selenium`.

**`NoSuchElementException` / `TimeoutException`.** Елемент ще не з'явився або локатор
неправильний: перевірте локатор у DevTools (`Ctrl+F` у вкладці Elements приймає CSS та XPath)
і додайте `WebDriverWait` перед пошуком.

**Тест «іноді падає».** Сторінка перемальовується після дії (SPA): чекайте на стан, який справді
перевіряєте (текст, кількість елементів, URL), а не на «щось з'явилося».

**Кирилиця у виводі перетворилася на `????`.** У `pom.xml` уже задано `UTF-8`; для консолі
Windows виконайте `chcp 65001` або запускайте тести з IDEA.

**`BigDecimal` — `expected: <120> but was: <120.00>`.** `assertEquals` порівнює `BigDecimal`
разом зі шкалою (кількістю знаків); задайте очікуване значення з тією ж шкалою
(`new BigDecimal("120.00")`) або порівнюйте через `compareTo`.
