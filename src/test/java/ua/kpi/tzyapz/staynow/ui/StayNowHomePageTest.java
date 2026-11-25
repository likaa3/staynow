package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тести головної сторінки demo-застосунку «СтейНау» (https://automationintesting.online).
 * Кожен тест автоматизує реальний тест-кейс спринта 3 (автор — Чаплюк Д.), ID кейсу — у
 * {@code @DisplayName} та в коментарі над методом. Запуск: {@code mvn test -DexcludedGroups=none}.
 */
@Tag("ui")
@DisplayName("СтейНау — головна сторінка: меню, номери, перевірка доступності, контактна форма")
class StayNowHomePageTest extends UiTestBase {

    /** Кейс TC-001 (F-10): назва, опис, «Book Now», меню з шести пунктів у заданому порядку. */
    @Test
    @DisplayName("TC-001: головна сторінка містить назву готелю, кнопку Book Now і меню з 6 пунктів")
    void homePageShowsHotelNameBookNowAndMenu() {
        // arrange
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();

        // act + assert (дані — за ОР кейсу TC-001: назва готелю demo, кнопка Book Now, порядок пунктів)
        assertAll(
                () -> assertEquals("Welcome to Shady Meadows B&B", page.heroTitle(), "назва готелю в hero"),
                () -> assertEquals("Book Now", page.heroBookNowText(), "кнопка Book Now у hero"),
                () -> assertEquals(
                        List.of("Rooms", "Booking", "Amenities", "Location", "Contact", "Admin"),
                        page.menuItems(), "пункти меню в порядку кейсу TC-001")
        );
    }

    /** Кейс TC-002 (F-10): пункти меню ведуть на якорі головної, «Admin» відкриває /admin. */
    @Test
    @DisplayName("TC-002: меню Rooms/Booking/Contact — якорі головної, Admin відкриває /admin з формою Login")
    void menuAnchorsAndAdminOpenLoginPage() {
        // arrange
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();
        List<String> hrefs = page.menuHrefs();

        // act
        StayNowAdminLoginPage adminPage = page.openAdminViaMenu().waitLoginForm();

        // assert
        assertAll(
                () -> assertTrue(hrefs.get(0).endsWith("/#rooms"), "Rooms — якорь #rooms: " + hrefs.get(0)),
                () -> assertTrue(hrefs.get(1).endsWith("/#booking"), "Booking — якорь #booking: " + hrefs.get(1)),
                () -> assertTrue(hrefs.get(4).endsWith("/#contact"), "Contact — якорь #contact: " + hrefs.get(4)),
                () -> assertTrue(hrefs.get(5).endsWith("/admin"), "Admin веде на /admin: " + hrefs.get(5)),
                () -> assertTrue(driver.getCurrentUrl().endsWith("/admin"), "поточна адреса — /admin"),
                () -> assertEquals("Login", adminPage.loginHeaderText(), "заголовок сторінки входу")
        );
    }

    /** Кейс TC-003 (F-20): розділ «Our Rooms» — картки номерів з типом, ціною «£N per night», Book now. */
    @Test
    @DisplayName("TC-003: Our Rooms показує картки Single/Double/Suite з ціною «£N per night» і кнопкою Book now")
    void ourRoomsSectionShowsRoomCards() {
        // arrange
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();

        // act + assert (ОР TC-003: картки номерів; ціна — у форматі «£N per night», значення demo не фіксуємо)
        assertAll(
                () -> assertEquals(3, page.roomCardCount(), "карток номерів на головній"),
                () -> assertEquals(List.of("Single", "Double", "Suite"), page.roomNames(), "типи номерів"),
                () -> assertTrue(page.roomPrices().stream()
                                .allMatch(p -> p.matches("£\\d+ per night")),
                        "ціна у форматі «£N per night», фактично: " + page.roomPrices()),
                () -> assertTrue(page.everyRoomCardHasBookLink(), "у кожній картці є кнопка Book now")
        );
    }

    /** Кейс TC-004 (F-30): «Check Availability» на вільні дати показує вільні номери. */
    @Test
    @DisplayName("TC-004: Check Availability від обраного інтервалу оновлює перелік — картки "
            + "Single/Double/Suite присутні, посилання ведуть на ці дати")
    void checkAvailabilityUpdatesRoomLinks() {
        // arrange: дати рахуємо від поточної (BL-10 забороняє заїзд у минулому); інтервал у майбутньому.
        // Формат поля дати demo — dd/MM/yyyy: фіксований формат react-datepicker, який не залежить
        // від locale браузера (зауваження ТА п. 3.1)
        LocalDate checkIn = LocalDate.now().plusDays(30);
        LocalDate checkOut = checkIn.plusDays(2);
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();

        // act
        page.checkAvailability(checkIn, checkOut);

        // assert (ОР TC-004: перелік оновлено, присутні картки «Single», «Double», «Suite»
        // — додано за зауваженням ТА п. 3.1; картки ведуть на reservation з датами РРРР-ММ-ДД)
        String expectedCheckIn = checkIn.format(DateTimeFormatter.ISO_DATE);
        String expectedCheckOut = checkOut.format(DateTimeFormatter.ISO_DATE);
        assertAll(
                () -> assertEquals(3, page.roomCardCount(), "перелік номерів після перевірки доступності"),
                () -> assertEquals(List.of("Single", "Double", "Suite"), page.roomNames(),
                        "присутні картки «Single», «Double», «Suite» (зауваження ТА п. 3.1)"),
                () -> assertTrue(page.roomBookHrefs().stream()
                                .allMatch(h -> h.contains("checkin=" + expectedCheckIn)
                                        && h.contains("checkout=" + expectedCheckOut)),
                        "усі картки ведуть на обраний інтервал: " + page.roomBookHrefs())
        );
    }

    /** Кейс TC-020 (F-70, GUI-85): контактна форма з коректними даними — подяка з іменем і темою. */
    @Test
    @DisplayName("TC-020: коректна контактна форма — подяка з іменем і темою, полів форми немає")
    void contactFormWithValidDataShowsThanks() {
        // arrange
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();
        page.fillContactForm("Test User", "tc.tester@example.com", "01234567890",
                "TC020 subject", "Message text for contact form check");

        // act
        page.submitContactForm();
        boolean thanksShown = page.waitThanksAppeared(10);

        // assert (ОР TC-020: текст подяки містить ім'я «Test User» і тему «TC020 subject»)
        assertAll(
                () -> assertTrue(thanksShown, "подяка з'явилась після надсилання"),
                () -> assertTrue(page.contactCardText().contains("Thanks for getting in touch Test User!"),
                        "подяка з іменем: " + page.contactCardText()),
                () -> assertTrue(page.contactCardText().contains("TC020 subject"),
                        "у подяці названо тему: " + page.contactCardText())
        );
    }

    /** Кейс TC-021 (F-70): тема з 4 символів (нижня межа 5 мінус 1) відхиляється. */
    @Test
    @DisplayName("TC-021: тема із 4 символів — повідомлення не надіслано, форма лишається "
            + "(перелік помилок над «Submit» demo не показує — не перевіряється, дефект demo)")
    void shortSubjectIsRejected() {
        // arrange
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();
        page.fillContactForm("Test User", "tc.tester@example.com", "01234567890",
                "Test", "Message text for contact form check");

        // act
        page.submitContactForm();
        boolean thanksShown = page.waitThanksAppeared(5);

        // assert (ОР TC-021: повідомлення не надіслано — подяки немає, форма лишається.
        // Зауваження ТА п. 3.2: частину ОР «над Submit перелік помилок про поле Subject»
        // demo не рендерить — не перевіряється, дефект demo; реєструється в трекері
        // з міткою demo)
        assertAll(
                () -> assertFalse(thanksShown, "подяки бути не має — повідомлення не надіслано"),
                () -> assertTrue(page.contactFormStillShown(), "форма лишається на сторінці"),
                () -> assertEquals("Test", page.contactSubjectValue(),
                        "введене значення теми не змінилось")
        );
    }

    /** Кейс TC-022 (F-70): повідомлення з 19 символів (нижня межа 20 мінус 1) відхиляється. */
    @Test
    @DisplayName("TC-022: повідомлення із 19 символів — повідомлення не надіслано, форма лишається "
            + "(перелік помилок над «Submit» demo не показує — не перевіряється, дефект demo)")
    void shortMessageIsRejected() {
        // arrange: рівно 19 символів (граничне a−1 для діапазону 20–2000)
        String nineteenChars = "1234567890123456789";
        assertEquals(19, nineteenChars.length(), "тестові дані — рівно 19 символів");
        StayNowHomePage page = new StayNowHomePage(driver, wait).open();
        page.fillContactForm("Test User", "tc.tester@example.com", "01234567890",
                "TC022 subject", nineteenChars);

        // act
        page.submitContactForm();
        boolean thanksShown = page.waitThanksAppeared(5);

        // assert (ОР TC-022: повідомлення не надіслано — подяки немає, форма лишається.
        // Зауваження ТА п. 3.2: частину ОР «над Submit перелік помилок про поле Message»
        // demo не рендерить — не перевіряється, дефект demo)
        assertAll(
                () -> assertFalse(thanksShown, "подяки бути не має — повідомлення не надіслано"),
                () -> assertTrue(page.contactFormStillShown(), "форма лишається на сторінці")
        );
    }
}
