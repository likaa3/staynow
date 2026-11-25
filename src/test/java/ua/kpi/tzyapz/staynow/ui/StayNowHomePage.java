package ua.kpi.tzyapz.staynow.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Page Object головної сторінки demo-застосунку «СтейНау» (https://automationintesting.online,
 * demo зі спринта 3; продукт «Шейді Медоуз B&B» — restful-booker-platform v2.2).
 *
 * Патерн Page Object: локатори — {@code public static final By} (id, data-testid, css-атрибути),
 * а не {@link WebElement} — елемент шукається щоразу заново, що уникає
 * {@code StaleElementReferenceException}. Жодних абсолютних XPath.
 *
 * Технічні особливості demo (виявлені під час автоматизації, задокументовані для рев'ю):
 *  - сторінка використовує програмний скрол (window.scrollY завжди 0), тому звичайний
 *    WebDriver-клік по елементах нижче вію порта перехоплюється (ElementClickInterceptedException);
 *    для таких елементів застосовується {@link #clickBelowFold(By)}: scrollIntoView + JS-клік;
 *  - поля дат віджета — react-datepicker (type=text, формат dd/MM/yyyy): значення замінюють
 *    через {@code Ctrl+A} + набір дати, інакше текст дописується у кінець.
 */
public class StayNowHomePage {

    public static final String BASE_URL = "https://automationintesting.online/";

    /* ---------- Локатори (тільки By; id / data-testid / стійкі css) ---------- */

    public static final By HERO_TITLE = By.cssSelector(".hero h1");
    public static final By HERO_BOOK_NOW = By.cssSelector(".hero a.btn");
    public static final By NAV_LINKS = By.cssSelector(".navbar-nav .nav-link");
    public static final By NAV_ADMIN = By.cssSelector(".navbar-nav a[href='/admin']");
    public static final By BOOKING_SECTION = By.cssSelector("#booking");
    public static final By DATE_INPUTS = By.cssSelector("#booking .react-datepicker__input-container input");
    public static final By CHECK_AVAILABILITY_BUTTON = By.cssSelector("#booking button.btn-primary");
    public static final By ROOM_CARDS = By.cssSelector(".room-card");
    public static final By ROOM_CARD_TITLES = By.cssSelector(".room-card .card-title");
    public static final By ROOM_CARD_PRICES = By.cssSelector(".room-card .card-footer .fw-bold");
    public static final By ROOM_BOOK_LINKS = By.cssSelector(".room-card a[href*='reservation']");
    public static final By CONTACT_NAME = By.id("name");
    public static final By CONTACT_EMAIL = By.id("email");
    public static final By CONTACT_PHONE = By.id("phone");
    public static final By CONTACT_SUBJECT = By.id("subject");
    public static final By CONTACT_MESSAGE = By.id("description");
    public static final By CONTACT_SUBMIT = By.cssSelector("#contact button.btn-primary");
    public static final By CONTACT_CARD_BODY = By.cssSelector("#contact .card-body");

    private static final DateTimeFormatter DISPLAY_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public StayNowHomePage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /* ---------- Навігація ---------- */

    /** Відкриває demo і чекає рендерингу SPA (готовність DOM + поява карток номерів). */
    public StayNowHomePage open() {
        driver.get(BASE_URL);
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.presenceOfElementLocated(ROOM_CARDS));
        return this;
    }

    /** Звичайний WebDriver-клік (для елементів у вікні перегляду: навбар, форма входу адмінки). */
    public StayNowHomePage click(By locator) {
        wait.until(ExpectedConditions.elementToBeClickable(locator)).click();
        return this;
    }

    /**
     * Клік по елементу нижче вікна перегляду: demo не прокручується програмно
     * (кастомний smooth-scroll), тому звичайний клік перехоплюється —
     * виконується scrollIntoView і клік через JavaScript.
     */
    public StayNowHomePage clickBelowFold(By locator) {
        WebElement element = wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", element);
        return this;
    }

    /** Вводить текст у поле з явним очікуванням видимості. */
    public StayNowHomePage type(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        element.clear();
        element.sendKeys(value);
        return this;
    }

    /* ---------- Віджет «Check Availability & Book Your Stay» (TC-004) ---------- */

    /**
     * Заповнює дати заїзду/виїзду (від {@code LocalDate.now()}), натискає
     * «Check Availability» і чекає, поки посилання «Book now» карток номерів
     * оновляться на обрані дати (параметри checkin/checkout — РРРР-ММ-ДД).
     */
    public StayNowHomePage checkAvailability(LocalDate checkIn, LocalDate checkOut) {
        List<WebElement> inputs = wait.until(
                ExpectedConditions.numberOfElementsToBe(DATE_INPUTS, 2));
        WebElement checkInField = inputs.get(0);
        WebElement checkOutField = inputs.get(1);
        String isoCheckIn = checkIn.format(DateTimeFormatter.ISO_DATE);
        String isoCheckOut = checkOut.format(DateTimeFormatter.ISO_DATE);

        checkInField.sendKeys(Keys.CONTROL + "a");
        checkInField.sendKeys(checkIn.format(DISPLAY_DATE));
        checkOutField.sendKeys(Keys.CONTROL + "a");
        checkOutField.sendKeys(checkOut.format(DISPLAY_DATE));

        clickBelowFold(CHECK_AVAILABILITY_BUTTON);
        // перший «Book now» (картка Single) має вести на обрані дати (РРРР-ММ-ДД у параметрах)
        wait.until(d -> {
            String href = d.findElement(ROOM_BOOK_LINKS).getAttribute("href");
            return href.contains("checkin=" + isoCheckIn) && href.contains("checkout=" + isoCheckOut);
        });
        return this;
    }

    /* ---------- Картки номерів (TC-003) ---------- */

    public List<String> roomNames() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ROOM_CARD_TITLES)).stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    /** Ціни у форматі «£N per night» (зайві пробіли demo нормалізуються). */
    public List<String> roomPrices() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ROOM_CARD_PRICES)).stream()
                .map(e -> e.getText().replaceAll("\\s+", " ").trim())
                .collect(Collectors.toList());
    }

    public int roomCardCount() {
        return driver.findElements(ROOM_CARDS).size();
    }

    /** Посилання «Book now» усіх карток (href з параметрами checkin/checkout). */
    public List<String> roomBookHrefs() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(ROOM_BOOK_LINKS)).stream()
                .map(e -> e.getAttribute("href"))
                .collect(Collectors.toList());
    }

    public boolean everyRoomCardHasBookLink() {
        List<WebElement> cards = driver.findElements(ROOM_CARDS);
        for (WebElement card : cards) {
            if (card.findElements(By.cssSelector("a[href*='reservation']")).isEmpty()) {
                return false;
            }
        }
        return !cards.isEmpty();
    }

    /* ---------- Меню і шапка (TC-001, TC-002) ---------- */

    public List<String> menuItems() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(NAV_LINKS)).stream()
                .map(e -> e.getText().trim())
                .collect(Collectors.toList());
    }

    public List<String> menuHrefs() {
        return wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(NAV_LINKS)).stream()
                .map(e -> e.getAttribute("href"))
                .collect(Collectors.toList());
    }

    public String heroTitle() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(HERO_TITLE)).getText().trim();
    }

    public String heroBookNowText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(HERO_BOOK_NOW)).getText().trim();
    }

    /** Переходить у адмінпанель пунктом меню «Admin». */
    public StayNowAdminLoginPage openAdminViaMenu() {
        click(NAV_ADMIN);
        return new StayNowAdminLoginPage(driver, wait);
    }

    /* ---------- Контактна форма (TC-020, TC-021, TC-022) ---------- */

    public StayNowHomePage fillContactForm(String name, String email, String phone,
                                           String subject, String message) {
        type(CONTACT_NAME, name);
        type(CONTACT_EMAIL, email);
        type(CONTACT_PHONE, phone);
        type(CONTACT_SUBJECT, subject);
        type(CONTACT_MESSAGE, message);
        return this;
    }

    /** Надсилає контактну форму (кнопка нижче вікна перегляду — JS-клік). */
    public StayNowHomePage submitContactForm() {
        clickBelowFold(CONTACT_SUBMIT);
        return this;
    }

    /**
     * Явне очікування подяки протягом {@code seconds}; {@code false} = подяка не з'явилась
     * (повідомлення не надіслано). Використовується і як негативне очікування
     * (TimeoutException перехоплюється — без Thread.sleep).
     */
    public boolean waitThanksAppeared(int seconds) {
        try {
            new WebDriverWait(driver, java.time.Duration.ofSeconds(seconds))
                    .until(ExpectedConditions.textToBePresentInElementLocated(CONTACT_CARD_BODY, "Thanks"));
            return true;
        } catch (TimeoutException expected) {
            return false;
        }
    }

    public String contactCardText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(CONTACT_CARD_BODY)).getText();
    }

    public boolean contactFormStillShown() {
        return !driver.findElements(CONTACT_NAME).isEmpty()
                && !driver.findElements(CONTACT_SUBMIT).isEmpty();
    }

    public String contactSubjectValue() {
        return driver.findElement(CONTACT_SUBJECT).getAttribute("value");
    }
}
