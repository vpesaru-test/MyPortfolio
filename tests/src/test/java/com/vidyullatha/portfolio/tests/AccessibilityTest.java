package com.vidyullatha.portfolio.tests;

import com.microsoft.playwright.Locator;
import com.vidyullatha.portfolio.base.BaseTest;
import com.vidyullatha.portfolio.config.Config;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

import static com.microsoft.playwright.assertions.PlaywrightAssertions.assertThat;
import static org.testng.Assert.assertTrue;

/** Baseline accessibility and link-safety checks. */
public class AccessibilityTest extends BaseTest {

    @Test(description = "Exactly one H1 on the page")
    public void singleH1() {
        assertThat(page.locator("h1")).hasCount(1);
    }

    @Test(description = "Page has nav, main and footer landmarks")
    public void landmarksPresent() {
        assertThat(page.locator("nav")).hasCount(1);
        assertThat(page.locator("main")).hasCount(1);
        assertThat(page.locator("footer")).hasCount(1);
    }

    @Test(description = "Every image has alt text")
    public void imagesHaveAltText() {
        List<String> missing = new ArrayList<>();
        for (Locator img : page.locator("img").all()) {
            String alt = img.getAttribute("alt");
            if (alt == null || alt.isBlank()) {
                missing.add(img.getAttribute("src"));
            }
        }
        assertTrue(missing.isEmpty(), "Images without alt text: " + missing);
    }

    @Test(description = "Icon-only buttons have an accessible name")
    public void iconButtonsHaveLabels() {
        List<String> unlabeled = new ArrayList<>();
        for (Locator button : page.locator("button").all()) {
            String label = button.getAttribute("aria-label");
            String text = button.textContent() == null ? "" : button.textContent().replaceAll("[^\\p{L}\\p{N}]", "");
            if ((label == null || label.isBlank()) && text.isEmpty()) {
                unlabeled.add(button.evaluate("b => b.outerHTML").toString());
            }
        }
        assertTrue(unlabeled.isEmpty(), "Buttons with no accessible name:\n" + String.join("\n", unlabeled));
    }

    @Test(description = "Links that open a new tab use rel=noopener")
    public void newTabLinksAreSafe() {
        List<String> unsafe = new ArrayList<>();
        for (Locator link : page.locator("a[target='_blank']").all()) {
            String rel = link.getAttribute("rel");
            if (rel == null || !rel.contains("noopener")) {
                unsafe.add(link.getAttribute("href"));
            }
        }
        assertTrue(unsafe.isEmpty(), "target=_blank links without rel=noopener: " + unsafe);
    }

    @Test(description = "No empty links (every link has text or a label)")
    public void linksHaveText() {
        List<String> empty = new ArrayList<>();
        for (Locator link : page.locator("a").all()) {
            String text = link.textContent() == null ? "" : link.textContent().trim();
            String label = link.getAttribute("aria-label");
            if (text.isEmpty() && (label == null || label.isBlank())) {
                empty.add(link.getAttribute("href"));
            }
        }
        assertTrue(empty.isEmpty(), "Links with no text: " + empty);
    }

    @Test(description = "Keyboard: Tab reaches the logo first and then the nav links")
    public void keyboardTabOrderStartsAtNav() {
        // Safari/WebKit only tabs through links with Option held, matching its default setting
        String tab = "webkit".equals(Config.browser()) ? "Alt+Tab" : "Tab";
        page.keyboard().press(tab);
        assertThat(portfolio.nav().logo()).isFocused();
        page.keyboard().press(tab);
        assertThat(portfolio.nav().links().first()).isFocused();
    }
}
