package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
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
    @DisplayName("TC-023: вхід admin/password відкриває дашборд з переліком номерів (101/102/103)")
    void adminLoginOpensDashboard() {
        // arrange (облікові дані demo — спільні, admin / password, за передумовами кейсу)
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();

        // act
        page.login("admin", "password").waitDashboard();

        // assert (ОР TC-023: завантажено розділ керування номерами з переліком номерів —
        // перелік 101/102/103 додано за зауваженням ТА п. 3.3; крок «Logout» не дублюється —
        // його перевіряє окремий кейс TC-025)
        String body = page.bodyText();
        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("/admin/"), "адреса адмінпанелі: "
                        + driver.getCurrentUrl()),
                () -> assertTrue(body.contains("Rooms"), "розділ Rooms у меню дашборда"),
                () -> assertTrue(body.contains("Report"), "розділ Report у меню дашборда"),
                () -> assertTrue(body.contains("Messages"), "розділ Messages у меню дашборда"),
                () -> assertTrue(body.contains("Branding"), "розділ Branding у меню дашборда"),
                () -> assertTrue(body.contains("101") && body.contains("102") && body.contains("103"),
                        "перелік номерів 101/102/103 у дашборді: "
                                + body.substring(0, Math.min(300, body.length())))
        );
    }

    /** Кейс TC-024 (F-80, GUI-70): неправильний пароль — вхід не виконано. */
    @Test
    @DisplayName("TC-024: неправильний пароль (F-80, GUI-70) — «Invalid credentials», форма входу лишається")
    void wrongPasswordShowsErrorAndKeepsLoginForm() {
        // arrange (дані кейсу: admin / wrongpass1)
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();

        // act
        page.login("admin", "wrongpass1");
        String error = page.waitErrorAlert();

        // assert (ОР TC-024: лишився на сторінці входу, доступу немає. Частину ОР «поля позначено
        // як помилкові» demo не виконує — BUG-003 спринта 3, не перевіряється (зауваження ТА п. 3.4);
        // повідомлення «Invalid credentials» кейс не вимагає — додаткова м'яка перевірка)
        assertAll(
                () -> assertEquals("Invalid credentials", error, "текст помилки входу"),
                () -> assertTrue(page.loginFormShown(), "форма входу лишається — вхід не виконано")
        );
    }

    /** Кейс TC-025 (F-85, S-10): вихід адміністратора — після виходу очікується сторінка входу. */
    @Test
    @DisplayName("TC-025: Logout (F-85, крок 6) — після виходу очікується сторінка входу; "
            + "demo повертає на головну (дефект demo, червоний)")
    void logoutReturnsToLoginPage() {
        // arrange: вхід успішний
        StayNowAdminLoginPage page = new StayNowAdminLoginPage(driver, wait).open();
        page.login("admin", "password").waitDashboard();

        // act
        page.logoutAndWaitForFrontPage();

        // assert (ОР TC-025 крок 6 і F-85: «після виходу — сторінка входу адміністратора».
        // Demo v2.2 повертає на головну — це окремий дефект demo (реєструється Issue з міткою
        // `demo`; BUG-001 спринта 3 не стосується), тому очікування з кейсу ВІДНОВЛЕНО
        // і тест лишається червоним до виправлення demo)
        assertAll(
                () -> assertTrue(driver.getCurrentUrl().contains("/admin"),
                        "F-85/TC-025: після Logout очікується сторінка входу /admin, фактично: "
                                + driver.getCurrentUrl()),
                () -> assertTrue(page.loginFormShown(),
                        "форма входу адміністратора показана після виходу")
        );
    }
}
