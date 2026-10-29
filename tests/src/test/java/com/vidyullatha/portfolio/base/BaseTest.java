package com.vidyullatha.portfolio.base;

import com.microsoft.playwright.Browser;
import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.BrowserType;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.Playwright;
import com.microsoft.playwright.options.ColorScheme;
import com.microsoft.playwright.options.ReducedMotion;
import com.vidyullatha.portfolio.config.Config;
import com.vidyullatha.portfolio.pages.PortfolioPage;
import org.testng.ITestResult;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;

import java.nio.file.Path;

/**
 * Browser lifecycle for every test class.
 *
 * <ul>
 *   <li>One browser per test class (fast).</li>
 *   <li>A fresh context and page per test method (isolated cookies, storage and viewport).</li>
 *   <li>A full-page screenshot in target/screenshots/ whenever a test fails.</li>
 * </ul>
 *
 * Contexts use reduced motion so scroll and fade-in animations finish instantly
 * and assertions are not timing-dependent.
 */
public abstract class BaseTest {

    protected static final int DESKTOP_WIDTH = 1440;
    protected static final int DESKTOP_HEIGHT = 900;

    protected Playwright playwright;
    protected Browser browser;
    protected BrowserContext context;
    protected Page page;
    protected PortfolioPage portfolio;

    @BeforeClass(alwaysRun = true)
    public void launchBrowser() {
        playwright = Playwright.create();
        BrowserType type = switch (Config.browser()) {
            case "firefox" -> playwright.firefox();
            case "webkit" -> playwright.webkit();
            case "chromium" -> playwright.chromium();
            default -> throw new IllegalArgumentException(
                    "Unknown browser '" + Config.browser() + "'. Use chromium, firefox or webkit.");
        };
        browser = type.launch(new BrowserType.LaunchOptions().setHeadless(Config.headless()));
    }

    @BeforeMethod(alwaysRun = true)
    public void openPortfolio() {
        openWith(defaultContext());
    }

    /** Desktop size, light system theme, reduced motion. */
    protected Browser.NewContextOptions defaultContext() {
        return new Browser.NewContextOptions()
                .setViewportSize(DESKTOP_WIDTH, DESKTOP_HEIGHT)
                .setColorScheme(ColorScheme.LIGHT)
                .setReducedMotion(ReducedMotion.REDUCE);
    }

    /** Throws away the current page and opens the portfolio in a new context with these options. */
    protected void openWith(Browser.NewContextOptions options) {
        if (context != null) {
            context.close();
        }
        context = browser.newContext(options);
        page = context.newPage();
        portfolio = new PortfolioPage(page);
        portfolio.open();
    }

    protected void openAtViewport(int width, int height) {
        openWith(defaultContext().setViewportSize(width, height));
    }

    @AfterMethod(alwaysRun = true)
    public void closeContext(ITestResult result) {
        try {
            if (result.getStatus() == ITestResult.FAILURE && page != null && !page.isClosed()) {
                String name = String.join("-",
                        Config.browser(),
                        getClass().getSimpleName(),
                        result.getMethod().getMethodName(),
                        String.valueOf(System.currentTimeMillis()));
                Path file = Config.screenshotDir().resolve(name + ".png");
                page.screenshot(new Page.ScreenshotOptions().setPath(file).setFullPage(true));
                System.out.println("Failure screenshot: " + file.toAbsolutePath());
            }
        } catch (RuntimeException e) {
            System.err.println("Could not save failure screenshot: " + e.getMessage());
        } finally {
            if (context != null) {
                context.close();
                context = null;
            }
        }
    }

    @AfterClass(alwaysRun = true)
    public void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }
}
