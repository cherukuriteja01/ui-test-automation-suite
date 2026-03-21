package org.example.base;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;

public class BaseTest {

    public WebDriver driver;

    @BeforeClass
    public void setup() {
        driver = createDriver();
    }

    // FIX 1: Extracted driver creation into its own method so
    // openLoginPage() can recreate the session if it has crashed.
    private WebDriver createDriver() {

        ChromeOptions options = new ChromeOptions();

        // FIX 2: ROOT CAUSE OF NoSuchSessionException —
        // Selenium 4.21.0 only bundles CDP bindings up to v125.
        // Chrome 146 uses CDP v146, causing a WebSocket disconnect
        // that kills the browser session mid-test.
        // --remote-debugging-port=0 disables CDP entirely so Selenium
        // communicates via pure WebDriver protocol with no CDP dependency.
        options.addArguments("--remote-debugging-port=0");

        // FIX 3: Additional stability flags for Chrome 146 on Windows 11.
        options.addArguments(
                "--no-sandbox",
                "--disable-dev-shm-usage",
                "--disable-extensions",
                "--disable-infobars",
                "--remote-allow-origins=*"
        );

        // Suppress "Chrome is being controlled..." banner.
        options.setExperimentalOption("excludeSwitches",
                new String[]{"enable-automation"});

        if (Boolean.parseBoolean(System.getProperty("headless", "false"))) {
            options.addArguments("--headless=new",
                    "--disable-gpu",
                    "--window-size=1920,1080");
        }

        WebDriver d = new ChromeDriver(options);
        d.manage().window().maximize();
        d.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
        d.manage().timeouts().scriptTimeout(Duration.ofSeconds(30));
        return d;
    }

    // FIX 4: Session recovery — if the browser has crashed or been closed
    // between tests, recreate the driver instead of failing every remaining
    // test with NoSuchSessionException. This is what caused 5 skipped tests.
    @BeforeMethod
    public void openLoginPage() {
        if (!isSessionAlive()) {
            System.out.println("[BaseTest] Session dead — recreating driver.");
            driver = createDriver();
        }
        navigateTo("https://www.saucedemo.com/");
    }

    // Checks whether the current WebDriver session is still usable.
    protected boolean isSessionAlive() {
        try {
            driver.getTitle(); // lightweight probe — throws if session is gone
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    protected void navigateTo(String url) {
        driver.get(url);
        // Wait for document.readyState == "complete" before handing
        // control to the test — prevents premature element lookups.
        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(d -> ((JavascriptExecutor) d)
                        .executeScript("return document.readyState")
                        .equals("complete"));
    }

    // FIX 5: Screenshot guard — check isSessionAlive() before attempting
    // getScreenshotAs(). The second NoSuchSessionException in the log was
    // caused by trying to screenshot after the session had already crashed.
    @AfterMethod(alwaysRun = true)
    public void captureScreenshotOnFailure(ITestResult result) {

        if (result.getStatus() == ITestResult.FAILURE && isSessionAlive()) {
            try {
                Path dir = Paths.get("test-screenshots");
                Files.createDirectories(dir);

                String fileName = result.getTestClass()
                        .getRealClass().getSimpleName()
                        + "_" + result.getMethod().getMethodName()
                        + ".png";

                File screenshot = ((TakesScreenshot) driver)
                        .getScreenshotAs(OutputType.FILE);

                Files.copy(screenshot.toPath(), dir.resolve(fileName));
                System.out.println("Screenshot saved: "
                        + dir.resolve(fileName));

            } catch (IOException e) {
                System.err.println("Could not save screenshot: "
                        + e.getMessage());
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void teardown() {
        if (driver != null) {
            try {
                driver.quit();
            } catch (Exception ignored) {
                // Session may already be dead — safe to swallow.
            }
        }
    }
}