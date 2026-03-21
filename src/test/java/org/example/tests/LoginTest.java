package org.example.tests;

import org.example.base.BaseTest;
import org.example.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.*;

public class LoginTest extends BaseTest {

    private LoginPage login;

    @BeforeMethod
    public void initLoginPage() {
        login = new LoginPage(driver);
    }

    // ─── Negative / Error Tests
    @Test(priority = 1,
            description = "Verify locked-out user cannot login")
    public void lockedUserTest() {
        announce("TEST 1 — Locked-out user login");

        login.enterUsername("locked_out_user");
        login.enterPassword("secret_sauce");
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("locked out"),
                "Locked-out user error message was not displayed or text changed");

        pass("TEST 1 PASSED");
    }

    @Test(priority = 2,
            description = "Verify empty username shows required-field error")
    public void emptyUsernameTest() {
        announce("TEST 2 — Empty username");

        // No enterUsername() call — intentionally blank
        login.enterPassword("secret_sauce");
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("Username is required"),
                "Expected 'Username is required' error — got: "
                        + login.getErrorMessage());

        pass("TEST 2 PASSED");
    }

    @Test(priority = 3,
            description = "Verify empty password shows required-field error")
    public void emptyPasswordTest() {
        announce("TEST 3 — Empty password");

        login.enterUsername("standard_user");
        // No enterPassword() call — intentionally blank
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("Password is required"),
                "Expected 'Password is required' error — got: "
                        + login.getErrorMessage());

        pass("TEST 3 PASSED");
    }

    @Test(priority = 4,
            description = "Verify wrong username + wrong password shows mismatch error")
    public void invalidLoginTest() {
        announce("TEST 4 — Wrong username + wrong password");

        login.enterUsername("wrong_user");
        login.enterPassword("wrong_password");
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("do not match"),
                "Expected credentials-mismatch error — got: "
                        + login.getErrorMessage());

        pass("TEST 4 PASSED");
    }

    @Test(priority = 5,
            description = "Verify valid username with wrong password shows mismatch error")
    public void wrongPasswordTest() {
        announce("TEST 5 — Valid username, wrong password");

        login.enterUsername("standard_user");
        login.enterPassword("wrong_password");
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("do not match"),
                "Expected credentials-mismatch error for wrong password — got: "
                        + login.getErrorMessage());

        pass("TEST 5 PASSED");
    }

    @Test(priority = 6,
            description = "Verify wrong username with valid password shows mismatch error")
    public void wrongUsernameTest() {
        announce("TEST 6 — Wrong username, valid password");

        login.enterUsername("wrong_user");
        login.enterPassword("secret_sauce");
        login.clickLogin();

        Assert.assertTrue(
                login.getErrorMessage().contains("do not match"),
                "Expected credentials-mismatch error for wrong username — got: "
                        + login.getErrorMessage());

        pass("TEST 6 PASSED");
    }

    @Test(priority = 7,
            description = "Verify valid login with correct username and password")
    public void validLoginTest() {
        announce("TEST 7 — Valid login (LAST)");

        login.enterUsername("standard_user");
        login.enterPassword("secret_sauce");
        login.clickLogin();

        Assert.assertTrue(login.isInventoryPageDisplayed(),
                "Valid login failed — inventory page was not displayed");

        pass("TEST 7 PASSED — Inventory page confirmed");
    }

    // ─── Console helpers ──────────────────────────────────────────────────────

    private void announce(String title) {
        System.out.println("\n" +
                "╔══════════════════════════════════════════╗\n" +
                "║  " + padRight(title, 40) + "║\n" +
                "╚══════════════════════════════════════════╝");
    }

    private void pass(String msg) {
        System.out.println("  ✔  " + msg + "\n");
    }

    private String padRight(String s, int n) {
        return String.format("%-" + n + "s", s);
    }
}