# BUG-M02 — `BookingValidator.validate` порівнює з today дату виїзду замість дати заїзду

> Звіт для GitHub Issues. Мітки: `bug`, `module`, `severity:major`, `priority:high`, `sprint:4`.
> **GitHub Issue: #8**
> Шаблон — спринт 3 (4.11), особливості звіту про дефект модуля — §4.9 методички спринта 4.
> Виконавець: Бєлянцева А. (ТА). Рецензент: Шкарупа М.

## ID
`BUG-M02` (GitHub Issue #8)

## Title
Бронювання із заїздом у минулому не відхиляється, якщо дата виїзду сьогодні або пізніше

## Severity / Priority
- **Severity:** Major — модуль приймає недопустиме бронювання: заїзд у минулому проходить валідацію без помилки.
- **Priority:** High — порушено явне правило BL-10; виправлення локальне (одна змінна в умові).

## Description
Вимога **BL-10** (SRS_04 «СтейНау», розд. 5; продубльована в `README.md` модуля): «дата заїзду не раніше `today`».

У `BookingValidator.validate` умова `if (checkOut.isBefore(today))` перевіряє дату **виїзду**, а не заїзду. Через це помилка `CHECK_IN_IN_PAST` з'являється лише тоді, коли в минулому обидві дати. Заїзд учора з виїздом сьогодні чи пізніше проходить без помилки. Має бути `checkIn.isBefore(today)`.

Дефект фіксує тест `BookingValidatorTest#checkInBeforeTodayIsRejected` — **2 червоні виконання**: рядки `"-1, 2"` (заїзд учора, виїзд за 2 дні) і `"-1, 0"` (заїзд учора, виїзд сьогодні). Рядок `"-5, -3"` (обидві дати в минулому) зелений. Над методом — коментар `// BUG-M02 (Issue #8)`. Тест залишено «червоним», без `@Disabled` і без зміни очікуваного значення. Один дефект — один звіт.

## Steps to reproduce
1. `mvn test -Dtest=BookingValidatorTest`
2. Або вручну:
   ```java
   BookingValidator validator = new BookingValidator();
   LocalDate today = LocalDate.of(2026, 9, 8);
   validator.validate(LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 10), 1, 2, today); // заїзд учора
   validator.validate(LocalDate.of(2026, 9, 7), LocalDate.of(2026, 9, 8), 1, 2, today);  // заїзд учора, виїзд сьогодні
   ```

## Actual result
| Виклик | Фактично |
|---|---|
| `validate(2026-09-07, 2026-09-10, 1, 2, 2026-09-08)` | `[]` |
| `validate(2026-09-07, 2026-09-08, 1, 2, 2026-09-08)` | `[]` |

Тест падає: `Очікувалась помилка CHECK_IN_IN_PAST, отримано: [] ==> expected: <true> but was: <false>`.

## Expected result
| Виклик | Очікувано (BL-10) |
|---|---|
| `validate(2026-09-07, 2026-09-10, 1, 2, 2026-09-08)` | `[CHECK_IN_IN_PAST]` |
| `validate(2026-09-07, 2026-09-08, 1, 2, 2026-09-08)` | `[CHECK_IN_IN_PAST]` |

Виправлення: у `BookingValidator.validate` замінити `checkOut.isBefore(today)` на `checkIn.isBefore(today)`.

## Environment
- Модуль **staynow v1** (1.0.0), `src/main/java/ua/kpi/tzyapz/staynow/BookingValidator.java`, метод `validate(checkIn, checkOut, guests, capacity, today)`.
- Команда: `mvn test -Dtest=BookingValidatorTest`. JDK 17 (Temurin), Apache Maven 3.9.x, JUnit Jupiter 5.10.2.
- Тест-артефакт: `src/test/java/ua/kpi/tzyapz/staynow/BookingValidatorTest.java`.

## Attachments
- `docs/sprint4/bug-reports/attachments/BUG-M02-surefire.txt` — копія `target/surefire-reports/ua.kpi.tzyapz.staynow.BookingValidatorTest.txt` (той самий файл, що для BUG-M01; додати після прогону на main).
- Скріншот падіння тесту — додати при реєстрації Issue.
