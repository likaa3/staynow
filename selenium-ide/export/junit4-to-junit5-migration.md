# Міграція чернетки експорту Selenium IDE з JUnit 4 на JUnit 5

Файл-джерело: `selenium-ide/export/StayNowIdeExportTest.java` — сирий експорт `staynow.side`
(Export → Java JUnit, генерує код у стилі JUnit 4) для demo https://automationintesting.online.
У проєкті підключено JUnit Jupiter 5.10, тому перед перенесенням у
`src/test/java/ua/kpi/tzyapz/staynow/ui/` виконуються такі заміни (відповідно до розділу 6
`README.md` і чек-листа §5.3). Готові адаптовані тести: `StayNowHomePageTest`,
`StayNowAdminAuthTest` (Page Object: `StayNowHomePage`, `StayNowAdminLoginPage`).

## 1. Імпорти

| Було (експорт IDE, JUnit 4) | Стало (JUnit 5) |
|---|---|
| `package com.example;` | `package ua.kpi.tzyapz.staynow.ui;` |
| `import org.junit.Test;` | `import org.junit.jupiter.api.Test;` |
| `import org.junit.Before;` | `import org.junit.jupiter.api.BeforeEach;` |
| `import org.junit.After;` | `import org.junit.jupiter.api.AfterEach;` |
| `import static org.junit.Assert.*;` | `import static org.junit.jupiter.api.Assertions.*;` |
| `import static org.hamcrest.CoreMatchers.is;` (Hamcrest) | видалити — замінюється на `assertEquals` |
| невикористані: `FirefoxDriver`, `RemoteWebDriver`, `Actions`, `Alert`, `Keys`, `Select`, `HashMap` | видалити |
| — | `import org.junit.jupiter.api.DisplayName;`, `import org.junit.jupiter.api.Tag;` |

## 2. Анотації життєвого циклу

| Було | Стало |
|---|---|
| `@Before public void setUp()` | `@BeforeEach void openBrowser()` — створює `ChromeDriver` з `ChromeOptions` |
| `@After public void tearDown()` | `@AfterEach void closeBrowser()` — обов'язковий `driver.quit()` |
| `driver = new ChromeDriver();` | `driver = new ChromeDriver(options);`, де `options.addArguments("--headless=new", "--window-size=1280,900")` (headless керується через `-Dheadless=false`) |

## 3. Перевірки

| Було | Стало |
|---|---|
| `assertThat(x, is("текст"))` (Hamcrest, JUnit 4) | `assertEquals("текст", x)` — статичний метод JUnit 5 `Assertions` |
| `List<WebElement> e = driver.findElements(...); assert(e.size() > 0);` (голе `assert`) | `assertTrue(e.size() > 0, "повідомлення")` або `assertAll(...)` для групових перевірок |
| `driver.findElement(...).getText();` без перевірки (запис «verify text») | явна перевірка: `assertEquals("", …)` / `assertTrue(text.contains(…))` |

## 4. Очікування

| Було | Стало |
|---|---|
| `Thread.sleep(timeout)` / заглушка `waitForWindow` | видалити; чекати конкретний стан:
`new WebDriverWait(driver, Duration.ofSeconds(15)).until(ExpectedConditions.visibilityOfElementLocated(...))` |
| команда `wait for element visible` → `wait.until(...)` | та сама бібліотека `WebDriverWait` + `ExpectedConditions` (або умови-лямбди) |
| неявні очікування `driver.manage().timeouts().implicitlyWait(...)` | ЗАБОРОНЕНО чек-листом — не використовувати взагалі |
| негативне очікування («подяка не з'явилась») | явне очікування з перехопленням `TimeoutException` — без `Thread.sleep` (див. `StayNowHomePage.waitThanksAppeared`) |

## 5. Локатори

| Було | Стало |
|---|---|
| абсолютні XPath (`/html/body/...`) | ЗАБОРОНЕНО чек-листом — лише `By.id`, `By.name`, `By.cssSelector`, зокрема атрибути `data-testid` (`ContactName`, `ContactEmail`, `ContactPhone`, `ContactSubject`, `ContactDescription`) |
| `xpath=//option[. = '2']` | `new Select(el).selectByValue("2")` — у нових сценаріях select-ів немає, але правило загальне |

## 6. Теги та читабельність

| Було | Стало |
|---|---|
| — | `@Tag("ui")` над класом (інакше тест потрапить у дефолтний прогін `mvn test`) |
| `void tc001tc003ГоловнаСторінкаИКарткиНомерів()` | `@DisplayName("TC-001: головна сторінка містить назву готелю…")` над кожним `@Test` — ID кейсу спринта 3 |
| кириличні імена методів з експорту | латинські імена у стилі camelCase (`homePageShowsHotelNameBookNowAndMenu`) |
| дублювання локаторів у кожному тесті | Page Object: `StayNowHomePage` / `StayNowAdminLoginPage` зберігають `By`, тести викликають методи дій |

## 7. Особливості demo (важливо для відтворюваності)

| Особливість | Як оброблено у фінальних тестах |
|---|---|
| програмний скрол demo (`window.scrollY` завжди 0) | клік по елементах нижче вікна перегляду — `scrollIntoView` + JS-клік (`StayNowHomePage.clickBelowFold`); для елементів у вікні — звичайний `click()` |
| react-datepicker у віджеті дат (текстові поля `dd/MM/yyyy`) | значення замінюються `Ctrl+A` + набір дати (`StayNowHomePage.checkAvailability`); дати — від `LocalDate.now()` |
| валидаційні помилки контактної форми не рендеряться (demo v2.2) | для TC-021/TC-022 перевіряється спек-поведінка «не надіслано»: подяка не з'явилась (негативне явне очікування), форма лишилась |
| Logout повертає на головну, а не на форму входу | TC-025 перевіряє завершення сесії: URL — головна сторінка, повторний `/admin` показує форму входу |

## 8. Порядок адаптації (чек-лист)

1. Скопіювати чернетку у `src/test/java/ua/kpi/tzyapz/staynow/ui/`, перейменувати клас.
2. Виконати заміни імпортів та анотацій (пункти 1–2).
3. Переписати перевірки на `Assertions` (пункт 3), згрупувати багатовимірні — у `assertAll`.
4. Замінити всі `Thread.sleep`/`pause` на `WebDriverWait` + `ExpectedConditions` (пункт 4).
5. Нормалізувати локатори на `By.id` / `By.cssSelector` + `data-testid` (пункт 5).
6. Додати `@Tag("ui")` і `@DisplayName("TC-xx: ...")` (пункт 6).
7. Перевірити запуск: `mvn test -DexcludedGroups=none -Dtest=<Клас>` (потрібен Chrome і доступ до demo).
