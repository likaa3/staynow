# BUG-M01 — `BookingValidator.validate` відхиляє бронювання рівно на 30 ночей

> Звіт для GitHub Issues. Мітки: `bug`, `module`, `severity:major`, `priority:high`, `sprint:4`.
> **GitHub Issue: #7**
> Шаблон — спринт 3 (4.11), особливості звіту про дефект модуля — §4.9 методички спринта 4.
> Виконавець: Бєлянцева А. (ТА). Рецензент: Шкарупа М.

## ID
`BUG-M01` (GitHub Issue #7)

## Title
Бронювання рівно на 30 ночей відхиляється з помилкою `NIGHTS_OUT_OF_RANGE`, хоча BL-10 дозволяє 1..30 ночей

## Severity / Priority
- **Severity:** Major — основна функція бронювання відхиляє допустиме бронювання: гість не може забронювати номер на максимально дозволений строк.
- **Priority:** High — порушено явну межу вимоги BL-10; виправлення локальне (одна умова).

## Description
Вимога **BL-10** (SRS_04 «СтейНау», розд. 5; продубльована в `README.md` модуля): «ночей 1..30» — 30 ночей включно допустимі.

У `BookingValidator.validate` умова `nights < MIN_NIGHTS || nights >= MAX_NIGHTS` відхиляє вже значення 30 (`MAX_NIGHTS = 30`). Має бути `nights > MAX_NIGHTS`. Нижня межа (1 ніч) і значення поза межею (31) обробляються правильно.

Дефект фіксує тест `BookingValidatorTest#nightsAreAllowedFromOneToThirty`, рядок `"30, true"` — **1 червоне виконання**; рядки 1, 2, 29 (допустимо) і 31, 45 (відхиляється) зелені. Над методом — коментар `// BUG-M01 (Issue #7)`. Тест залишено «червоним», без `@Disabled` і без зміни очікуваного значення.

## Steps to reproduce
1. `mvn test -Dtest=BookingValidatorTest`
2. Або вручну:
   ```java
   BookingValidator validator = new BookingValidator();
   validator.validate(
           LocalDate.of(2026, 9, 15),   // заїзд
           LocalDate.of(2026, 10, 15),  // виїзд: рівно 30 ночей
           1, 2,                        // гостей 1, місткість 2
           LocalDate.of(2026, 9, 8));   // today
   ```

## Actual result
`validate(2026-09-15, 2026-10-15, 1, 2, 2026-09-08)` повертає `[NIGHTS_OUT_OF_RANGE]`.

Тест падає: `Ночей: 30, помилки: [NIGHTS_OUT_OF_RANGE] ==> expected: <false> but was: <true>`.

## Expected result
За BL-10 (ночей 1..30) — порожній список помилок `[]`: бронювання на 30 ночей допустиме.
Для 31 ночі — `[NIGHTS_OUT_OF_RANGE]` (так і працює зараз).

Виправлення: у `BookingValidator.validate` замінити `nights >= MAX_NIGHTS` на `nights > MAX_NIGHTS`.

## Environment
- Модуль **staynow v1** (1.0.0), `src/main/java/ua/kpi/tzyapz/staynow/BookingValidator.java`, метод `validate(checkIn, checkOut, guests, capacity, today)`.
- Команда: `mvn test -Dtest=BookingValidatorTest`. JDK 17 (Temurin), Apache Maven 3.9.x, JUnit Jupiter 5.10.2.
- Тест-артефакт: `src/test/java/ua/kpi/tzyapz/staynow/BookingValidatorTest.java`.

## Attachments
- `docs/sprint4/bug-reports/attachments/BUG-M01-surefire.txt` — копія `target/surefire-reports/ua.kpi.tzyapz.staynow.BookingValidatorTest.txt` (додати після прогону на main).
- Скріншот падіння тесту — додати при реєстрації Issue.
