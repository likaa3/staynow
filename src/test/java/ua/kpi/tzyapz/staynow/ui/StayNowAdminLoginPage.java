package ua.kpi.tzyapz.staynow.ui;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

/**
 * Page Object сторінки входу адміністратора ({@code /admin}) demo «СтейНау».
 * Покриває кейси спринта 3: TC-069 (шапка сторінки входу), TC-023 (успішний вхід),
 * TC-024 (неправильний пароль), TC-025 (вихід).
 */
public class StayNowAdminLoginPage {

    public static final By LOGIN_HEADER = By.cssSelector(".card-header h2");
    public static final By USERNAME = By.id("username");
    public static final By PASSWORD = By.id("password");
    public static final By LOGIN_BUTTON = By.id("doLogin");
    public static final By FRONT_PAGE_LINK = By.id("frontPageLink");
    public static final By LOGOUT_BUTTON = By.cssSelector("button.btn-outline-danger");
    public static final By ERROR_ALERT = By.cssSelector(".alert.alert-danger");
    private static final By BODY = By.tagName("body");

    private final WebDriver driver;
    private final WebDriverWait wait;

    public StayNowAdminLoginPage(WebDriver driver, WebDriverWait wait) {
        this.driver = driver;
        this.wait = wait;
    }

    /** Відкриває /admin і чекає форму входу. */
    public StayNowAdminLoginPage open() {
        driver.get(StayNowHomePage.BASE_URL + "admin");
        wait.until(d -> ((JavascriptExecutor) d).executeScript("return document.readyState").equals("complete"));
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        return this;
    }

    /** Явне очікування появи форми входу (після переходу з головної через меню «Admin»). */
    public StayNowAdminLoginPage waitLoginForm() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME));
        return this;
    }

    /** Вводить облікові дані і натискає «Login» (форма — у вікні перегляду, звичайний клік). */
    public StayNowAdminLoginPage login(String username, String password) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(USERNAME)).sendKeys(username);
        driver.findElement(PASSWORD).sendKeys(password);
        wait.until(ExpectedConditions.elementToBeClickable(LOGIN_BUTTON)).click();
        return this;
    }

    /** Чекає завантаження дашборда адміністратора (форма входу зникає, URL → /admin/…). */
    public StayNowAdminLoginPage waitDashboard() {
        wait.until(ExpectedConditions.invisibilityOfElementLocated(LOGIN_BUTTON));
        wait.until(d -> d.getCurrentUrl().contains("/admin/")
                && !d.getCurrentUrl().endsWith("/admin"));
        return this;
    }

    /**
     * Натискає «Logout» і чекає повернення на головну сторінку demo.
     * Поведінка demo v2.2 (зафіксовано при автоматизації): вихід завершує сесію
     * і перенаправляє на головну сторінку, а не на форму входу адміністратора.
     */
    public StayNowAdminLoginPage logoutAndWaitForFrontPage() {
        wait.until(ExpectedConditions.elementToBeClickable(LOGOUT_BUTTON)).click();
        wait.until(d -> !d.getCurrentUrl().contains("/admin"));
        return this;
    }

    /** Явне очікування повідомлення про помилку входу. */
    public String waitErrorAlert() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(ERROR_ALERT)).getText().trim();
    }

    public boolean loginFormShown() {
        return driver.findElement(USERNAME).isDisplayed();
    }

    public String frontPageLinkText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(FRONT_PAGE_LINK)).getText().trim();
    }

    public String logoutButtonText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LOGOUT_BUTTON)).getText().trim();
    }

    public String loginHeaderText() {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(LOGIN_HEADER)).getText().trim();
    }

    public String bodyText() {
        return driver.findElement(BODY).getText();
    }
}
