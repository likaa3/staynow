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

Де взяти:

- **JDK 17 (Temurin):** https://adoptium.net/temurin/releases/?version=17 — Windows x64, інсталятор `.msi`; у майстрі увімкнути «Set JAVA_HOME variable» і «Add to PATH».
- **Apache Maven 3.9:** https://maven.apache.org/download.cgi — архів `apache-maven-3.9.x-bin.zip` (Binary zip); розпакувати й додати папку `bin` у `PATH`. Інструкція зі встановлення: https://maven.apache.org/install.html
- Після встановлення відкрити **новий** термінал і перевірити `java -version` та `mvn -v`.

Інтернет потрібен для першого збирання (`~/.m2`) і для UI-тестів (публічний demo). Клонувати
репозиторій у шлях **без кирилиці та пробілів** — деякі інструменти некоректно працюють з
не-ASCII шляхами. Якщо `mvn -v` показує іншу Java — задати `JAVA_HOME` на JDK 17 (розд. 7).

## 2. Структура репозиторію

```
qa-staynow/
├── pom.xml
├── README.md                                # цей файл
├── .gitignore
├── docs/
│   ├── sprint4/
│   │   ├── surefire-summary.md              # КОМАНДНИЙ протокол, фінальні цифри (Вігура С., 10.2)
│   │   ├── ui-stability-runs.md             # стабільність UI-набору (Чаплюк Д.)
│   │   ├── ui-scenarios.md                  # TC-xx → тест-метод, локатори (Чаплюк Д. / Бєлянцева А.)
│   │   ├── coverage-matrix.md               # BL → тести → рецензент (Бєлянцева А.)
│   │   ├── audit-protocol.md                # протокол аудиту (Гетьман Є.)
│   │   ├── dossier-v4-automation.md         # розділ «Автоматизація» досьє v4 (Бєлянцева А.)
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
    ├── main/java/ua/kpi/tzyapz/staynow/
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
mvn test                              # юніт-тести (UI з тегом "ui" виключені pom-ом)
mvn test -DexcludedGroups=none        # юніт- + UI-тести
mvn test -DexcludedGroups=none -Dheadless=false
mvn test -Dtest=PriceCalculatorSeasonTest
mvn clean test                        # чисте збирання «з нуля»
```

> Команда `mvn test -Dgroups=ui` **не працює** — pom.xml виключає тег `ui` саме через
> `excludedGroups`, а не `groups`.

### Очікуваний результат

**`BUILD FAILURE` при `mvn clean test` — очікувана поведінка**, доки дефекти модуля v1 не
виправлено у v2. Причина: у модулі навмисно закладено 6 дефектів (задача 5), на кожен написано
«червоний» тест, і команда їх свідомо не вимикає й не підганяє (правило спринту).

Точні цифри (`Tests run`, `Failures`, `Errors`) змінюються з кожним комітом, тому їх **не
дублюємо тут** — єдине достовірне джерело: **`docs/sprint4/surefire-summary.md`**. Там же —
пояснення, чому один тест (`AvailabilityTest.bookBackToBackRegistersSecondStay`) рахується як
`Error`, а не `Failure`.

UI-прогони та їх стабільність — `docs/sprint4/ui-stability-runs.md` (Чаплюк Д.).

## 4. Selenium IDE

- `selenium-ide/staynow.side` — 2 сценарії на demo продукту.
- `selenium-ide/export/` — чернетка експорту Java JUnit 4 (не збирається Maven) + таблиця замін
  для адаптації до JUnit 5.

## 5. Дефекти (норматив ≥5 із 6) — GitHub Issues

| ID | Клас.метод | BL | Issue | Статус |
|---|---|---|---|---|
| BUG-M01 | `BookingValidator.validate` (ночі=30) | BL-10 | **#7** | підтверджено |
| BUG-M02 | `BookingValidator.validate` (checkOut замість checkIn) | BL-10 | **#8** | підтверджено |
| BUG-M03 | `PriceCalculator.isWeekendNight` | BL-30 | **#4** | підтверджено |
| BUG-M04 | `PriceCalculator.nightPrice` округлення | BL-50 | **#6** | підтверджено |
| BUG-M05 | `Availability.isAvailable` | BL-70 | #2 | підтверджено |
| BUG-M06 | `Availability.cancellationFee` | BL-60 | #3 | підтверджено |

> Номери звірено 25.11.2026: #1 — PR інфраструктури, #2 — BUG-M05, #3 — BUG-M06, #4 — BUG-M03
> (не BUG-M04, як вважалося раніше), #5 — PR `sprint4-qa-lead` (Вігура С., злиття README і
> протоколу в `main`), #6 — BUG-M04, #7 — BUG-M01, #8 — BUG-M02. Якщо нумерація ще зміститься —
> правити тут і в заголовках відповідних `BUG-M0x.md`.

Дефект demo (не модуля): `GET /api/room/{id}` → HTTP 500, сторінка `/reservation/{id}` не
рендериться; кейси TC-007…TC-019 відкладено до відновлення demo. Issue з міткою `demo` — **#10**.

UI-тест `StayNowAdminAuthTest#logoutTerminatesSession` (TC-025) червоний у прогоні з UI — **Issue #12**; це не дефект модуля v1 (деталі — `docs/sprint4/surefire-summary.md`).

## 6. Хто що написав (Спринт 4)

| Виконавець | Внесок |
|---|---|
| Чаплюк Д. (ІТ / SDET) | інфраструктура, модуль staynow v1, `PriceCalculatorTotalTest` (BL-50, BUG-M04), Selenium IDE, WebDriver-тести (11 сценаріїв), `ui-stability-runs.md` |
| Бєлянцева А. (ТА) | `BookingValidatorTest` (BL-10, BUG-M01/M02), `coverage-matrix.md`, `dossier-v4-automation.md`, звірка UI-сценаріїв |
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
закінчується на `Test`/`Tests`/`TestCase`.

**UI-тест не запускається.** Тег `ui` виключено за замовчуванням — `mvn test -DexcludedGroups=none`.

**`SessionNotCreatedException`.** Selenium Manager не завантажив драйвер — перевірте інтернет і
Chrome; за проксі — `HTTPS_PROXY`. Кеш — `~/.cache/selenium`.

**`NoSuchElementException`/`TimeoutException`.** Елемент ще не з'явився або локатор неправильний.

**Тест «іноді падає».** SPA перемальовується — чекайте на конкретний стан, а не «щось з'явилося».

**Кирилиця → `????`.** У `pom.xml` вже `UTF-8`; для консолі Windows — `chcp 65001`.

**`BigDecimal` — `expected: <120> but was: <120.00>`.** Порівнюйте зі шкалою
(`new BigDecimal("120.00")`) або через `compareTo`.
