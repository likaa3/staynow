package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * UI-тести адміністративної частини demo «СтейНау» ({@code /admin}) за кейсами спринта 3:
 * TC-069, TC-023, TC-024, TC-025. Облікові дані demo — спільні (admin / password), як у
 * передумовах кейсів спринта 3. Кожен тест незалежний: власний браузер у @BeforeEach.
 */
@Tag("ui")
@DisplayName("СтейНау — адмінпанель: шапка входу, вхід, помилка входу, вихід")
class StayNowAdminAuthTest extends UiTestBase {

    /** Кейс TC-069 (GUI-70): шапка сторінки входу — посилання «Front Page» і кнопка «Logout». */
    @Test
    @DisplayName("TC-069: шапка сторінки входу /admin містить Front Page і Logout, заголовок Login")
    void adminLoginPageHeaderShowsFrontPageAndLogout() {
        // arrange + act
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();

        // assert (ОР TC-069: у шапці є посилання «Front Page» і кнопка «Logout»; заголовок «Login»)
        assertAll(
                () -> assertEquals("Login", page.loginHeaderText(), "заголовок картки входу"),
                () -> assertEquals("Front Page", page.frontPageLinkText(), "посилання у шапці"),
                () -> assertEquals("Logout", page.logoutButtonText(), "кнопка у шапці"),
                () -> assertTrue(page.loginFormShown(), "форма входу показана")
        );
    }

    /** Кейс TC-023 (F-80): вхід адміністратора з коректними обліковими даними. */
    @Test
    @DisplayName("TC-023: вхід admin/password відкриває дашборд адміністратора (Rooms/Report/Messages)")
    void adminLoginOpensDashboard() {
        // arrange (облікові дані demo — спільні, admin / password, за передумовами кейсу)
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();

        // act
        page.login("admin", "password").waitDashboard();

        // assert (ОР TC-023: завантажено панель адміністратора з розділами керування)
        String body = page.bodyText();
        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("/admin/"), "адреса адмінпанелі: "
                        + driver.getCurrentUrl()),
                () -> assertTrue(body.contains("Rooms"), "розділ Rooms у меню дашборда"),
                () -> assertTrue(body.contains("Report"), "розділ Report у меню дашборда"),
                () -> assertTrue(body.contains("Messages"), "розділ Messages у меню дашборда"),
                () -> assertTrue(body.contains("Branding"), "розділ Branding у меню дашборда")
        );
    }

    /** Кейс TC-024 (F-80): неправильний пароль — повідомлення про помилку, вхід не виконано. */
    @Test
    @DisplayName("TC-024: неправильний пароль — «Invalid credentials», форма входу лишається")
    void wrongPasswordShowsErrorAndKeepsLoginForm() {
        // arrange
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();

        // act
        page.login("admin", "wrong_password");
        String error = page.waitErrorAlert();

        // assert (ОР TC-024: повідомлення про помилку; вхід не виконано — форма лишається)
        assertAll(
                () -> assertEquals("Invalid credentials", error, "текст помилки входу"),
                () -> assertTrue(page.loginFormShown(), "форма входу лишається — вхід не виконано")
        );
    }

    /** Кейс TC-025 (F-85): вихід адміністратора завершує сесію. */
    @Test
    @DisplayName("TC-025: Logout завершує сесію — повернення на головну, /admin знову вимагає вхід")
    void logoutTerminatesSession() {
        // arrange: вхід успішний
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();
        page.login("admin", "password").waitDashboard();

        // act (поведінка demo v2.2: після Logout користувач повертається на головну сторінку)
        page.logoutAndWaitForFrontPage();

        // assert (ОР TC-025: вихід виконано; повторний доступ до /admin — лише через вхід)
        assertAll(
                () -> assertFalse(driver.getCurrentUrl().contains("/admin"),
                        "після виходу користувач не в адмінпанелі: " + driver.getCurrentUrl()),
                () -> assertTrue(driver.getCurrentUrl().equals(StayNowHomePage.BASE_URL)
                                || driver.getCurrentUrl().equals(StayNowHomePage.BASE_URL + "/"),
                        "повернено на головну сторінку demo: " + driver.getCurrentUrl())
        );

        // сесія завершена: повторне відкриття /admin показує форму входу, а не дашборд
        page.open();
        assertTrue(page.loginFormShown(), "після виходу /admin вимагає вхід — форма Login показана");
    }
}
