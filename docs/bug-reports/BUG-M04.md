# BUG-M04 — округлення DOWN замість HALF_UP у `PriceCalculator.nightPrice`

> Звіт підготовлено для перенесення в GitHub Issues. Мітка: **`module`**.
> **GitHub Issue:** #1 — BUG-M04 (номер звірено з Issues #2 — BUG-M05 та #3 — BUG-M06).
> Шаблон звіту — §4.9 методичних вказівок Спринту 4 (поля за шаблоном спринта 3).
> Виконавець: Чаплюк Д. (ІТ / SDET).

## ID

`BUG-M04` (GitHub Issue #1)

## Title

Округлення `RoundingMode.DOWN` замість `HALF_UP` у `PriceCalculator.nightPrice` — вартість ночі занижується на копійки

## Severity / Priority

- **Severity:** Major (некоректні грошові суми у первинних розрахунках; підсумок формується із занижених нічних тарифів)
- **Priority:** High (порушено явну вимогу BL-50 щодо округлення; виправлення локальне — один рядок)

## Description

Вимога **BL-50** (SRS_04 «СтейНау», розділ 5 «Вимоги до бізнес-логіки модуля», продубльована у
`README.md` модуля `staynow v1`): *«вартість ночі = базовий тариф × сезонний коефіцієнт × (1,15 для
вихідної ночі), округлення `HALF_UP` до 2 знаків; підсумок = сума ночей»*.

Фактично в `src/main/java/ua/kpi/tzyapz/staynow/PriceCalculator.java`, метод `nightPrice`, рядок
`return price.setScale(SCALE, RoundingMode.DOWN);` застосовує режим `DOWN` (усічення), через що
значення з третім знаком ≥ 5 (наприклад, `10.005`) округлюються вниз (`10.00`) замість округлення
за правилом «половини вгору» (`10.01`).

Дефект зафіксовано червоним тестом **`PriceCalculatorTotalTest#nightPriceRoundsHalfUp`**
(`src/test/java/ua/kpi/tzyapz/staynow/PriceCalculatorTotalTest.java`, над методом — коментар
`// BUG-M04, Issue #1`). Тест залишено червоним згідно з правилом спринту: червоні тести дефектів
не вимикати й не «підганяти» (без `@Disabled`, без зміни очікуваного значення під `DOWN`).

## Steps to reproduce

1. У корені проєкту виконати команду:
   `mvn test -Dtest=PriceCalculatorTotalTest`
2. Або відтворити виклик вручну:
   ```java
   PriceCalculator calc = new PriceCalculator();
   BigDecimal price = calc.nightPrice(
           new BigDecimal("8.70"),          // базовий тариф
           LocalDate.of(2026, 4, 11));      // субота, звичайний сезон → множник 1.15
   // 8.70 × 1.15 = 10.005
   System.out.println(price);               // → 10.00 (фактично), має бути 10.01
   ```

## Actual result

`nightPrice(new BigDecimal("8.70"), 2026-04-11)` повертає **`10.00`** — дробова частина `0.005`
відкинута (усічення `DOWN`).

Тест `PriceCalculatorTotalTest#nightPriceRoundsHalfUp` падає:
`expected: <10.01> but was: <10.00>`.

## Expected result

`nightPrice(new BigDecimal("8.70"), 2026-04-11)` повертає **`10.01`** — за BL-50 округлення
`HALF_UP` до 2 знаків: `10.005 → 10.01`.

Виправлення: у `PriceCalculator.nightPrice` замінити
`price.setScale(SCALE, RoundingMode.DOWN)` на `price.setScale(SCALE, RoundingMode.HALF_UP)`.

## Environment

- Модуль: **staynow v1** (1.0.0), пакет `ua.kpi.tzyapz.staynow`, файл
  `src/main/java/ua/kpi/tzyapz/staynow/PriceCalculator.java`, метод `nightPrice`
- Команда запуску: `mvn test -Dtest=PriceCalculatorTotalTest`
- Інфраструктура: JDK 17 (Temurin 17.0.21.1), Apache Maven 3.9.11, JUnit Jupiter 5.10.2,
  Windows 11
- Тест-артефакт: `src/test/java/ua/kpi/tzyapz/staynow/PriceCalculatorTotalTest.java`
  (метод `nightPriceRoundsHalfUp`, коментар `// BUG-M04, Issue #1`)

## Attachments

- `docs/bug-reports/attachments/BUG-M04-surefire.txt` — копія звіту Surefire
  (`target/surefire-reports/ua.kpi.tzyapz.staynow.PriceCalculatorTotalTest.txt`) із трасуванням
  падіння `nightPriceRoundsHalfUp` (`expected: <10.01> but was: <10.00>`)
- скріншот падіння тесту (`BUG-M04-failure.png`) — додається при реєстрації Issue в трекері
  (знімок вікна IDE/терміналу з червоним тестом)
