package ua.kpi.tzyapz.staynow.ui;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

/**
 * Спільний життєвий цикл UI-тестів: незалежний запуск браузера на кожен тест.
 *
 * - Браузер відкривається у {@link BeforeEach}: Chrome з аргументами
 *   {@code --headless=new} та {@code --window-size=1280,900}. Для демонстрації на екрані
 *   запустіть тести з системною властивістю {@code -Dheadless=false}:
 *   {@code mvn test -DexcludedGroups=none -Dheadless=false}.
 * - Браузер обов'язково закривається у {@link AfterEach} через {@code driver.quit()}.
 *
 * ЗАБОРОНЕНО чек-листом §5.3: {@code Thread.sleep(...)} і неявні очікування
 * {@code implicitlyWait(...)} — у тестах застосовуються лише явні очікування
 * {@link WebDriverWait} з {@code ExpectedConditions} або умовами-лямбдами.
 */
public abstract class UiTestBase {

    /** Таймаут явних очікувань. */
    protected static final Duration TIMEOUT = Duration.ofSeconds(10);

    protected WebDriver driver;
    protected WebDriverWait wait;

    @BeforeEach
    void openBrowser() {
        ChromeOptions options = new ChromeOptions();
        if (Boolean.parseBoolean(System.getProperty("headless", "true"))) {
            options.addArguments("--headless=new");
        }
        options.addArguments("--window-size=1280,900");
        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, TIMEOUT);
    }

    @AfterEach
    void closeBrowser() {
        if (driver != null) {
            driver.quit();
        }
    }
}
