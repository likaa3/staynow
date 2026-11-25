# BUG-M03 — `PriceCalculator.isWeekendNight` вважає вихідними суботу й неділю замість п'ятниці й суботи

> Звіт для перенесення в GitHub Issues. Мітки: `bug`, `module`, `severity:major`, `priority:high`, `sprint:4`.
> **GitHub Issue:** #4 — створити і вписати реальний номер. На момент підготовки звіту зайняті:
> #1 — PR з інфраструктурою, #2 — BUG-M05, #3 — BUG-M06, #4 — BUG-M04. Отже M03 отримає №5
> або більший, залежно від того, чи Бєлянцева А. встигне раніше створити Issues для BUG-M01/M02.
> Перевірити фактичний номер у GitHub Issues перед заповненням цього поля і коментаря в тесті.
> Шаблон — §4.9 методички. Виконавець: Вігура С. (КТ).

## ID
`BUG-M03` (Issue #4 — звірити з GitHub Issues перед публікацією)

## Title
Надбавка 15 % за вихідну ніч застосовується до ночі з нд (помилково) і не застосовується до ночі з пт (помилково)

## Severity / Priority
- **Severity:** Major — систематично неправильна сума за будь-яке бронювання, що містить п'ятницю або неділю.
- **Priority:** High — порушено явну вимогу BL-30; виправлення локальне (одна умова).

## Description
Вимога **BL-30** (SRS_04 «СтейНау», розд. 5; продубльована в `README.md` модуля): ночі з п'ятниці на суботу та з суботи на неділю — надбавка 15 %, тобто «вихідними» є ночі, що **починаються** в п'ятницю та суботу.

У `PriceCalculator.isWeekendNight` умова `dow == DayOfWeek.SATURDAY || dow == DayOfWeek.SUNDAY` вважає вихідними суботу й неділю. Правильно: `DayOfWeek.FRIDAY` і `DayOfWeek.SATURDAY`. Наслідок виходить за межі `isWeekendNight`: помилковий результат успадковують `nightPrice` і `total` (BL-50). Один дефект — один звіт.

Дефект фіксують тести `PriceCalculatorSeasonTest#weekendNightsAreFridayAndSaturday`, `#weekendSurchargeAppliedOnFridayAndSaturdayOnly`, `#seasonAndWeekendCombine` та `#totalAcrossSeasonBoundaryMayToJune` — сумарно **9 червоних виконань** (параметризовані рядки пт/нд у кожному методі; підтверджено рецензентом Шкарупою М., значення узгоджені). Над ними — коментар `// BUG-M03 (Issue #4)`. Тести залишено «червоними».

## Steps to reproduce
1. `mvn test -Dtest=PriceCalculatorSeasonTest`
2. Або вручну:
   ```java
   PriceCalculator calc = new PriceCalculator();
   calc.isWeekendNight(LocalDate.of(2026, 9, 18));                        // п'ятниця
   calc.isWeekendNight(LocalDate.of(2026, 9, 20));                        // неділя
   calc.nightPrice(new BigDecimal("100"), LocalDate.of(2026, 9, 18));     // п'ятниця, звичайний сезон
   calc.nightPrice(new BigDecimal("100"), LocalDate.of(2026, 9, 20));     // неділя, звичайний сезон
   ```

## Actual result
| Виклик | Фактично |
|---|---|
| `isWeekendNight(2026-09-18)` (пт) | `false` |
| `isWeekendNight(2026-09-20)` (нд) | `true` |
| `nightPrice(100, 2026-09-18)` | `100.00` |
| `nightPrice(100, 2026-09-20)` | `115.00` |

## Expected result
| Виклик | Очікувано (BL-30) |
|---|---|
| `isWeekendNight(2026-09-18)` | `true` |
| `isWeekendNight(2026-09-20)` | `false` |
| `nightPrice(100, 2026-09-18)` | `115.00` |
| `nightPrice(100, 2026-09-20)` | `100.00` |

Виправлення: у `isWeekendNight` замінити `DayOfWeek.SUNDAY` на `DayOfWeek.FRIDAY`.

## Environment
- Модуль **staynow v1** (1.0.0), `src/main/java/ua/kpi/tzyapz/staynow/PriceCalculator.java`, метод `isWeekendNight`.
- Команда: `mvn test -Dtest=PriceCalculatorSeasonTest`. JDK 17 (Temurin), Apache Maven 3.9.x, JUnit Jupiter 5.10.2.
- Тест-артефакт: `src/test/java/ua/kpi/tzyapz/staynow/PriceCalculatorSeasonTest.java`.

## Attachments
- `docs/sprint4/bug-reports/attachments/BUG-M03-surefire.txt` — копія `target/surefire-reports/ua.kpi.tzyapz.staynow.PriceCalculatorSeasonTest.txt` (додати після реального прогону на main).
- Скріншот падіння — додати при реєстрації Issue.
