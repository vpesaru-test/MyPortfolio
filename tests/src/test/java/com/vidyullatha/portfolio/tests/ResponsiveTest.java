package com.vidyullatha.portfolio.tests;

import com.vidyullatha.portfolio.base.BaseTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

/** Layout holds up from small phones to large desktops; the mobile menu works. */
public class ResponsiveTest extends BaseTest {

    private static final int PHONE_WIDTH = 390;
    private static final int PHONE_HEIGHT = 844;

    @DataProvider(name = "viewports")
    public Object[][] viewports() {
        return new Object[][] {
                {"small phone", 360, 740},
                {"phone", 390, 844},
                {"tablet", 768, 1024},
                {"small laptop", 1024, 768},
                {"laptop", 1280, 800},
                {"desktop", 1920, 1080},
        };
    }

    @Test(dataProvider = "viewports", description = "No sideways scrolling at common screen sizes")
    public void noHorizontalScroll(String device, int width, int height) {
        openAtViewport(width, height);
        assertFalse(portfolio.hasHorizontalOverflow(), "Page scrolls sideways on " + device + " (" + width + "px)");
    }

    @Test(description = "Desktop shows the nav links and hides the menu button")
    public void desktopShowsInlineNav() {
        assertThat(portfolio.nav().menuToggle()).isHidden();
        assertThat(portfolio.nav().linkList()).isVisible();
    }

    @Test(description = "Phone: menu button opens the links, and choosing one closes the menu")
    public void mobileMenuOpensAndCloses() {
        openAtViewport(PHONE_WIDTH, PHONE_HEIGHT);
        var nav = portfolio.nav();

        assertThat(nav.menuToggle()).isVisible();
        assertThat(nav.linkList()).isHidden();
        assertThat(nav.menuToggle()).hasAttribute("aria-expanded", "false");

        nav.openMobileMenu();
        assertThat(nav.linkList()).isVisible();
        assertThat(nav.menuToggle()).hasAttribute("aria-expanded", "true");

        nav.link("skills").click();
        assertThat(page).hasURL(Pattern.compile(".*#skills$"));
        assertThat(nav.linkList()).isHidden();
        assertThat(nav.menuToggle()).hasAttribute("aria-expanded", "false");
    }

    @Test(description = "Phone: photo sits above the greeting")
    public void mobilePhotoAboveGreeting() {
        openAtViewport(PHONE_WIDTH, PHONE_HEIGHT);
        double photoY = portfolio.hero().photoWrapper().boundingBox().y;
        double headingY = portfolio.hero().heading().boundingBox().y;
        assertTrue(photoY < headingY, "Photo (" + photoY + ") should be above the greeting (" + headingY + ")");
    }

    @Test(description = "Phone: stats band uses two columns")
    public void mobileStatsTwoColumns() {
        openAtViewport(PHONE_WIDTH, PHONE_HEIGHT);
        double first = portfolio.statItems().nth(0).boundingBox().y;
        double second = portfolio.statItems().nth(1).boundingBox().y;
        double third = portfolio.statItems().nth(2).boundingBox().y;
        assertTrue(Math.abs(first - second) < 1, "First two stats should share a row");
        assertTrue(third > first, "Third stat should start a new row");
    }
}
