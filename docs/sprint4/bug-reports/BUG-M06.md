# BUG-M06 — Availability.cancellationFee утримує 50 % рівно за 7 днів до заїзду

> **GitHub Issue: #3** · Мітки: `bug`, `module`, `severity:major`, `priority:high`, `sprint:4`
> Шаблон звіту — §4.9 методичних вказівок Спринту 4. Виконавець: Шкарупа М. (СМ). Рецензент: Гетьман Є. (23.11.2026).

| Поле | Значення |
|---|---|
| **ID** | BUG-M06 (Issue #3) |
| **Title** | Скасування за 7 днів до заїзду не безкоштовне: утримується 50 % вартості |
| **Severity** | Major — гостя помилково штрафують на суму до 50 % бронювання |
| **Priority** | High |
| **Description** | Порушення вимоги **BL-60**: «7 і більше днів до заїзду — плата 0». У `cancellationFee` умова `daysBeforeCheckIn > FREE_CANCELLATION_DAYS` має бути `>=`. Фіксують тести `AvailabilityTest.cancellationFeeAtBoundaries` (набір `"7,0.00"`, 3-й запуск) та `AvailabilityTest.refundAtBoundaries` (набір `"7,1000.00"`, 2-й запуск): `refund` залежить від `cancellationFee`, тому побічний ефект того самого дефекту. Верхня межа (8 днів) і нижні межі (6, 3, 2 дні) працюють правильно. |
| **Steps to reproduce** | 1. `new Availability().cancellationFee(new BigDecimal("1000.00"), 7)` 2. `new Availability().refund(new BigDecimal("1000.00"), 7)` |
| **Actual result** | Крок 1: `500.00`; крок 2: `500.00` |
| **Expected result** | Крок 1: `0.00`; крок 2: `1000.00` (BL-60) |
| **Environment** | Модуль «СтейНау» v1.0.0, JDK 17, JUnit 5.10.2. Команда: `mvn test -Dtest=AvailabilityTest`. Результат класу: `Tests run: 27, Failures: 4, Errors: 1` (з них BUG-M06: 2 Failure) |
| **Attachments** | `docs/sprint4/bug-reports/attachments/AvailabilityTest-surefire.txt` — копія звіту Surefire класу `AvailabilityTest` |

Один дефект — один звіт: тест `refundAtBoundaries` окремого звіту не потребує. Тест лишається «червоним» до виправлення у v2.
