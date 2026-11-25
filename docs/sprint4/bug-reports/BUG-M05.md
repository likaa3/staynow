# BUG-M05 — Availability.isAvailable вважає перетином виїзд і заїзд в один день

> **GitHub Issue: #2** · Мітки: `bug`, `module`, `severity:major`, `priority:high`, `sprint:4`
> Шаблон звіту — §4.9 методичних вказівок Спринту 4. Виконавець: Шкарупа М. (СМ). Рецензент: Гетьман Є. (23.11.2026).

| Поле | Значення |
|---|---|
| **ID** | BUG-M05 (Issue #2) |
| **Title** | Номер недоступний, якщо новий заїзд збігається з днем виїзду попереднього гостя (і навпаки) |
| **Severity** | Major — коректні бронювання «спина до спини» відхиляються, номер простоює |
| **Priority** | High |
| **Description** | Порушення вимоги **BL-70** (SRS_04 «СтейНау», розд. 5): номер вільний, якщо новий інтервал не перетинається з жодним існуючим бронюванням цього номера; інтервал проживання — напіввідкритий `[checkIn, checkOut)`, тому виїзд у день заїзду іншого гостя (і заїзд у день виїзду попереднього) дозволено. У `Availability.isAvailable` умова `boolean overlaps = !(checkOut.isBefore(s.checkIn()) \|\| checkIn.isAfter(s.checkOut()))` вважає рівність дат перетином. Коректно: перетин є лише тоді, коли `checkIn.isBefore(s.checkOut()) && checkOut.isAfter(s.checkIn())`. Дефект фіксують тести `AvailabilityTest.checkInOnPreviousGuestCheckOutDayIsAllowed` і `AvailabilityTest.checkOutOnNextGuestCheckInDayIsAllowed` (Failure), а також `AvailabilityTest.bookBackToBackRegistersSecondStay` (Error: `book()` кидає `IllegalStateException` на бронюванні «спина до спини» — той самий дефект). |
| **Steps to reproduce** | 1. `Availability a = new Availability();` 2. `a.book("101", LocalDate.of(2026, 9, 15), LocalDate.of(2026, 9, 17));` 3. `a.isAvailable("101", LocalDate.of(2026, 9, 17), LocalDate.of(2026, 9, 19));` 4. `a.isAvailable("101", LocalDate.of(2026, 9, 13), LocalDate.of(2026, 9, 15));` 5. `a.book("101", LocalDate.of(2026, 9, 17), LocalDate.of(2026, 9, 19));` |
| **Actual result** | Кроки 3 і 4 повертають `false`; крок 5 кидає `IllegalStateException` |
| **Expected result** | Кроки 3 і 4 повертають `true`; крок 5 реєструє бронювання (BL-70) |
| **Environment** | Модуль «СтейНау» v1.0.0, JDK 17, JUnit 5.10.2. Команда: `mvn test -Dtest=AvailabilityTest`. Результат класу: `Tests run: 27, Failures: 4, Errors: 1` (з них BUG-M05: 2 Failure + 1 Error) |
| **Attachments** | `docs/sprint4/bug-reports/attachments/AvailabilityTest-surefire.txt` — копія звіту Surefire класу `AvailabilityTest` |

Тест лишається «червоним» до виправлення у v2 (перевірка у спринті 5: closed / reopened).
