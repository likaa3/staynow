# Протокол запуску тестів — Спринт 4 («СтейНау», модуль v1)

Веде: Вігура С. (КТ), задача 10.2. **Увага:** протокол Чаплюка Д. раніше теж називався
`surefire-summary.md` — перейменовано на `docs/sprint4/ui-stability-runs.md`, щоб не було
двох файлів з однаковою назвою. Цей файл — командний зведений протокол.

## Як читати цей протокол

**Очікуваний результат команди `mvn clean test` — `BUILD FAILURE`, і це правильно.**
У модулі v1 навмисно закладено 6 дефектів (задача 5), на кожен написано «червоний» тест.
За правилом команди: *«Червоні тести дефектів не вимикати й не “підганяти”»*. `BUILD FAILURE`
означає «тести зафіксували закладені дефекти», а не «збірка зламана».

**Failures проти Errors — розрізняти, не зводити все до Failures.** Тест
`AvailabilityTest#bookBackToBackRegistersSecondStay` падає не через `assert`, а через те, що
`book()` кидає `IllegalStateException` там, де мало пройти успішне бронювання «спина до спини» —
це той самий дефект BUG-M05, тільки JUnit рахує таке падіння як `Error`, а не `Failure` (виняток
вилетів із самого виклику, а не з перевірки умови). Виправляти тест не обов'язково. У звітах і
README писати точно **«Failures: 5, Errors: 1»**, а не «Failures: 6» — інакше цифра не збіжиться
з реальним виводом Maven.

Критерій готовності інкременту:
1. сума Failures + Errors відповідає кількості підтверджених закладених дефектів;
2. кожен проблемний тест має коментар `// BUG-M0x` і відповідний звіт з реальним номером Issue;
3. немає неочікуваних падінь, що не відповідають жодному BUG-M01…M06.

## Команда запуску

```
mvn clean test
```

**Виправлення (дякую Шкарупі за зауваження):** команда `mvn test -Dgroups=ui` — неробоча, pom.xml
виключає тег `ui` за замовчуванням через `excludedGroups`. Правильно:

```
mvn test -DexcludedGroups=none
```

## Стан на 25.11.2026 (проміжний прогін Шкарупи М., після оновленого `AvailabilityTest`)

| Поле | Значення |
|---|---|
| Команда | `mvn clean test` |
| Підсумок Maven | `Tests run: 67, Failures: 5, Errors: 1, Skipped: 0` |
| Результат збірки | `BUILD FAILURE` (очікувано) |

Цей прогін **не містить** ще двох класів: `PriceCalculatorSeasonTest` (мій, BL-30/BL-40 —
схвалено, заливаю) і `BookingValidatorTest` (Бєлянцева А., BL-10 — ще не залито). Після заливки
обох числа зміняться: з'явиться ще +9 червоних виконань від BUG-M03 (у моєму класі) і, за
нормативом, ще дефекти BUG-M01/BUG-M02 з `BookingValidatorTest`.

### Список FAILED/ERROR із ID дефектів (станом на проміжний прогін)

| Клас.метод | Тип | Дефект | Issue |
|---|---|---|---|
| `PriceCalculatorTotalTest.nightPriceRoundsHalfUp` | Failure | BUG-M04 | #6 |
| `AvailabilityTest.checkInOnPreviousGuestCheckOutDayIsAllowed` | Failure | BUG-M05 | #2 |
| `AvailabilityTest.checkOutOnNextGuestCheckInDayIsAllowed` | Failure | BUG-M05 | #2 |
| `AvailabilityTest.cancellationFeeAtBoundaries[7,0.00]` | Failure | BUG-M06 | #3 |
| `AvailabilityTest.bookBackToBackRegistersSecondStay` | **Error** (`IllegalStateException`) | BUG-M05 (побічний ефект) | #2 |
| `AvailabilityTest.refundAtBoundaries[2]` | Failure | BUG-M06 | #3 |

### Після повної заливки (фінальний прогін — заповнити)

| Поле | Значення |
|---|---|
| Команда | `mvn clean test` |
| Підсумок Maven | `Tests run: __, Failures: __, Errors: __, Skipped: 0` |
| Результат збірки | `BUILD FAILURE` (очікувано) |

Очікується додатково: 9 Failure від `PriceCalculatorSeasonTest` (BUG-M03), плюс падіння
`BookingValidatorTest` (BUG-M01, BUG-M02) — кількість уточнити після заливки Бєлянцевої А.

## Повторний прогін (для стабільності, задача 7.4)

Заповнюється аналогічно — мета показати, що список FAILED/ERROR не змінюється між прогонами.
UI-прогони (headless) і їх стабільність — окремо в `docs/sprint4/ui-stability-runs.md` (Чаплюк Д.).

## Вкладення
- Повний вивід консолі фінального прогону: `docs/sprint4/surefire-console-final.txt`
- Звіти Surefire по кожному класу → релевантні копіювати в `docs/sprint4/bug-reports/attachments/`
