package com.vidyullatha.portfolio.tests;

import com.microsoft.playwright.Locator;
import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.data.SiteData;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/** Top navigation, in-page scrolling, active link highlighting and back-to-top. */
public class NavigationTest extends BaseTest {

    /** Sections the nav should link to, given what is in _data. */
    @DataProvider(name = "navSections")
    public Object[][] navSections() {
        List<String> ids = new ArrayList<>(List.of("about", "skills", "experience"));
        if (!SiteData.projects().isEmpty()) {
            ids.add("projects");
        }
        ids.add("education");
        ids.add("contact");
        return ids.stream().map(id -> new Object[] {id}).toArray(Object[][]::new);
    }

    @Test(description = "Nav shows exactly the expected links, in order")
    public void navLinksMatchVisibleSections() {
        List<String> expected = new ArrayList<>();
        for (Object[] row : navSections()) {
            expected.add((String) row[0]);
        }
        assertEquals(portfolio.nav().linkTargets(), expected);
    }

    @Test(description = "Every in-page link on the page points to an element that exists")
    public void noBrokenInPageLinks() {
        List<String> broken = new ArrayList<>();
        for (Locator link : page.locator("a[href^='#']").all()) {
            String href = link.getAttribute("href");
            if (href.length() > 1 && page.locator(href).count() != 1) {
                broken.add(href);
            }
        }
        assertTrue(broken.isEmpty(), "Links to missing sections: " + broken);
    }

    @Test(dataProvider = "navSections", description = "Clicking a nav link scrolls that section into view")
    public void navLinkScrollsToSection(String sectionId) {
        portfolio.nav().link(sectionId).click();
        assertThat(page).hasURL(Pattern.compile(".*#" + sectionId + "$"));
        assertThat(portfolio.section(sectionId)).isInViewport();
    }

    @Test(description = "The nav link for the section in view is highlighted")
    public void activeLinkFollowsScrollPosition() {
        portfolio.scrollIntoView("experience");
        assertThat(portfolio.nav().link("experience")).hasClass(Pattern.compile("\\bis-active\\b"));

        portfolio.scrollIntoView("education");
        assertThat(portfolio.nav().link("education")).hasClass(Pattern.compile("\\bis-active\\b"));
        assertThat(portfolio.nav().link("experience")).not().hasClass(Pattern.compile("\\bis-active\\b"));
    }

    @Test(description = "Clicking the logo returns to the top of the page")
    public void logoReturnsToTop() {
        portfolio.scrollTo(3000);
        portfolio.nav().logo().click();
        portfolio.waitForScrollNear(0, 5);
    }

    @Test(description = "Back-to-top button appears after scrolling and returns to the top")
    public void backToTopButton() {
        Pattern visible = Pattern.compile("\\bis-visible\\b");
        assertThat(portfolio.backToTop()).not().hasClass(visible);

        portfolio.scrollTo(2000);
        assertThat(portfolio.backToTop()).hasClass(visible);

        portfolio.backToTop().click();
        portfolio.waitForScrollNear(0, 5);
        assertThat(portfolio.backToTop()).not().hasClass(visible);
    }

    @Test(description = "Numbered section labels run 01, 02, 03… with no gaps")
    public void sectionNumbersAreSequential() {
        Pattern number = Pattern.compile("^(\\d{2}) /");
        List<Integer> found = new ArrayList<>();
        for (String label : portfolio.numberedEyebrows().allTextContents()) {
            Matcher m = number.matcher(label.trim());
            if (m.find()) {
                found.add(Integer.parseInt(m.group(1)));
            }
        }
        assertTrue(found.size() >= 4, "Expected at least 4 numbered sections, found " + found);
        for (int i = 0; i < found.size(); i++) {
            assertEquals((int) found.get(i), i + 1, "Section numbers: " + found);
        }
    }
}
