package org.example.pages;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class LoginPage {

    private final WebDriver driver;
    private final WebDriverWait wait;

    // ── Slow-motion config ────────────────────────────────────────────────────
    // Controls how long (ms) the browser pauses after each visible action.
    // Change STEP_DELAY_MS to make tests faster or slower:
    //   500  = comfortable demo pace  ← current setting
    //   1000 = one full second per step (very easy to follow)
    //   200  = quick but still visible
    private static final int STEP_DELAY_MS = 500;

    // ─── Locators ─────────────────────────────────────────────────────────────

    private final By username      = By.id("user-name");
    private final By password      = By.id("password");
    private final By loginButton   = By.id("login-button");
    private final By inventoryList = By.className("inventory_list");
    private final By errorPrimary  = By.cssSelector("[data-test='error']");
    private final By errorFallback = By.cssSelector(".error-message-container h3");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait   = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    // ─── Page Actions ─────────────────────────────────────────────────────────

    public void enterUsername(String user) {
        log("Waiting for username field...");
        WebElement el = wait.until(
                ExpectedConditions.elementToBeClickable(username));

        highlight(el, "orange");           // STEP 1 — field found, highlight it
        pause("Highlighting username field");

        el.clear();
        typeSlowly(el, user);              // STEP 2 — type character by character
        highlight(el, "green");            // STEP 3 — turn green when done
        pause("Finished typing username: " + user);
    }

    public void enterPassword(String pass) {
        log("Waiting for password field...");
        WebElement el = wait.until(
                ExpectedConditions.elementToBeClickable(password));

        highlight(el, "orange");
        pause("Highlighting password field");

        el.clear();
        typeSlowly(el, pass);
        highlight(el, "green");
        pause("Finished typing password");
    }

    public void clickLogin() {
        log("Waiting for login button...");
        WebElement el = wait.until(
                ExpectedConditions.elementToBeClickable(loginButton));

        highlight(el, "blue");             // STEP — button found, highlight blue
        pause("About to click login button...");

        el.click();
        log("Login button clicked — waiting for page response...");
        pause("Clicked — observing result");
    }

    public String getErrorMessage() {
        log("Waiting for error message to appear...");

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
        WebElement errorEl;

        try {
            errorEl = shortWait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorPrimary));
        } catch (Exception ignored) {
            log("Primary error locator missed — trying fallback...");
            errorEl = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(errorFallback));
        }

        highlight(errorEl, "red");         // STEP — flash the error message red
        pause("Error message visible: " + errorEl.getText());
        return errorEl.getText();
    }

    public boolean isInventoryPageDisplayed() {
        log("Waiting for inventory page to load...");
        try {
            WebElement inv = wait.until(
                    ExpectedConditions.visibilityOfElementLocated(inventoryList));
            highlightSuccess(inv);         // STEP — flash green border on product list
            pause("Inventory page loaded — login SUCCESS");
            return true;
        } catch (Exception e) {
            log("Inventory page did NOT load.");
            return false;
        }
    }

    // ─── Slow-motion helpers ──────────────────────────────────────────────────

    // Types each character individually with a small inter-key delay so you
    // can watch the text appear letter-by-letter in the browser.
    private void typeSlowly(WebElement el, String text) {
        for (char c : text.toCharArray()) {
            el.sendKeys(String.valueOf(c));
            sleep(80); // 80 ms per keystroke — visible but not painfully slow
        }
    }

    // Pauses execution for STEP_DELAY_MS and prints what is happening.
    private void pause(String stepDescription) {
        log("  → " + stepDescription);
        sleep(STEP_DELAY_MS);
    }

    // Highlights the element with a coloured border and a light background tint.
    private void highlight(WebElement el, String colour) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.border='3px solid " + colour + "';" +
                            "arguments[0].style.backgroundColor='rgba(255,255,0,0.25)';",
                    el);
        } catch (StaleElementReferenceException ignored) { }
    }

    // Green border + green tint for success states.
    private void highlightSuccess(WebElement el) {
        try {
            ((JavascriptExecutor) driver).executeScript(
                    "arguments[0].style.border='3px solid #00AA00';" +
                            "arguments[0].style.backgroundColor='rgba(0,200,0,0.15)';",
                    el);
        } catch (StaleElementReferenceException ignored) { }
    }

    private void log(String msg) {
        System.out.println("[LoginPage] " + msg);
    }

    private void sleep(int ms) {
        try { Thread.sleep(ms); }
        catch (InterruptedException e) { Thread.currentThread().interrupt(); }
    }
}