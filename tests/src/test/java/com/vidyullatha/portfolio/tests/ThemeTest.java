package com.vidyullatha.portfolio.tests;

import com.microsoft.playwright.options.ColorScheme;
import com.vidyullatha.portfolio.base.BaseTest;
import org.testng.annotations.Test;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;

/** Light/dark mode: follows the system setting, can be toggled, and remembers the choice. */
public class ThemeTest extends BaseTest {

    @Test(description = "With a light system theme the page is light and no override is set")
    public void followsLightSystemTheme() {
        assertThat(page.locator("html")).not().hasAttribute("data-theme", "dark");
        portfolio.waitForBackground(false);
    }

    @Test(description = "With a dark system theme the page is dark without any click")
    public void followsDarkSystemTheme() {
        openWith(defaultContext().setColorScheme(ColorScheme.DARK));
        portfolio.waitForBackground(true);
    }

    @Test(description = "Toggle switches to dark and back to light")
    public void toggleSwitchesTheme() {
        portfolio.nav().toggleTheme();
        assertThat(page.locator("html")).hasAttribute("data-theme", "dark");
        portfolio.waitForBackground(true);

        portfolio.nav().toggleTheme();
        assertThat(page.locator("html")).hasAttribute("data-theme", "light");
        portfolio.waitForBackground(false);
    }

    @Test(description = "The chosen theme is remembered after a reload")
    public void themeChoicePersistsAcrossReload() {
        portfolio.nav().toggleTheme();
        assertThat(page.locator("html")).hasAttribute("data-theme", "dark");

        page.reload();

        assertThat(page.locator("html")).hasAttribute("data-theme", "dark");
        portfolio.waitForBackground(true);
    }
}
